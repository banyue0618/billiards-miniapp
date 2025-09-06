# 贝尔实验室 Spring 官方推荐镜像 JDK下载地址 https://bell-sw.com/pages/downloads/
FROM bellsoft/liberica-openjdk-debian:17.0.11-cds
#FROM bellsoft/liberica-openjdk-debian:21.0.5-cds
#FROM findepi/graalvm:java17-native

LABEL maintainer="zhang si peng"

RUN mkdir -p /ruoyi/server/logs \
    /ruoyi/server/temp \
    #/ruoyi/skywalking/agent

WORKDIR /ruoyi/server

ENV SERVER_PORT=8080 LANG=C.UTF-8 LC_ALL=C.UTF-8 JAVA_OPTS=""

EXPOSE ${SERVER_PORT}

ADD ./target/ruoyi-admin.jar ./app.jar
# 工作流字体文件
ADD ./zhFonts/ /usr/share/fonts/zhFonts/

SHELL ["/bin/bash", "-c"]

ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${SERVER_PORT} \
           # 应用名称 如果想区分集群节点监控 改成不同的名称即可
           #-Dskywalking.agent.service_name=ruoyi-server \
           #-javaagent:/ruoyi/skywalking/agent/skywalking-agent.jar \
           -XX:+HeapDumpOnOutOfMemoryError -XX:+UseZGC ${JAVA_OPTS} \
           -jar app.jar

# 构建阶段
FROM maven:3.8-openjdk-17-slim AS builder
WORKDIR /app
# 复制所有模块的pom.xml
COPY ./pom.xml ./pom.xml
COPY ./ruoyi-admin/pom.xml ./ruoyi-admin/pom.xml
COPY ./ruoyi-common/pom.xml ./ruoyi-common/pom.xml
#COPY ./ruoyi-extend/pom.xml ./ruoyi-extend/pom.xml 
COPY ./ruoyi-modules/pom.xml ./ruoyi-modules/pom.xml
COPY ./billiards-service/pom.xml ./billiards-service/pom.xml
COPY ./ruoyi-common/ruoyi-common-bom/pom.xml ./ruoyi-common/ruoyi-common-bom/pom.xml
# 复制各子模块pom文件（确保Maven能正确解析依赖）
RUN find ./ruoyi-common -name 'pom.xml' -not -path "*/ruoyi-common-bom/*" | xargs -I {} sh -c 'mkdir -p $(dirname {})/src/main/java && cp {} {}'
RUN find ./ruoyi-modules -name 'pom.xml' | xargs -I {} sh -c 'mkdir -p $(dirname {})/src/main/java && cp {} {}'
#RUN find ./ruoyi-extend -name 'pom.xml' | xargs -I {} sh -c 'mkdir -p $(dirname {})/src/main/java && cp {} {}'
RUN find ./billiards-service -name 'pom.xml' | xargs -I {} sh -c 'mkdir -p $(dirname {})/src/main/java && cp {} {}'

# 下载Maven依赖
RUN mvn dependency:go-offline -B

# 复制所有源代码
COPY ./ruoyi-admin/src ./ruoyi-admin/src
COPY ./ruoyi-common ./ruoyi-common
#COPY ./ruoyi-extend ./ruoyi-extend
COPY ./ruoyi-modules ./ruoyi-modules
COPY ./billiards-service ./billiards-service

# 编译打包
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:17-slim
WORKDIR /app

# 创建存放日志和临时文件的目录
RUN mkdir -p /app/logs /app/temp

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 复制构建阶段生成的jar包
COPY --from=builder /app/ruoyi-admin/target/ruoyi-admin.jar /app/app.jar

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Djava.security.egd=file:/dev/./urandom"

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar ${SPRING_PROFILES_ACTIVE:+--spring.profiles.active=${SPRING_PROFILES_ACTIVE}}"]

