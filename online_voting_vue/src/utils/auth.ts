/**
 * 认证信息存储工具
 *
 * 认证信息只存 sessionStorage：sessionStorage 按 Tab 隔离，
 * 多个 Tab 登录不同账号时权限互不影响。
 * 非认证数据（主题色、侧边栏折叠状态等）才用 localStorage，以便跨 Tab 保持一致。
 *
 * 注意：早期版本会把认证信息同时写入 localStorage，而 localStorage 跨 Tab 共享，
 * 会破坏多 Tab 隔离、导致登录态互相覆盖，现已移除该写入；
 * clearAuth 仍会顺带清理 localStorage 中的历史残留数据。
 */

const AUTH_KEYS = ['token', 'userId', 'username', 'utype', 'avatar'] as const
type AuthKey = typeof AUTH_KEYS[number]

/** 读取认证信息（仅 sessionStorage） */
export function getAuth(key: AuthKey): string | null {
  return sessionStorage.getItem(key)
}

/** 写入认证信息（仅 sessionStorage，保证多 Tab 隔离） */
export function setAuth(key: AuthKey, value: string): void {
  sessionStorage.setItem(key, value)
}

/** 清除认证信息（同时清理 localStorage 中的历史残留） */
export function clearAuth(): void {
  for (const key of AUTH_KEYS) {
    sessionStorage.removeItem(key)
    localStorage.removeItem(key)
  }
}

// 便捷访问
export const getToken = (): string | null => {
  return sessionStorage.getItem('token')
}

export const setToken = (token: string): void => {
  sessionStorage.setItem('token', token)
}

export const removeToken = (): void => {
  sessionStorage.removeItem('token')
}

export const getUserId = (): number => {
  return Number(sessionStorage.getItem('userId') || 0)
}

export const setUserId = (userId: string): void => {
  sessionStorage.setItem('userId', userId)
}

export const getUsername = (): string => {
  return sessionStorage.getItem('username') || ''
}

export const setUsername = (username: string): void => {
  sessionStorage.setItem('username', username)
}
export const getUtype = (): string => {
  return sessionStorage.getItem('utype') || 'ROLE_3'
}

export const setUtype = (utype: string): void => {
  sessionStorage.setItem('utype', utype)
}

export const isAdmin = (): boolean => {
  return getUtype() === 'ROLE_1'
}