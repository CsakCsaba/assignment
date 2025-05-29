package me.csaba.csak.weatherservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "location")
public class LocationEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "latitude", updatable = false, nullable = false)
    private Double latitude;

    @Column(name = "longitude", updatable = false, nullable = false)
    private Double longitude;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location", cascade = CascadeType.ALL, targetEntity = LocationProperties.class)
    private List<LocationProperties> properties;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
