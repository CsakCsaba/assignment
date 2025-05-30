package me.csaba.csak.weatherservice.scheduling;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class EventSyncScheduler {

    private final EventSyncTask eventSyncTask;

    @PostConstruct
    public void runOnStartup() {
        this.eventSyncTask.run();
    }

    @Scheduled(cron = "${scheduler.event-cron}")
    public void scheduledSync() {
        this.eventSyncTask.run();
    }
}
