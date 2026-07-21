@echo off
chcp 65001 >nul
echo ========================================
echo    停止数字健康平台所有服务
echo ========================================
echo.

echo [1/3] 关闭后端 Spring Boot...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%a 2>nul
    echo     已关闭 PID: %%a
)

echo [2/3] 关闭前端 Vite 开发服务器...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":5173" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%a 2>nul
    echo     已关闭 PID: %%a
)

echo [3/3] 关闭 Nginx...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":9091" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%a 2>nul
    echo     已关闭 PID: %%a
)

echo.
echo ========================================
echo    全部服务已停止
echo ========================================
echo.
pause
