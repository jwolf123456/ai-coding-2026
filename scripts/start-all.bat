@echo off
chcp 65001 >nul 2>&1
title 中间件一键启动
setlocal enabledelayedexpansion

echo.
echo ============================================
echo       中间件一键启动脚本
echo ============================================
echo.

set MYSQL_PORT=3306
set REDIS_PORT=6379
set KAFKA_PORT=9092
set NACOS_HTTP_PORT=8080
set NACOS_GRPC_PORT=8848

set KAFKA_HOME=C:\tools\kafka\kafka_2.13-3.9.2
set NACOS_HOME=C:\Users\Administrator\ai-infra\nacos\standalone\nacos-3.3.0

echo [1/4] MySQL ...
netstat -ano | findstr ":%MYSQL_PORT% " | findstr "LISTENING" >nul 2>&1
if %errorlevel%==0 (
    echo       [已启动] MySQL 已在运行，端口 %MYSQL_PORT%
) else (
    net start MySQL >nul 2>&1
    set mysql_ready=0
    for /L %%i in (1,1,10) do (
        if !mysql_ready!==0 (
            timeout /t 1 /nobreak >nul
            netstat -ano | findstr ":%MYSQL_PORT% " | findstr "LISTENING" >nul 2>&1
            if !errorlevel!==0 (
                set mysql_ready=1
                echo       [启动成功] MySQL，端口 %MYSQL_PORT%
            )
        )
    )
    if !mysql_ready!==0 echo       [启动失败] MySQL，请检查 Windows 服务是否已安装
)

echo.
echo [2/4] Redis ...
netstat -ano | findstr ":%REDIS_PORT% " | findstr "LISTENING" >nul 2>&1
if %errorlevel%==0 (
    echo       [已启动] Redis 已在运行，端口 %REDIS_PORT%
) else (
    where redis-server >nul 2>&1
    if !errorlevel! neq 0 (
        echo       [跳过] 未找到 redis-server，请确认已安装并加入 PATH
    ) else (
        start "" /B redis-server >nul 2>&1
        set redis_ready=0
        for /L %%i in (1,1,10) do (
            if !redis_ready!==0 (
                timeout /t 1 /nobreak >nul
                netstat -ano | findstr ":%REDIS_PORT% " | findstr "LISTENING" >nul 2>&1
                if !errorlevel!==0 (
                    set redis_ready=1
                    echo       [启动成功] Redis，端口 %REDIS_PORT%
                )
            )
        )
        if !redis_ready!==0 echo       [启动失败] Redis，redis-server 启动异常
    )
)

echo.
echo [3/4] Kafka (KRaft) ...
netstat -ano | findstr ":%KAFKA_PORT% " | findstr "LISTENING" >nul 2>&1
if %errorlevel%==0 (
    echo       [已启动] Kafka 已在运行，端口 %KAFKA_PORT%
) else (
    if exist "%KAFKA_HOME%\start-kafka.bat" (
        start "" /B "%KAFKA_HOME%\start-kafka.bat" >nul 2>&1
        echo       [正在启动] Kafka，等待端口就绪...
        set kafka_ready=0
        for /L %%i in (1,1,15) do (
            if !kafka_ready!==0 (
                timeout /t 2 /nobreak >nul
                netstat -ano | findstr ":%KAFKA_PORT% " | findstr "LISTENING" >nul 2>&1
                if !errorlevel!==0 (
                    set kafka_ready=1
                    echo       [启动成功] Kafka，端口 %KAFKA_PORT%
                )
            )
        )
        if !kafka_ready!==0 echo       [启动失败] Kafka，请检查 start-kafka.bat
    ) else (
        echo       [跳过] 未找到 %KAFKA_HOME%\start-kafka.bat
    )
)

echo.
echo [4/4] Nacos 3.3.0 (Standalone) ...
netstat -ano | findstr ":%NACOS_HTTP_PORT% " | findstr "LISTENING" >nul 2>&1
if %errorlevel%==0 (
    echo       [已启动] Nacos 已在运行，端口 %NACOS_HTTP_PORT%/%NACOS_GRPC_PORT%
) else (
    if exist "%NACOS_HOME%\bin\startup.cmd" (
        pushd "%NACOS_HOME%\bin"
        start "" /B cmd /c "call startup.cmd -m standalone" >nul 2>&1
        popd
        echo       [正在启动] Nacos，等待端口就绪...
        set nacos_ready=0
        for /L %%i in (1,1,20) do (
            if !nacos_ready!==0 (
                timeout /t 2 /nobreak >nul
                netstat -ano | findstr ":%NACOS_HTTP_PORT% " | findstr "LISTENING" >nul 2>&1
                if !errorlevel!==0 (
                    set nacos_ready=1
                    echo       [启动成功] Nacos，HTTP:%NACOS_HTTP_PORT% gRPC:%NACOS_GRPC_PORT%
                )
            )
        )
        if !nacos_ready!==0 echo       [启动失败] Nacos，请检查 startup.cmd
    ) else (
        echo       [跳过] 未找到 %NACOS_HOME%\bin\startup.cmd
    )
)

echo.
echo ============================================
echo       启动完成，服务状态汇总：
echo ============================================
echo.

set all_ok=1

netstat -ano | findstr ":%MYSQL_PORT% " | findstr "LISTENING" >nul 2>&1
if %errorlevel%==0 (
    echo   [OK] MySQL    :%MYSQL_PORT%
) else (
    echo   [X]  MySQL    :%MYSQL_PORT%  未运行
    set all_ok=0
)

netstat -ano | findstr ":%REDIS_PORT% " | findstr "LISTENING" >nul 2>&1
if %errorlevel%==0 (
    echo   [OK] Redis    :%REDIS_PORT%
) else (
    echo   [X]  Redis    :%REDIS_PORT%  未运行
    set all_ok=0
)

netstat -ano | findstr ":%KAFKA_PORT% " | findstr "LISTENING" >nul 2>&1
if %errorlevel%==0 (
    echo   [OK] Kafka    :%KAFKA_PORT%
) else (
    echo   [X]  Kafka    :%KAFKA_PORT%  未运行
    set all_ok=0
)

netstat -ano | findstr ":%NACOS_PORT% " | findstr "LISTENING" >nul 2>&1
if %errorlevel%==0 (
    echo   [OK] Nacos    :%NACOS_PORT%
) else (
    echo   [X]  Nacos    :%NACOS_PORT%  未运行
    set all_ok=0
)

echo.
if !all_ok!==1 (
    echo   全部服务已就绪！
) else (
    echo   部分服务未启动，请检查上方日志。
)

echo.
echo   Nacos 控制台: http://localhost:%NACOS_PORT%/nacos
echo.
pause