@echo off
setlocal
FOR /F "tokens=*" %%i in ('type link.env') do SET %%i
git clone "%LINK%" project
cd project
if errorlevel 1 exit /b
git checkout 0.2.5
call gradlew clean build
call gradlew clean build
cd src\main\docker
docker-compose build
docker-compose up
pause
endlocal
