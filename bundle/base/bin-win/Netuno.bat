@ECHO OFF

cd %~dp0

set SCRIPT="%TEMP%\%RANDOM%-%RANDOM%-%RANDOM%-%RANDOM%.vbs"

echo Set oWS = WScript.CreateObject("WScript.Shell") >> %SCRIPT%
echo sLinkFile = "%USERPROFILE%\Desktop\Netuno.lnk" >> %SCRIPT%
echo Set oLink = oWS.CreateShortcut(sLinkFile) >> %SCRIPT%
echo oLink.IconLocation = "%~dp0\img\netuno.ico" >> %SCRIPT%
echo oLink.TargetPath ="%~dp0\Netuno.bat" >> %SCRIPT%
echo oLink.Save >> %SCRIPT%

cscript /nologo %SCRIPT%

CLS

ECHO. (You can stop Netuno at any time pressing left CTRL+C)
ECHO.

ECHO 1.Start Netuno normally
ECHO 2.Start Netuno App
ECHO 3.Create Netuno App
ECHO 4.Set Netuno License
ECHO 5.Start Netuno with commandline
ECHO 6.Open Netuno folder
ECHO 7.Update Netuno
ECHO.

CHOICE /C 1234567 /M "Choose an option: "

IF ERRORLEVEL 7 GOTO UpdateNetuno
IF ERRORLEVEL 6 GOTO OpenDir
IF ERRORLEVEL 5 GOTO StartCmd
IF ERRORLEVEL 4 GOTO SetNetunoLicense
IF ERRORLEVEL 3 GOTO CreateNetunoApp
IF ERRORLEVEL 2 GOTO StartNetunoApp
IF ERRORLEVEL 1 GOTO StartNetuno

:UpdateNetuno
call update.bat

:OpenDir
start /I /MAX .. 
GOTO End

:StartCmd
cd ..
start "Netuno with commandline"
GOTO End

:StartNetuno
call start.bat

:StartNetunoApp
call app-start.bat

:CreateNetunoApp
call app-create.bat

:SetNetunoLicense
call license.bat

:End