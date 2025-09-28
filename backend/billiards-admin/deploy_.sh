#!/bin/bash

# =========================================
# 台球厅SaaS系统 完整部署脚本
# =========================================
# 集成所有功能的一键部署脚本

set -e  # 遇到错误时退出

clear
echo "==========================================="
echo " 台球厅SaaS系统 完整部署脚本"
echo "==========================================="
echo

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 配置变量
PROJECT_NAME="billiards-saas"
PROJECT_DIR="/opt/${PROJECT_NAME}"
BACKUP_DIR="/opt/backups/${PROJECT_NAME}"

# 打印函数
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗]${NC} $1"
}

print_step() {
    echo -e "${PURPLE}[STEP]${NC} $1"
}

print_banner() {
    echo -e "${CYAN}$1${NC}"
}

# 显示欢迎信息
show_welcome() {
    print_banner "欢迎使用台球厅SaaS系统完整部署脚本！"
    echo
    print_info "此脚本将自动完成以下操作："
    echo "  ✓ 系统环境检查"
    echo "  ✓ Docker环境安装"
    echo "  ✓ 项目代码准备"
    echo "  ✓ 安全配置生成"
    echo "  ✓ 服务容器部署（含Nginx）"
    echo "  ✓ SSL证书配置（可选）"
    echo "  ✓ 网络和监控配置"
    echo
    print_warning "请确保您有sudo权限，部署过程约需10-20分钟"
    echo
}

# 获取用户输入
get_user_inputs() {
    print_step "收集部署配置信息"
    echo

    # 域名配置
    read -p "🌐 请输入域名（直接回车使用默认：banyue.xin）: " DOMAIN_NAME
    DOMAIN_NAME=${DOMAIN_NAME:-banyue.xin}

    # 数据库密码（将在prepare-db-init.sh中自动生成）
    print_info "数据库密码将在初始化时自动生成（20位强密码）"

    # Redis密码（将在初始化时根据容器状态处理）
    print_info "Redis密码将根据容器状态自动处理"

    # 部署模式选择
    echo
    print_warning "部署模式选择："
    echo "1) 简单模式（HTTP）- 应用直接暴露8080端口"
    echo "2) 生产模式（HTTPS）- 包含Nginx反向代理+SSL"
    read -p "请选择 [1-2] (默认: 2): " DEPLOY_MODE
    DEPLOY_MODE=${DEPLOY_MODE:-2}

    # 系统更新选项
    echo
    print_warning "系统组件更新选项："
    echo "1) 跳过系统包更新（推荐，节省时间）"
    echo "2) 更新系统包（较慢但更安全）"
    read -p "请选择 [1-2] (默认: 1): " UPDATE_CHOICE
    UPDATE_CHOICE=${UPDATE_CHOICE:-1}

    # Maven缓存优化选项
    echo
    print_warning "Maven构建优化选项："
    echo "1) 标准构建（每次构建都拉取依赖jar包）"
    echo "2) 缓存优化构建（建立maven缓存卷）"
    read -p "请选择 [1-2] (默认: 2): " MAVEN_CACHE_CHOICE
    MAVEN_CACHE_CHOICE=${MAVEN_CACHE_CHOICE:-2}

    # SSL配置（仅生产模式）
    if [[ "$DEPLOY_MODE" = "2" && "$DOMAIN_NAME" != "localhost" ]]; then
        echo
        read -p "🔒 是否配置SSL证书？[Y/n] (默认: Y): " SETUP_SSL
        SETUP_SSL=${SETUP_SSL:-y}
    else
        SETUP_SSL="n"
    fi

    # Git仓库配置（直接使用默认）
    GIT_REPO_URL="https://gitee.com/banyue0618/billiards-miniapp.git -b CueTime-Miniapp-saas-open-1.0"
    print_info "使用默认Git仓库: $GIT_REPO_URL"

    # 确认信息
    echo
    print_banner "=== 部署配置确认 ==="
    echo "域名: $DOMAIN_NAME"
    echo "MySQL密码: 将自动生成（20位强密码）"
    echo "Redis密码: 将根据容器状态自动处理"
    echo "部署模式: $([ "$DEPLOY_MODE" = "2" ] && echo "生产模式（含Nginx）" || echo "简单模式")"
    echo "系统更新: $([ "$UPDATE_CHOICE" = "2" ] && echo "是" || echo "否")"
    echo "Maven构建: $([ "$MAVEN_CACHE_CHOICE" = "2" ] && echo "缓存优化" || echo "标准构建")"
    echo "SSL证书: $([ "$SETUP_SSL" = "y" ] && echo "是" || echo "否")"
    echo "Git仓库: ${GIT_REPO_URL:-"不使用"}"
    echo "项目目录: $PROJECT_DIR"
    echo

    read -p "确认开始部署？[y/N]: " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_error "部署已取消"
        exit 1
    fi
}

# 检查系统环境
check_system() {
    print_step "检查系统环境"

    # 检查操作系统
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        print_success "操作系统: Linux"
    else
        print_error "不支持的操作系统: $OSTYPE"
        exit 1
    fi

    # 检查权限
    if [[ $EUID -eq 0 ]]; then
        print_warning "正在以root用户运行"
    else
        print_info "当前用户: $USER"
    fi

    # 检查系统资源
    TOTAL_MEM=$(free -m | awk 'NR==2{printf "%.0f", $2}')
    DISK_SPACE=$(df -BG / | awk 'NR==2 {print $4}' | sed 's/G//')

    echo "系统内存: ${TOTAL_MEM}MB"
    echo "可用磁盘: ${DISK_SPACE}GB"

    if [[ $TOTAL_MEM -lt 1024 ]]; then
        print_warning "内存较少，建议至少2GB"
    fi

    if [[ $DISK_SPACE -lt 5 ]]; then
        print_warning "磁盘空间较少，建议至少10GB"
    fi

    print_success "系统环境检查完成"
}

# 更新系统（可选）
update_system() {
    if [[ "$UPDATE_CHOICE" = "2" ]]; then
        print_step "更新系统包"
        sudo apt update
        sudo apt upgrade -y
        sudo apt install -y curl wget git vim unzip openssl
        print_success "系统更新完成"
    else
        print_step "安装必要工具"
         # 必要工具列表
        local tools=("curl" "wget" "git" "vim" "unzip" "openssl")
        for tool in "${tools[@]}"; do
            if ! command -v "$tool" >/dev/null 2>&1; then
                print_info "正在安装 $tool ..."
                sudo apt install -y "$tool" > /dev/null 2>&1
            else
                print_info "$tool 已安装，跳过"
            fi
        done
        print_success "必要工具安装完成"
    fi
}

