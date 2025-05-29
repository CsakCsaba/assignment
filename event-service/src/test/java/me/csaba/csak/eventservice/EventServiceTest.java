package me.csaba.csak.eventservice;

import me.csaba.csak.eventservice.model.EventDTO;
import me.csaba.csak.eventservice.model.EventEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
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
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusSeconds(3600))
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
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusSeconds(3600))
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
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusSeconds(3600))
                .build();
        final EventDTO dto = EventDTO.builder()
                .name("New")
                .longitude(3.0)
                .latitude(4.0)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusSeconds(7200))
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
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusSeconds(7200))
                .build();
        when(this.eventRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        final boolean result = this.eventService.updateEvent(id, dto);

        // Assert
        assertThat(result).isFalse();
        verify(this.eventRepository, never()).save(any());
    }
}
