# Weather Forecast Web Application

A Spring Boot web application that provides weather forecasts from multiple free APIs.

## Features

- Fetches weather data from two free APIs (wttr.in and GoWeather)
- Supports 1, 3, or 7-day forecasts
- Input validation for city names and forecast duration
- AWS-styled responsive web interface
- Shows actual error messages when APIs are unavailable

## Requirements

- JDK 11
- Gradle 7.6.4 (included via wrapper)
- Docker (optional, for containerized deployment)

## Running the Application

### Using Gradle

```bash
./gradlew bootRun
```

### Using Docker

Build and run the Docker container:

```bash
# Build the Docker image
docker build -t weather-app .

# Run the container
docker run -p 8080:8080 weather-app
```

### Using Docker Compose

```bash
# Build and run with Docker Compose
docker-compose up --build

# Run in detached mode
docker-compose up -d
```

### Using the Helper Script

```bash
# Make the script executable
chmod +x docker-build-run.sh

# Run the script
./docker-build-run.sh
```

Visit http://localhost:8080 to access the application.

## Testing

```bash
./gradlew test
```

## API Integration

The application attempts to use:
1. wttr.in API (free, no authentication required)
2. GoWeather API (free, no authentication required)

When APIs are unavailable, the application displays the actual error messages from the APIs.