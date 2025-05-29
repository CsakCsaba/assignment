# Assignment

This project provides a Spring Boot-based event management service. It allows you to create, store, and retrieve event data using a RESTful API. 
The services use a postgresql databases, kafka and is containerized using Docker.

## Services

- **event-service**: Exposes endpoints to create and retrieve events.
- **weather-service**: Fetches weather data and sends updated to the event-service via kafka to populate the events with weather data

## How to Test

1. Start the services: 
```
 docker compose -f docker-compose.yml up --build -d
```
2. Populate events by running one of the scripts:
   - On macOS/Linux:
     ```
     ./create_events.sh
     ```
   - On Windows:
     ```
     create_events.bat
     ```
3. Wait 30-45 seconds, then check the events:
```
curl -L -X GET 'http://localhost:8080/events'
```
4. Some events should now have been updated with weather data.
5. You can now turn off the services:
```
docker compose -f docker-compose.yml down
```

## Requirements

- Maven
- Docker desktop
- curl


## TODOs:

- Proper security for all the endpoints
- CI/CD pipeline
- Distributed scheduling: currently you can start multiple instance of the event-service to handle bigger loads, but the
weather-service needs some way to handle more thousands of updates parallely
- On event creation and modification the event-serivce should also send events via kafka, which the weather-service could consume
to have better updates about the events