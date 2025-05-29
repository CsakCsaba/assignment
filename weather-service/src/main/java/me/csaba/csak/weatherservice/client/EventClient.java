package me.csaba.csak.weatherservice.client;

import me.csaba.csak.EventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "eventClient", url = "${event-api.url}")
public interface EventClient {

    @GetMapping("/events")
    List<EventDTO> getAllEvents();
}
