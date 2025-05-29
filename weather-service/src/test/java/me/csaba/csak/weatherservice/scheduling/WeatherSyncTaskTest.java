package me.csaba.csak.weatherservice.scheduling;


import me.csaba.csak.weatherservice.model.EventEntity;
import me.csaba.csak.weatherservice.model.LocationProperties;
import me.csaba.csak.weatherservice.repository.EventRepository;
import me.csaba.csak.weatherservice.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class WeatherSyncTaskTest {

    private WeatherService weatherService;
    private EventRepository eventRepository;
    private KafkaTemplate<String, String> kafkaTemplate;
    private WeatherSyncTask weatherSyncTask;

    @BeforeEach
    void setUp() {
        this.weatherService = mock(WeatherService.class);
        this.eventRepository = mock(EventRepository.class);

        this.weatherSyncTask = new WeatherSyncTask(this.weatherService, this.eventRepository, this.kafkaTemplate);
    }

    @Test
    void run_setsWeatherDataOnEvent() {
        final EventEntity event = new EventEntity();
        event.setStartTime(Instant.now().plusSeconds(3600));
        event.setLongitude(10.0);
        event.setLatitude(20.0);

        when(this.eventRepository.findAllByStartTimeBefore(any())).thenReturn(List.of(event));

        final LocationProperties prop1 = new LocationProperties();
        prop1.setTimestamp(event.getStartTime().minusSeconds(600));
        prop1.setTemperature(25.0);
        prop1.setWindSpeed(5.0);

        final LocationProperties prop2 = new LocationProperties();
        prop2.setTimestamp(event.getStartTime().minusSeconds(200));
        prop2.setTemperature(26.0);
        prop2.setWindSpeed(8.0);

        when(this.weatherService.getWeather(10.0, 20.0)).thenReturn(List.of(prop1, prop2));

        this.weatherSyncTask.run();

        assertEquals(26.0, event.getTemperature());
        assertEquals(8.0, event.getWindSpeed());
        verify(this.eventRepository).save(any(EventEntity.class));
    }


    @Test
    void run_doesNothingIfNoChange() {
        final EventEntity event = new EventEntity();
        event.setStartTime(Instant.now().plusSeconds(3600));
        event.setLongitude(10.0);
        event.setLatitude(20.0);
        event.setTemperature(26.0);
        event.setWindSpeed(8.0);

        when(this.eventRepository.findAllByStartTimeBefore(any())).thenReturn(List.of(event));

        final LocationProperties prop1 = new LocationProperties();
        prop1.setTimestamp(event.getStartTime().minusSeconds(600));
        prop1.setTemperature(25.0);
        prop1.setWindSpeed(5.0);

        final LocationProperties prop2 = new LocationProperties();
        prop2.setTimestamp(event.getStartTime().minusSeconds(200));
        prop2.setTemperature(26.0);
        prop2.setWindSpeed(8.0);

        when(this.weatherService.getWeather(10.0, 20.0)).thenReturn(List.of(prop1, prop2));

        this.weatherSyncTask.run();

        assertEquals(26.0, event.getTemperature());
        assertEquals(8.0, event.getWindSpeed());
        verify(this.eventRepository, never()).save(any(EventEntity.class));
    }

    @Test
    void run_doesNothingIfNoClosestWeather() {
        final EventEntity event = new EventEntity();
        event.setStartTime(Instant.now().plusSeconds(3600));
        event.setLongitude(10.0);
        event.setLatitude(20.0);

        when(this.eventRepository.findAllByStartTimeBefore(any())).thenReturn(List.of(event));
        when(this.weatherService.getWeather(10.0, 20.0)).thenReturn(Collections.emptyList());

        this.weatherSyncTask.run();

        assertNull(event.getTemperature());
        assertNull(event.getWindSpeed());
        verify(this.eventRepository, never()).save(any(EventEntity.class));
    }
}