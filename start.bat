@echo off
chcp 65001 >nul
echo ========================================
echo    启动数字健康平台
echo ========================================
echo.

REM ========== 1. 关闭上一次启动的进程 ==========
echo [1/4] 关闭已有的进程...

REM 关闭后端进程（Spring Boot 默认端口 8080）
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%a 2>nul
)

REM 关闭前端 Vite 进程（默认端口 5173）
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":5173" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%a 2>nul
)

REM 关闭 Nginx 进程（端口 9091）
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":9091" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%a 2>nul
)

echo     已有进程已关闭
echo.

REM ========== 2. 启动 Nginx ==========
echo [2/4] 启动 Nginx...
cd /d "%~dp0"
if exist "nginx\nginx.exe" (
    start "Nginx" nginx\nginx.exe
    echo     Nginx 启动成功 (端口: 9091)
) else (
    echo     警告: 未找到 nginx\nginx.exe
)
echo.

REM ========== 3. 启动前端 ==========
echo [3/4] 启动前端 Vue 开发服务器...
start "Frontend" cmd /k "cd /d %~dp0digit_healthcarevue && npm run dev"
echo     前端正在启动...
echo.

REM 等待前端启动
timeout /t 5 /nobreak >nul

REM ========== 4. 启动后端 ==========
echo [4/4] 启动后端 Spring Boot...
start "Backend" cmd /k "cd /d %~dp0 && mvn spring-boot:run"
echo     后端正在启动...
echo.

REM ========== 完成 ==========
echo ========================================
echo    全部启动完成！
echo ========================================
echo.
echo    前端地址: http://localhost:5173
echo    后端地址: http://localhost:8080
echo    Nginx地址: http://localhost:9091
echo.
echo    提示: 各窗口会独立打开，请等待启动完成
echo    关闭窗口即可停止对应服务
echo.
pause
