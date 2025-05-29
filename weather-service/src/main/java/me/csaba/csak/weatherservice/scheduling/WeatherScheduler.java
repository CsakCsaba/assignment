package me.csaba.csak.weatherservice.scheduling;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WeatherScheduler {

    private final WeatherSyncTask weatherSyncTask;

    @Scheduled(cron = "${scheduler.weather-cron}")
    public void scheduledSync() {
        this.weatherSyncTask.run();
    }
}
