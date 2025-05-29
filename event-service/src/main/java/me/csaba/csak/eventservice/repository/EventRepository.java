package me.csaba.csak.eventservice.repository;


import me.csaba.csak.eventservice.model.EventEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface EventRepository extends CrudRepository<EventEntity, UUID> {
}
