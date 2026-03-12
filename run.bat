@echo off
REM Smart Plagiarism Detector - Windows Batch Runner
REM This script compiles and runs the project without Maven

cd /d "%~dp0"

echo.
echo ====================================================
echo Smart Plagiarism Detector - Starter
echo ====================================================
echo.

REM Create output directory
if not exist "target\classes" mkdir "target\classes"
if not exist "target\lib" mkdir "target\lib"

REM Download dependencies if needed
echo Checking dependencies...
if not exist "target\lib\*" (
    echo Creating lib directory structure...
)

REM Compile project
echo.
echo Compiling source code...
javac -d target\classes -encoding UTF-8 ^
    src\main\java\com\plagiarism\*.java ^
    src\main\java\com\plagiarism\engine\*.java ^
    src\main\java\com\plagiarism\models\*.java ^
    src\main\java\com\plagiarism\ui\*.java ^
    src\main\java\com\plagiarism\utils\*.java ^
    src\main\java\com\plagiarism\ml\*.java 2>NUL

if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed!
    echo Maven might not be installed. Please install Maven or Java compiler.
    echo.
    echo Visit: https://maven.apache.org/download.cgi
    echo.
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo ====================================================
echo Launching application...
echo ====================================================
echo.

REM Try to run with Java
java -cp "target\classes" com.plagiarism.PlagiarismDetectorMain

if errorlevel 1 (
    echo.
    echo Application launch failed. Check error above.
    pause
    exit /b 1
)

pause