# 安装Docker
install_docker() {
    print_step "安装Docker环境"

    if command -v docker &> /dev/null; then
        print_success "Docker已安装: $(docker --version | cut -d' ' -f3 | cut -d',' -f1)"
    else
        print_info "正在安装Docker..."
        curl -fsSL https://get.docker.com | sh > /dev/null 2>&1
        sudo systemctl enable docker > /dev/null 2>&1
        sudo systemctl start docker > /dev/null 2>&1
        sudo usermod -aG docker $USER > /dev/null 2>&1
        print_success "Docker安装完成"
    fi

    if command -v docker-compose &> /dev/null; then
        print_success "Docker Compose已安装: $(docker-compose --version | cut -d' ' -f3 | cut -d',' -f1)"
    else
        print_info "正在安装Docker Compose..."
        sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose > /dev/null 2>&1
        sudo chmod +x /usr/local/bin/docker-compose > /dev/null 2>&1
        print_success "Docker Compose安装完成"
    fi
}

# 配置防火墙
configure_firewall() {
    print_step "配置防火墙"

    if command -v ufw &> /dev/null; then
        sudo ufw allow ssh > /dev/null 2>&1
        sudo ufw allow 80/tcp > /dev/null 2>&1
        sudo ufw allow 443/tcp > /dev/null 2>&1
        sudo ufw allow 3306/tcp > /dev/null 2>&1

        if [[ "$DEPLOY_MODE" = "1" ]]; then
            sudo ufw allow 8080/tcp > /dev/null 2>&1
            sudo ufw allow 6379/tcp > /dev/null 2>&1
        else
            # 生产模式：数据库端口仅本地访问
            sudo ufw deny 6379/tcp > /dev/null 2>&1
        fi

        echo "y" | sudo ufw enable > /dev/null 2>&1
        print_success "防火墙配置完成"
    else
        print_warning "未发现ufw，请手动配置防火墙"
    fi
}

# 准备项目目录
setup_directories() {
    print_step "创建项目目录"

    sudo mkdir -p $PROJECT_DIR > /dev/null 2>&1
    sudo mkdir -p $PROJECT_DIR/web > /dev/null 2>&1
    sudo mkdir -p $BACKUP_DIR > /dev/null 2>&1
    sudo mkdir -p /var/log/${PROJECT_NAME} > /dev/null 2>&1

    # 设置权限
    sudo chown -R $USER:$USER $PROJECT_DIR > /dev/null 2>&1

    print_success "项目目录创建完成"
}

# 准备项目代码
prepare_code() {
    print_step "准备项目代码"

    cd $PROJECT_DIR

    if [[ -n "$GIT_REPO_URL" ]]; then
        print_info "从Git仓库获取最新代码..."

        # 检查是否已经是Git仓库
        if [[ -d ".git" ]]; then
            print_info "检测到现有Git仓库，正在更新到最新版本..."

            # 清理可能的本地修改
            git reset --hard HEAD > /dev/null 2>&1
            git clean -fd > /dev/null 2>&1

            # 获取远程信息
            git fetch --all > /dev/null 2>&1

            # 检查并切换到正确分支
            BRANCH_NAME=$(echo "$GIT_REPO_URL" | grep -o '\-b [^ ]*' | sed 's/-b //')
            if [[ -n "$BRANCH_NAME" ]]; then
                print_info "切换到分支: $BRANCH_NAME"
                git checkout $BRANCH_NAME > /dev/null 2>&1 || git checkout -b $BRANCH_NAME origin/$BRANCH_NAME > /dev/null 2>&1
                git pull origin $BRANCH_NAME > /dev/null 2>&1
            else
                print_info "更新主分支..."
                git pull origin main > /dev/null 2>&1 || git pull origin master > /dev/null 2>&1
            fi
        else
            print_info "克隆新的Git仓库..."
            # 清空目录（除了我们创建的脚本）
            find . -maxdepth 1 ! -name "." ! -name "deploy.sh" ! -name "logs" ! -name "uploads" -exec rm -rf {} + 2>/dev/null || true

            # 解析Git URL和分支
            if [[ "$GIT_REPO_URL" == *" -b "* ]]; then
                # 包含分支参数的情况
                git clone $GIT_REPO_URL . > /dev/null 2>&1
            else
                # 不包含分支参数的情况
                git clone $GIT_REPO_URL . > /dev/null 2>&1
            fi
        fi
        print_success "代码获取完成"

        # 验证关键文件
        if [[ ! -f "$PROJECT_DIR/backend/billiards-admin/ruoyi-admin/Dockerfile" ]]; then
            print_error "代码拉取成功，但未找到关键项目文件"
            print_error "请检查Git仓库是否包含完整的项目代码"
            exit 1
        fi

        print_success "项目代码验证通过"
    else
        print_warning "跳过Git代码拉取"
        print_warning "请确保项目代码已存在于 $PROJECT_DIR"
        if [[ ! -f "$PROJECT_DIR/backend/billiards-admin/ruoyi-admin/Dockerfile" ]]; then
            print_error "未找到项目文件，请检查代码是否正确上传"
            print_error "建议重新运行脚本并选择使用Git仓库"
            exit 1
        fi
        print_success "代码检查完成"
    fi
}

