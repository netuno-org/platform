@echo off

cd ..

:start
SET /p _inputname=Enter App Name:
IF "%_inputname%"=="" GOTO :empty_name
java -Dfile.encoding="UTF-8" -jar netuno.jar server app="%_inputname%"
:empty_name
ECHO No app found with the inserted name.
goto start