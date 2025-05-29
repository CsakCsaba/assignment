package me.csaba.csak.eventservice.api;

import jakarta.validation.Valid;
import me.csaba.csak.eventservice.model.EventDTO;
import me.csaba.csak.eventservice.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity createEvent(@Valid @RequestBody final EventDTO eventDTO) {
        this.eventService.createEvent(eventDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable final UUID id) {
        final EventDTO eventDTO = this.eventService.getEventById(id);
        return eventDTO != null ? ResponseEntity.ok(eventDTO) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEventById(@PathVariable final UUID id) {
        final boolean deleted = this.eventService.deleteEventById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable final UUID id, @Valid @RequestBody final EventDTO eventDTO) {
        final boolean updated = this.eventService.updateEvent(id, eventDTO);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
