@echo off

REM Check if Gradle is installed in the system's PATH
where gradle >nul 2>nul
if errorlevel 1 (
    echo Gradle could not be found in the system's PATH.
    REM Check if Gradle Wrapper is present in the current directory
    if exist gradlew.bat (
        echo Using Gradle Wrapper in the current directory.
        set gradleCommand=gradlew.bat
    ) else (
        echo Gradle could not be found. Please install Gradle or use a Gradle Wrapper.
        exit /b 1
    )
) else (
    echo Using Gradle from the system's PATH.
    set gradleCommand=gradle
)

REM Execute the Gradle build command with the specified output directory
%gradleCommand% build -x test

echo Build completed.

