# Stage 1: Build the application
# Use a JDK image to compile and package your Spring Boot application
FROM eclipse-temurin:17-jdk-jammy AS build

# Set the working directory inside the container
WORKDIR /app

# Copy Maven wrapper and pom.xml to allow Maven to download dependencies
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download project dependencies - this speeds up subsequent builds
# by caching dependencies if pom.xml hasn't changed.
# -DskipTests is used here to avoid running tests during the Docker build stage.
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the Spring Boot application (generate the JAR)
# -DskipTests is important to avoid running tests during the Docker build,
# as tests should ideally be run in CI/CD before Dockerizing.
RUN ./mvnw clean install -DskipTests

# Stage 2: Create the final runnable image
# Use a JRE (Java Runtime Environment) image for the final image.
# It's smaller than a JDK image, ideal for production.
FROM eclipse-temurin:17-jre-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the 'build' stage to the final image
# Ensure the JAR name matches what your Spring Boot build produces.
# It's usually target/your-artifactid-your-version.jar
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8080

# Command to run your Spring Boot application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]