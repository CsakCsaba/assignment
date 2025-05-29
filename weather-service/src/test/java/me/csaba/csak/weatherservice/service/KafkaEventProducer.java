package me.csaba.csak.weatherservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.csaba.csak.WeatherEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WeatherKafkaEventProducerTest {

    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;
    private WeatherKafkaEventProducer weatherKafkaEventProducer;

    @BeforeEach
    void setUp() {
        this.kafkaTemplate = mock(KafkaTemplate.class);
        this.objectMapper = new ObjectMapper();
        this.weatherKafkaEventProducer = new WeatherKafkaEventProducer(this.kafkaTemplate, "test-topic", this.objectMapper);
    }


    @Test
    void send_weatherEvent_serializesAndSendsToKafka() throws Exception {
        final WeatherEvent event = WeatherEvent.builder()
                .eventId(UUID.randomUUID())
                .temperature(15.0)
                .windSpeed(2.0)
                .build();

        this.weatherKafkaEventProducer.send(event);

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(this.kafkaTemplate).send(eq("test-topic"), captor.capture());

        assertEquals(this.objectMapper.writeValueAsString(event), captor.getValue());
    }
}