package me.csaba.csak;

import java.util.UUID;

public record WeatherEvent(UUID eventId, Double temperature, Double windSpeed) {
}
