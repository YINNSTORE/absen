@echo off
setlocal
set "APP_HOME=%~dp0"
set "GRADLE_VERSION=8.7"
if defined GRADLE_USER_HOME (set "CACHE_DIR=%GRADLE_USER_HOME%\wrapper\dists\manual-gradle-%GRADLE_VERSION%") else (set "CACHE_DIR=%USERPROFILE%\.gradle\wrapper\dists\manual-gradle-%GRADLE_VERSION%")
set "GRADLE_HOME=%CACHE_DIR%\gradle-%GRADLE_VERSION%"

if exist "%GRADLE_HOME%\bin\gradle.bat" (
  call "%GRADLE_HOME%\bin\gradle.bat" %*
  exit /b %ERRORLEVEL%
)

where gradle >nul 2>nul
if %ERRORLEVEL% EQU 0 (
  call gradle %*
  exit /b %ERRORLEVEL%
)

where curl >nul 2>nul
if not %ERRORLEVEL% EQU 0 (
  echo Butuh curl atau instalasi Gradle untuk menjalankan project ini.
  exit /b 1
)

if not exist "%CACHE_DIR%" mkdir "%CACHE_DIR%"
curl -fL --retry 3 --connect-timeout 20 "https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip" -o "%CACHE_DIR%\gradle-%GRADLE_VERSION%-bin.zip"
if not %ERRORLEVEL% EQU 0 exit /b 1

powershell -NoProfile -Command "Expand-Archive -Force '%CACHE_DIR%\gradle-%GRADLE_VERSION%-bin.zip' '%CACHE_DIR%'"
if not exist "%GRADLE_HOME%\bin\gradle.bat" exit /b 1
call "%GRADLE_HOME%\bin\gradle.bat" %*
exit /b %ERRORLEVEL%
