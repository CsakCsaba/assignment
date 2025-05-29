package me.csaba.csak.weatherservice.scheduling;


import me.csaba.csak.WeatherEvent;
import me.csaba.csak.weatherservice.model.EventEntity;
import me.csaba.csak.weatherservice.model.LocationProperties;
import me.csaba.csak.weatherservice.model.PropertyDTO;
import me.csaba.csak.weatherservice.repository.EventRepository;
import me.csaba.csak.weatherservice.service.WeatherKafkaEventProducer;
import me.csaba.csak.weatherservice.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

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
    private WeatherSyncTask weatherSyncTask;
    private WeatherKafkaEventProducer weatherKafkaEventProducer;

    @BeforeEach
    void setUp() {
        this.weatherService = mock(WeatherService.class);
        this.eventRepository = mock(EventRepository.class);
        this.weatherKafkaEventProducer = mock(WeatherKafkaEventProducer.class);
        this.weatherSyncTask = new WeatherSyncTask(this.weatherService, this.eventRepository, this.weatherKafkaEventProducer);
    }

    @Test
    void run_setsWeatherDataOnEvent_andSendsWeatherEvent() {
        // Arrange
        final UUID eventId = UUID.randomUUID();
        final EventEntity event = new EventEntity();
        event.setId(eventId);
        event.setStartTime(Instant.now().plusSeconds(3600));
        event.setLongitude(10.0);
        event.setLatitude(20.0);

        when(this.eventRepository.findAllByStartTimeBefore(any())).thenReturn(Stream.of(event));

        final LocationProperties prop1 = new LocationProperties();
        prop1.setTimestamp(event.getStartTime().minusSeconds(600));
        prop1.setTemperature(25.0);
        prop1.setWindSpeed(5.0);
        final PropertyDTO dto1 = new PropertyDTO(prop1);

        final LocationProperties prop2 = new LocationProperties();
        prop2.setTimestamp(event.getStartTime().minusSeconds(200));
        prop2.setTemperature(26.0);
        prop2.setWindSpeed(8.0);
        final PropertyDTO dto2 = new PropertyDTO(prop2);

        when(this.weatherService.getWeather(10.0, 20.0)).thenReturn(List.of(dto1, dto2));

        // Act
        this.weatherSyncTask.run();

        // Assert
        assertEquals(26.0, event.getTemperature());
        assertEquals(8.0, event.getWindSpeed());
        verify(this.eventRepository).save(any(EventEntity.class));

        final ArgumentCaptor<WeatherEvent> captor = ArgumentCaptor.forClass(WeatherEvent.class);
        verify(this.weatherKafkaEventProducer).send(captor.capture());
        final WeatherEvent sentEvent = captor.getValue();
        assertEquals(eventId, sentEvent.eventId());
        assertEquals(26.0, sentEvent.temperature());
        assertEquals(8.0, sentEvent.windSpeed());
    }

    @Test
    void run_doesNothingIfNoChange() {
        // Arrange
        final EventEntity event = new EventEntity();
        event.setId(UUID.randomUUID());
        event.setStartTime(Instant.now().plusSeconds(3600));
        event.setLongitude(10.0);
        event.setLatitude(20.0);
        event.setTemperature(26.0);
        event.setWindSpeed(8.0);

        when(this.eventRepository.findAllByStartTimeBefore(any())).thenReturn(Stream.of(event));

        final LocationProperties prop1 = new LocationProperties();
        prop1.setTimestamp(event.getStartTime().minusSeconds(600));
        prop1.setTemperature(25.0);
        prop1.setWindSpeed(5.0);
        final PropertyDTO dto1 = new PropertyDTO(prop1);

        final LocationProperties prop2 = new LocationProperties();
        prop2.setTimestamp(event.getStartTime().minusSeconds(200));
        prop2.setTemperature(26.0);
        prop2.setWindSpeed(8.0);
        final PropertyDTO dto2 = new PropertyDTO(prop2);

        when(this.weatherService.getWeather(10.0, 20.0)).thenReturn(List.of(dto1, dto2));

        // Act
        this.weatherSyncTask.run();

        // Assert
        assertEquals(26.0, event.getTemperature());
        assertEquals(8.0, event.getWindSpeed());
        verify(this.eventRepository, never()).save(any(EventEntity.class));
        verify(this.weatherKafkaEventProducer, never()).send(any());
    }

    @Test
    void run_doesNothingIfNoClosestWeather() {
        // Arrange
        final EventEntity event = new EventEntity();
        event.setId(UUID.randomUUID());
        event.setStartTime(Instant.now().plusSeconds(3600));
        event.setLongitude(10.0);
        event.setLatitude(20.0);

        when(this.eventRepository.findAllByStartTimeBefore(any())).thenReturn(Stream.of(event));
        when(this.weatherService.getWeather(10.0, 20.0)).thenReturn(Collections.emptyList());

        // Act
        this.weatherSyncTask.run();

        // Assert
        assertNull(event.getTemperature());
        assertNull(event.getWindSpeed());
        verify(this.eventRepository, never()).save(any(EventEntity.class));
        verify(this.weatherKafkaEventProducer, never()).send(any());
    }
}