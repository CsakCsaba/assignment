package me.csaba.csak.eventservice;


import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface EventRepository extends CrudRepository<EventEntity, UUID> {
}
