# ================================
# 简化版 Dockerfile（避免构建错误）
# ================================

# 使用Maven镜像进行构建
FROM maven:3.8-openjdk-17-slim AS builder

# 设置工作目录
WORKDIR /app

# 复制整个项目
COPY . .

# 执行Maven编译打包
RUN mvn clean package -DskipTests -Dmaven.compile.fork=true

# =====================================
# 运行阶段
# =====================================
FROM openjdk:17-jdk-slim

# 设置镜像信息
LABEL maintainer="banyue" \
      description="Billiards SaaS Application" \
      version="1.0.0"

# 设置工作目录
WORKDIR /app

# 创建必要目录
RUN mkdir -p /app/logs \
             /app/temp \
             /app/uploads/files

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安装curl（用于健康检查）
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 复制jar包
COPY --from=builder /app/ruoyi-admin/target/billiards-admin.jar /app/app.jar

# 设置环境变量
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Djava.security.egd=file:/dev/./urandom"
ENV SERVER_PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

# 声明数据卷
VOLUME ["/app/logs", "/app/uploads"]

# 暴露端口
EXPOSE ${SERVER_PORT}

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
