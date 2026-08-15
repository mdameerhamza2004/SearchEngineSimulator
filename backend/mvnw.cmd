@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM Begin all REM://
@REM Maven Wrapper script for Windows
@REM Required ENV vars:
@REM   JAVA_HOME - location of a JDK home dir

@echo off
@setlocal

set ERROR_CODE=0

@REM To isolate internal variables from possible post scripts, we use another setlocal
@setlocal

@REM ==== START VALIDATION ====
if NOT "%JAVA_HOME%"=="" goto OkJHome

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

@REM ==== END VALIDATION ====

:init

@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
IF NOT "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir

set EXEC_DIR=%CD%
set WDIR=%EXEC_DIR%
:findBaseDir
IF EXIST "%WDIR%"\.mvn goto baseDirFound
cd ..
IF "%WDIR%"=="%CD%" goto baseDirNotFound
set WDIR=%CD%
goto findBaseDir

:baseDirFound
set MAVEN_PROJECTBASEDIR=%WDIR%
cd "%EXEC_DIR%"
goto endDetectBaseDir

:baseDirNotFound
set MAVEN_PROJECTBASEDIR=%EXEC_DIR%
cd "%EXEC_DIR%"

:endDetectBaseDir

IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties" goto endReadProperties

@REM Read the maven-wrapper.properties file
set WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties
for /F "usebackq tokens=1,* delims==" %%A in ("%WRAPPER_PROPERTIES%") do (
    if "%%A"=="distributionUrl" set DOWNLOAD_URL=%%B
    if "%%A"=="wrapperUrl" set WRAPPER_URL=%%B
)

:endReadProperties

IF "%DOWNLOAD_URL%"=="" (
    echo Error: distributionUrl not found in %WRAPPER_PROPERTIES% >&2
    goto error
)

@REM Maven download location
set MAVEN_HOME_PARENT=%USERPROFILE%\.m2\wrapper\dists
@REM Derive a directory name from the URL
for %%F in ("%DOWNLOAD_URL%") do set DIST_NAME=%%~nF
set MAVEN_HOME=%MAVEN_HOME_PARENT%\%DIST_NAME%

IF EXIST "%MAVEN_HOME%\bin\mvn.cmd" goto runMaven

echo Downloading Maven from %DOWNLOAD_URL% ...
mkdir "%MAVEN_HOME_PARENT%" 2>nul

@REM Download using PowerShell
powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%MAVEN_HOME_PARENT%\%DIST_NAME%.zip' }"
if %ERRORLEVEL% neq 0 goto error

echo Extracting Maven...
powershell -Command "Expand-Archive -Path '%MAVEN_HOME_PARENT%\%DIST_NAME%.zip' -DestinationPath '%MAVEN_HOME_PARENT%' -Force"
if %ERRORLEVEL% neq 0 goto error

@REM The zip extracts to a subdirectory like apache-maven-3.9.9
for /D %%G in ("%MAVEN_HOME_PARENT%\apache-maven-*") do (
    if NOT "%%G"=="%MAVEN_HOME%" (
        move "%%G" "%MAVEN_HOME%" >nul 2>nul
    )
)

del "%MAVEN_HOME_PARENT%\%DIST_NAME%.zip" 2>nul

:runMaven

set MAVEN_CMD_LINE_ARGS=%*

"%MAVEN_HOME%\bin\mvn.cmd" %MAVEN_CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

cmd /C exit /B %ERROR_CODE%
