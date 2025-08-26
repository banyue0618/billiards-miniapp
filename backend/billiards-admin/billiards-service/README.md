# 自助台球厅管理系统后端服务

自助台球厅管理系统是一个基于Spring Boot开发的后端服务，提供用户端小程序和管理端的API接口。

## 技术栈

- Java 17
- Spring Boot 3.1.x
- MyBatis Plus 3.5.x
- MySQL 8.0
- Redis 7.0
- Sa-Token 1.34.x (权限认证)
- Knife4j/Swagger (API文档)
- Lombok (代码简化)
- Hutool (工具类库)

## 功能模块

- 用户管理：小程序用户注册登录、个人信息管理
- 门店管理：门店信息维护、地理位置查询
- 桌台管理：桌台信息维护、二维码生成
- 计费规则：标准计费、阶梯计费配置
- 订单管理：开台、计费、结算流程
- 统计分析：营收统计、使用率分析
- 系统管理：商家账户、权限管理

## 项目结构

```
billiards-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── banyue/
│   │   │           └── billiards/
│   │   │               ├── BilliardsApplication.java     # 应用入口类
│   │   │               ├── config/                       # 配置类
│   │   │               ├── controller/                   # 控制器
│   │   │               ├── service/                      # 业务逻辑层
│   │   │               ├── mapper/                       # MyBatis Mapper接口
│   │   │               ├── model/                        # 数据模型
│   │   │               ├── common/                       # 通用组件
│   │   │               └── framework/                    # 框架核心
│   │   └── resources/                                    # 资源文件
└── pom.xml                                              # Maven配置
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.0+

### 构建与运行

1. 克隆项目
```bash
git clone https://github.com/your-username/billiards-service.git
cd billiards-service
```

2. 配置数据库
- 创建数据库
```sql
CREATE DATABASE billiards DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```
- 修改`application-dev.yml`中的数据库连接信息

3. 构建项目
```bash
mvn clean package -DskipTests
```

4. 运行项目
```bash
java -jar target/billiards-service.jar
```

5. 访问Swagger文档
```
http://localhost:8080/doc.html
```

## 部署指南

### 开发环境部署

使用开发配置文件：
```bash
java -jar billiards-service.jar --spring.profiles.active=dev
```

### 生产环境部署

使用生产配置文件：
```bash
java -jar billiards-service.jar --spring.profiles.active=prod
```

## 接口文档

系统启动后，访问以下地址查看API文档：
```
http://localhost:8080/doc.html
```

## 贡献指南

1. Fork项目
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启Pull Request

## 许可证

[MIT](LICENSE) 