# 配置Nginx
configure_nginx() {
    print_step "配置Nginx"

    cd /opt
    mkdir -p nginx/conf/conf.d

    # 生成Nginx主配置
    cat > nginx/conf/nginx.conf << 'EOF'
user root;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
    use epoll;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;

    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;

    # Gzip压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;

    # 安全头
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";

    include /etc/nginx/conf/conf.d/*.conf;
}
EOF

    # 生成站点配置
    if [[ "$SETUP_SSL" = "y" ]]; then
        # HTTPS配置
        cat > nginx/conf/conf.d/billiards.conf << EOF
server {
    listen 80;
    server_name ${DOMAIN_NAME};
    return 301 https://\$server_name\$request_uri;
}

server {
    listen 443 ssl;
    server_name ${DOMAIN_NAME};

    # SSL配置
    ssl_certificate      /etc/nginx/cert/${DOMAIN_NAME}.pem;
	ssl_certificate_key  /etc/nginx/cert/${DOMAIN_NAME}.key;
    ssl_session_timeout 1d;
    ssl_session_cache shared:MozTLS:10m;
    ssl_session_tickets off;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;
    ssl_prefer_server_ciphers off;

    add_header Strict-Transport-Security "max-age=63072000" always;

    client_max_body_size 10M;

    location / {
        root /var/www/web/index/;
        index  index.html;
    }

    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }

    location /uploads/ {
        alias /var/www/uploads/;
        expires 30d;
        add_header Cache-Control "public, no-transform";
    }

    # 后端API代理
    location /api/ {
        proxy_pass http://billiards-admin:8080/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    location /billiards/ {
        # 前端静态资源目录（宿主机: /opt/web 挂载到容器: /var/www/web）
        alias /var/www/web/;
        index  index.html;
        try_files \$uri \$uri/ /index.html;
    }
}
EOF
    else
        # HTTP配置
        cat > nginx/conf/conf.d/billiards.conf << EOF
server {
    listen 80;
    server_name ${DOMAIN_NAME};

    client_max_body_size 100M;

    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }

    location /uploads/ {
        alias /var/www/uploads/;
        expires 30d;
        add_header Cache-Control "public, no-transform";
    }

    # 后端API代理
    location /api/ {
        proxy_pass http://billiards-admin:8080/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    location /billiards/ {
        alias /var/www/web/;
        index  index.html;
        try_files \$uri \$uri/ /billiards/index.html;
    }
}
EOF
    fi

    print_success "Nginx配置完成"
}

# 设置SSL证书
setup_ssl() {
    if [[ "$SETUP_SSL" != "y" || "$DOMAIN_NAME" = "localhost" ]]; then
        return
    fi

    print_step "配置SSL证书"

    # 安装certbot
    if ! command -v certbot &> /dev/null; then
        sudo apt update > /dev/null 2>&1
        sudo apt install -y certbot > /dev/null 2>&1
    fi

    # 获取证书
    sudo certbot certonly --standalone -d $DOMAIN_NAME --non-interactive --agree-tos --email admin@$DOMAIN_NAME

    # 设置自动续期
    echo "0 12 * * * /usr/bin/certbot renew --quiet" | sudo crontab -

    print_success "SSL证书配置完成"
}

# 生成Docker配置
generate_docker_config() {
    print_step "生成Docker配置文件"
    cd $PROJECT_DIR/backend/billiards-admin

    # 如果存在数据库环境变量文件，加载它
    if [[ -f ".env.db" ]]; then
        source .env.db
        print_info "已加载数据库凭据进行配置生成"
        print_info "使用Root密码: ${MYSQL_ROOT_PASSWORD:0:4}***"
        print_info "使用Redis密码: ${REDIS_PASSWORD:0:4}***"
    else
        print_warning "未找到数据库凭据文件，将使用检测到密码"
        # 检测现有容器的密码
        detect_mysql_password
        detect_redis_password
        MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD}"
        REDIS_PASSWORD="${REDIS_PASSWORD}"
    fi

    # 验证关键变量不为空
    if [[ -z "$MYSQL_ROOT_PASSWORD" ]]; then
        print_error "MYSQL_ROOT_PASSWORD 为空，无法生成配置"
        exit 1
    fi

    if [[ -z "$REDIS_PASSWORD" ]]; then
        print_error "REDIS_PASSWORD 为空，无法生成配置"
        exit 1
    fi

    # 根据部署模式生成不同配置
    if [[ "$DEPLOY_MODE" = "2" ]]; then
        # 生产模式：包含Nginx
        CONFIG_FILE="docker-compose.prod.yml"
        APP_PORTS="- \"127.0.0.1:8080:8080\"  # 仅本地访问，通过Nginx代理"
        MYSQL_PORTS="- \"3306:3306\""
        REDIS_PORTS="- \"127.0.0.1:6379:6379\"  # 仅本地访问"
        NGINX_SERVICE="
  # Nginx反向代理
  nginx:
    image: nginx:alpine
    container_name: ${PROJECT_NAME}-nginx
    restart: always
    ports:
      - \"80:80\"
      - \"443:443\"
    volumes:
      - /opt/nginx/conf/nginx.conf:/etc/nginx/nginx.conf:ro
      - /opt/nginx/conf:/etc/nginx/conf:ro
      - /opt/uploads:/var/www/uploads:ro
      - /opt/web:/var/www/web:ro
      - /opt/nginx/cert:/etc/nginx/cert:ro
    depends_on:
      - billiards-admin
    networks:
      - billiards-network
    healthcheck:
      test: [\"CMD\", \"curl\", \"-f\", \"-H\", \"Host: ${DOMAIN_NAME}\", \"http://localhost/health\"]
      interval: 30s
      timeout: 10s
      retries: 3"
        UPLOAD_PREFIX="https://${DOMAIN_NAME}/uploads/files"
    else
        # 简单模式：直接暴露端口
        CONFIG_FILE="docker-compose.simple.yml"
        APP_PORTS="- \"8080:8080\""
        MYSQL_PORTS="- \"3306:3306\""
        REDIS_PORTS="- \"6379:6379\""
        NGINX_SERVICE=""
        UPLOAD_PREFIX="http://${DOMAIN_NAME}:8080/uploads/files"
    fi

    # 选择初始化SQL目录（优先使用已替换好凭据的有序目录）
    local INIT_SQL_DIR="./script/sql/"
    if [[ -d "script/temp_sql" ]]; then
        INIT_SQL_DIR="./script/temp_sql/"
    fi

    # 生成配置文件
    cat > $CONFIG_FILE << EOF
version: '3.8'

services:
  # 后端应用服务
  billiards-admin:
    build:
      context: .
      dockerfile: ruoyi-admin/Dockerfile
    container_name: ${PROJECT_NAME}-admin
    restart: always
    ports:
      $APP_PORTS
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SERVER_PORT=8080
      - SPRING_REDIS_HOST=redis
      - SPRING_REDIS_PASSWORD=${REDIS_PASSWORD}
      - SPRING_DATASOURCE_DYNAMIC_DATASOURCE_ADMIN_USERNAME=billiards_admin
      - SPRING_DATASOURCE_DYNAMIC_DATASOURCE_ADMIN_PASSWORD=${MYSQL_ROOT_PASSWORD}
      - SPRING_DATASOURCE_DYNAMIC_DATASOURCE_PLATFORM_USERNAME=billiards_admin
      - SPRING_DATASOURCE_DYNAMIC_DATASOURCE_PLATFORM_PASSWORD=${MYSQL_ROOT_PASSWORD}
      - RESOURCE_STORAGE_LOCAL_BASE_URL=${UPLOAD_PREFIX}
      - SSE_ENABLED=false
      - BILLIARDS_WECHAT_APPID=ownerappid
      - BILLIARDS_WECHAT_SECRET=ownerappidsecret
      - JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom
    volumes:
      - /opt/logs:/app/logs
      - /opt/uploads:/app/uploads
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - billiards-network
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:8080/actuator/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 90s

  # MySQL数据库
  mysql:
    image: mysql:5.7
    container_name: ${PROJECT_NAME}-mysql
    restart: always
    ports:
      $MYSQL_PORTS
    environment:
      - MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
      - MYSQL_DATABASE=billiards_admin
      - MYSQL_USER=billiards_admin
      - MYSQL_PASSWORD=${MYSQL_ROOT_PASSWORD}
      - MYSQL_ROOT_HOST=%
      - TZ=Asia/Shanghai
    command:
      - --default-authentication-plugin=mysql_native_password
    volumes:
      - mysql-data:/var/lib/mysql
      - ${INIT_SQL_DIR}:/docker-entrypoint-initdb.d/
      - ./mysql-config/:/etc/mysql/conf.d/
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-p${MYSQL_ROOT_PASSWORD}"]
      interval: 10s
      timeout: 5s
      retries: 10
      start_period: 30s
    networks:
      - billiards-network

  # Redis缓存
  redis:
    image: redis:5.0.14-alpine
    container_name: ${PROJECT_NAME}-redis
    restart: always
    ports:
      $REDIS_PORTS
    command: redis-server --requirepass ${REDIS_PASSWORD} --appendonly yes
    volumes:
      - redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "-a", "${REDIS_PASSWORD}", "ping"]
      interval: 10s
      timeout: 3s
      retries: 5
      start_period: 10s
    networks:
      - billiards-network
${NGINX_SERVICE}

networks:
  billiards-network:
    driver: bridge

volumes:
  mysql-data:
    name: ${PROJECT_NAME}-mysql-data
  redis-data:
EOF

    # 生成 .env 文件确保Docker Compose使用正确的环境变量
    cat > .env << EOF
MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
DB_PASSWORD=${MYSQL_ROOT_PASSWORD}
REDIS_PASSWORD=${REDIS_PASSWORD}
PROJECT_NAME=${PROJECT_NAME}
DOMAIN_NAME=${DOMAIN_NAME}
COMPOSE_PROJECT_NAME=${PROJECT_NAME}
EOF

    # 更新应用配置文件中的密码
    if [[ -f "ruoyi-admin/src/main/resources/application-prod.yml" ]]; then
        sed -i "s/password: password/password: ${MYSQL_ROOT_PASSWORD}/g" ruoyi-admin/src/main/resources/application-prod.yml > /dev/null 2>&1
    fi

    print_success "Docker配置生成完成：$CONFIG_FILE"
    print_info "环境变量已写入：.env"

    # 调试：验证配置文件中的关键变量
    print_info "=== 配置验证 ==="
    print_info "配置文件中的MySQL Root密码长度: $(grep -o 'MYSQL_ROOT_PASSWORD=' $CONFIG_FILE | wc -l) 处"
    print_info "配置文件中的Redis密码长度: $(grep -o 'REDIS_PASSWORD=' $CONFIG_FILE | wc -l) 处"

    # 验证.env文件内容
    if [[ -f ".env" ]]; then
        print_info ".env文件内容验证:"
        while IFS= read -r line; do
            if [[ $line == MYSQL_ROOT_PASSWORD=* ]]; then
                pwd_value="${line#*=}"
                if [[ -n "$pwd_value" ]]; then
                    print_success "✓ MySQL密码已设置 (长度: ${#pwd_value})"
                else
                    print_error "✗ MySQL密码为空！"
                fi
            elif [[ $line == REDIS_PASSWORD=* ]]; then
                pwd_value="${line#*=}"
                if [[ -n "$pwd_value" ]]; then
                    print_success "✓ Redis密码已设置 (长度: ${#pwd_value})"
                else
                    print_error "✗ Redis密码为空！"
                fi
            fi
        done < .env
    else
        print_error ".env 文件不存在！"
    fi
}

# Maven缓存优化构建
build_with_maven_cache() {
    print_step "Maven缓存优化构建"

    cd $PROJECT_DIR/backend/billiards-admin

    # Maven缓存卷名称
    local MAVEN_CACHE_VOLUME="billiards-maven-cache"
    local start_time=$(date +%s)

    print_info "使用Maven缓存优化构建，首次构建会慢，后续会很快..."

    # 检查并创建Maven缓存卷
    if ! docker volume inspect $MAVEN_CACHE_VOLUME >/dev/null 2>&1; then
        print_info "创建Maven缓存卷..."
        docker volume create $MAVEN_CACHE_VOLUME > /dev/null 2>&1
    else
        print_info "Maven缓存卷已存在，将重用依赖..."
        local volume_size=$(docker run --rm -v $MAVEN_CACHE_VOLUME:/cache alpine du -sh /cache/repository 2>/dev/null | cut -f1 || echo "未知")
        print_info "缓存大小: $volume_size"
    fi

    # 使用临时容器进行Maven构建
    print_info "正在进行Maven构建（使用缓存卷）..."
    docker run --rm \
        -v "$PROJECT_DIR/backend/billiards-admin:/workspace" \
        -v "$MAVEN_CACHE_VOLUME:/root/.m2" \
        -w /workspace \
        maven:3.8-openjdk-17-slim \
        bash -c "
            echo '====== Maven缓存优化构建 ======';
            echo '工作目录: \$(pwd)';
            echo '检查现有缓存...';
            ls -la /root/.m2/repository 2>/dev/null | head -3 || echo 'Maven仓库为空，首次构建';
            echo '';

            # 配置阿里云镜像加速
            mkdir -p /root/.m2;
            cat > /root/.m2/settings.xml << 'EOF'
<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <name>aliyun Maven</name>
      <mirrorOf>central</mirrorOf>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
EOF

            echo '🔧 修复死循环问题：避免重复clean操作';
            echo '步骤1: 下载外部依赖...';
            mvn dependency:go-offline -B -q || echo '部分依赖下载失败，继续构建';

            echo '步骤2: 一次性清理，避免重复clean死循环...';
            mvn clean -q;

            echo '步骤3: 分层构建模块（不重复clean）...';
            echo '  -> 构建BOM模块（依赖管理）...';
            mvn install -pl ruoyi-common/ruoyi-common-bom -DskipTests -q;

            echo '  -> 构建core模块（基础模块）...';
            mvn install -pl ruoyi-common/ruoyi-common-core -DskipTests -q;

            echo '  -> 验证core模块安装...';
            if [[ -f \"/root/.m2/repository/org/dromara/ruoyi-common-core/5.3.1/ruoyi-common-core-5.3.1.jar\" ]]; then
                echo '    ✓ ruoyi-common-core 模块已正确安装';
            else
                echo '    ⚠️ core模块jar未找到，但继续构建（可能是版本号问题）';
            fi

            echo '  -> 构建entire common模块...';
            mvn install -pl ruoyi-common -am -DskipTests -q;

            echo '步骤4: 打包整个项目（不清理）...';
            mvn package -DskipTests -q;

            echo '';
            echo '====== 构建统计 ======';
            echo \"Maven仓库大小: \$(du -sh /root/.m2/repository 2>/dev/null | cut -f1 || echo '未知')\";
            echo \"依赖jar数量: \$(find /root/.m2/repository -name '*.jar' 2>/dev/null | wc -l)\";
            echo '====== 构建完成 ======';
        "

    local end_time=$(date +%s)
    local duration=$((end_time - start_time))
    local minutes=$((duration / 60))
    local seconds=$((duration % 60))

    print_success "Maven构建完成，耗时: ${minutes}分${seconds}秒"
    print_info "jar包位置: ruoyi-admin/target/billiards-admin.jar"

    # 创建运行时镜像
    print_info "创建Docker运行时镜像..."
    cat > Dockerfile.runtime << 'EOF'
FROM openjdk:17-jdk-slim

WORKDIR /app

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 复制jar包
COPY ruoyi-admin/target/billiards-admin.jar /app/app.jar

# 声明数据卷
VOLUME ["/app/logs", "/app/uploads"]

# 暴露端口
EXPOSE $SERVER_PORT

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD netstat -an | grep ${SERVER_PORT} | grep LISTEN > /dev/null || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE} \
    --resource.storage.local.base-url=${RESOURCE_STORAGE_LOCAL_BASE_URL} \
    --sse.enabled=${SSE_ENABLED}" \
    --billiards.wechat.appid=${BILLIARDS_WECHAT_APPID} \
    --billiards.wechat.secret=${BILLIARDS_WECHAT_SECRET}]
EOF

    # 构建运行时镜像
    docker build -f Dockerfile.runtime -t billiards-admin:latest . > /dev/null 2>&1

    # 清理临时文件
    rm -f Dockerfile.runtime

    print_success "Docker镜像构建完成"
    print_info "💡 下次构建将重用Maven缓存，速度会更快！"
}

# 检测现有MySQL容器的密码
detect_mysql_password() {
    local mysql_container_name="${PROJECT_NAME}-mysql"

    # 检查MySQL容器是否存在
    if docker ps -a --filter "name=${mysql_container_name}" --format "{{.Names}}" | grep -q "${mysql_container_name}"; then
        print_info "检测到现有MySQL容器，尝试获取密码..."

        # 优先从.env.db文件中获取密码（首次部署已生成）
        if [[ -f ".env.db" ]] && grep -q "MYSQL_ROOT_PASSWORD=" .env.db; then
            local saved_password=$(grep "MYSQL_ROOT_PASSWORD=" .env.db | cut -d'=' -f2-)
            if [[ -n "$saved_password" ]]; then
                # 验证获取到的密码
                print_info "尝试使用.env.db中的密码验证..."
                if docker exec ${mysql_container_name} mysql -h 127.0.0.1 -u root -p"$saved_password" -e "SELECT 1;" >/dev/null 2>&1; then
                    MYSQL_ROOT_PASSWORD="$saved_password"
                    print_success "从凭据文件中恢复MySQL密码: ${MYSQL_ROOT_PASSWORD:0:4}***"
                    return 0
                else
                    print_error "从.env.db获取的密码验证失败"
                fi
            fi
        fi

        # 次选：从当前目录 .env 中读取（由generate_docker_config写入）
        if [[ -f ".env" ]] && grep -q "MYSQL_ROOT_PASSWORD=" .env; then
            local env_password=$(grep "MYSQL_ROOT_PASSWORD=" .env | cut -d'=' -f2-)
            if [[ -n "$env_password" ]]; then
                # 验证获取到的密码
                print_info "尝试使用.env中的密码验证..."
                if docker exec ${mysql_container_name} mysql -h 127.0.0.1 -u root -p"$env_password" -e "SELECT 1;" >/dev/null 2>&1; then
                    MYSQL_ROOT_PASSWORD="$env_password"
                    print_success "从.env恢复MySQL密码: ${MYSQL_ROOT_PASSWORD:0:4}***"
                    return 0
                else
                    print_error "从.env获取的密码验证失败"
                fi
            fi
        fi

        # 尝试从容器环境变量中获取
        local container_env=$(docker inspect ${mysql_container_name} --format='{{range .Config.Env}}{{println .}}{{end}}' 2>/dev/null | grep "^MYSQL_ROOT_PASSWORD=")
        if [[ -n "$container_env" ]]; then
            local extracted_password=$(echo "$container_env" | cut -d'=' -f2-)
            if [[ -n "$extracted_password" ]] && [[ "$extracted_password" != "password" ]]; then
                # 验证密码是否有效
                if docker exec ${mysql_container_name} mysql -h 127.0.0.1 -u root -p"${extracted_password}" -e "SELECT 1;" >/dev/null 2>&1; then
                    MYSQL_ROOT_PASSWORD="$extracted_password"
                    print_success "从容器环境变量中检测到MySQL密码: ${MYSQL_ROOT_PASSWORD:0:4}***"
                    return 0
                fi
            fi
        fi

        print_warning "无法检测现有MySQL容器密码"
        print_warning "请选择以下处理方式："
        echo "1) 手动输入现有MySQL root密码（推荐）"
        echo "2) 重建MySQL容器（会丢失现有数据！）"
        echo
        read -p "请选择 [1-2] (默认: 1): " password_choice
        password_choice=${password_choice:-1}

        case $password_choice in
            1)
                read -p "请输入现有MySQL root密码: " user_mysql_password
                if [[ -n "$user_mysql_password" ]]; then
                    # 验证用户输入的密码
                    if docker exec ${mysql_container_name} mysql -u root -p${user_mysql_password} -e "SELECT 1;" >/dev/null 2>&1; then
                        MYSQL_ROOT_PASSWORD="$user_mysql_password"
                        print_success "用户输入的密码验证成功"
                        # 保存到凭据文件
                        echo "MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD" > .env.db
                        print_info "密码已保存到凭据文件"
                        return 0
                    else
                        print_error "用户输入的密码验证失败"
                        return 1
                    fi
                else
                    print_error "密码不能为空"
                    return 1
                fi
                ;;
            2)
                print_warning "将重建MySQL容器，现有数据将丢失！"
                read -p "确认重建容器？[y/N]: " confirm_rebuild
                if [[ "$confirm_rebuild" =~ ^[Yy]$ ]]; then
                    print_info "停止并删除现有MySQL容器..."
                    docker stop ${mysql_container_name} >/dev/null 2>&1 || true
                    docker rm ${mysql_container_name} >/dev/null 2>&1 || true

                    # 删除数据卷
                    docker volume rm ${PROJECT_NAME}-mysql-data >/dev/null 2>&1 || true

                    print_success "现有MySQL容器已删除，将创建新容器"
                    return 1  # 返回需要初始化的状态
                else
                    print_info "取消重建，使用默认密码"
                    MYSQL_ROOT_PASSWORD="password"
                    return 0
                fi
                ;;
        esac
    else
        print_info "未检测到现有MySQL容器，将生成新密码"
        prepare_database_initialization
    fi
    return 0
}

# 检查数据库是否已初始化
check_database_initialized() {
    # 检查认证并判断是否存在任一业务数据库
    print_info "检查业务数据库是否存在..."
    local check_user="root"

    # 先尝试root
    if ! docker exec ${PROJECT_NAME}-mysql mysql -u root -p"${MYSQL_ROOT_PASSWORD}" -e "SELECT 1;" >/dev/null 2>&1; then
        # root失败则尝试应用用户
        local app_pwd=""
        if [[ -f ".env.db" ]] && grep -q "BILLIARDS_DB_PASSWORD=" .env.db; then
            app_pwd=$(grep "BILLIARDS_DB_PASSWORD=" .env.db | cut -d'=' -f2-)
        fi
        if [[ -n "$app_pwd" ]] && docker exec ${PROJECT_NAME}-mysql mysql -u billiards_admin -p"${app_pwd}" -e "SELECT 1;" >/dev/null 2>&1; then
            check_user="billiards_admin"
            mysql_password="$app_pwd"
            print_info "使用应用用户验证数据库存在性"
        else
            print_warning "root与应用用户均无法认证，可能密码不匹配或未就绪"
            return 1
        fi
    fi

    # 使用 information_schema 查询是否存在任一业务库（用已验证通过的账户）
    local check_sql="SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name IN ('billiards_saas','billiards_admin');"
    local db_count=$(docker exec ${PROJECT_NAME}-mysql sh -c "mysql -N -s -u ${check_user} -p\"${mysql_password}\" -e \"${check_sql}\"" 2>/dev/null || echo "")

    if [[ "${db_count}" =~ ^[0-9]+$ ]] && (( db_count > 0 )); then
        print_success "检测到业务数据库已存在，跳过初始化"
        return 0
    else
        print_info "未检测到业务数据库，需要初始化"
        return 0
    fi
}

# 准备数据库初始化
prepare_database_initialization() {
    print_step "准备数据库初始化"

    cd $PROJECT_DIR/backend/billiards-admin

    # 检查是否已有数据库凭据文件
    if [[ -f ".env.db" ]]; then
        print_info "发现已存在的数据库凭据文件"
        source .env.db
        print_success "数据库凭据已加载"

        # 即使凭据存在，也要检查SQL脚本是否准备好
        if [[ ! -d "script/temp_sql" ]]; then
            print_info "SQL脚本未准备，重新生成..."
            if [[ -f "script/prepare-db-init.sh" ]]; then
                chmod +x script/prepare-db-init.sh
                ./script/prepare-db-init.sh
                print_success "SQL脚本重新生成完成"

                # 重新加载更新后的环境变量
                if [[ -f ".env.db" ]]; then
                    source .env.db
                    print_info "重新加载更新后的数据库凭据"
                fi
            fi
        else
            print_info "SQL脚本已准备完成"
        fi
    else
        # 生成数据库凭据
        print_info "首次部署，生成数据库凭据..."
        if [[ -f "script/prepare-db-init.sh" ]]; then
            chmod +x script/prepare-db-init.sh
            ./script/prepare-db-init.sh
            print_success "数据库凭据生成完成"

            if [[ -f ".env.db" ]]; then
                source .env.db
                local masked_root_pwd=$(echo $MYSQL_ROOT_PASSWORD | sed 's/\(.\{4\}\).*/\1***/')
                local masked_app_pwd=$(echo $MYSQL_ROOT_PASSWORD | sed 's/\(.\{4\}\).*/\1***/')
                print_info "Root密码: $masked_root_pwd"
                print_info "应用密码: $masked_app_pwd"
            fi
        else
            print_warning "数据库准备脚本不存在，将使用默认配置"
        fi
    fi
}

