# Cloud Nine Weather 🌤️

A Java and Spring Boot application that provides weather forecasts with Redis caching capabilities.
Author: Tarah Christy

## Features ✨

- Real-time weather forecasts using Open-Meteo API
- Geocoding support via Google Maps API
- Redis caching for improved performance
- Current temperature and 7-day forecast display
- Temperature displayed in Fahrenheit
- Cache status indicator

## Prerequisites 📋

- Java 17 or higher
- Redis server running locally
- Google Maps API key
- Gradle

## Configuration ⚙️

1. Create a `.env` file in the project root:
```properties
GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here
```

2. Configure Redis in `application.properties`:
```properties
spring.cache.type=redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.redis.time-to-live=3600000
```

## Running the Application 🚀

1. Start Redis server:
```bash
redis-server
```

2. Build and run the application:
```bash
./gradlew bootRun
```

3. Access the landing page:
http://localhost:8080/

4. Example API call:
http://localhost:8080/api/forecast?street=123%20Main&city=New%20York&state=NY&zipcode=10001

## API Endpoints 🌐

### Get Weather Forecast
GET /api/forecast

Query Parameters:
- `street`: Street address
- `city`: City name
- `state`: State code
- `zipcode`: ZIP code

## Response Format 📝

```json
{
    "forecast": "Current Temperature: 72.5°F (Humidity: 45%)\n\n7-day forecast:\n...",
    "fromCache": false
}
```

## Caching 🔄

- Weather forecasts are cached by ZIP code
- Cache TTL: 30 minutes
- Cache status is indicated in the response

## Technologies Used 🛠️

- Spring Boot
- Redis
- Open-Meteo API
- Google Maps Geocoding API
- Gradle

## Running Tests

The project uses JUnit 5 and Mockito for testing. To run the tests:

```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# Run specific test class
./gradlew test --tests com.cloudnineweather.service.ForecastServiceTest

# Run specific test method
./gradlew test --tests com.cloudnineweather.service.ForecastServiceTest.getForecast_WhenCached_ReturnsCachedResponse
```

Test coverage reports will be generated in `build/reports/jacoco/test/html/index.html`.

### Test Structure
- `ForecastServiceTest`: Tests weather forecast functionality and Redis caching
- `GeocodingServiceTest`: Tests address geocoding functionality

## Future Functionality Ideas

1. Implement a static map using Google Maps Static API to show a map of the address entered
2. Implement another button to show an extended forecast
