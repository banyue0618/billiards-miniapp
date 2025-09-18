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
    print_info "生成数据库凭据..."
    
    # 生成随机密码
    ROOT_PASSWORD=$(generate_password 20)
    BILLIARDS_DB_PASSWORD=$(generate_password 18)
    
    print_success "数据库凭据生成完成"
    print_info "Root密码: $ROOT_PASSWORD"
    print_info "应用数据库密码: $BILLIARDS_DB_PASSWORD"
    
    # 导出环境变量
    export MYSQL_ROOT_PASSWORD="$ROOT_PASSWORD"
    export BILLIARDS_DB_PASSWORD="$BILLIARDS_DB_PASSWORD"
    
    # 保存到环境变量文件（供Docker Compose使用）
    cat > "$SCRIPT_DIR/../.env.db" << EOF
# 数据库凭据（自动生成）
# 生成时间: $(date)
MYSQL_ROOT_PASSWORD=$ROOT_PASSWORD
BILLIARDS_DB_PASSWORD=$BILLIARDS_DB_PASSWORD
EOF
    
    print_success "数据库凭据已保存到 .env.db"
}

# 准备SQL脚本
prepare_sql_scripts() {
    print_info "准备SQL初始化脚本..."
    
    # 清理并创建临时目录
    rm -rf "$TEMP_SQL_DIR"
    mkdir -p "$TEMP_SQL_DIR"
    
    # 1. 复制并处理01-init-databases.sql（确保最先执行）
    print_info "处理数据库创建脚本..."
    sed "s/\${BILLIARDS_DB_PASSWORD}/$BILLIARDS_DB_PASSWORD/g" \
        "$SQL_DIR/01-init-databases.sql" > "$TEMP_SQL_DIR/01-init-databases.sql"
    
    # 2. 复制管理端数据库脚本（第二执行）
    print_info "准备管理端数据库脚本..."
    cp "$SQL_DIR/ry_vue_5.X.sql" "$TEMP_SQL_DIR/02-admin-schema.sql"
    
    # 3. 复制SaaS平台数据库脚本（第三执行）
    print_info "准备SaaS平台数据库脚本..."
    cp "$SQL_DIR/billiards-saas.sql" "$TEMP_SQL_DIR/03-saas-schema.sql"
    
    # 4. 验证脚本文件
    for script in "$TEMP_SQL_DIR"/*.sql; do
        if [[ -f "$script" ]]; then
            print_success "✓ $(basename "$script") 准备完成"
        else
            print_error "✗ $(basename "$script") 准备失败"
            exit 1
        fi
    done
    
    print_success "所有SQL脚本准备完成"
    print_info "脚本目录: $TEMP_SQL_DIR"
    print_info "执行顺序:"
    print_info "  1. 01-init-databases.sql  (创建数据库和用户)"
    print_info "  2. 02-admin-schema.sql    (管理端表结构和数据)"
    print_info "  3. 03-saas-schema.sql     (SaaS平台表结构和数据)"
}

# 生成应用配置文件模板
generate_app_config() {
    print_info "生成应用配置文件模板..."
    
    cat > "$SCRIPT_DIR/../application-db.yml" << EOF
# 数据库配置（自动生成）
# 生成时间: $(date)

spring:
  datasource:
    # 主数据源（管理端）
    master:
      url: jdbc:mysql://mysql:3306/billiards_admin?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
      username: billiards_admin
      password: ${BILLIARDS_DB_PASSWORD}
      driver-class-name: com.mysql.cj.jdbc.Driver
    
    # 从数据源（SaaS平台）
    slave:
      url: jdbc:mysql://mysql:3306/billiards_saas?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
      username: billiards_admin
      password: ${BILLIARDS_DB_PASSWORD}
      driver-class-name: com.mysql.cj.jdbc.Driver

# Redis配置
redis:
  host: redis
  port: 6379
  password: \${REDIS_PASSWORD:password}
  timeout: 10s
  lettuce:
    pool:
      min-idle: 0
      max-idle: 8
      max-active: 8
      max-wait: -1ms
EOF
    
    print_success "应用配置模板已生成: application-db.yml"
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
    echo "密码: $BILLIARDS_DB_PASSWORD"
    echo "数据库: billiards_admin, billiards_saas"
    echo ""
    echo "========================================"
    echo "📝 连接命令示例"
    echo "========================================"
    echo "mysql -h localhost -P 3306 -u billiards_admin -p"
    echo "输入密码: $BILLIARDS_DB_PASSWORD"
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
    
    # 生成配置文件
    generate_app_config
    
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
