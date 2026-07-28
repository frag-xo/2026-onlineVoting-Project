# Online_Voting 在线投票系统

基于 Spring Boot 3.1.5 + Vue 3 的在线投票系统，支持投票管理、积分系统、社交功能、AI助手、动漫PK对战等。

## 技术栈

### 后端
- **Spring Boot 3.1.5** — 核心框架
- **MyBatis-Plus 3.5.3.1** — ORM
- **MySQL 8.0** — 数据库
- **Redis** — 缓存
- **JWT (jjwt)** — 身份认证
- **WebSocket** — 实时聊天
- **SpringDoc OpenAPI** — 接口文档
- **Druid** — 连接池
- **GLM-4-Flash API** — AI 能力

### 前端
- **Vue 3 + TypeScript**
- **Element Plus**
- **ECharts** — 数据可视化
- **Vite**

## 快速开始

### 1. 环境要求
- JDK 17+
- MySQL 8.0+
- Redis
- Node.js 18+

### 2. 创建数据库
```sql
CREATE DATABASE online_vote DEFAULT CHARSET utf8mb4;
```

### 3. 修改配置
编辑 `src/main/resources/application.yaml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_vote
    username: root
    password: 你的密码
```

### 4. 启动后端
运行 `OnlineVoteApplication.java`（表结构由 MyBatis-Plus 自动/手动初始化）

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

## 快速账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | testuser | 123456 |
| 普通用户 | 自行注册 | — |

## 功能一览

### 🗳️ 投票核心
| 功能 | 说明 |
|------|------|
| 投票CRUD | 发布、编辑、删除、结束投票 |
| 选项管理 | 增删改投票选项 |
| 多状态 | 未开始 / 进行中 / 已结束 |
| 截止时间 | 到期自动截止 |
| 图形验证码 | 投票需验证码防刷 |
| 一人一票 | 每人每票只能投一次 |
| 模糊搜索 | 按标题搜索投票 |
| 排序筛选 | 按状态/时间/热度排序 |
| 批量操作 | 管理员批量删除投票 |

### 📊 数据展示
| 功能 | 说明 |
|------|------|
| 饼图 | 投票结果实时饼图展示 |
| 数据看板 | 总投票数/参与人数/趋势图/柱状图 |
| 投票排行 | 按参与人数排名 |
| 积分排行 | 按用户总积分排名 |
| 投票趋势 | 近7天/30天投票趋势分析 |

### 💰 积分系统
| 功能 | 说明 |
|------|------|
| 赚积分 | 注册+20、登录+10、投票+5、评论+2、收藏+3、被推荐+50 |
| 花积分 | 发布投票-10、置顶-50、匿名-5 |
| 等级系统 | Lv1~Lv5，由总积分决定 |
| 积分明细 | 查看积分增减记录 |
| 积分排行榜 | 全站排行 |

### 🏪 积分商城
| 商品 | 积分 | 效果 |
|------|------|------|
| 称号 | 100~200 | 昵称旁显示称号文字 |
| 改名卡 | 50 | 首次免费，之后需改名卡 |
| 置顶卡 | 200 | 投票置顶24小时 |
| 头像框×6 | 80~200 | 极光/烈焰/冰晶/霓虹/暗夜紫/冠军 |

### 👥 社交功能
| 功能 | 说明 |
|------|------|
| 评论 | 投票评论区 |
| 收藏 | 收藏投票 |
| 点赞 | 点赞投票 |
| 好友系统 | 添加/同意/拒绝/删除好友 |
| 实时聊天 | WebSocket 实时消息，在线推送 |
| 在线状态 | 好友在线状态标识 |
| 通知系统 | 审核结果通知等 |

### 🤖 AI 小助手
- 全局悬浮聊天按钮
- 基于 GLM-4-Flash 智能对话
- 支持系统功能咨询（积分、投票、好友等）
- API 异常自动降级到关键词匹配模式

### 🎮 动漫PK对战
- 暗黑风格独立竞技场页面
- 30轮胜者保留机制
- ELO 评分算法（动态K值）
- 32部动漫不重复挑战
- Hover 卡片放大 + 柔光边框
- 终选彩蛋 + 真爱粉称号
- 胜率&ELO排行

### 🔥 多分类话题PK
- 6个分类：美食/生活/数码/游戏/南北/奶茶
- 50+争议话题，每个分类独立对战
- 奶茶冠军模式（胜者留守）
- 动态称号系统（香菜教父、喜茶信徒等）
- 个性化结果分析（「你是69%北方人」）
- PkIndex分类选择页 + PkBattle对战页

### 🎡 其他功能
| 功能 | 说明 |
|------|------|
| 转盘抽奖 | 投票后可抽奖赢积分 |
| 分享链接 | 生成投票分享链接 |
| 二维码 | 生成投票二维码 |
| Excel导出 | 导出投票结果 |
| 审核机制 | 普通用户发布需管理员审核 |
| 用户管理 | 管理员可启用/禁用用户、改角色 |
| 多Tab隔离 | 不同浏览器Tab独立登录 |

## 角色权限

| 角色 | 说明 |
|------|------|
| ROLE_1 管理员 | 全部功能，含审核/用户管理/看板 |
| ROLE_3 普通用户 | 投票、发布（需审核）、商城、PK |

## 认证方式

JWT Token 认证，请求头携带：
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 项目结构

```
Online_voting/
├── src/main/java/org/mjc/
│   ├── controller/     # 控制器
│   ├── service/        # 服务层
│   ├── mapper/         # 数据访问层
│   ├── entity/         # 实体类
│   ├── dto/            # DTO
│   ├── config/         # 配置类（WebMVC/JWT/SpringDoc/WebSocket）
│   ├── interceptor/    # JWT 权限拦截器
│   ├── websocket/      # WebSocket 聊天处理器
│   ├── utils/          # 工具类
│   ├── cache/          # Redis 缓存
│   └── exception/      # 异常处理
├── src/main/resources/
│   ├── application.yaml
│   └── static/images/  # 静态资源（动漫图/商城图标）
├── online_voting_vue/  # 前端项目
│   ├── src/views/      # 页面组件
│   ├── src/api/        # API封装
│   ├── src/utils/      # 工具
│   ├── src/router/     # 路由
│   └── src/components/ # 公共组件
└── sql/                # 数据库脚本
```

## 数据库（23张表）

account 用户表、vote 投票表、vote_option 选项表、vote_record 投票记录
vote_comment 评论、vote_favorite 收藏、vote_like 点赞、vote_notification 通知
vote_audit 审核、user_friend 好友、chat_message 聊天
user_points 积分、points_log 积分流水
shop_item 商城商品、user_item 用户物品
anime_fighter 动漫选手、anime_battle 动漫对战
pk_category PK分类、pk_pair PK话题、pk_battle PK对战
wheel_prize 转盘奖品、wheel_record 转盘记录、vote_group 投票分组

## 许可证

MIT License
