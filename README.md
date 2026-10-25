# PetStore 宠物商城

## 项目介绍

PetStore 是一款功能完善的宠物商城系统，提供宠物商品买卖、店铺管理、购物车、订单处理等完整电商功能。系统采用前后端分离架构，支持 Web 端（Vue 3）和移动端（UniApp）。

## 技术栈

### 后端
- **Spring Boot** - Java 快速开发框架
- **MyBatis Plus** - ORM 持久层框架
- **Redis** - 缓存数据库
- **MySQL** - 关系型数据库
- **Sa-Token** - 认证授权框架

### 前端 Web 端
- **Vue 3** - 渐进式前端框架
- **Vite** - 构建工具
- **Element Plus** - UI 组件库

### 移动端
- **UniApp** - 跨平台应用框架

## 软件架构

```
├── back/           # Spring Boot 后端服务
├── front/          # Vue 3 Web 端前端
└── uniapp/         # UniApp 移动端/小程序
```

## 功能模块

### 用户模块
- 邮箱注册登录
- 验证码登录
- 个人信息中心
- 头像更新
- 密码修改

### 店铺模块
- 店铺创建与管理
- 店铺信息修改
- 店铺 Logo 上传
- 店铺商品列表

### 商品模块
- 商品发布与编辑
- 商品分类管理
- 商品媒体（图片/视频）上传
- 商品搜索
- 商品上下架

### 购物车模块
- 加入购物车
- 购物车列表
- 批量删除

### 订单模块
- 即时下单
- 购物车下单
- 订单列表查询
- 订单状态管理
- 取消订单
- 申请退款
- 退款审核

### 收货地址模块
- 添加收货地址
- 地址列表
- 修改地址
- 删除地址
- 设置默认地址

### 评论模块
- 发表评论
- 商品评论列表

### 管理员模块
- 会员管理
- 店铺管理
- 商品管理

### AI 助手
- AI 智能聊天功能

## 项目结构

### 后端结构
```
back/src/main/java/com/example/back/
├── BackApplication.java          # 启动类
├── config/                        # 配置类
├── controller/                    # 控制器
├── entity/                        # 实体类
├── mapper/                        # 数据访问层
├── service/                      # 业务接口
│   └── impl/                     # 业务实现
└── utils/                        # 工具类
```

## 安装教程

### 后端配置

1. **环境要求**
   - JDK 1.8+
   - MySQL 5.7+
   - Redis

2. **数据库配置**
   修改 `back/src/main/resources/application.yml` 中的数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/petstore
       username: root
       password: your_password
     redis:
       host: localhost
       port: 6379
   ```

3. **编译运行**
   ```bash
   cd back
   ./mvnw spring-boot:run
   ```

### 前端配置

1. **环境要求**
   - Node.js 14+
   - npm 或 yarn

2. **安装依赖**
   ```bash
   cd front
   npm install
   ```

3. **启动开发服务器**
   ```bash
   npm run dev
   ```

### 移动端配置

```bash
cd uniapp
npm install
# 使用 HBuilderX 或命令行编译
```

## 使用说明

### 买家使用流程
1. 注册账号 / 邮箱登录
2. 浏览商品
3. 加入购物车 / 直接购买
4. 填写收货地址
5. 提交订单
6. 申请退款（如需）

### 卖家使用流程
1. 登录账号
2. 创建店铺
3. 上传商品信息和媒体
4. 管理订单
5. 处理退款申请

### 管理员使用流程
1. 登录管理员账号
2. 管理会员
3. 管理店铺
4. 管理商品

## 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

## 许可证

本项目仅供学习交流使用。


---

---

## 关于本仓库

- **项目**：萌宠乐园宠物商城系统（B2C 宠物交易平台）
- **团队**：厦门理工学院「龙虾组」，2026.06 – 2026.07
- **我的职责（全栈开发）**：

| 模块 | 我做的事 |
|---|---|
| 交易闭环 | 用 `@Transactional` 保证「校验库存 → 创建订单 → 扣减库存 → 清空购物车」的原子性，库存不足抛业务异常整体回滚；实现立即购买、购物车结算、订单状态流转、取消订单、退款申请与审核 |
| 数据建模与检索 | 设计用户、店铺、商品、宠物、用品、订单、退款、评价、地址等 13 张表；基于 MyBatis-Plus 分页插件实现分类筛选与「名称 + 描述」关键词搜索，仅返回上架商品并批量装配封面媒体 |
| 认证与权限 | 基于 Sa-Token 实现登录态管理与注销，支持用户名密码、邮箱验证码双登录；区分普通用户 / 商家 / 管理员三类角色；密码 MD5 + 固定盐加密存储，禁用账号拦截登录 |
| 多端与集成 | Vue3 + Element Plus 完成首页、商品详情、购物车、订单、个人中心页面，UniApp 复用同一套接口；集成高德地图实现附近店铺 LBS 展示，接入智谱 GLM-4-Flash AI 客服 |

## 安全说明

本仓库已移除全部硬编码凭据，配置统一走环境变量（见 `.env.example`）：

| 变量 | 用途 |
|---|---|
| `MYSQL_PASSWORD` | 数据库密码 |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | 邮件验证码发件账号与 SMTP 授权码 |
| `ZHIPU_API_KEY` | 智谱 GLM-4-Flash（AI 客服） |
| `VITE_DEEPSEEK_API_KEY` | 前端 DeepSeek 调用（仅本地演示，生产应走后端代理） |

上传目录 `.uploads/`、构建产物 `back/target/`、`front/dist/`、`node_modules/` 均不入库。
