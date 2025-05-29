package me.csaba.csak.eventservice.service;

import lombok.AllArgsConstructor;
import me.csaba.csak.eventservice.repository.EventRepository;
import me.csaba.csak.eventservice.model.EventDTO;
import me.csaba.csak.eventservice.model.EventEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public void createEvent(final EventDTO eventDTO) {
        final EventEntity eventEntity = EventEntity.builder()
                .id(UUID.randomUUID())
                .name(eventDTO.getName())
                .longitude(eventDTO.getLongitude())
                .latitude(eventDTO.getLatitude())
                .startTime(eventDTO.getStartTime())
                .endTime(eventDTO.getEndTime())
                .build();

        this.eventRepository.save(eventEntity);
    }

    public EventDTO getEventById(final UUID id) {
        final Optional<EventEntity> entityOpt = this.eventRepository.findById(id);
        return entityOpt.map(entity -> EventDTO.builder()
                .name(entity.getName())
                .longitude(entity.getLongitude())
                .latitude(entity.getLatitude())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .build()
        ).orElse(null);
    }

    public boolean deleteEventById(final UUID id) {
        if (this.eventRepository.existsById(id)) {
            this.eventRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean updateEvent(final UUID id, final EventDTO eventDTO) {
        final Optional<EventEntity> entityOpt = this.eventRepository.findById(id);
        if (entityOpt.isPresent()) {
            final EventEntity entity = entityOpt.get();
            entity.setName(eventDTO.getName());
            entity.setLongitude(eventDTO.getLongitude());
            entity.setLatitude(eventDTO.getLatitude());
            entity.setStartTime(eventDTO.getStartTime());
            entity.setEndTime(eventDTO.getEndTime());
            this.eventRepository.save(entity);
            return true;
        }
        return false;
    }
}
