package me.csaba.csak.weatherservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import me.csaba.csak.WeatherEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WeatherKafkaEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String eventTopic;
    private final ObjectMapper objectMapper;

    public WeatherKafkaEventProducer(final KafkaTemplate<String, String> kafkaTemplate,
                                     @Value("${event-topic}") final String eventTopic, final ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.eventTopic = eventTopic;
        this.objectMapper = objectMapper;
    }

    @Transactional(transactionManager = "kafkaTransactionManager")
    public void send(final WeatherEvent event) {
        final String eventString = this.transformEvent(event);
        this.kafkaTemplate.send(this.eventTopic, eventString);
    }

    @SneakyThrows
    private String transformEvent(final WeatherEvent event) {
        final String eventString = this.objectMapper.writeValueAsString(event);
        return eventString;
    }
}
