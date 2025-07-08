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
- Podman (optional, for containerized deployment)

## Running the Application

### Using Gradle

**Linux/macOS:**
```bash
./gradlew bootRun
```

**Windows:**
```cmd
gradlew.bat bootRun
```

### Using Podman

Build and run the Podman container:

```bash
# Build the container image
podman build -t weather-app .

# Run the container
podman run -p 8070:8080 weather-app
```

### Using Podman Compose

```bash
# Build and run with Podman Compose
podman-compose up --build

# Run in detached mode
podman-compose up -d
```

### Using the Helper Script

```bash
# Make the script executable
chmod +x podman-build-run.sh

# Run the script
./podman-build-run.sh
```

Visit http://localhost:8080 to access the application.

## Testing

**Linux/macOS:**
```bash
./gradlew test
```

**Windows:**
```cmd
gradlew.bat test
```

## API Integration

The application attempts to use:
1. wttr.in API (free, no authentication required)
2. GoWeather API (free, no authentication required)

When APIs are unavailable, the application displays the actual error messages from the APIs.