import request from './index'

// 获取与某好友的历史消息（分页）
export const getChatHistory = (friendId: number, pageNum = 1, pageSize = 50) => {
  return request.get(`/chat/messages/${friendId}`, {
    params: { pageNum, pageSize }
  })
}

// 标记与某好友的消息为已读
export const markMessagesRead = (friendId: number) => {
  return request.put(`/chat/read/${friendId}`)
}

// 获取总未读消息数
export const getTotalUnread = () => {
  return request.get('/chat/unread-count')
}