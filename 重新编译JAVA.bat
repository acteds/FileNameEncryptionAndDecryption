@ECHO OFF
mode con lines=20 cols=90
color 0a
title %~n0
setlocal enabledelayedexpansion
cd /d "%~dp0"
cd src

javac -cp .;commons-io-2.8.0.jar;fastjson-1.2.75.jar -encoding UTF-8 -d . ���ɱ����ļ���¼���ܶ��ư�.java
javac -cp .;fastjson-1.2.75.jar -encoding UTF-8 -d . �ļ����Ƽ��ܽ���.java
javac -cp .;fastjson-1.2.75.jar -encoding UTF-8 -d . ͨ��JSON��ȷ����.java
echo ����ɱ���&pause