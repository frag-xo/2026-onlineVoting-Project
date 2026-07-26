import request from './index'

// 发送好友申请
export const sendFriendRequest = (friendId: number) => {
  return request.post(`/friend/request/${friendId}`)
}

// 获取好友申请列表（待处理）
export const getFriendRequests = () => {
  return request.get('/friend/requests')
}

// 同意好友申请
export const acceptFriendRequest = (relationId: number) => {
  return request.put(`/friend/request/${relationId}/accept`)
}

// 拒绝好友申请
export const rejectFriendRequest = (relationId: number) => {
  return request.put(`/friend/request/${relationId}/reject`)
}

// 获取好友列表
export const getFriendList = () => {
  return request.get('/friend/list')
}

// 删除好友
export const deleteFriend = (friendId: number) => {
  return request.delete(`/friend/${friendId}`)
}