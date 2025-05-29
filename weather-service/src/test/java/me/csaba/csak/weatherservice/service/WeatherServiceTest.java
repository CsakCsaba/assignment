package me.csaba.csak.weatherservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.csaba.csak.weatherservice.client.WeatherClient;
import me.csaba.csak.weatherservice.model.LocationEntity;
import me.csaba.csak.weatherservice.model.LocationProperties;
import me.csaba.csak.weatherservice.model.WeatherReport;
import me.csaba.csak.weatherservice.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WeatherServiceTest {

    private WeatherClient weatherClient;
    private LocationRepository locationRepository;
    private WeatherService weatherService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.weatherClient = mock(WeatherClient.class);
        this.locationRepository = mock(LocationRepository.class);
        this.weatherService = new WeatherService(this.weatherClient, "test-agent", this.locationRepository);
    }

    @Test
    void getWeather_returnsCached_whenNotExpired() {
        // Arrange
        final double lon = 10.0;
        final double lat = 20.0;
        final LocationEntity entity = LocationEntity.builder()
                .id(UUID.randomUUID())
                .longitude(lon)
                .latitude(lat)
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .properties(List.of())
                .build();

        when(this.locationRepository.findByLongitudeAndLatitude(lon, lat)).thenReturn(Optional.of(entity));

        // Act
        final List<LocationProperties> result = this.weatherService.getWeather(lon, lat);

        // Assert
        assertSame(entity.getProperties(), result);
        verify(this.locationRepository, never()).save(any());
        verify(this.weatherClient, never()).getWeather(anyDouble(), anyDouble(), anyString());
    }

    @Test
    void getWeather_fetchesAndSaves_whenNotCached() throws Exception {
        // Arrange
        final double lon = 10.0;
        final double lat = 20.0;
        when(this.locationRepository.findByLongitudeAndLatitude(lon, lat)).thenReturn(Optional.empty());

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Expires", "Wed, 21 Oct 2020 07:28:00 GMT");

        final WeatherReport report = this.objectMapper.readValue(
                this.getClass().getResourceAsStream("/weather-report.json"),
                WeatherReport.class
        );

        final ResponseEntity<WeatherReport> response = ResponseEntity.ok().headers(headers).body(report);

        when(this.weatherClient.getWeather(lat, lon, "test-agent")).thenReturn(response);

        // Act
        final List<LocationProperties> result = this.weatherService.getWeather(lon, lat);

        // Assert
        verify(this.locationRepository).save(any(LocationEntity.class));
        assertNotNull(result);
    }

    @Test
    void getWeather_updates_whenExpired() throws Exception {
        // Arrange
        final double lon = 10.0;
        final double lat = 20.0;
        final LocationEntity entity = LocationEntity.builder()
                .id(UUID.randomUUID())
                .longitude(lon)
                .latitude(lat)
                .expiresAt(Instant.now().minus(1, ChronoUnit.HOURS))
                .properties(List.of())
                .build();

        when(this.locationRepository.findByLongitudeAndLatitude(lon, lat)).thenReturn(Optional.of(entity));

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Expires", "Wed, 21 Oct 2020 07:28:00 GMT");
        final WeatherReport report = this.objectMapper.readValue(
                this.getClass().getResourceAsStream("/weather-report.json"),
                WeatherReport.class
        );
        final ResponseEntity<WeatherReport> response = ResponseEntity.ok().headers(headers).body(report);

        when(this.weatherClient.getWeather(lat, lon, "test-agent")).thenReturn(response);

        // Act
        final List<LocationProperties> result = this.weatherService.getWeather(lon, lat);

        // Assert
        verify(this.locationRepository).save(entity);
        assertNotNull(result);
    }
}