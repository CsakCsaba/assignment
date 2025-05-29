package me.csaba.csak.eventservice;

import me.csaba.csak.EventDTO;
import me.csaba.csak.eventservice.model.EventEntity;
import me.csaba.csak.eventservice.repository.EventRepository;
import me.csaba.csak.eventservice.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventServiceTest {

    private EventRepository eventRepository;
    private EventService eventService;

    @BeforeEach
    void setUp() {
        this.eventRepository = mock(EventRepository.class);
        this.eventService = new EventService(this.eventRepository);
    }

    @Test
    void createEvent_savesEntity() {
        // Arrange
        final EventDTO dto = EventDTO.builder()
                .name("Test Event")
                .longitude(1.0)
                .latitude(2.0)
                .startTime(Instant.now())
                .endTime(Instant.now().plusSeconds(3600))
                .build();

        // Act
        this.eventService.createEvent(dto);

        // Assert
        final ArgumentCaptor<EventEntity> captor = ArgumentCaptor.forClass(EventEntity.class);
        verify(this.eventRepository).save(captor.capture());
        final EventEntity saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo(dto.getName());
        assertThat(saved.getLongitude()).isEqualTo(dto.getLongitude());
        assertThat(saved.getLatitude()).isEqualTo(dto.getLatitude());
        assertThat(saved.getStartTime()).isEqualTo(dto.getStartTime());
        assertThat(saved.getEndTime()).isEqualTo(dto.getEndTime());
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void getEventById_returnsDTO_whenFound() {
        // Arrange
        final UUID id = UUID.randomUUID();
        final EventEntity entity = EventEntity.builder()
                .id(id)
                .name("Event")
                .longitude(1.0)
                .latitude(2.0)
                .startTime(Instant.now())
                .endTime(Instant.now().plusSeconds(3600))
                .build();
        when(this.eventRepository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        final EventDTO dto = this.eventService.getEventById(id);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo(entity.getName());
    }

    @Test
    void getEventById_returnsNull_whenNotFound() {
        // Arrange
        final UUID id = UUID.randomUUID();
        when(this.eventRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        final EventDTO dto = this.eventService.getEventById(id);

        // Assert
        assertThat(dto).isNull();
    }

    @Test
    void deleteEventById_deletes_whenExists() {
        // Arrange
        final UUID id = UUID.randomUUID();
        when(this.eventRepository.existsById(id)).thenReturn(true);

        // Act
        final boolean result = this.eventService.deleteEventById(id);

        // Assert
        assertThat(result).isTrue();
        verify(this.eventRepository).deleteById(id);
    }

    @Test
    void deleteEventById_returnsFalse_whenNotExists() {
        // Arrange
        final UUID id = UUID.randomUUID();
        when(this.eventRepository.existsById(id)).thenReturn(false);

        // Act
        final boolean result = this.eventService.deleteEventById(id);

        // Assert
        assertThat(result).isFalse();
        verify(this.eventRepository, never()).deleteById(id);
    }

    @Test
    void updateEvent_updates_whenFound() {
        // Arrange
        final UUID id = UUID.randomUUID();
        final EventEntity entity = EventEntity.builder()
                .id(id)
                .name("Old")
                .longitude(1.0)
                .latitude(2.0)
                .startTime(Instant.now())
                .endTime(Instant.now().plusSeconds(3600))
                .build();
        final EventDTO dto = EventDTO.builder()
                .name("New")
                .longitude(3.0)
                .latitude(4.0)
                .startTime(Instant.now())
                .endTime(Instant.now().plusSeconds(7200))
                .build();
        when(this.eventRepository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        final boolean result = this.eventService.updateEvent(id, dto);

        // Assert
        assertThat(result).isTrue();
        assertThat(entity.getName()).isEqualTo(dto.getName());
        assertThat(entity.getLongitude()).isEqualTo(dto.getLongitude());
        assertThat(entity.getLatitude()).isEqualTo(dto.getLatitude());
        assertThat(entity.getStartTime()).isEqualTo(dto.getStartTime());
        assertThat(entity.getEndTime()).isEqualTo(dto.getEndTime());
        verify(this.eventRepository).save(entity);
    }

    @Test
    void updateEvent_returnsFalse_whenNotFound() {
        // Arrange
        final UUID id = UUID.randomUUID();
        final EventDTO dto = EventDTO.builder()
                .name("New")
                .longitude(3.0)
                .latitude(4.0)
                .startTime(Instant.now())
                .endTime(Instant.now().plusSeconds(7200))
                .build();
        when(this.eventRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        final boolean result = this.eventService.updateEvent(id, dto);

        // Assert
        assertThat(result).isFalse();
        verify(this.eventRepository, never()).save(any());
    }

    @Test
    void getAllEvents_returnsMappedDTOs() {
        // Arrange
        final EventEntity entity1 = EventEntity.builder()
                .name("Event 1")
                .longitude(10.0)
                .latitude(20.0)
                .startTime(Instant.now())
                .endTime(Instant.now().plus(1, ChronoUnit.HOURS))
                .temperature(25.0)
                .windSpeed(5.0)
                .build();
        final EventEntity entity2 = EventEntity.builder()
                .name("Event 2")
                .longitude(30.0)
                .latitude(40.0)
                .startTime(Instant.now())
                .endTime(Instant.now().plus(2, ChronoUnit.HOURS))
                .temperature(22.0)
                .windSpeed(3.0)
                .build();
        final Iterable<EventEntity> entities = List.of(entity1, entity2);
        when(this.eventRepository.findAll()).thenReturn(entities);

        // Act
        final List<EventDTO> result = this.eventService.getAllEvents();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Event 1");
        assertThat(result.get(1).getName()).isEqualTo("Event 2");
        assertThat(result.get(0).getTemperature()).isEqualTo(25.0);
        assertThat(result.get(1).getWindSpeed()).isEqualTo(3.0);
    }
}
