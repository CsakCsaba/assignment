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

CREATE TABLE event
(
    id         UUID PRIMARY KEY,
    longitude  DOUBLE PRECISION NOT NULL,
    latitude   DOUBLE PRECISION NOT NULL,
    start_time TIMESTAMP        NOT NULL
);