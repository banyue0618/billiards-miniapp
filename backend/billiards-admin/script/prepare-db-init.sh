#!/bin/bash

# ==========================================
# 数据库初始化脚本准备工具（仅首次部署该脚本会执行，后续数据库变更动作请自行复制sql语句执行）
# 功能：生成随机密码并准备SQL初始化脚本
# ==========================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_DIR="$SCRIPT_DIR/sql"
TEMP_SQL_DIR="$SCRIPT_DIR/temp_sql"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 生成数据库凭据
generate_db_credentials() {
    print_info "生成/复用数据库凭据..."

    print_info "脚本dir: $SCRIPT_DIR"

    local ENV_FILE="$SCRIPT_DIR/../.env.db"

    if [[ -f "$ENV_FILE" ]] && grep -q "MYSQL_ROOT_PASSWORD=" "$ENV_FILE"; then
        # 复用已有凭据，避免与已初始化数据库密码不一致
        print_info "检测到已存在的 .env.db，复用其中的凭据"
        # shellcheck source=/dev/null
        source "$ENV_FILE"
        ROOT_PASSWORD="$MYSQL_ROOT_PASSWORD"
        if [[ -z "$REDIS_PASSWORD" ]]; then
            REDIS_PASSWORD=$(generate_password 16)
            print_info "自动生成Redis密码: ${REDIS_PASSWORD:0:4}***"
        fi
        print_success "已复用现有凭据"
    else
        # 统一使用一个密码：只生成Root密码，应用也使用相同密码
        ROOT_PASSWORD=$(generate_password 20)
        print_success "数据库凭据生成完成"
        print_info "统一密码: $ROOT_PASSWORD"
        print_info "Root用户和应用用户将使用相同密码"

        # 生成Redis密码（如果没有设置的话）
        if [[ -z "$REDIS_PASSWORD" ]]; then
            REDIS_PASSWORD=$(generate_password 16)
            print_info "自动生成Redis密码: ${REDIS_PASSWORD:0:4}***"
        else
            print_info "使用传入的Redis密码: ${REDIS_PASSWORD:0:4}***"
        fi
        echo "环境目录是: $ENV_FILE"
        # 保存到环境变量文件（供Docker Compose使用）
        cat > "$ENV_FILE" << EOF
# 数据库和缓存凭据（自动生成）
# 生成时间: $(date)
# 注意：Root用户和应用用户使用相同密码以避免混乱
MYSQL_ROOT_PASSWORD=$ROOT_PASSWORD
BILLIARDS_DB_PASSWORD=$ROOT_PASSWORD
REDIS_PASSWORD=$REDIS_PASSWORD
EOF
        print_success "数据库凭据已保存到 .env.db"
    fi

    # 导出环境变量（无论复用还是新生成都导出）
    export MYSQL_ROOT_PASSWORD="$ROOT_PASSWORD"
    export BILLIARDS_DB_PASSWORD="$ROOT_PASSWORD"
    export REDIS_PASSWORD="$REDIS_PASSWORD"
}

