CREATE TABLE location
(
    id         UUID PRIMARY KEY,
    latitude   DOUBLE PRECISION NOT NULL,
    longitude  DOUBLE PRECISION NOT NULL,
    expires_at TIMESTAMP        NOT NULL,
    CONSTRAINT uq_lat_lon UNIQUE (latitude, longitude)
);

CREATE TABLE properties
(
    id          UUID PRIMARY KEY,
    location_id UUID             NOT NULL,
    temperature DOUBLE PRECISION NOT NULL,
    wind_speed  DOUBLE PRECISION NOT NULL,
    timestamp   TIMESTAMP        NOT NULL,
    CONSTRAINT fk_location
        FOREIGN KEY (location_id)
            REFERENCES location (id)
            ON DELETE CASCADE
);
CREATE INDEX idx_properties_location_id ON properties (location_id);

CREATE TABLE event
(
    id          UUID PRIMARY KEY,
    longitude   DOUBLE PRECISION NOT NULL,
    latitude    DOUBLE PRECISION NOT NULL,
    temperature DOUBLE PRECISION,
    wind_speed  DOUBLE PRECISION,
    start_time  TIMESTAMP        NOT NULL
);