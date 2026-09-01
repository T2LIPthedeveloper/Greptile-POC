FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY admin-service/build/libs/admin-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
