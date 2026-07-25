/**
 * 认证信息存储工具
 *
 * 使用 sessionStorage（每个 Tab 独立），避免多 Tab 登录互相覆盖。
 * 非认证数据（主题、侧边栏等）仍用 localStorage。
 */

const AUTH_KEYS = ['token', 'userId', 'username', 'utype', 'avatar'] as const
type AuthKey = typeof AUTH_KEYS[number]

/** 获取认证信息（从 sessionStorage，兼容回退到 localStorage） */
export function getAuth(key: AuthKey): string | null {
  return sessionStorage.getItem(key) ?? localStorage.getItem(key)
}

/** 设置认证信息（同时写入 sessionStorage + localStorage） */
export function setAuth(key: AuthKey, value: string): void {
  sessionStorage.setItem(key, value)
  localStorage.setItem(key, value)
}

/** 清除所有认证信息（两个存储都清） */
export function clearAuth(): void {
  for (const key of AUTH_KEYS) {
    sessionStorage.removeItem(key)
    localStorage.removeItem(key)
  }
}

// 便捷访问
export const getToken = () => getAuth('token')
export const getUserId = () => Number(getAuth('userId') || 0)
export const getUsername = () => getAuth('username') || '用户'
export const getUtype = () => getAuth('utype') || ''
export const isAdmin = () => getUtype() === 'ROLE_1'
