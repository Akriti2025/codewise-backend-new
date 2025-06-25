# Use an official Maven image to build the application
FROM maven:3.9.6-eclipse-temurin-17-alpine
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Use a smaller OpenJDK image for the final runtime
FROM openjdk:17-slim-buster
WORKDIR /app
COPY --from=build /app/target/codewise-backend-0.0.1-SNAPSHOT.jar codewise-backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "codewise-backend.jar"]