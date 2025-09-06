# Docker 部署指南

本文档提供了使用 Docker 和 Docker Compose 部署自助台球厅管理系统的详细说明。

## 准备工作

1. 安装 Docker 和 Docker Compose：
   - Docker：https://docs.docker.com/get-docker/
   - Docker Compose：https://docs.docker.com/compose/install/

2. 确保项目代码已经完全克隆到本地。

## 部署后端服务

### 方法一：使用 Docker Compose（推荐）

1. 进入项目根目录：
   ```bash
   cd backend/billiards-admin
   ```

2. 修改 `docker-compose.yml` 文件中的数据库密码和Redis密码：
   ```yaml
   # MySQL配置
   - MYSQL_ROOT_PASSWORD=your_secure_password
   
   # 应用配置
   - SPRING_DATASOURCE_PASSWORD=your_secure_password
   - SPRING_REDIS_PASSWORD=your_secure_redis_password
   
   # Redis配置
   command: redis-server --requirepass your_secure_redis_password
   ```

3. 启动所有服务：
   ```bash
   docker-compose up -d
   ```

4. 查看服务状态：
   ```bash
   docker-compose ps
   ```

5. 查看应用日志：
   ```bash
   docker-compose logs -f billiards-admin
   ```

### 方法二：单独构建和运行

如果你想单独构建和运行后端应用，可以按照以下步骤操作：

1. 构建Docker镜像：
   ```bash
   cd backend/billiards-admin
   docker build -t billiards-admin:latest -f ruoyi-admin/Dockerfile .
   ```

2. 运行容器：
   ```bash
   docker run -d --name billiards-admin \
     -p 8080:8080 \
     -e SPRING_PROFILES_ACTIVE=prod \
     -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/billiards?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai \
     -e SPRING_DATASOURCE_USERNAME=root \
     -e SPRING_DATASOURCE_PASSWORD=password \
     -e SPRING_REDIS_HOST=host.docker.internal \
     -v $(pwd)/logs:/app/logs \
     -v $(pwd)/upload:/app/upload \
     billiards-admin:latest
   ```

## 部署前端服务

前端应用是一个静态Web应用，可以使用Nginx部署。

### 方法一：使用Docker Compose（与后端一起部署）

1. 取消注释 `docker-compose.yml` 中的前端服务部分：
   ```yaml
   # 前端应用
   billiards-ui:
     build:
       context: ../frontend/admin-ui
       dockerfile: Dockerfile
     container_name: billiards-ui
     restart: always
     ports:
       - "80:80"
     depends_on:
       - billiards-admin
     networks:
       - billiards-network
   ```

2. 重启服务：
   ```bash
   docker-compose up -d
   ```

### 方法二：单独构建和运行前端

1. 构建前端Docker镜像：
   ```bash
   cd frontend/admin-ui
   docker build -t billiards-ui:latest .
   ```

2. 运行前端容器：
   ```bash
   docker run -d --name billiards-ui -p 80:80 billiards-ui:latest
   ```

## 注意事项

1. **数据备份**：确保定期备份MySQL数据卷中的数据。

2. **环境变量**：根据不同环境调整环境变量配置。

3. **安全性**：
   - 修改默认的数据库和Redis密码
   - 在生产环境中，应该限制容器的资源使用
   - 考虑使用Docker Secrets或环境变量文件来管理敏感信息

4. **监控**：
   - 使用Docker的健康检查功能监控容器状态
   - 配置日志收集和监控系统

## 故障排除

1. 如果应用无法连接到数据库，检查：
   - 数据库容器是否正常运行
   - 连接字符串是否正确
   - 网络配置是否正确

2. 如果前端无法访问API：
   - 检查Nginx配置中的代理设置
   - 确认后端服务是否正常运行

3. 查看容器日志以获取更多故障信息：
   ```bash
   docker logs billiards-admin
   docker logs billiards-mysql
   docker logs billiards-redis
   docker logs billiards-ui
   ```

## 扩展配置

### 配置HTTPS

如果需要配置HTTPS，可以修改前端的Nginx配置，添加SSL证书：

```nginx
server {
    listen 443 ssl;
    server_name your_domain.com;
    
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    
    # 其他配置...
}
```

然后在`docker-compose.yml`中添加相应的卷挂载：

```yaml
volumes:
  - ./ssl:/etc/nginx/ssl
``` 