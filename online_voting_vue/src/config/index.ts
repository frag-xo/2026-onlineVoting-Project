/**
 * 全局环境配置
 *
 * 后端地址通过 .env / .env.production 中的 VITE_* 变量注入，
 * 避免把主机地址硬编码在业务代码里（换环境时不用改代码）。
 */
export const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080'
export const WS_BASE = import.meta.env.VITE_WS_BASE || 'ws://localhost:8080'
