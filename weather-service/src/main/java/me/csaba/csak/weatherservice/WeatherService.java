package me.csaba.csak.weatherservice;

import me.csaba.csak.weatherservice.model.LocationEntity;
import me.csaba.csak.weatherservice.model.LocationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public List<LocationProperties> getWeather(final double lon, final double lat) {
        final Optional<LocationEntity> optLocation = this.locationRepository.findByLongitudeAndLatitude(lon, lat);

        final LocationEntity locationEntity;
        if (optLocation.isEmpty()) {
            locationEntity = this.createNewLocation(lon, lat);
            return locationEntity.getProperties();
        } else {
            locationEntity = optLocation.get();
            if (locationEntity.getExpiresAt().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
                this.updateExistingLocation(locationEntity);
            }
        }
        return locationEntity.getProperties();

    }

    private LocationEntity createNewLocation(final double lon, final double lat) {
        final var response = this.getWeatherFor(lon, lat);
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
        final var response = this.getWeatherFor(locationEntity.getLongitude(), locationEntity.getLatitude());
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
                            .timestamp(ts.time())
                            .build())
                    .toList();
            locationEntity.setProperties(propertiesList);
        }
    }

    private ResponseEntity<WeatherReport> getWeatherFor(final double lon, final double lat) {
        final var response = this.weatherClient.getWeather(lat, lon, this.userAgent);
        return response;
    }

    private LocalDateTime parseExpires(final String expires) {
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse(expires, DateTimeFormatter.RFC_1123_DATE_TIME);
        return zonedDateTime.toLocalDateTime();
    }
}