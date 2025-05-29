package me.csaba.csak.weatherservice.repository;

import me.csaba.csak.weatherservice.model.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {
    Stream<EventEntity> findAllByStartTimeBefore(Instant startTime);
}
