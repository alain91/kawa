@echo off

rem Ideas and some code re-used from Ant's ant.bat.

@setlocal

if "%KAWA_HOME%"=="" goto setDefaultKawaHome
:stripKawaHome
if not _%KAWA_HOME:~-1%==_\ goto endKawaHome
set KAWA_HOME=%KAWA_HOME:~0,-1%
goto stripKawaHome
:setDefaultKawaHome
set KAWA_HOME=%~dp0..
rem set THIS_SCRIPT2=%~dp$PATH:0
:endKawaHome

:checkJava
set _JAVACMD=%JAVACMD%
if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe
goto endcheckJava
:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java.exe
:endcheckJava

if "%KAWA_VERSION%" == "" set KAWA_VERSION=1.14.alain1
set KAWA_PATH=%KAWA_HOME%\kawa-%KAWA_VERSION%.jar
set EXTRA_PATH=;

"%_JAVACMD%" -classpath "%EXTRA_PATH%%KAWA_PATH%" kawa.repl %*%
