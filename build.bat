@echo off
set PATH=C:\Users\byy90\AppData\Local\Microsoft\WinGet\Packages\OpenJS.NodeJS.LTS_Microsoft.Winget.Source_8wekyb3d8bbwe\node-v24.16.0-win-x64;%PATH%
cd /d D:\personal-blog\client
call npm run build
exit /b %ERRORLEVEL%
