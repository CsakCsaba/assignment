package me.csaba.csak.weatherservice.repository;

import me.csaba.csak.weatherservice.model.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {
    List<EventEntity> findAllByStartTimeBefore(Instant startTime);
}
