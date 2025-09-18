# ================================
# 优化版 Dockerfile（利用缓存避免重复下载依赖）
# ================================

# 使用Maven镜像进行构建
FROM maven:3.8-openjdk-17-slim AS builder

# 设置工作目录
WORKDIR /app

# ========================================
# 第一步：仅复制pom文件并下载依赖
# 这一层会被Docker缓存，只有pom文件变化时才重新执行
# ========================================

# 复制根pom文件
COPY pom.xml ./

# 复制所有模块的pom文件（保持目录结构）
COPY ruoyi-admin/pom.xml ./ruoyi-admin/
COPY ruoyi-common/pom.xml ./ruoyi-common/
COPY ruoyi-common/ruoyi-common-bom/pom.xml ./ruoyi-common/ruoyi-common-bom/
COPY ruoyi-common/ruoyi-common-core/pom.xml ./ruoyi-common/ruoyi-common-core/
COPY ruoyi-common/ruoyi-common-doc/pom.xml ./ruoyi-common/ruoyi-common-doc/
COPY ruoyi-common/ruoyi-common-encrypt/pom.xml ./ruoyi-common/ruoyi-common-encrypt/
COPY ruoyi-common/ruoyi-common-excel/pom.xml ./ruoyi-common/ruoyi-common-excel/
COPY ruoyi-common/ruoyi-common-file/pom.xml ./ruoyi-common/ruoyi-common-file/
COPY ruoyi-common/ruoyi-common-idempotent/pom.xml ./ruoyi-common/ruoyi-common-idempotent/
COPY ruoyi-common/ruoyi-common-job/pom.xml ./ruoyi-common/ruoyi-common-job/
COPY ruoyi-common/ruoyi-common-json/pom.xml ./ruoyi-common/ruoyi-common-json/
COPY ruoyi-common/ruoyi-common-log/pom.xml ./ruoyi-common/ruoyi-common-log/
COPY ruoyi-common/ruoyi-common-mail/pom.xml ./ruoyi-common/ruoyi-common-mail/
COPY ruoyi-common/ruoyi-common-mybatis/pom.xml ./ruoyi-common/ruoyi-common-mybatis/
COPY ruoyi-common/ruoyi-common-oss/pom.xml ./ruoyi-common/ruoyi-common-oss/
COPY ruoyi-common/ruoyi-common-pay/pom.xml ./ruoyi-common/ruoyi-common-pay/
COPY ruoyi-common/ruoyi-common-ratelimiter/pom.xml ./ruoyi-common/ruoyi-common-ratelimiter/
COPY ruoyi-common/ruoyi-common-redis/pom.xml ./ruoyi-common/ruoyi-common-redis/
COPY ruoyi-common/ruoyi-common-satoken/pom.xml ./ruoyi-common/ruoyi-common-satoken/
COPY ruoyi-common/ruoyi-common-security/pom.xml ./ruoyi-common/ruoyi-common-security/
COPY ruoyi-common/ruoyi-common-sensitive/pom.xml ./ruoyi-common/ruoyi-common-sensitive/
COPY ruoyi-common/ruoyi-common-sms/pom.xml ./ruoyi-common/ruoyi-common-sms/
COPY ruoyi-common/ruoyi-common-social/pom.xml ./ruoyi-common/ruoyi-common-social/
COPY ruoyi-common/ruoyi-common-sse/pom.xml ./ruoyi-common/ruoyi-common-sse/
COPY ruoyi-common/ruoyi-common-tenant/pom.xml ./ruoyi-common/ruoyi-common-tenant/
COPY ruoyi-common/ruoyi-common-translation/pom.xml ./ruoyi-common/ruoyi-common-translation/
COPY ruoyi-common/ruoyi-common-web/pom.xml ./ruoyi-common/ruoyi-common-web/
COPY ruoyi-common/ruoyi-common-websocket/pom.xml ./ruoyi-common/ruoyi-common-websocket/
COPY ruoyi-modules/pom.xml ./ruoyi-modules/
COPY ruoyi-modules/ruoyi-generator/pom.xml ./ruoyi-modules/ruoyi-generator/
COPY ruoyi-modules/ruoyi-job/pom.xml ./ruoyi-modules/ruoyi-job/
COPY ruoyi-modules/ruoyi-system/pom.xml ./ruoyi-modules/ruoyi-system/
COPY ruoyi-modules/ruoyi-workflow/pom.xml ./ruoyi-modules/ruoyi-workflow/

# 复制业务模块pom文件
COPY billiards-service/pom.xml ./billiards-service/

# ========================================
# 下载所有依赖（这一步会被缓存）
# 只有pom文件变化时才会重新下载
# ========================================
RUN mvn dependency:go-offline -B

# ========================================
# 第二步：复制源代码并编译
# 源代码变化时只重新执行这一步，不重新下载依赖
# ========================================

# 复制所有源代码
COPY . .

# 先安装common模块到本地仓库，解决模块间依赖问题
RUN mvn clean install -pl ruoyi-common -am -DskipTests -Dmaven.compile.fork=true

# 执行Maven编译打包（模块依赖已安装到本地仓库）
RUN mvn clean package -DskipTests -Dmaven.compile.fork=true

# =====================================
# 运行阶段 - 使用包含常用工具的镜像
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

# 不安装额外软件，避免网络依赖问题
# 健康检查改用Java应用端口检测

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

# 健康检查 - 使用netstat检测端口，避免curl依赖
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD netstat -an | grep ${SERVER_PORT} | grep LISTEN > /dev/null || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