# 检测现有Redis容器的密码
detect_redis_password() {
    local redis_container_name="${PROJECT_NAME}-redis"

    # 检查Redis容器是否存在
    if docker ps -a --filter "name=${redis_container_name}" --format "{{.Names}}" | grep -q "${redis_container_name}"; then
        print_info "检测到现有Redis容器，尝试获取密码..."

        # 检查容器是否在运行
        if docker ps --filter "name=${redis_container_name}" --format "{{.Names}}" | grep -q "${redis_container_name}"; then
            # 尝试从容器的环境变量或启动命令中获取密码
            local redis_command=$(docker inspect ${redis_container_name} --format='{{.Config.Cmd}}' 2>/dev/null)
            if [[ $redis_command == *"--requirepass"* ]]; then
                # 提取密码
                local extracted_password=$(echo "$redis_command" | grep -o 'requirepass [^ ]*' | cut -d' ' -f2)
                if [[ -n "$extracted_password" ]]; then
                    REDIS_PASSWORD="$extracted_password"
                    print_success "从现有Redis容器中检测到密码: ${REDIS_PASSWORD:0:4}***"
                    return 0
                fi
            fi
        fi

        # 如果无法检测密码，检查是否有保存的凭据文件
        if [[ -f ".env.db" ]] && grep -q "REDIS_PASSWORD=" .env.db; then
            local saved_password=$(grep "REDIS_PASSWORD=" .env.db | cut -d'=' -f2)
            if [[ -n "$saved_password" ]]; then
                REDIS_PASSWORD="$saved_password"
                print_success "从凭据文件中恢复Redis密码: ${REDIS_PASSWORD:0:4}***"
                return 0
            fi
        fi

        print_warning "无法检测现有Redis容器密码，将使用默认密码"
        REDIS_PASSWORD="password"
        return 1
    else
        print_info "未检测到现有Redis容器，将生成新密码"
        print_info "生成的密码: ${REDIS_PASSWORD}"
    fi
    return 0
}

