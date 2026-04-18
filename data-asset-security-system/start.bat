@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM 数据资产安全系统 - 统一启动脚本 (Windows版本)
REM 版本: v1.0.4
REM 日期: 2025-06-16

title 数据资产安全系统启动脚本

set PROJECT_ROOT=%~dp0
set FRONTEND_DIR=%PROJECT_ROOT%frontend
set BACKEND_DIR=%PROJECT_ROOT%simple-backend
set FRONTEND_PID_FILE=%PROJECT_ROOT%frontend.pid
set BACKEND_PID_FILE=%PROJECT_ROOT%backend.pid
set FRONTEND_LOG=%PROJECT_ROOT%frontend.log
set BACKEND_LOG=%PROJECT_ROOT%backend.log

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║                                                              ║
echo ║   数据资产安全及分类分级管理系统 - 启动脚本                  ║
echo ║   Data Asset Security and Classification Management System   ║
echo ║                                                              ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

if "%1"=="" goto start
if "%1"=="start" goto start
if "%1"=="stop" goto stop
if "%1"=="restart" goto restart
if "%1"=="status" goto status
if "%1"=="logs" goto logs
if "%1"=="install" goto install
if "%1"=="help" goto help
goto unknown

:check_dependencies
echo 🔍 检查系统依赖...
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Node.js 未安装，请先安装 Node.js
    pause
    exit /b 1
)

where npm >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ npm 未安装，请先安装 npm
    pause
    exit /b 1
)

for /f "tokens=*" %%i in ('node --version') do set NODE_VERSION=%%i
for /f "tokens=*" %%i in ('npm --version') do set NPM_VERSION=%%i

echo ✅ Node.js 版本: %NODE_VERSION%
echo ✅ npm 版本: %NPM_VERSION%
goto :eof

:install_frontend_deps
echo 📦 安装前端依赖...
cd /d "%FRONTEND_DIR%"
if not exist "node_modules" (
    call npm install
    echo ✅ 前端依赖安装完成
) else (
    echo ✅ 前端依赖已存在
)
goto :eof

:install_backend_deps
echo 📦 安装后端依赖...
cd /d "%BACKEND_DIR%"
if not exist "node_modules" (
    call npm install
    echo ✅ 后端依赖安装完成
) else (
    echo ✅ 后端依赖已存在
)
goto :eof

:start_backend
echo 🚀 启动后端服务...

if exist "%BACKEND_PID_FILE%" (
    set /p BACKEND_PID=<%BACKEND_PID_FILE%
    tasklist /FI "PID eq !BACKEND_PID!" 2>nul | find "!BACKEND_PID!" >nul
    if !errorlevel! equ 0 (
        echo ⚠️  后端服务已在运行 (PID: !BACKEND_PID!)
        goto :eof
    ) else (
        del "%BACKEND_PID_FILE%"
    )
)

cd /d "%BACKEND_DIR%"
start /B node server.js > "%BACKEND_LOG%" 2>&1
for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq node.exe" /FO csv ^| find "node.exe"') do (
    set BACKEND_PID=%%~i
)
echo !BACKEND_PID! > "%BACKEND_PID_FILE%"

timeout /t 3 /nobreak >nul

tasklist /FI "PID eq !BACKEND_PID!" 2>nul | find "!BACKEND_PID!" >nul
if !errorlevel! equ 0 (
    echo ✅ 后端服务启动成功 (PID: !BACKEND_PID!)
    echo 📍 后端地址: http://localhost:8080
) else (
    echo ❌ 后端服务启动失败，请查看日志: %BACKEND_LOG%
    pause
    exit /b 1
)
goto :eof

:start_frontend
echo 🚀 启动前端服务...

if exist "%FRONTEND_PID_FILE%" (
    set /p FRONTEND_PID=<%FRONTEND_PID_FILE%
    tasklist /FI "PID eq !FRONTEND_PID!" 2>nul | find "!FRONTEND_PID!" >nul
    if !errorlevel! equ 0 (
        echo ⚠️  前端服务已在运行 (PID: !FRONTEND_PID!)
        goto :eof
    ) else (
        del "%FRONTEND_PID_FILE%"
    )
)

cd /d "%FRONTEND_DIR%"
start /B npm run dev > "%FRONTEND_LOG%" 2>&1
timeout /t 5 /nobreak >nul

for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq node.exe" /FO csv ^| find "node.exe"') do (
    set FRONTEND_PID=%%~i
)
echo !FRONTEND_PID! > "%FRONTEND_PID_FILE%"

tasklist /FI "PID eq !FRONTEND_PID!" 2>nul | find "!FRONTEND_PID!" >nul
if !errorlevel! equ 0 (
    echo ✅ 前端服务启动成功 (PID: !FRONTEND_PID!)
    echo 📍 前端地址: http://localhost:5173
) else (
    echo ❌ 前端服务启动失败，请查看日志: %FRONTEND_LOG%
    pause
    exit /b 1
)
goto :eof

:stop_services
echo 🛑 停止服务...

