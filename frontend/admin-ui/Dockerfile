# 构建阶段
FROM node:18-alpine AS builder
WORKDIR /app

# 复制package.json和package-lock.json
COPY package*.json ./

# 安装依赖（更稳定，可复现）
RUN npm ci

# 复制项目文件
COPY . .

# 构建项目
RUN npm run build

# 交付阶段：仅用于把构建产物复制到宿主机挂载的 /opt/${PROJECT_NAME}/web
FROM alpine:3.19
WORKDIR /opt/${PROJECT_NAME}/web

RUN mkdir -p /opt/${PROJECT_NAME}/web && apk add --no-cache bash coreutils

# 暂存构建结果到镜像内的临时目录
COPY --from=builder /app/dist /tmp/dist

# 启动时复制到挂载目录并保持容器存活，便于一次性交付静态文件
ENTRYPOINT ["sh", "-c", "mkdir -p /opt/${PROJECT_NAME}/web && cp -a /tmp/dist/. /opt/${PROJECT_NAME}/web && ls -lah /opt/${PROJECT_NAME}/web && tail -f /dev/null"]
