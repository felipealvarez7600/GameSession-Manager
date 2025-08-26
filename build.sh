#!/bin/bash

# Check if Gradle Wrapper is present in the current directory
if [ -f gradlew ]; then
    echo "Using Gradle Wrapper in the current directory."
    gradleCommand="./gradlew"
elif [ -f gradlew.bat ]; then
    echo "Using Gradle Wrapper in the current directory."
    gradleCommand="./gradlew.bat"
else
    # Check if Gradle is installed in the system's PATH
    if ! command -v gradle &> /dev/null; then
        echo "Gradle could not be found. Please install Gradle or use a Gradle Wrapper."
        exit 1
    else
        echo "Using Gradle from the system's PATH."
        gradleCommand="gradle"
    fi
fi


# Execute the Gradle build command with the specified output directory
$gradleCommand build -x test

echo "Build completed."