if exist "%FRONTEND_PID_FILE%" (
    set /p FRONTEND_PID=<%FRONTEND_PID_FILE%
    tasklist /FI "PID eq !FRONTEND_PID!" 2>nul | find "!FRONTEND_PID!" >nul
    if !errorlevel! equ 0 (
        taskkill /PID !FRONTEND_PID! /F >nul 2>&1
        echo ✅ 前端服务已停止 (PID: !FRONTEND_PID!)
    )
    del "%FRONTEND_PID_FILE%"
)

if exist "%BACKEND_PID_FILE%" (
    set /p BACKEND_PID=<%BACKEND_PID_FILE%
    tasklist /FI "PID eq !BACKEND_PID!" 2>nul | find "!BACKEND_PID!" >nul
    if !errorlevel! equ 0 (
        taskkill /PID !BACKEND_PID! /F >nul 2>&1
        echo ✅ 后端服务已停止 (PID: !BACKEND_PID!)
    )
    del "%BACKEND_PID_FILE%"
)
goto :eof

:check_status
echo 📊 服务状态检查...

if exist "%FRONTEND_PID_FILE%" (
    set /p FRONTEND_PID=<%FRONTEND_PID_FILE%
    tasklist /FI "PID eq !FRONTEND_PID!" 2>nul | find "!FRONTEND_PID!" >nul
    if !errorlevel! equ 0 (
        echo ✅ 前端服务运行中 (PID: !FRONTEND_PID!)
        echo 📍 地址: http://localhost:5173
    ) else (
        echo ❌ 前端服务未运行
        del "%FRONTEND_PID_FILE%"
    )
) else (
    echo ❌ 前端服务未运行
)

if exist "%BACKEND_PID_FILE%" (
    set /p BACKEND_PID=<%BACKEND_PID_FILE%
    tasklist /FI "PID eq !BACKEND_PID!" 2>nul | find "!BACKEND_PID!" >nul
    if !errorlevel! equ 0 (
        echo ✅ 后端服务运行中 (PID: !BACKEND_PID!)
        echo 📍 地址: http://localhost:8080
    ) else (
        echo ❌ 后端服务未运行
        del "%BACKEND_PID_FILE%"
    )
) else (
    echo ❌ 后端服务未运行
)
goto :eof

:show_logs
if "%2"=="frontend" goto show_frontend_logs
if "%2"=="backend" goto show_backend_logs
goto show_all_logs

:show_frontend_logs
echo 📋 前端日志:
if exist "%FRONTEND_LOG%" (
    type "%FRONTEND_LOG%"
) else (
    echo ❌ 前端日志文件不存在
)
goto :eof

:show_backend_logs
echo 📋 后端日志:
if exist "%BACKEND_LOG%" (
    type "%BACKEND_LOG%"
) else (
    echo ❌ 后端日志文件不存在
)
goto :eof

:show_all_logs
echo 📋 前端日志:
if exist "%FRONTEND_LOG%" (
    type "%FRONTEND_LOG%"
) else (
    echo ❌ 前端日志文件不存在
)
echo.
echo 📋 后端日志:
if exist "%BACKEND_LOG%" (
    type "%BACKEND_LOG%"
) else (
    echo ❌ 后端日志文件不存在
)
goto :eof

:start
call :check_dependencies
call :install_frontend_deps
call :install_backend_deps
call :start_backend
call :start_frontend
echo.
echo 🎉 系统启动完成！
echo 📍 前端地址: http://localhost:5173
echo 📍 后端地址: http://localhost:8080
echo 👤 默认账号: admin / admin123
echo.
echo 💡 提示: 使用 'start.bat stop' 停止服务
echo 💡 提示: 使用 'start.bat status' 查看状态
goto end

:stop
call :stop_services
echo ✅ 所有服务已停止
goto end

:restart
call :stop_services
timeout /t 2 /nobreak >nul
call :check_dependencies
call :start_backend
call :start_frontend
echo ✅ 服务重启完成
goto end

:status
call :check_status
goto end

:logs
call :show_logs
goto end

:install
call :check_dependencies
call :install_frontend_deps
call :install_backend_deps
echo ✅ 依赖安装完成
goto end

:help
echo 数据资产安全系统 - 启动脚本 (Windows版本)
echo.
echo 用法: start.bat [选项]
echo.
echo 选项:
echo     start       启动所有服务 (默认)
echo     stop        停止所有服务
echo     restart     重启所有服务
echo     status      查看服务状态
echo     logs        查看所有日志
echo     install     安装依赖
echo     help        显示此帮助信息
echo.
echo 示例:
echo     start.bat           # 启动所有服务
echo     start.bat stop      # 停止所有服务
echo     start.bat status    # 查看服务状态
echo.
echo 默认账号:
echo     用户名: admin
echo     密码: admin123
echo.
echo 访问地址:
echo     前端: http://localhost:5173
echo     后端: http://localhost:8080
echo.
goto end

:unknown
echo ❌ 未知选项: %1
echo.
call :help
goto end

:end
pause
