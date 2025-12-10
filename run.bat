java -jar ./weather-1.0.0.jar %*@echo off
setlocal

:: 检查Java是否安装
echo 正在检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未检测到Java运行环境，请先安装Java。
    pause
    exit /b 1
)

:: 检查Java版本是否为17
echo 正在检查Java版本...
for /f "tokens=3 delims= " %%v in ('java -version 2^>^&1 ^| findstr "version"') do (
    set JAVA_VERSION=%%v
)
set JAVA_VERSION=%JAVA_VERSION:"=%

:: 提取主版本号
for /f "delims=." %%i in ("%JAVA_VERSION%") do (
    set MAIN_VERSION=%%i
)

if not "%MAIN_VERSION%"=="17" (
    echo 警告: 检测到Java版本 %MAIN_VERSION%，建议使用Java 17。
    echo 继续运行可能会出现问题。
    choice /m "是否继续运行"
    if errorlevel 2 (
        echo 已取消运行。
        pause
        exit /b 1
    )
) else (
    echo Java版本检查通过: %JAVA_VERSION%
)

:: 启动JAR文件
echo 正在启动 weather-1.0.0.jar...
start "" javaw -jar ./weather-1.0.0.jar

:: 等待几秒让服务启动
echo 等待服务启动...
timeout /t 5 /nobreak >nul

:: 使用默认浏览器打开网页
echo 正在打开网页...
start "" http://localhost:18080/weather/static/index.html

echo 服务已启动，网页已在浏览器中打开。
echo 如需关闭服务，请关闭新打开的命令行窗口。
pause