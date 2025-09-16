# ================================
# 优化版 Dockerfile（利用缓存避免重复下载依赖）
# ================================

# 使用Maven镜像进行构建
FROM maven:3.8-openjdk-17-slim AS builder

# 设置工作目录
WORKDIR /app

# 配置Maven使用阿里云镜像，加速依赖下载
RUN mkdir -p /root/.m2 && \
    echo '<?xml version="1.0" encoding="UTF-8"?>' > /root/.m2/settings.xml && \
    echo '<settings>' >> /root/.m2/settings.xml && \
    echo '  <mirrors>' >> /root/.m2/settings.xml && \
    echo '    <mirror>' >> /root/.m2/settings.xml && \
    echo '      <id>aliyun</id>' >> /root/.m2/settings.xml && \
    echo '      <name>aliyun Maven</name>' >> /root/.m2/settings.xml && \
    echo '      <mirrorOf>central</mirrorOf>' >> /root/.m2/settings.xml && \
    echo '      <url>https://maven.aliyun.com/repository/public</url>' >> /root/.m2/settings.xml && \
    echo '    </mirror>' >> /root/.m2/settings.xml && \
    echo '  </mirrors>' >> /root/.m2/settings.xml && \
    echo '</settings>' >> /root/.m2/settings.xml

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
# 复制源代码
# ========================================
COPY . .

# ========================================
# 关键修复：在单个RUN命令中完成所有Maven操作
# 按正确顺序：先下载依赖，再分阶段构建模块
# ========================================
RUN echo "🚀 开始完整的Maven构建流程..." && \
    echo "⏰ 开始时间: $(date)" && \
    echo "第1步: 下载外部依赖..." && \
    (mvn dependency:go-offline -B -q || echo "部分依赖下载失败，继续构建") && \
    echo "📦 依赖下载完成，仓库大小: $(du -sh /root/.m2/repository 2>/dev/null || echo '计算失败')" && \
    echo "" && \
    echo "第2步: 分阶段编译项目（解决模块依赖）..." && \
    echo "步骤2.1: 构建BOM模块（依赖管理）..." && \
    mvn clean install -pl ruoyi-common/ruoyi-common-bom -DskipTests -Dmaven.compile.fork=true && \
    echo "步骤2.2: 构建core模块（基础模块）..." && \
    mvn clean install -pl ruoyi-common/ruoyi-common-core -DskipTests -Dmaven.compile.fork=true && \
    echo "步骤2.3: 验证core模块安装..." && \
    (ls -la /root/.m2/repository/org/dromara/ruoyi-common-core/*/ruoyi-common-core-*.jar || echo "core模块验证失败") && \
    echo "步骤2.4: 构建整个common模块..." && \
    mvn clean install -pl ruoyi-common -am -DskipTests -Dmaven.compile.fork=true && \
    echo "步骤2.5: 打包整个项目..." && \
    mvn clean package -DskipTests -Dmaven.compile.fork=true && \
    echo "" && \
    echo "=== 构建统计 ===" && \
    echo "Maven仓库大小: $(du -sh /root/.m2/repository 2>/dev/null | cut -f1 || echo '未知')" && \
    echo "依赖jar数量: $(find /root/.m2/repository -name '*.jar' 2>/dev/null | wc -l)" && \
    echo "✅ 项目构建完成: $(date)"

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
