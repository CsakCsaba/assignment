package me.csaba.csak.weatherservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "properties")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationProperties {
    @Id
    private UUID id;

    @ManyToOne(targetEntity = LocationEntity.class, optional = false)
    LocationEntity location;

    @Column(name = "temperature", nullable = false)
    Double temperature;

    @Column(name = "wind_speed", nullable = false)
    Double windSpeed;

    @Column(name = "timestamp", nullable = false)
    LocalDateTime timestamp;
}