# 检查并启动组件容器（MySQL、Redis、Nginx）
ensure_component_containers() {
    print_step "检查并启动组件容器"

    cd $PROJECT_DIR/backend/billiards-admin

    # 确定配置文件
    if [[ "$DEPLOY_MODE" = "2" ]]; then
        COMPOSE_FILE="docker-compose.prod.yml"
    else
        COMPOSE_FILE="docker-compose.simple.yml"
    fi
    print_info "使用的Docker Compose文件: $COMPOSE_FILE"
    # 准备环境变量（包括Redis和MySQL密码）
    prepare_environment_variables

    # 检查MySQL容器
    if docker ps -a --filter "name=${PROJECT_NAME}-mysql" --format "{{.Names}}" | grep -q "${PROJECT_NAME}-mysql"; then
        print_info "MySQL容器已存在"
        if ! docker ps --filter "name=${PROJECT_NAME}-mysql" --format "{{.Names}}" | grep -q "${PROJECT_NAME}-mysql"; then
            print_info "启动现有MySQL容器..."
            docker start ${PROJECT_NAME}-mysql
        fi
    else
        print_info "MySQL容器不存在，将创建新容器"

        # 删除可能存在的旧数据卷以确保干净的初始化
        docker volume rm ${PROJECT_NAME}-mysql-data >/dev/null 2>&1 || true

        docker-compose -f $COMPOSE_FILE up -d mysql
        wait_for_mysql_initialization
    fi

    # 检查Redis容器
    if docker ps -a --filter "name=${PROJECT_NAME}-redis" --format "{{.Names}}" | grep -q "${PROJECT_NAME}-redis"; then
        print_info "Redis容器已存在"
        if ! docker ps --filter "name=${PROJECT_NAME}-redis" --format "{{.Names}}" | grep -q "${PROJECT_NAME}-redis"; then
            print_info "启动现有Redis容器..."
            docker start ${PROJECT_NAME}-redis
        fi
    else
        print_info "创建Redis容器..."
        docker-compose -f $COMPOSE_FILE up -d redis
    fi

    # 检查Nginx容器（生产模式）
    if [[ "$DEPLOY_MODE" = "2" ]]; then
        if docker ps -a --filter "name=${PROJECT_NAME}-nginx" --format "{{.Names}}" | grep -q "${PROJECT_NAME}-nginx"; then
            print_info "Nginx容器已存在"
            if ! docker ps --filter "name=${PROJECT_NAME}-nginx" --format "{{.Names}}" | grep -q "${PROJECT_NAME}-nginx"; then
                print_info "启动现有Nginx容器..."
                docker start ${PROJECT_NAME}-nginx
            fi
        else
            print_info "创建Nginx容器..."
            docker-compose -f $COMPOSE_FILE up -d nginx
        fi
    fi

    print_success "组件容器检查完成"
}

