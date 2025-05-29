FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY target/event-service.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]