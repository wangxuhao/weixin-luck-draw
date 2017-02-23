@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

:: SHELL_ROOT - Location(Path) of this batch file
pushd "%~dp0"
set SHELL_ROOT=!cd!
popd
:: REDIST_ROOT - The root path of Yigo-redist
set REDIST_ROOT=!SHELL_ROOT!/../..
pushd "!REDIST_ROOT!"
set REDIST_ROOT=!cd!
popd

:: JAVA_HOME
if "%JAVA_HOME%"=="" (
    set JAVA_HOME=!REDIST_ROOT!\infrastructure\jdk\default
    echo ^>^>^> Environment variable [JAVA_HOME] is empty, use the defaule value [!JAVA_HOME!]
)

:: Call rhino-shell
set XP_PATCH_KILL_BY_CMDLINE=!REDIST_ROOT!\etc\runner\vbs\killByStamp.vbs
set CLASSPATH=!REDIST_ROOT!\etc\runner\app-js;!REDIST_ROOT!\etc\runner\rhino-shell\lib-js;!REDIST_ROOT!\etc\runner\rhino-shell\lib-java\*;
set CMDLINE="!JAVA_HOME!\bin\java" -cp "!CLASSPATH!" net.thinkbase.shell.rhino.Main
echo !CMDLINE!
call !CMDLINE!

ENDLOCAL
