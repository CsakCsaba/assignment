package me.csaba.csak.weatherservice.scheduling;

import lombok.AllArgsConstructor;
import me.csaba.csak.WeatherEvent;
import me.csaba.csak.weatherservice.model.EventEntity;
import me.csaba.csak.weatherservice.model.LocationProperties;
import me.csaba.csak.weatherservice.repository.EventRepository;
import me.csaba.csak.weatherservice.service.WeatherKafkaEventProducer;
import me.csaba.csak.weatherservice.service.WeatherService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Component
@AllArgsConstructor
public class WeatherSyncTask implements ScheduleTask {

    private final WeatherService weatherService;
    private final EventRepository eventRepository;
    private final WeatherKafkaEventProducer weatherKafkaEventProducer;

    @Override
    @Transactional
    public void run() {
        final List<EventEntity> toBeScheduledEvents = this.eventRepository.findAllByStartTimeBefore(Instant.now().plus(7, ChronoUnit.DAYS));
        toBeScheduledEvents.forEach(eventEntity -> {
            final List<LocationProperties> properties = this.weatherService.getWeather(eventEntity.getLongitude(), eventEntity.getLatitude());
            final LocationProperties closest = properties.stream()
                    .filter(p -> p.getTimestamp().isBefore(eventEntity.getStartTime()))
                    .min(Comparator.comparingLong(a -> Math.abs(a.getTimestamp().until(eventEntity.getStartTime(), ChronoUnit.SECONDS))))
                    .orElse(null);
            if (closest != null &&
                    (!closest.getTemperature().equals(eventEntity.getTemperature())
                            || !closest.getWindSpeed().equals(eventEntity.getWindSpeed()))) {
                eventEntity.setTemperature(closest.getTemperature());
                eventEntity.setWindSpeed(closest.getWindSpeed());

                this.eventRepository.save(eventEntity);

                final WeatherEvent weatherEvent = WeatherEvent.builder()
                        .eventId(eventEntity.getId())
                        .windSpeed(eventEntity.getWindSpeed())
                        .temperature(eventEntity.getTemperature())
                        .build();
                this.weatherKafkaEventProducer.send(weatherEvent);
            }
        });
    }
}
