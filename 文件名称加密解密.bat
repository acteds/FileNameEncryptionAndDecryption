@ECHO OFF
if "%~1"=="" echo ��ֹ�������иó���,����json��ѡ�����,����Ŀ¼ѡ����ܣ�&pause&exit
mode con lines=20 cols=90
color 0a
title %~n0
setlocal enabledelayedexpansion
cd /d "%~dp0"
cd src

rem ������:
rem javac -cp .;commons-io-2.8.0.jar;fastjson-1.2.75.jar -encoding UTF-8 -d . ���ɱ����ļ���¼���ܶ��ư�.java
rem javac -cp .;fastjson-1.2.75.jar -encoding UTF-8 -d . �ļ����Ƽ��ܽ���.java

rem ��������һ��б��
set filePath=%~1
if %filePath:~-1% == \ (
	set filePath=%filePath:~0,-1%
)

CHOICE /T 10 /D C /M ���ܻ��ǽ���(Y/N)�����˳�(C) /C YNC
goto a%ERRORLEVEL%




rem ����
:a1
rem �ص�,����json����
for /f "usebackq delims=" %%i in (`"java -cp .;fastjson-1.2.75.jar ����.�ļ����Ƽ���.�ļ����Ƽ��ܽ��� + 1 1 ""%filePath%"""`) do (
	echo %%i
	echo %%i|find /i "��Ŀ¼���м����ļ�" >nul && ( goto end1 )
	set lastline=%%i
)

echo �����ļ���¼

java -cp .;commons-io-2.8.0.jar;fastjson-1.2.75.jar ����.�ļ����Ƽ���.���ɱ����ļ���¼���ܶ��ư� true "%lastline%"

cd /d "%filePath%"
move �ļ���¼.txt ..\%~n1-�����ļ���¼.txt

goto end1




rem ����
:a2
echo Ĭ�������ϸ�ģʽ
java -cp .;fastjson-1.2.75.jar ����.�ļ����Ƽ���.�ļ����Ƽ��ܽ��� - true  "%filePath%"

goto end1

rem �˳�
:a3
goto end1




:end1

CHOICE /T 100 /D N /C YN /N /M 100����˳�,��Y/N��ǰ�˳