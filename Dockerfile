# Stage 1: Build the application
FROM adoptopenjdk:11-jdk-hotspot AS builder
WORKDIR /app

# Install Gradle directly
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-7.6.4-bin.zip && \
    unzip gradle-7.6.4-bin.zip && \
    mv gradle-7.6.4 /opt/gradle && \
    rm gradle-7.6.4-bin.zip

ENV PATH="/opt/gradle/bin:${PATH}"

# Copy build files
COPY build.gradle ./
COPY src/ src/

# Build the application
RUN gradle build --no-daemon

# Stage 2: Create the runtime image
FROM adoptopenjdk:11-jre-hotspot
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]