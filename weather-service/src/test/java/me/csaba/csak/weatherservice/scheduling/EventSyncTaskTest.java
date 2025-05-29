package me.csaba.csak.weatherservice.scheduling;

import me.csaba.csak.EventDTO;
import me.csaba.csak.weatherservice.client.EventClient;
import me.csaba.csak.weatherservice.model.EventEntity;
import me.csaba.csak.weatherservice.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventSyncTaskTest {

    private EventClient eventClient;
    private EventRepository eventRepository;
    private EventSyncTask eventSyncTask;

    @BeforeEach
    void setUp() {
        this.eventClient = mock(EventClient.class);
        this.eventRepository = mock(EventRepository.class);
        this.eventSyncTask = new EventSyncTask(this.eventClient, this.eventRepository);
    }

    @Test
    void run_shouldUpdateAndRemoveEvents() {
        // Arrange
        final UUID id1 = UUID.randomUUID();
        final UUID id2 = UUID.randomUUID();
        final EventDTO dto1 = EventDTO.builder()
                .id(id1)
                .name("name1")
                .latitude(10.0)
                .longitude(20.0)
                .startTime(Instant.now())
                .endTime(Instant.now())
                .build();
        final EventDTO dto2 = EventDTO.builder()
                .id(id2)
                .name("name2")
                .latitude(30.0)
                .longitude(40.0)
                .startTime(Instant.now())
                .endTime(Instant.now())
                .build();
        final List<EventDTO> fetchedEvents = Arrays.asList(dto1, dto2);

        when(this.eventClient.getAllEvents()).thenReturn(fetchedEvents);

        // Act
        this.eventSyncTask.run();

        // Assert
        final ArgumentCaptor<Set<UUID>> idsCaptor = ArgumentCaptor.forClass(Set.class);
        verify(this.eventRepository).deleteAllById(idsCaptor.capture());
        assertTrue(idsCaptor.getValue().containsAll(Arrays.asList(id1, id2)));

        final ArgumentCaptor<List<EventEntity>> entitiesCaptor = ArgumentCaptor.forClass(List.class);
        verify(this.eventRepository).saveAll(entitiesCaptor.capture());
        final List<EventEntity> savedEntities = entitiesCaptor.getValue();
        assertEquals(2, savedEntities.size());
        assertTrue(savedEntities.stream().anyMatch(e -> e.getId().equals(id1)));
        assertTrue(savedEntities.stream().anyMatch(e -> e.getId().equals(id2)));
    }
}
