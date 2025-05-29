package me.csaba.csak.weatherservice.model;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class PropertyDTO {
    private final UUID id;
    private final Double temperature;
    private final Double windSpeed;
    private final Instant timestamp;

    public PropertyDTO(final LocationProperties properties) {
        this.id = properties.getId();
        this.temperature = properties.getTemperature();
        this.windSpeed = properties.getWindSpeed();
        this.timestamp = properties.getTimestamp();
    }
}
