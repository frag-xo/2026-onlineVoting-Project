# Online_Voting 在线投票系统

## 项目简介

基于 Spring Boot + Vue 3 的在线投票系统，支持创建投票、参与投票、结果统计等功能。

## 技术栈

### 后端
- Spring Boot 3.1.5
- MyBatis-Plus 3.5.3.1
- MySQL 8.0
- Redis
- JWT (jjwt)
- Swagger (SpringDoc OpenAPI)

### 前端
- Vue 3 + TypeScript
- Element Plus
- ECharts（数据可视化）
- Vite

## 快速开始

### 1. 环境要求
- JDK 17+
- MySQL 8.0+
- Redis
- Node.js 18+

### 2. 数据库配置
```sql
CREATE DATABASE online_vote DEFAULT CHARSET utf8mb4;
```

执行建表脚本：`src/main/resources/db/vote_schema.sql`

### 3. 修改配置
编辑 `src/main/resources/application.yaml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_vote
    username: root
    password: 123456
```

### 4. 启动后端
运行 `OnlineVoteApplication.java`

### 5. 启动前端
```bash
cd online_voting_vue
npm install
npm run dev
```

### 6. 访问
- 前端：http://localhost:5173
- 后端：http://localhost:8080
- Swagger：http://localhost:8080/swagger-ui.html

## 角色权限

| 角色 | 权限 |
|------|------|
| ROLE_1 管理员 | 所有功能 |
| ROLE_3 普通用户 | 查看、投票、分享 |

## 认证方式

本系统使用 JWT (JSON Web Token) 进行身份认证。

### 1. 登录获取 Token
```
POST /api/account/login?uname=testuser&pwd=123456
```
返回：
```json
{
  "code": 200,
  "msg": "登录成功",
  "t": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "id": 2,
    "uname": "testuser",
    "utype": "ROLE_3"
  }
}
```

### 2. 请求携带 Token
在请求头中添加：
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 3. Token 过期时间
默认 24 小时，可在 `application.yaml` 中修改：
```yaml
jwt:
  expiration: 86400000  # 24小时
```

## 接口列表

### 公用接口（不需要登录）
- `POST /api/account/register` - 注册 - [AccountController.java](src/main/java/org/mjc/controller/AccountController.java)
- `POST /api/account/login` - 登录 - [AccountController.java](src/main/java/org/mjc/controller/AccountController.java)

### 通用接口（需要登录，携带 Token）
- `POST /api/vote/page` - 分页查投票列表（支持排序+状态筛选） - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)
- `GET /api/vote/{id}` - 查投票详情 - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)
- `GET /api/vote/result/{id}` - 查投票结果 - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)
- `POST /api/vote/vote` - 用户投票（需要验证码） - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)
- `GET /api/captcha/generate` - 生成验证码 - [CaptchaController.java](src/main/java/org/mjc/controller/CaptchaController.java)
- `GET /api/vote/hasVoted` - 查是否已投票 - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)
- `GET /api/vote/share/link/{id}` - 生成分享链接 - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)
- `GET /api/vote/share/qrcode/{id}` - 生成二维码 - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)
- `GET /api/vote/export/{id}` - 导出投票结果 - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)
- `GET /api/account/{id}` - 查询用户信息 - [AccountController.java](src/main/java/org/mjc/controller/AccountController.java)
- `PUT /api/account` - 修改用户信息 - [AccountController.java](src/main/java/org/mjc/controller/AccountController.java)
- `PUT /api/account/password` - 修改密码 - [AccountController.java](src/main/java/org/mjc/controller/AccountController.java)

### 管理专用接口（需要管理员权限 ROLE_1）
- `POST /api/vote` - 新增投票 - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)
- `PUT /api/vote` - 修改投票 - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)
- `DELETE /api/vote/{id}` - 删除投票 - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)
- `PUT /api/vote/end/{id}` - 结束投票 - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)
- `GET /api/admin/dashboard` - 数据看板 - [AdminController.java](src/main/java/org/mjc/controller/AdminController.java)
- `GET /api/admin/user/list` - 用户列表 - [AdminController.java](src/main/java/org/mjc/controller/AdminController.java)
- `PUT /api/admin/user/role` - 修改用户角色 - [AdminController.java](src/main/java/org/mjc/controller/AdminController.java)
- `PUT /api/admin/user/status` - 禁用/启用用户 - [AdminController.java](src/main/java/org/mjc/controller/AdminController.java)
- `DELETE /api/admin/user/{id}` - 删除用户 - [AdminController.java](src/main/java/org/mjc/controller/AdminController.java)
- `POST /api/vote/option` - 新增选项 - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)
- `PUT /api/vote/option` - 修改选项 - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)
- `DELETE /api/vote/option/{id}` - 删除选项 - [VoteController.java](src/main/java/org/mjc/controller/VoteController.java)

完整接口文档请访问 Swagger UI。

## 核心功能

### 用户功能
- **注册/登录** - JWT Token 认证
- **投票列表** - 按状态筛选（全部/进行中/未开始/已结束），按截止时间/创建时间排序
- **投票详情** - 查看选项、参与投票（需验证码）
- **实时结果** - 饼图展示投票结果，30秒自动刷新
- **投票分享** - 生成分享链接 + 二维码（扫码即可参与）
- **导出结果** - 导出投票结果为 Excel 文件

### 管理员功能
- **投票管理** - 创建/编辑/删除/结束投票，支持选项管理
- **数据看板** - 总投票数、总参与人数、进行中投票统计，柱状图+趋势图
- **用户管理** - 用户列表、修改角色、禁用/启用用户
- **生成测试数据** - 随机生成投票和用户数据

### 防刷票机制
- 图形验证码：投票时需要输入验证码
- 投票截止时间：超过截止时间无法投票
- 一人一票：同一用户对同一投票只能投一次（数据库唯一约束）

### 前端特色
- Outfit + Inter 字体组合，现代排版
- 卡片式设计，圆角+阴影+hover 动画
- 统计横幅 + 进度条可视化
- 响应式布局，适配移动端

## 项目结构

```
Online_voting/
├── src/main/java/org/mjc/
│   ├── controller/        # 控制器（4个）
│   │   ├── AccountController.java
│   │   ├── VoteController.java
│   │   ├── AdminController.java
│   │   └── CaptchaController.java
│   ├── service/           # 服务层
│   │   ├── AccountService.java
│   │   ├── VoteService.java
│   │   ├── VoteOptionService.java
│   │   ├── VoteRecordService.java
│   │   ├── CaptchaService.java
│   │   ├── ExportService.java
│   │   └── ShareService.java
│   ├── mapper/            # 数据访问层
│   ├── entity/            # 实体类
│   ├── dto/vote/          # 数据传输对象
│   ├── config/            # 配置类
│   ├── interceptor/       # 权限拦截器
│   ├── utils/             # JWT工具类
│   └── exception/         # 异常处理
├── src/main/resources/
│   ├── application.yaml
│   └── db/vote_schema.sql # 建表脚本
├── online_voting_vue/     # 前端项目
│   ├── src/
│   │   ├── views/         # 页面组件
│   │   │   ├── Login.vue, Register.vue
│   │   │   ├── VoteList.vue, VoteDetail.vue, VoteResult.vue
│   │   │   └── Admin.vue
│   │   ├── api/           # API封装
│   │   ├── router/        # 路由配置
│   │   └── App.vue        # 根组件
│   └── index.html
└── nginx/                 # Nginx配置
```

## 开发团队

- 后端开发
- 前端开发

## 许可证

MIT License