# 准备环境变量
prepare_environment_variables() {
    print_info "准备环境变量..."

    # 如果存在数据库环境变量文件，加载它
    if [[ -f ".env.db" ]]; then
        source .env.db
        print_info "已加载现有数据库凭据"

        # 更新凭据文件，确保检测到的密码被保存
        local env_updated=false

        # 如果检测到MySQL密码但文件中没有，更新文件
        if [[ -n "$MYSQL_ROOT_PASSWORD" ]] && ! grep -q "MYSQL_ROOT_PASSWORD=" .env.db; then
            echo "MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD" >> .env.db
            print_info "已将MySQL密码保存到凭据文件"
            env_updated=true
        fi

        # 如果检测到Redis密码但文件中没有，更新文件
        if [[ -n "$REDIS_PASSWORD" ]] && ! grep -q "REDIS_PASSWORD=" .env.db; then
            echo "REDIS_PASSWORD=$REDIS_PASSWORD" >> .env.db
            print_info "已将Redis密码保存到凭据文件"
            env_updated=true
        fi

        # 重新加载更新后的文件
        if [[ "$env_updated" = true ]]; then
            source .env.db
        fi
    else
        # 如果没有凭据文件，使用检测到的密码或默认值
        MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD}"
        REDIS_PASSWORD="${REDIS_PASSWORD}"

        # 创建新的凭据文件
        cat > .env.db << EOF
