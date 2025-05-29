@echo off

set API_URL=http://localhost:8080/events

curl -X POST -H "Content-Type: application/json" -d "{\"name\":\"Event One\",\"longitude\":19.04,\"latitude\":47.50,\"startTime\":\"2025-06-04-01-15T10:00:00Z\",\"endTime\":\"2025-06-04T12:00:00Z\"}" %API_URL%
curl -X POST -H "Content-Type: application/json" -d "{\"name\":\"Event Two\",\"longitude\":19.05,\"latitude\":47.51,\"startTime\":\"2025-06-05T09:00:00Z\",\"endTime\":\"2025-06-05T11:00:00Z\"}" %API_URL%
curl -X POST -H "Content-Type: application/json" -d "{\"name\":\"Event Three\",\"longitude\":19.06,\"latitude\":47.52,\"startTime\":\"2025-06-06T14:00:00Z\",\"endTime\":\"2025-06-06T16:00:00Z\"}" %API_URL%
curl -X POST -H "Content-Type: application/json" -d "{\"name\":\"Event Four\",\"longitude\":19.07,\"latitude\":47.53,\"startTime\":\"2025-06-07T13:00:00Z\",\"endTime\":\"2025-06-07T15:00:00Z\"}" %API_URL%
curl -X POST -H "Content-Type: application/json" -d "{\"name\":\"Event Five\",\"longitude\":19.08,\"latitude\":47.54,\"startTime\":\"2025-06-08T08:00:00Z\",\"endTime\":\"2025-06-08T10:00:00Z\"}" %API_URL%