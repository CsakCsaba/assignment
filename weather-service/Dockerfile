FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY target/weather-service.jar app.jar
EXPOSE 9080
ENTRYPOINT ["java", "-jar", "app.jar"]