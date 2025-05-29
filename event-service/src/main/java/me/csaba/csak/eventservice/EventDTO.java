package me.csaba.csak.eventservice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class EventDTO {
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
}
