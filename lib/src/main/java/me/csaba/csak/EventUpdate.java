package me.csaba.csak;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventUpdate (UUID eventId, LocalDateTime startTime, Double longitude, Double latitude) {
}
