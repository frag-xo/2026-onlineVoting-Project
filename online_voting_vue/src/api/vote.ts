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

// 7. 新增投票（管理员）
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

// 12. 用户注册
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

// 13. 用户登录
export const login = (uname: string, pwd: string) => {
  return request.post('/account/login', null, {
    params: { uname, pwd }
  })
}

// ========== 投票排行 ==========

// 14. 获取投票排行
export const getVoteRanking = (limit: number = 10) => {
  return request.get('/vote/ranking', { params: { limit } })
}

// ========== 分享功能 ==========

// 15. 生成分享链接
export const getShareLink = (voteId: number, baseUrl: string) => {
  return request.get(`/vote/share/link/${voteId}`, { params: { baseUrl } })
}

// 16. 生成二维码
export const getQrCode = (voteId: number, baseUrl: string, width = 300, height = 300) => {
  return request.get(`/vote/share/qrcode/${voteId}`, {
    params: { baseUrl, width, height },
    responseType: 'blob'
  })
}

// ========== 个人中心 ==========

// 17. 获取我的投票历史
export const getMyVoteHistory = () => {
  return request.get('/vote/history')
}

// 18. 获取我的收藏
export const getMyFavorites = () => {
  return request.get('/user/favorites')
}

// 19. 获取我的积分
export const getMyPoints = () => {
  return request.get('/points/my')
}

// 20. 获取我的通知
export const getMyNotifications = () => {
  return request.get('/user/notifications')
}

// 21. 获取未读通知数
export const getUnreadCount = () => {
  return request.get('/user/notifications/unread-count')
}

// ========== 头像和个人信息 ==========

// 22. 上传头像
export const uploadAvatar = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/account/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 23. 修改用户名
export const updateUsername = (newUsername: string) => {
  return request.put('/account/username', null, { params: { newUsername } })
}

// 24. 修改密码
export const updatePassword = (oldPwd: string, newPwd: string) => {
  return request.put('/account/password', null, { params: { oldPwd, newPwd } })
}