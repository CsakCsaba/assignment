package me.csaba.csak.eventservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.csaba.csak.WeatherEvent;
import me.csaba.csak.eventservice.model.EventEntity;
import me.csaba.csak.eventservice.repository.EventRepository;
import me.csaba.csak.eventservice.service.WeatherEventConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class WeatherEventConsumerTest {

    private ObjectMapper objectMapper;
    private EventRepository eventRepository;
    private WeatherEventConsumer consumer;

    @BeforeEach
    void setUp() {
        this.objectMapper = mock(ObjectMapper.class);
        this.eventRepository = mock(EventRepository.class);
        this.consumer = new WeatherEventConsumer(this.objectMapper, this.eventRepository);
    }

    @Test
    void consume_updatesEventEntity_whenEventExists() throws Exception {
        // Arrange
        final UUID eventId = UUID.randomUUID();
        final WeatherEvent weatherEvent = new WeatherEvent(eventId, 12.3, 4.5);
        final String eventJson = "{\"eventId\":\"" + eventId + "\",\"temperature\":12.3,\"windSpeed\":4.5}";
        final EventEntity entity = mock(EventEntity.class);

        when(this.objectMapper.readValue(eventJson, WeatherEvent.class)).thenReturn(weatherEvent);
        when(this.eventRepository.findById(eventId)).thenReturn(Optional.of(entity));

        final ConsumerRecord<String, String> record = new ConsumerRecord<>("test-topic", 0, 0L, "key", eventJson);

        // Act
        this.consumer.consume(record);

        // Assert
        verify(entity).setTemperature(12.3);
        verify(entity).setWindSpeed(4.5);
    }

    @Test
    void consume_doesNothing_whenEventNotFound() throws Exception {
        // Arrange
        final UUID eventId = UUID.randomUUID();
        final WeatherEvent weatherEvent = new WeatherEvent(eventId, 12.3, 4.5);
        final String eventJson = "{\"eventId\":\"" + eventId + "\",\"temperature\":12.3,\"windSpeed\":4.5}";

        when(this.objectMapper.readValue(eventJson, WeatherEvent.class)).thenReturn(weatherEvent);
        when(this.eventRepository.findById(eventId)).thenReturn(Optional.empty());

        final ConsumerRecord<String, String> record = new ConsumerRecord<>("test-topic", 0, 0L, "key", eventJson);

        // Act
        this.consumer.consume(record);

        // Assert
        verify(this.eventRepository, never()).save(any());
    }
}