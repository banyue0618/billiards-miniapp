#!/bin/bash

# ==========================================
# 数据库初始化脚本准备工具
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

# 生成随机密码
generate_password() {
    local length=${1:-16}
    # 生成包含大小写字母、数字和特殊字符的密码
    openssl rand -base64 32 | tr -d "=+/" | cut -c1-${length}
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

    # 检查必要的SQL文件是否存在
    local required_files=("$SQL_DIR/01-init-databases.sql" "$SQL_DIR/ry_vue_5.X.sql" "$SQL_DIR/billiards-saas.sql")
    for file in "${required_files[@]}"; do
        if [[ ! -f "$file" ]]; then
            print_error "缺少必要的SQL文件: $file"
            exit 1
        fi
    done

    # 复制并处理SQL脚本（按执行顺序命名）
    print_info "处理数据库创建脚本..."
    sed "s/\${BILLIARDS_DB_PASSWORD}/$ROOT_PASSWORD/g" \
        "$SQL_DIR/01-init-databases.sql" > "$TEMP_SQL_DIR/01-init-databases.sql"

    print_info "复制管理端数据库脚本..."
    cp "$SQL_DIR/ry_vue_5.X.sql" "$TEMP_SQL_DIR/02-admin-schema.sql"

    print_info "复制SaaS平台数据库脚本..."
    cp "$SQL_DIR/billiards-saas.sql" "$TEMP_SQL_DIR/03-saas-schema.sql"

    print_success "SQL脚本准备完成 ($(ls -1 "$TEMP_SQL_DIR"/*.sql | wc -l) 个文件)"
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
