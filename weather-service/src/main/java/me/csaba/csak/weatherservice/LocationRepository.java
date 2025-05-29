package me.csaba.csak.weatherservice;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface LocationRepository extends CrudRepository<LocationEntity, UUID> {

    Optional<LocationEntity> findByLongitudeAndLatitude(double longitude, double latitude);
}
