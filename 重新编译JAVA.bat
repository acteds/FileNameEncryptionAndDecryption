@ECHO OFF
mode con lines=20 cols=90
color 0a
title %~n0
setlocal enabledelayedexpansion
cd /d "%~dp0"
cd src

javac -cp .;commons-io-2.8.0.jar;fastjson-1.2.75.jar -encoding UTF-8 -d . 生成本地文件记录加密定制版.java
javac -cp .;fastjson-1.2.75.jar -encoding UTF-8 -d . 文件名称加密解密.java
javac -cp .;fastjson-1.2.75.jar -encoding UTF-8 -d . 通过JSON精确加密.java
echo 已完成编译&pause