# 准备SQL脚本
prepare_sql_scripts() {
    print_info "准备SQL初始化脚本..."

    # 清理并创建临时目录
    rm -rf "$TEMP_SQL_DIR"
    mkdir -p "$TEMP_SQL_DIR"

    # 检查SQL目录是否存在
    if [[ ! -d "$SQL_DIR" ]]; then
        print_error "SQL目录不存在: $SQL_DIR"
        exit 1
    fi

    # 获取所有文件夹，按修改时间排序（最旧的在前）
    local folders
    # 使用 find + stat 或 ls 查找目录，按修改时间排序
    # 优先使用 find -printf（GNU find），否则使用 ls -td（更通用）
    if find "$SQL_DIR" -mindepth 1 -maxdepth 1 -type d -printf '%T@ %p\n' >/dev/null 2>&1; then
        # GNU find 支持 -printf，按修改时间戳排序（最旧的在前）
        mapfile -t folders < <(find "$SQL_DIR" -mindepth 1 -maxdepth 1 -type d -printf '%T@ %p\n' | sort -n | cut -d' ' -f2-)
    else
        # 使用 ls -td 按修改时间排序（最新的在前），然后反转数组
        local temp_folders
        mapfile -t temp_folders < <(ls -td "$SQL_DIR"/*/ 2>/dev/null)
        # 反转数组（最旧的在前）
        folders=()
        for ((i=${#temp_folders[@]}-1; i>=0; i--)); do
            folders+=("${temp_folders[i]}")
        done
    fi

    if [[ ${#folders[@]} -eq 0 ]]; then
        print_warning "SQL目录下没有找到任何文件夹"
        return 0
    fi

    print_info "找到 ${#folders[@]} 个SQL文件夹，按时间顺序处理..."

    local file_counter=1
    local total_files=0

    # 遍历每个文件夹
    for folder in "${folders[@]}"; do
        local folder_name=$(basename "$folder")
        print_info "处理文件夹: $folder_name"

        # 查找文件夹中的所有SQL文件，按文件名排序
        local sql_files
        mapfile -t sql_files < <(find "$folder" -maxdepth 1 -type f -name "*.sql" | sort)

        if [[ ${#sql_files[@]} -eq 0 ]]; then
            print_warning "  文件夹 $folder_name 中没有找到SQL文件，跳过"
            continue
        fi

        # 处理每个SQL文件
        for sql_file in "${sql_files[@]}"; do
            local file_name=$(basename "$sql_file")
            local seq_num=$(printf "%02d" "$file_counter")
            local target_file="$TEMP_SQL_DIR/${seq_num}-${folder_name}-${file_name}"

            # 如果文件包含密码占位符，则替换；否则直接复制
            if grep -q '\${BILLIARDS_DB_PASSWORD}' "$sql_file" 2>/dev/null; then
                print_info "  处理文件: $file_name (替换密码占位符)"
                print_info "  源文件路径: $sql_file"
                print_info "  目标文件路径: $target_file"
                
                # 确保目标目录存在
                mkdir -p "$(dirname "$target_file")"
                
                # 优先使用 perl，更安全地处理特殊字符
                # 如果 perl 不可用，使用 sed（使用 | 作为分隔符）
                if command -v perl >/dev/null 2>&1; then
                    print_info "  使用 perl 进行替换..."
                    # 使用 perl 替换，通过环境变量传递密码
                    # 临时禁用 set -e，以便捕获错误
                    set +e
                    ROOT_PASSWORD="$ROOT_PASSWORD" perl -pe 's/\$\{BILLIARDS_DB_PASSWORD\}/$ENV{ROOT_PASSWORD}/g' "$sql_file" > "$target_file" 2>&1
                    local perl_exit=$?
                    set -e
                    if [[ $perl_exit -ne 0 ]]; then
                        print_error "  perl 替换失败，退出码: $perl_exit"
                        print_error "  源文件: $sql_file"
                        print_error "  目标文件: $target_file"
                        exit 1
                    fi
                    print_info "  perl 替换成功"
                else
                    print_info "  perl 不可用，使用 sed..."
                    # 回退到 sed，使用 | 作为分隔符
                    # 转义密码中的特殊字符（| 和 &）
                    local escaped_password=$(printf '%s\n' "$ROOT_PASSWORD" | sed 's/[|&]/\\&/g')
                    set +e
                    sed "s|\${BILLIARDS_DB_PASSWORD}|${escaped_password}|g" \
                        "$sql_file" > "$target_file" 2>&1
                    local sed_exit=$?
                    set -e
                    if [[ $sed_exit -ne 0 ]]; then
                        print_error "  sed 替换失败，退出码: $sed_exit"
                        print_error "  源文件: $sql_file"
                        print_error "  目标文件: $target_file"
                        exit 1
                    fi
                    print_info "  sed 替换成功"
                fi
            else
                print_info "  复制文件: $file_name"
                cp "$sql_file" "$target_file" || {
                    print_error "  复制文件失败: $file_name"
                    print_error "  源文件: $sql_file"
                    print_error "  目标文件: $target_file"
                    exit 1
                }
            fi

            ((file_counter++))
            ((total_files++))
        done
    done

    if [[ $total_files -eq 0 ]]; then
        print_warning "没有找到任何SQL文件"
    else
        print_success "SQL脚本准备完成 ($total_files 个文件)"
    fi
}


# 显示数据库连接信息
show_connection_info() {
    print_info "数据库连接信息:"
    echo ""
    echo "========================================"
    echo "🔐 ROOT 用户连接"
    echo "========================================"
    echo "主机: localhost:3306"
    echo "用户: root"
    echo "密码: $MYSQL_ROOT_PASSWORD"
    echo ""
    echo "========================================"
    echo "🏢 应用用户连接"
    echo "========================================"
    echo "主机: localhost:3306"
    echo "用户: billiards_admin"
    echo "密码: $ROOT_PASSWORD"
    echo "数据库: billiards_admin, billiards_saas"
    echo ""
    echo "========================================"
    echo "📝 连接命令示例"
    echo "========================================"
    echo "mysql -h localhost -P 3306 -u billiards_admin -p"
    echo "输入密码: $ROOT_PASSWORD"
    echo ""
}

# 主函数
main() {
    print_info "开始准备数据库初始化..."

    # 检查必要的命令
    command -v openssl >/dev/null 2>&1 || { print_error "需要安装 openssl"; exit 1; }

    # 生成凭据
    generate_db_credentials

    # 准备SQL脚本
    prepare_sql_scripts

    # 显示连接信息
    show_connection_info

    print_success "数据库初始化准备完成！"
    print_warning "请将 temp_sql 目录挂载到 MySQL 容器的 /docker-entrypoint-initdb.d/"
    print_warning "请将 .env.db 中的环境变量加载到 Docker Compose"
}

# 错误处理
trap 'print_error "脚本执行失败，请检查错误信息"; exit 1' ERR

# 执行主函数
main "$@"
