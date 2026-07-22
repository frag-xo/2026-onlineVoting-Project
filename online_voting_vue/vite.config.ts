import { fileURLToPath, URL } from 'node:url'
// @ts-ignore
import { defineConfig } from 'vite'
import vueJsx from '@vitejs/plugin-vue-jsx'
import { viteMockServe } from 'vite-plugin-mock'  // 引入 mock 插件提供的方法
// @ts-ignore
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

// https://vitejs.dev/config/
export default defineConfig(( { command } )=> {
    // @ts-ignore
    // @ts-ignore
    // @ts-ignore
    return {

        plugins: [
            vue(),
            vueJsx(),
            // mock 配置项
            viteMockServe({
                mockPath: 'mock',
                localEnabled: command === 'serve',
            }),

        ],
        resolve: {
            alias: {
                // @ts-ignore
                '@': fileURLToPath(new URL('./src', import.meta.url))
            }
        },
// 部署应用时的基本 URL
        publicPath: process.env.NODE_ENV === "production" ? "./" : "/",
        // build时构建文件的目录 构建时传入 --no-clean 可关闭该行为
        outputDir: "dist",
        // build时放置生成的静态资源 (js、css、img、fonts) 的 (相对于 outputDir 的) 目录
        assetsDir: "home",
        // 指定生成的 index.html 的输出路径 (相对于 outputDir)。也可以是一个绝对路径。
        indexPath: "index.html",
        // 默认在生成的静态资源文件名中包含hash以控制缓存
        filenameHashing: true,
        // 问你是否使用eslint
        lintOnSave: false,
        server: {
            // open: true,    // 自动启动浏览器
            // https: true,
            proxy: {
                "/api": {
                    target: "http://localhost:8080/", // 正式
                    changeOrigin: true,
                },
                "/target": {
                    target: "http://localhost:9091/", // 正式
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/target/, ''),
                }
            },
        }
    }

})