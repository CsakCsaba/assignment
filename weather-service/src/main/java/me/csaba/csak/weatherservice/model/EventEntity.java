package me.csaba.csak.weatherservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "event")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "temperature")
    Double temperature;

    @Column(name = "wind_speed")
    Double windSpeed;

}