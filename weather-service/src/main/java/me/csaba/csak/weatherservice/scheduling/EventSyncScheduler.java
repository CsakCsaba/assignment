package me.csaba.csak.weatherservice.scheduling;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventSyncScheduler {

    private final EventSyncTask eventSyncTask;


    @PostConstruct
    public void runOnStartup() {
        this.eventSyncTask.run();
    }

    @Scheduled(fixedRate = 30 * 60 * 1000) // every 30 minutes
    public void scheduledSync() {
        this.eventSyncTask.run();
    }
}
