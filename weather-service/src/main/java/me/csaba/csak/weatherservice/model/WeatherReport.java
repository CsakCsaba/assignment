package me.csaba.csak.weatherservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherReport(
            @JsonProperty("properties") Properties properties) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Properties(
            @JsonProperty("timeseries") List<TimeseriesEntry> timeseries) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TimeseriesEntry(
            @JsonProperty("time") LocalDateTime time,
            @JsonProperty("data") TimeData data) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TimeData(
            @JsonProperty("instant") Instant instant) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Instant(
            @JsonProperty("details") InstantDetails details) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record InstantDetails(
            @JsonProperty("air_temperature") double airTemperature,
            @JsonProperty("wind_speed") double windSpeed) {

    }

}
