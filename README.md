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

## Running the Application

```bash
./gradlew bootRun
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