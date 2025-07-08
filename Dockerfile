# Stage 1: Build the application
FROM adoptopenjdk:11-jdk-hotspot AS builder
WORKDIR /app

# Copy gradle files first for better caching
COPY gradle/ gradle/
COPY gradlew build.gradle ./

# Make the gradlew script executable
RUN chmod +x ./gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy the source code
COPY src/ src/

# Build the application
RUN ./gradlew build --no-daemon

# Stage 2: Create the runtime image
FROM adoptopenjdk:11-jre-hotspot
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]