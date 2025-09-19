@echo off

REM ============================================================================
REM Credit Simulator - Root Level Launcher (Test Requirement)
REM ============================================================================
REM Purpose: Backend Engineer Test - Meets MANDATORY requirement for executable
REM          file called 'credit_simulator' at the root of project directory
REM Requirements: Java 17+, Maven
REM ============================================================================

REM Navigate to project root
cd /d "%~dp0"

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 17+ and make sure it's in your PATH
    exit /b 1
)

REM Find and set JAVA_HOME to Java 17+ installation
echo Detecting Java installation...
java -version 2>&1 | findstr /C:"17" >nul || java -version 2>&1 | findstr /C:"21" >nul
if errorlevel 1 (
    echo Error: Java 17+ required but current Java version is:
    java -version
    exit /b 1
)

for /f "delims=" %%i in ('where java') do set JAVA_PATH=%%i
for %%i in ("%JAVA_PATH%") do set JAVA_HOME=%%~dpi..
echo Using JAVA_HOME: %JAVA_HOME%

REM Force Maven to use the correct Java version
set MAVEN_OPTS=-Djava.version=17

REM Check if Maven is available
call mvnw.cmd --version >nul 2>&1
if errorlevel 1 (
    echo Error: Maven wrapper not found or not executable
    echo Please ensure Maven is properly configured
    exit /b 1
)

REM Clean and build the application
echo Cleaning previous build...
if exist "target" rmdir /s /q target

echo Building the application...
call mvnw.cmd clean compile package -DskipTests -Dmaven.compiler.source=17 -Dmaven.compiler.target=17
if errorlevel 1 (
    echo Error: Build failed
    echo Trying to clean Maven cache and rebuild...
    call mvnw.cmd dependency:purge-local-repository -DreResolve=false -DactTransitively=false
    call mvnw.cmd clean compile package -DskipTests -Dmaven.compiler.source=17 -Dmaven.compiler.target=17
    if errorlevel 1 (
        echo Error: Build failed after cache cleanup
        echo Please check that you have Java 17+ properly installed and configured
        exit /b 1
    )
)

REM Run the Spring Boot application
echo Starting Credit Simulator...
java -jar target/credit_simulator-0.0.1-SNAPSHOT.jar %*
