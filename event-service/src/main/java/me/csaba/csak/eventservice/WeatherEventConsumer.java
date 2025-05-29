package me.csaba.csak.eventservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import me.csaba.csak.WeatherEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class WeatherEventConsumer {

    private final ObjectMapper objectMapper;
    private final EventRepository eventRepository;

    @KafkaListener(topics = "test-topic")
    @Transactional(transactionManager = "transactionManager")
    public void consume(final ConsumerRecord<String, String> record) {
        final WeatherEvent weatherEvent = this.mapEvent(record.value());
        this.eventRepository.findById(weatherEvent.eventId()).ifPresent(
                eventEntity -> {
                    eventEntity.setTemperature(weatherEvent.temperature());
                    eventEntity.setWindSpeed(weatherEvent.windSpeed());
                }
        );
    }

    @SneakyThrows
    private WeatherEvent mapEvent(final String event){
        return this.objectMapper.readValue(event, WeatherEvent.class);
    }
}
