@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo   CloudExify Guessing Game - Builder ^& Launcher
echo ===================================================
echo.

:: Detect java / javac on system path
where javac >nul 2>nul
if %errorlevel% equ 0 (
    set JAVAC_CMD=javac
    set JAVA_CMD=java
    echo Detected Java compiler in system PATH.
) else (
    :: Fallback to detected JDK 26 installation path
    set JDK_PATH=C:\Program Files\Java\jdk-26.0.1\bin
    if exist "!JDK_PATH!\javac.exe" (
        set JAVAC_CMD="!JDK_PATH!\javac.exe"
        set JAVA_CMD="!JDK_PATH!\java.exe"
        echo Detected Java installation at C:\Program Files\Java\jdk-26.0.1
    ) else (
        echo ERROR: Java compiler (javac) not found in system PATH or C:\Program Files\Java\jdk-26.0.1.
        echo Please ensure the Java Development Kit (JDK) is installed.
        echo.
        pause
        exit /b 1
    )
)

echo.
echo Compiling project source files...
if not exist out mkdir out
%JAVAC_CMD% -d out src/com/cloudexify/guessgame/*.java
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b %errorlevel%
)
echo Compilation successful. Compiled classes outputted to 'out' directory.
echo.

echo ==========================================
echo SELECT GAME MODE TO RUN:
echo ==========================================
echo [1] Graphical Dashboard (GUI) [Default]
echo [2] Retro Terminal (Console)
echo ==========================================
set /p choice="Enter option (1 or 2): "

if "%choice%"=="2" (
    echo Launching Console Mode...
    %JAVA_CMD% -cp out com.cloudexify.guessgame.Main --console
) else (
    echo Launching Graphical Mode...
    %JAVA_CMD% -cp out com.cloudexify.guessgame.Main --gui
)

endlocal
