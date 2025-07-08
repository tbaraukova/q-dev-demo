#!/bin/bash

# Exit on error
set -e

echo "Building Docker image..."
docker build -t weather-app .

echo "Running Docker container..."
docker run -p 8080:8080 weather-app

# To stop the container, press Ctrl+C