# 数据库和缓存凭据（检测或生成）
# 生成时间: $(date)
MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD
REDIS_PASSWORD=$REDIS_PASSWORD
EOF
        print_info "已创建新的凭据文件"
    fi

    # 导出环境变量
    export MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD}"
    export DB_PASSWORD="$MYSQL_ROOT_PASSWORD"
    export REDIS_PASSWORD="$REDIS_PASSWORD"

    print_info "环境变量已准备完成"
    print_info "MySQL Root密码: ${MYSQL_ROOT_PASSWORD:0:4}***"
    print_info "Redis密码: ${REDIS_PASSWORD:0:4}***"
}

# 等待MySQL初始化完成
wait_for_mysql_initialization() {
    print_info "等待MySQL初始化完成..."

    local max_wait=180  # 3分钟
    local wait_time=0

    while [[ $wait_time -lt $max_wait ]]; do
        # 检查容器是否在运行
        if ! docker ps --filter "name=${PROJECT_NAME}-mysql" --format "{{.Names}}" | grep -q "${PROJECT_NAME}-mysql"; then
            print_error "MySQL容器意外停止"
            docker logs ${PROJECT_NAME}-mysql --tail 30
            return 1
        fi

        # 检查MySQL是否准备好接受连接
        if docker exec ${PROJECT_NAME}-mysql mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" -e "SELECT 1;" >/dev/null 2>&1; then
            # 额外等待10秒确保初始化脚本执行完成
            print_info "MySQL已就绪，等待初始化脚本完成..."
            sleep 10

            # 验证数据库是否初始化成功
            if check_database_initialized; then
                print_success "MySQL初始化完成"
                return 0
            fi
        fi

        sleep 10
        wait_time=$((wait_time + 10))
        print_info "初始化进行中... (${wait_time}s/${max_wait}s)"
    done

    print_warning "MySQL初始化超时，继续部署但可能需要手动检查"
    return 0
}

# 部署应用服务
deploy_application() {
    print_step "部署应用服务"

    cd $PROJECT_DIR/backend/billiards-admin

    # 确定配置文件
    if [[ "$DEPLOY_MODE" = "2" ]]; then
        COMPOSE_FILE="docker-compose.prod.yml"
    else
        COMPOSE_FILE="docker-compose.simple.yml"
    fi

    # 确保组件容器正常运行
    ensure_component_containers

    # 停止并移除现有的应用容器（强制更新）
    print_info "更新应用容器..."
    docker-compose -f $COMPOSE_FILE stop billiards-admin >/dev/null 2>&1 || true
    docker-compose -f $COMPOSE_FILE rm -f billiards-admin >/dev/null 2>&1 || true

    # 使用Maven缓存优化构建
    print_info "Docker构建应用容器..."
    build_with_maven_cache
    # 启动应用服务（使用已构建的镜像）
    docker-compose -f $COMPOSE_FILE up -d billiards-admin

    print_info "构建应用容器完成"

    # 等待应用启动
    wait_for_application_startup

    print_success "应用部署完成"
}

# 等待应用启动
wait_for_application_startup() {
    print_info "等待应用启动..."

    local max_attempts=24  # 2分钟（5秒 × 24）
    local attempt=1

    while [[ $attempt -le $max_attempts ]]; do
        if curl -f -u ruoyi:123456 http://localhost:8080/actuator/health >/dev/null 2>&1; then
            print_success "应用启动成功！"
            return 0
        fi

        print_info "等待应用启动... ($attempt/$max_attempts)"
        sleep 5
        ((attempt++))
    done

    print_warning "应用启动超时，请检查日志"
    docker logs ${PROJECT_NAME}-admin --tail 20
    return 0
}

# 设置监控
setup_monitoring() {
    print_step "配置系统监控"

    # 确定配置文件
    if [[ "$DEPLOY_MODE" = "2" ]]; then
        COMPOSE_FILE="docker-compose.prod.yml"
    else
        COMPOSE_FILE="docker-compose.simple.yml"
    fi

    # 创建监控脚本
    cat > ${PROJECT_DIR}/monitor.sh << EOF
#!/bin/bash
# 系统监控脚本

PROJECT_DIR="/opt/billiards-saas/backend/billiards-admin"
LOG_FILE="/var/log/billiards-saas/monitor.log"
COMPOSE_FILE="$COMPOSE_FILE"

# 检查容器状态
check_containers() {
    cd \$PROJECT_DIR
    if ! docker-compose -f \$COMPOSE_FILE ps | grep -q "Up"; then
        echo "\$(date): 检测到容器异常，正在重启..." >> \$LOG_FILE
        docker-compose -f \$COMPOSE_FILE up -d
    fi
}

# 检查磁盘空间
check_disk_space() {
    DISK_USAGE=\$(df / | awk 'NR==2 {print \$5}' | sed 's/%//')
    if [[ \$DISK_USAGE -gt 85 ]]; then
        echo "\$(date): 磁盘空间不足，使用率：\${DISK_USAGE}%" >> \$LOG_FILE
    fi
}

check_containers
check_disk_space
EOF

    chmod +x ${PROJECT_DIR}/monitor.sh

    # 添加到crontab（每5分钟检查一次）
    (crontab -l 2>/dev/null; echo "*/5 * * * * ${PROJECT_DIR}/monitor.sh") | crontab - > /dev/null 2>&1

    print_success "监控配置完成"
}

