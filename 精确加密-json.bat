@ECHO OFF
if "%~1"=="" echo ��ֹ�������иó���,������json�ļ���&pause&exit
mode con lines=20 cols=90
color 0a
title %~n0
setlocal enabledelayedexpansion
cd /d "%~dp0"
cd src

rem ������:
rem javac -cp .;fastjson-1.2.75.jar -encoding UTF-8 -d . ͨ��JSON��ȷ����.java


rem ��������һ��б��
set filePath=%~1
if %filePath:~-1% == \ (
	set filePath=%filePath:~0,-1%
)

rem ����
rem �ص�
for /f "usebackq delims=" %%i in (`"java -cp .;fastjson-1.2.75.jar ����.�ļ����Ƽ���.ͨ��JSON��ȷ���� ""%filePath%"""`) do (
	echo %%i
	echo %%i|find /i "��Ŀ¼�Ѽ���" >nul && ( goto end1 )
	echo %%i|find /i "json�ļ�������" >nul && ( goto end1 )
	echo %%i|find /i "��ȷ��json��Ҫ��ȷ���ܵ�" >nul && ( goto end1 )
	set lastline=%%i
)

echo �����ļ���¼

java -cp .;commons-io-2.8.0.jar;fastjson-1.2.75.jar ����.�ļ����Ƽ���.���ɱ����ļ���¼���ܶ��ư� false "%filePath%"

cd /d "!lastline!"
cd ..
set father=!lastline:%cd%\=!
cd /d "!lastline!"
move �ļ���¼.txt ..\!father!-�����ļ���¼.txt

goto end1
:end1
CHOICE /T 100 /D N /C YN /N /M 100����˳�,��Y/N��ǰ�˳