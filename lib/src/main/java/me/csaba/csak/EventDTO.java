package me.csaba.csak;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class EventDTO {
    private UUID id;
    @NotBlank
    private String name;
    @NotNull
    private Double longitude;
    @NotNull
    private Double latitude;
    @NotNull
    private Instant startTime;
    @NotNull
    private Instant endTime;
    private Double temperature;
    private Double windSpeed;
}