# 显示部署结果
show_deployment_result() {
    echo
    print_banner "🎉 部署完成！"
    echo

    # 确定配置文件和访问地址
    if [[ "$DEPLOY_MODE" = "2" ]]; then
        COMPOSE_FILE="docker-compose.prod.yml"
        if [[ "$SETUP_SSL" = "y" ]]; then
            ACCESS_URL="https://${DOMAIN_NAME}"
        else
            ACCESS_URL="http://${DOMAIN_NAME}"
        fi
    else
        COMPOSE_FILE="docker-compose.simple.yml"
        ACCESS_URL="http://${DOMAIN_NAME}:8080"
    fi

    # 检查服务状态
    cd $PROJECT_DIR/backend/billiards-admin
    echo "=== 服务状态 ==="
    docker-compose -f $COMPOSE_FILE ps
    echo

    print_success "=== 访问信息 ==="
    echo "🌐 网站地址: $ACCESS_URL"
    echo "🏠 管理后台: $ACCESS_URL/admin"
    echo "💾 健康检查: $ACCESS_URL/health"
    echo

    print_success "=== 数据库信息 ==="

    # 显示数据库连接信息
    if [[ -f ".env.db" ]]; then
        source .env.db
        echo "👤 Root用户: root"
        echo "🔐 Root密码: $MYSQL_ROOT_PASSWORD"
        echo "👤 应用用户: billiards_admin"
        echo "🔑 应用密码: $MYSQL_ROOT_PASSWORD"
        echo "📊 数据库: billiards_admin, billiards_saas"
        echo ""
        echo "📝 连接命令:"
        echo "   mysql -h localhost -P 3306 -u billiards_admin -p"
        echo "   输入密码: $MYSQL_ROOT_PASSWORD"
    else
        echo "🔐 MySQL密码: ${MYSQL_ROOT_PASSWORD:-password}"
        echo "📊 数据库: billiards_admin, billiards_saas"
    fi

    echo "🔑 Redis密码: ${REDIS_PASSWORD}"
    if [[ "$DEPLOY_MODE" = "1" ]]; then
        echo "📊 MySQL端口: 3306"
        echo "📡 Redis端口: 6379"
    else
        echo "📊 MySQL端口: 仅本地访问"
        echo "📡 Redis端口: 仅本地访问"
    fi
    echo

    print_success "=== 重要目录 ==="
    echo "📁 项目目录: ${PROJECT_DIR}"
    echo "💾 备份目录: ${BACKUP_DIR}"
    echo "📝 日志目录: /var/log/${PROJECT_NAME}"
    echo

    print_success "=== 常用命令 ==="
    echo "查看状态: cd ${PROJECT_DIR}/backend/billiards-admin && docker-compose -f $COMPOSE_FILE ps"
    echo "查看日志: cd ${PROJECT_DIR}/backend/billiards-admin && docker-compose -f $COMPOSE_FILE logs -f"
    echo "重启服务: cd ${PROJECT_DIR}/backend/billiards-admin && docker-compose -f $COMPOSE_FILE restart"
    echo "停止服务: cd ${PROJECT_DIR}/backend/billiards-admin && docker-compose -f $COMPOSE_FILE down"
    if [[ "$MAVEN_CACHE_CHOICE" = "2" ]]; then
        echo "查看Maven缓存: docker volume ls | grep maven"
        echo "清理Maven缓存: docker volume rm billiards-maven-cache"
    fi
    echo

    # 保存部署信息
    cat > ${PROJECT_DIR}/deployment-info.txt << EOF
台球厅SaaS系统部署信息
部署时间: $(date)
部署模式: $([ "$DEPLOY_MODE" = "2" ] && echo "生产模式" || echo "简单模式")
Maven构建: $([ "$MAVEN_CACHE_CHOICE" = "2" ] && echo "缓存优化" || echo "标准构建")
域名: ${DOMAIN_NAME}
访问地址: $ACCESS_URL
MySQL密码: ${MYSQL_ROOT_PASSWORD:-password}
Redis密码: ${REDIS_PASSWORD}
项目目录: ${PROJECT_DIR}
配置文件: ${PROJECT_DIR}/backend/billiards-admin/$COMPOSE_FILE
SSL证书: $([ "$SETUP_SSL" = "y" ] && echo "已配置" || echo "未配置")
EOF

    print_warning "⚠️  重要提醒："
    echo "1. 请妥善保存数据库密码等敏感信息"
    echo "2. 建议定期备份数据库数据"
    if [[ "$DEPLOY_MODE" = "1" ]]; then
        echo "3. 简单模式适用于开发测试，生产环境建议使用生产模式"
        echo "4. 当前数据库和Redis可外网访问，注意安全"
    fi
    if [[ "$SETUP_SSL" != "y" && "$DOMAIN_NAME" != "localhost" ]]; then
        echo "3. 建议配置SSL证书保证数据传输安全"
    fi
    if [[ "$MAVEN_CACHE_CHOICE" = "2" ]]; then
        echo "4. Maven缓存已启用，下次构建将大幅提速"
    fi
    echo

    print_success "✅ 完整部署完成！您的台球厅SaaS系统已成功部署！"
}

# 错误处理
handle_error() {
    print_error "部署过程中发生错误，正在清理..."
    cd $PROJECT_DIR/backend/billiards-admin 2>/dev/null || true
    docker-compose -f docker-compose.prod.yml down 2>/dev/null || true
    docker-compose -f docker-compose.simple.yml down 2>/dev/null || true
    print_error "部署失败，请检查错误信息后重试"
    exit 1
}

# 主函数
main() {
    # 设置错误处理
    trap 'handle_error' ERR

    show_welcome
    get_user_inputs
    check_system
    update_system
    install_docker
    configure_firewall
    setup_directories
    prepare_code
    configure_nginx
    #setup_ssl
    generate_docker_config
    deploy_application
    setup_monitoring
    show_deployment_result
}

# 处理Ctrl+C信号
trap 'print_error "部署被中断"; exit 1' INT

# 执行主函数
main "$@"
