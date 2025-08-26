FROM openjdk:17-alpine
COPY build/libs/*.jar app.jar
EXPOSE 3030
COPY frontend frontend/
CMD java -jar app.jar