@ECHO OFF
if "%~1"=="" echo 禁止单独运行该程序！&pause&exit
mode con lines=20 cols=90
color 0a
title %~n0
setlocal enabledelayedexpansion
cd /d "%~dp0"
cd src

rem 编译用:
rem javac -cp .;commons-io-2.8.0.jar;fastjson-1.2.75.jar -encoding UTF-8 -d . 生成本地文件记录加密定制版.java
rem javac -cp .;fastjson-1.2.75.jar -encoding UTF-8 -d . 文件名称加密解密.java

rem 处理掉最后一个斜杠
set filePath=%~1
if %filePath:~-1% == \ (
	set filePath=%filePath:~0,-1%
)

set /p fileNO=请输入最后的文件序列
set /p folderNO=请输入最后的文件夹序列
rem 加密
:a1
rem 回调,接收json名称
for /f "usebackq delims=" %%i in (`"java -cp .;fastjson-1.2.75.jar 工具.文件名称加密.文件名称加密解密 + %fileNO% %folderNO% ""%filePath%"""`) do (
	echo %%i
	echo %%i|find /i "该目录已有加密文件" >nul && ( goto end1 )
	set lastline=%%i
)

echo 生成文件记录

java -cp .;commons-io-2.8.0.jar;fastjson-1.2.75.jar 工具.文件名称加密.生成本地文件记录加密定制版 true "%lastline%"

cd /d "%filePath%"
move 文件记录.txt ..\%~n1-加密文件记录.txt

CHOICE /T 100 /D N /C YN /N /M 100秒后退出,按Y/N提前退出