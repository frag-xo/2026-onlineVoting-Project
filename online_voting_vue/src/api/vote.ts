import request from './index'

// ========== 用户端 ==========

// 1. 获取投票列表（分页）
export const getVoteList = (params: any = {}) => {
  return request.post('/vote/page', {
    pageNum: params.pageNum || 1,
    pageSize: params.pageSize || 100,
    status: params.status,
    orderBy: params.orderBy,
    orderDirection: params.orderDirection
  })
}

// 2. 获取投票详情（含选项）
export const getVoteDetail = (voteId: number) => {
  return request.get(`/vote/${voteId}/detail`)
}

// 3. 获取投票结果统计
export const getVoteResult = (voteId: number) => {
  return request.get(`/vote/result/${voteId}`)
}

// 4. 提交投票（需要 userId, captchaId, captchaCode）
export const submitVote = (data: {
  voteId: number
  optionId: number
  userId: number
  captchaId: string
  captchaCode: string
}) => {
  return request.post('/vote/vote', null, {
    params: data
  })
}

// 5. 检查是否已投票
export const hasVoted = (voteId: number, userId: number) => {
  return request.get('/vote/hasVoted', {
    params: { voteId, userId }
  })
}

// 6. 获取验证码
export const getCaptcha = () => {
  return request.get('/captcha/generate')
}

// ========== 管理端 ==========

// 7. 新增投票（管理员直接发布）
export const createVote = (data: {
  title: string
  description?: string
  status?: number
  startTime?: string
  endTime: string
  creatorId?: number
  options: string[]
}) => {
  return request.post('/vote', data)
}

// 8. 修改投票（管理员）
export const updateVote = (data: {
  id: number
  title?: string
  description?: string
  status?: number
  startTime?: string
  endTime?: string
  creatorId?: number
  options?: string[]
}) => {
  return request.put('/vote', data)
}

// 9. 删除投票（管理员）
export const deleteVote = (voteId: number) => {
  return request.delete(`/vote/${voteId}`)
}

// 10. 结束投票（管理员）
export const endVote = (voteId: number) => {
  return request.put(`/vote/end/${voteId}`)
}

// 11. 获取数据看板统计（管理员）
export const getDashboard = () => {
  return request.get('/admin/dashboard')
}

// 15. 获取投票趋势数据（管理员）
export const getTrend = () => {
  return request.get('/admin/trend')
}

// ========== 用户账号 ==========

// 13. 用户注册
export const register = (data: {
  uname: string
  pwd: string
  realname?: string
  phoneNumber?: string
}) => {
  return request.post('/account/register', null, {
    params: data
  })
}

// 14. 用户登录
export const login = (uname: string, pwd: string) => {
  return request.post('/account/login', null, {
    params: { uname, pwd }
  })
}

// 14. 用户登出
export const logout = () => {
  return request.post('/account/logout')
}

// 16. 修改用户名
export const updateUsername = (newUsername: string) => {
  return request.put('/account/username', null, {
    params: { newUsername }
  })
}

// 17. 上传头像
export const uploadAvatar = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/account/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// ========== 普通用户发布投票（待审核） ==========

// 18. 普通用户发布投票（需审核）
export const submitVoteForAudit = (data: {
  title: string
  description?: string
  endTime: string
  options: string[]
}) => {
  return request.post('/vote/submit', data)
}

// ========== 投票排行 & 历史 ==========

// 19. 投票排行
export const getVoteRanking = (limit = 10) => {
  return request.get('/vote/ranking', { params: { limit } })
}

// 20. 用户投票历史
export const getVoteHistory = () => {
  return request.get('/vote/history')
}

// ========== 收藏 ==========

// 21. 获取收藏列表
export const getFavorites = () => {
  return request.get('/user/favorites')
}

// 22. 收藏投票
export const favoriteVote = (voteId: number) => {
  return request.post(`/user/favorite/${voteId}`)
}

// 23. 取消收藏
export const unfavoriteVote = (voteId: number) => {
  return request.delete(`/user/favorite/${voteId}`)
}

// 24. 检查是否已收藏
export const checkFavorited = (voteId: number) => {
  return request.get(`/user/favorite/check/${voteId}`)
}

// ========== 评论 ==========

// 25. 添加评论
export const addComment = (voteId: number, content: string, parentId?: number) => {
  return request.post('/user/comment', null, {
    params: { voteId, content, parentId }
  })
}

// 26. 获取评论列表
export const getComments = (voteId: number) => {
  return request.get(`/user/comments/${voteId}`)
}

// 27. 删除评论
export const deleteComment = (commentId: number) => {
  return request.delete(`/user/comment/${commentId}`)
}

// ========== 审核 ==========

// 28. 获取待审核列表（管理员）
export const getPendingAudits = () => {
  return request.get('/vote-audit/pending')
}

// 29. 审核投票（管理员）
export const auditVote = (voteId: number, status: number, remark?: string) => {
  return request.post('/vote-audit/audit', null, {
    params: { voteId, status, remark }
  })
}

// ========== 通知 ==========

// 30. 获取通知列表
export const getNotifications = () => {
  return request.get('/user/notifications')
}

// 31. 获取未读通知数
export const getUnreadCount = () => {
  return request.get('/user/notifications/unread-count')
}

// 32. 标记通知已读
export const markNotificationRead = (id: number) => {
  return request.put(`/user/notifications/${id}/read`)
}

// 33. 全部标记已读
export const markAllRead = () => {
  return request.put('/user/notifications/read-all')
}

// ========== 积分 ==========

// 34. 获取用户积分
export const getMyPoints = () => {
  return request.get('/points/my')
}

// ========== 投票分组 ==========

// 35. 获取分组列表
export const getGroupList = () => {
  return request.get('/vote-group/list')
}

// 获取推荐投票列表
export const getRecommendedVotes = () => {
  return request.get('/vote/recommended')
}

// 推荐或取消推荐投票（管理员）
export const recommendVote = (voteId: number) => {
  return request.put(`/vote/recommend/${voteId}`)
}

// 点赞或取消点赞
export const likeVote = (voteId: number) => {
  return request.post('/like', null, { params: { voteId } })
}

// 获取点赞数
export const getLikeCount = (voteId: number) => {
  return request.get(`/like/count/${voteId}`)
}