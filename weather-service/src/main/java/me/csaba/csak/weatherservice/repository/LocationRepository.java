package me.csaba.csak.weatherservice.repository;

import me.csaba.csak.weatherservice.model.LocationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface LocationRepository extends CrudRepository<LocationEntity, UUID> {

    Optional<LocationEntity> findByLatitudeAndLongitude(double latitude, double longitude);
}
