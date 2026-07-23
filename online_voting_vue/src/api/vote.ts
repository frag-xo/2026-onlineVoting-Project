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