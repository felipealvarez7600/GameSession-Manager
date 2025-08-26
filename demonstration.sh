#!/bin/bash
source link.env
git clone "$LINK" /project
cd ./project || exit
git checkout 0.2.2
./gradlew clean build
./gradlew clean build
cd ./src/main/docker || exit
docker-compose build
docker-compose start