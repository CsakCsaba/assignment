package me.csaba.csak;

import lombok.Builder;

import java.util.UUID;

@Builder
public record WeatherEvent(UUID eventId, Double temperature, Double windSpeed) {
}
