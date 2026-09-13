# Multi-stage Dockerfile for Wellness360 Task Management System (Java 25)
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9090
ENV PORT=9090
ENTRYPOINT ["java", "-jar", "app.jar"]
