@ECHO OFF
if "%~1"=="" echo 禁止单独运行该程序,请拖入json文件！&pause&exit
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
rem 解密
echo 启用相对模式
java -cp .;fastjson-1.2.75.jar 工具.文件名称加密.文件名称加密解密 - false  "%filePath%"

CHOICE /T 100 /D N /C YN /N /M 100秒后退出,按Y/N提前退出