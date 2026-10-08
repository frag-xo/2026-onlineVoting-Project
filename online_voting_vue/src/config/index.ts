/**
 * 全局环境配置
 * 通过 .env / .env.production 中的 VITE_* 变量区分开发与生产环境，
 * 避免在业务代码中硬编码后端地址。
 */
export const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080'
export const WS_BASE = import.meta.env.VITE_WS_BASE || 'ws://localhost:8080'
