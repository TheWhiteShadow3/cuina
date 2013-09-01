@echo off
rem Script:	start.bat
rem Author:	TheWhiteShadow
rem Version:	1.1

set CUINA_OPTS=%CUINA_OPTS% -Dcuina.game.path="../../CuinaEclipse/TestWorkspace/Test"
set CUINA_OPTS=%CUINA_OPTS% -Dcuina.plugin.path="plugins"

echo %CUINA_OPTS%
java -Xms512m %CUINA_OPTS% -jar cuina.engine.jar logfile=null

pause