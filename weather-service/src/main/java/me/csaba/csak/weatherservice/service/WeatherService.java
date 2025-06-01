package me.csaba.csak.weatherservice.service;

import me.csaba.csak.weatherservice.client.WeatherClient;
import me.csaba.csak.weatherservice.model.LocationEntity;
import me.csaba.csak.weatherservice.model.LocationProperties;
import me.csaba.csak.weatherservice.model.PropertyDTO;
import me.csaba.csak.weatherservice.model.WeatherReport;
import me.csaba.csak.weatherservice.model.WeatherResponse;
import me.csaba.csak.weatherservice.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private final WeatherClient weatherClient;
    private final String userAgent;
    private final LocationRepository locationRepository;

    public WeatherService(final WeatherClient weatherClient, @Value("${weather-api.user-agent}") final String userAgent, final LocationRepository locationRepository) {
        this.weatherClient = weatherClient;
        this.userAgent = userAgent;
        this.locationRepository = locationRepository;
    }

    @Transactional(transactionManager = "transactionManager")
    public List<PropertyDTO> getWeather(final double lat, final double lon) {
        //Rounding to 3 digits
        final double roundedLat = Math.round(lat * 1000.0) / 1000.0;
        final double roundedLon = Math.round(lon * 1000.0) / 1000.0;

        final Optional<LocationEntity> optLocation = this.locationRepository.findByLatitudeAndLongitude(roundedLat, roundedLon);
        final LocationEntity locationEntity;
        if (optLocation.isEmpty()) {
            locationEntity = this.createNewLocation(lat, lon);
            return mapProperties(locationEntity);
        } else {
            locationEntity = optLocation.get();
            if (locationEntity.getExpiresAt().isBefore(Instant.now())) {
                this.updateExistingLocation(locationEntity);
            }
        }
        return mapProperties(locationEntity);
    }

    private static List<PropertyDTO> mapProperties(final LocationEntity locationEntity) {
        return locationEntity.getProperties().stream().map(PropertyDTO::new).collect(Collectors.toList());
    }

    private LocationEntity createNewLocation(final double lat, final double lon) {
        final var response = this.getWeatherFor(lat, lon);
        final String expires = response.getHeaders().getFirst("Expires");
        final WeatherResponse weatherResponse = new WeatherResponse(response.getBody(), expires);

        final LocationEntity locationEntity = LocationEntity.builder()
                .id(UUID.randomUUID())
                .latitude(lat)
                .longitude(lon)
                .expiresAt(this.parseExpires(expires))
                .build();

        enrichLocationWithWeatherInfo(weatherResponse, locationEntity);

        this.locationRepository.save(locationEntity);
        return locationEntity;
    }

    private void updateExistingLocation(final LocationEntity locationEntity) {
        final var response = this.getWeatherFor(locationEntity.getLatitude(), locationEntity.getLongitude());
        final String expires = response.getHeaders().getFirst("Expires");
        final WeatherResponse weatherResponse = new WeatherResponse(response.getBody(), expires);

        enrichLocationWithWeatherInfo(weatherResponse, locationEntity);

        this.locationRepository.save(locationEntity);
    }

    private static void enrichLocationWithWeatherInfo(final WeatherResponse weatherResponse, final LocationEntity locationEntity) {
        final WeatherReport report = weatherResponse.weatherReport();
        if (report != null && report.properties() != null && report.properties().timeseries() != null) {
            final List<LocationProperties> propertiesList = report.properties().timeseries().stream()
                    .map(ts -> LocationProperties.builder()
                            .id(UUID.randomUUID())
                            .location(locationEntity)
                            .temperature(ts.data().instant().details().airTemperature())
                            .windSpeed(ts.data().instant().details().windSpeed())
                            .timestamp(ZonedDateTime.of(ts.time(), ZoneId.of("UTC")).toInstant())
                            .build())
                    .toList();
            locationEntity.setProperties(propertiesList);
        }
    }

    private ResponseEntity<WeatherReport> getWeatherFor(final double lat, final double lon) {
        final var response = this.weatherClient.getWeather(lat, lon, this.userAgent);
        return response;
    }

    private Instant parseExpires(final String expires) {
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse(expires, DateTimeFormatter.RFC_1123_DATE_TIME);
        final int randomMinutes = java.util.concurrent.ThreadLocalRandom.current().nextInt(1, 10); // 1 (inclusive) to 10 (exclusive)
        return zonedDateTime.toInstant().plus(randomMinutes, ChronoUnit.MINUTES);
    }
}