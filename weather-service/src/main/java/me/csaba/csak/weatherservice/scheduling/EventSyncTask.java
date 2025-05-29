package me.csaba.csak.weatherservice.scheduling;

import lombok.AllArgsConstructor;
import me.csaba.csak.EventDTO;
import me.csaba.csak.weatherservice.client.EventClient;
import me.csaba.csak.weatherservice.model.EventEntity;
import me.csaba.csak.weatherservice.repository.EventRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EventSyncTask implements ScheduleTask{

    private final EventClient eventClient;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public void run() {
        final List<EventDTO> fetchedEvents = this.eventClient.getAllEvents();

        this.removeMissingEvents(fetchedEvents);

        this.updateEvents(fetchedEvents);
    }

    private void updateEvents(final List<EventDTO> fetchedEvents) {
        final List<EventEntity> entities = fetchedEvents.stream()
                .map(dto -> EventEntity.builder()
                        .id(dto.getId())
                        .latitude(dto.getLatitude())
                        .longitude(dto.getLongitude())
                        .startTime(dto.getStartTime())
                        .build())
                .collect(Collectors.toList());
        this.eventRepository.saveAll(entities);
    }

    private void removeMissingEvents(final List<EventDTO> fetchedEvents) {
        final Set<UUID> fetchedEventIds = fetchedEvents.stream()
                .map(EventDTO::getId)
                .collect(Collectors.toSet());
        this.eventRepository.deleteAllById(fetchedEventIds);
    }

}
