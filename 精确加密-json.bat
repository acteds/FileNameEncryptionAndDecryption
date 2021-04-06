@ECHO OFF
if "%~1"=="" echo 禁止单独运行该程序,请拖入json文件！&pause&exit
mode con lines=20 cols=90
color 0a
title %~n0
setlocal enabledelayedexpansion
cd /d "%~dp0"
cd src

rem 编译用:
rem javac -cp .;fastjson-1.2.75.jar -encoding UTF-8 -d . 通过JSON精确加密.java


rem 处理掉最后一个斜杠
set filePath=%~1
if %filePath:~-1% == \ (
	set filePath=%filePath:~0,-1%
)

rem 加密
rem 回调
for /f "usebackq delims=" %%i in (`"java -cp .;fastjson-1.2.75.jar 工具.文件名称加密.通过JSON精确加密 ""%filePath%"""`) do (
	echo %%i
	echo %%i|find /i "该目录已加密" >nul && ( goto end1 )
	echo %%i|find /i "json文件不存在" >nul && ( goto end1 )
	echo %%i|find /i "请确保json与要精确加密的" >nul && ( goto end1 )
	set lastline=%%i
)

echo 生成文件记录

java -cp .;commons-io-2.8.0.jar;fastjson-1.2.75.jar 工具.文件名称加密.生成本地文件记录加密定制版 false "%filePath%"

cd /d "!lastline!"
cd ..
set father=!lastline:%cd%\=!
cd /d "!lastline!"
move 文件记录.txt ..\!father!-加密文件记录.txt

goto end1
:end1
CHOICE /T 100 /D N /C YN /N /M 100秒后退出,按Y/N提前退出