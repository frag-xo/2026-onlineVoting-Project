# Online_Voting 在线投票系统

## 项目简介

基于 Spring Boot + Vue 3 的在线投票系统，支持创建投票、参与投票、结果统计等功能。

## 技术栈

### 后端
- Spring Boot 3.1.5
- MyBatis-Plus 3.5.3.1
- MySQL 8.0
- Redis
- Swagger (SpringDoc OpenAPI)

### 前端
- Vue 3
- TypeScript
- Element Plus
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

## 接口列表

### 公用接口（不需要登录）
- `POST /api/account/register` - 注册
- `POST /api/account/login` - 登录

### 通用接口（需要登录）
- `GET /api/vote/page/simple` - 查投票列表
- `GET /api/vote/{id}` - 查投票详情
- `GET /api/vote/result/{id}` - 查投票结果
- `POST /api/vote/vote` - 用户投票
- `GET /api/captcha/generate` - 生成验证码
- `GET /api/vote/share/link/{id}` - 生成分享链接
- `GET /api/vote/share/qrcode/{id}` - 生成二维码
- `GET /api/vote/export/{id}` - 导出投票结果

### 管理专用接口（需要管理员权限）
- `POST /api/vote` - 新增投票
- `PUT /api/vote` - 修改投票
- `DELETE /api/vote/{id}` - 删除投票
- `GET /api/admin/dashboard` - 数据看板
- `GET /api/admin/user/list` - 用户列表

完整接口文档请访问 Swagger UI。

## 项目结构

```
Online_voting/
├── src/main/java/org/mjc/
│   ├── controller/     # 控制器
│   ├── service/        # 服务层
│   ├── mapper/         # 数据访问层
│   ├── entity/         # 实体类
│   ├── dto/            # 数据传输对象
│   ├── config/         # 配置类
│   ├── interceptor/    # 拦截器
│   └── exception/      # 异常处理
├── src/main/resources/
│   ├── application.yaml
│   └── db/             # 数据库脚本
├── online_voting_vue/  # 前端项目
└── nginx/              # Nginx配置
```

## 开发团队

- 后端开发
- 前端开发

## 许可证

MIT License
