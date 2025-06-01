package me.csaba.csak.weatherservice.model;

import lombok.Getter;

import java.time.Instant;

@Getter
public class PropertyDTO {
    private final Double temperature;
    private final Double windSpeed;
    private final Instant timestamp;

    public PropertyDTO(final PropertyEntity properties) {
        this.temperature = properties.getTemperature();
        this.windSpeed = properties.getWindSpeed();
        this.timestamp = properties.getTimestamp();
    }
}
