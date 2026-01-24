# Mall-Ease 商城项目

## 项目简介

Mall-Ease 是一个基于 Spring Boot 3.2.2 和 Spring Cloud 2023.0.1 的微服务商城系统。

## 技术栈

- **Java**: 17
- **Spring Boot**: 3.2.2
- **Spring Cloud**: 2023.0.1
- **Spring Cloud Alibaba**: 2023.0.1.0
- **MyBatis**: 3.5.14
- **MySQL**: 8.0.29
- **Druid**: 1.2.9
- **Sa-Token**: 1.37.0
- **Hutool**: 5.8.16
- **PageHelper**: 6.1.0

## 项目结构

```
mall-ease/
├── pom.xml                          # 父 POM 文件
├── mall-ease-auth/                  # 认证服务模块
├── mall-ease-gateway/               # 网关模块
├── mall-ease-user/                  # 用户服务模块
├── mall-ease-product/               # 商品服务模块
├── mall-ease-trade/                 # 交易服务模块（订单/支付）
├── mall-ease-content/               # 内容服务模块
├── mall-ease-marketing/             # 营销服务模块
├── mall-ease-search/                # 搜索服务模块
├── mall-ease-monitor/               # 监控模块
├── mall-ease-app/                   # 应用编排/BFF 模块
└── mall-ease-common/                # 公共模块
```

## 模块说明

- **mall-ease-auth**: 提供用户认证和授权服务
- **mall-ease-gateway**: API 网关服务
- **mall-ease-user**: 用户服务
- **mall-ease-product**: 商品服务
- **mall-ease-trade**: 交易服务（订单/支付）
- **mall-ease-content**: 内容服务
- **mall-ease-marketing**: 营销服务
- **mall-ease-search**: 商品搜索服务
- **mall-ease-monitor**: 系统监控服务
- **mall-ease-app**: 应用编排/BFF 服务
- **mall-ease-common**: 公共工具类和通用组件

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 构建项目

```bash
mvn clean install
```

### 运行项目

各个模块可以独立运行，启动对应的 Application 主类即可。

## 依赖管理

项目使用 Maven 的 `dependencyManagement` 统一管理依赖版本，各子模块继承父 POM 的依赖配置。

## 许可证

本项目仅供学习使用。

