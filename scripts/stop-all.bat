@echo off
chcp 65001 >nul 2>&1
title 中间件一键停止
setlocal enabledelayedexpansion

echo.
echo ============================================
echo       中间件一键停止脚本
echo ============================================
echo.

set MYSQL_PORT=3306
set REDIS_PORT=6379
set KAFKA_PORT=9092
set NACOS_PORT=8848

set KAFKA_HOME=C:\tools\kafka\kafka_2.13-3.9.2

echo [1/4] Nacos ...
netstat -ano | findstr ":%NACOS_PORT% " | findstr "LISTENING" >nul 2>&1
if %errorlevel%==0 (
    for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":%NACOS_PORT% " ^| findstr "LISTENING"') do (
        taskkill /PID %%p /F >nul 2>&1
    )
    echo       [已停止] Nacos
) else (
    echo       [未运行] Nacos
)

echo.
echo [2/4] Kafka ...
if exist "%KAFKA_HOME%\stop-kafka.bat" (
    call "%KAFKA_HOME%\stop-kafka.bat" >nul 2>&1
    echo       [已停止] Kafka
) else (
    netstat -ano | findstr ":%KAFKA_PORT% " | findstr "LISTENING" >nul 2>&1
    if %errorlevel%==0 (
        for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":%KAFKA_PORT% " ^| findstr "LISTENING"') do (
            taskkill /PID %%p /F >nul 2>&1
        )
        echo       [已停止] Kafka
    ) else (
        echo       [未运行] Kafka
    )
)

echo.
echo [3/4] Redis ...
tasklist | findstr /I "redis-server.exe" >nul 2>&1
if %errorlevel%==0 (
    taskkill /IM redis-server.exe /F >nul 2>&1
    echo       [已停止] Redis
) else (
    echo       [未运行] Redis
)

echo.
echo [4/4] MySQL ...
netstat -ano | findstr ":%MYSQL_PORT% " | findstr "LISTENING" >nul 2>&1
if %errorlevel%==0 (
    net stop MySQL >nul 2>&1
    if %errorlevel%==0 (
        echo       [已停止] MySQL
    ) else (
        echo       [停止失败] MySQL，可能需要管理员权限
    )
) else (
    echo       [未运行] MySQL
)

echo.
echo ============================================
echo       停止完成
echo ============================================
echo.
pause