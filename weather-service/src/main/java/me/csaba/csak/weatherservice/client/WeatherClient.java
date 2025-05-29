package me.csaba.csak.weatherservice.client;

import me.csaba.csak.weatherservice.model.WeatherReport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weatherClient", url = "${weather-api.url}")
public interface WeatherClient {

    @GetMapping(value = "/weatherapi/locationforecast/2.0/compact", produces = "application/json")
    ResponseEntity<WeatherReport> getWeather(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon,
            @RequestHeader("User-Agent") String userAgent
    );
}
