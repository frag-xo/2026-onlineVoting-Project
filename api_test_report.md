# 在线投票系统 API 测试报告

## 测试时间
2026-07-22

## 测试环境
- 后端地址: http://localhost:8080
- 测试账号: testuser (ROLE_3)

## 测试结果汇总

| 模块 | 接口 | 方法 | 状态 | 结果 |
|------|------|------|------|------|
| 账号管理 | 用户登录 | POST | /api/account/login | ✅ 通过 |
| 账号管理 | 获取用户信息 | GET | /api/account/{id} | ✅ 通过 |
| 验证码 | 获取验证码 | GET | /api/captcha/generate | ✅ 通过 |
| 投票查询 | 分页查询投票 | POST | /api/vote/page | ✅ 通过 |
| 投票查询 | 简单分页查询 | GET | /api/vote/page/simple | ✅ 通过 |
| 投票查询 | 查询投票详情 | GET | /api/vote/{id} | ✅ 通过 |
| 投票查询 | 查询投票详情（含选项）| GET | /api/vote/{id}/detail | ✅ 通过 |
| 投票查询 | 查询投票结果 | GET | /api/vote/result/{id} | ✅ 通过 |
| 投票查询 | 检查是否已投票 | GET | /api/vote/hasVoted | ✅ 通过 |
| 选项管理 | 查询投票的所有选项 | GET | /api/vote/option/list/{voteId} | ✅ 通过 |
| 投票记录 | 查询投票记录 | GET | /api/vote/record/list/{voteId} | ✅ 通过 |
| 分享功能 | 生成分享链接 | GET | /api/vote/share/link/{id} | ✅ 通过 |
| 管理员 | 管理员仪表盘 | GET | /api/admin/dashboard | ✅ 通过 (403) |
| 管理员 | 用户列表 | GET | /api/admin/user/list | ✅ 通过 (403) |
| 投票管理 | 创建投票 | POST | /api/vote | ❌ 失败 (409) |

## 测试统计
- 总测试数: 15
- 通过: 14
- 失败: 1
- 通过率: 93.3%

## 问题说明

### 创建投票接口返回409错误
- 接口: POST /api/vote
- 错误信息: "未知异常"
- 可能原因: 数据库验证失败或服务端内部错误

## API 认证说明

系统使用请求头进行认证：
- `X-User-Id`: 用户ID
- `X-User-Role`: 用户角色 (ROLE_1=管理员, ROLE_3=普通用户)

## 接口列表

### 账号管理 (/api/account)
1. POST /api/account/login - 用户登录
2. POST /api/account/register - 用户注册
3. GET /api/account/{id} - 获取用户信息
4. PUT /api/account - 修改用户信息
5. PUT /api/account/password - 修改密码
6. POST /api/account/init/random - 生成随机用户

### 投票管理 (/api/vote)
1. POST /api/vote/page - 分页查询投票
2. GET /api/vote/page/simple - 简单分页查询
3. GET /api/vote/{id} - 查询投票详情
4. GET /api/vote/{id}/detail - 查询投票详情（含选项）
5. POST /api/vote - 新增投票
6. PUT /api/vote - 修改投票
7. DELETE /api/vote/{id} - 删除投票
8. DELETE /api/vote/batch - 批量删除投票
9. POST /api/vote/vote - 用户投票
10. GET /api/vote/result/{id} - 查询投票结果
11. GET /api/vote/hasVoted - 检查是否已投票
12. PUT /api/vote/end/{id} - 结束投票
13. POST /api/vote/init/random - 生成随机投票

### 选项管理
14. GET /api/vote/option/{id} - 查询选项
15. GET /api/vote/option/list/{voteId} - 查询投票的所有选项
16. POST /api/vote/option - 新增选项
17. PUT /api/vote/option - 修改选项
18. DELETE /api/vote/option/{id} - 删除选项

### 投票记录
19. GET /api/vote/record/list/{voteId} - 查询投票记录
20. DELETE /api/vote/record/{id} - 删除投票记录
21. DELETE /api/vote/record/clear/{voteId} - 清空投票记录

### 分享功能
22. GET /api/vote/share/link/{id} - 生成分享链接
23. GET /api/vote/share/qrcode/{id} - 生成二维码

### 导出功能
24. GET /api/vote/export/{id} - 导出投票结果

### 管理员 (/api/admin)
25. GET /api/admin/dashboard - 管理员仪表盘
26. GET /api/admin/user/list - 用户列表
27. PUT /api/admin/user/role - 修改用户角色
28. PUT /api/admin/user/status - 修改用户状态
29. DELETE /api/admin/user/{id} - 删除用户

### 验证码 (/api/captcha)
30. GET /api/captcha/generate - 获取验证码

### 文件上传 (/api)
31. GET /api/upload - 上传页面
32. POST /api/uploadImg - 上传图片
