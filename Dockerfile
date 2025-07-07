# Stage 1: Build the application
# Use the specific Maven base image suggested by Render for building
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build # <-- CHANGED THIS LINE
WORKDIR /app
# Copy Maven wrapper and pom.xml to allow Maven to download dependencies
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Download project dependencies - this speeds up subsequent builds
RUN ./mvnw dependency:go-offline
# Copy the rest of the source code
COPY src ./src
# Build the Spring Boot application (generate the JAR)
RUN ./mvnw clean install -DskipTests

# Stage 2: Create the final runnable image
# Use a smaller Alpine-based JRE image for the final application runtime
FROM eclipse-temurin:17-jre-alpine # <-- CHANGED THIS LINE (for consistency and smaller image size)
WORKDIR /app
# Copy the built JAR file from the 'build' stage to the final image
COPY --from=build /app/target/*.jar app.jar
# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8080
# Command to run your Spring Boot application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]