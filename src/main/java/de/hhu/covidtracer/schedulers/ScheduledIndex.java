package de.hhu.covidtracer.schedulers;

import de.hhu.covidtracer.services.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledIndex {
    private final SchedulerService schedulerService;
    @Value("custom.config.index.delete.afterDays")
    String afterDays;

    @Autowired
    public ScheduledIndex(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }


    @Scheduled(cron = "@daily")
    public void changeQuarantineStatusDaily() {
        log.info("Index patients - Change quarantine status.");
        schedulerService.setScheduledQuarantineStatus(14);
    }


    @Scheduled(cron = "@daily")
    public void deleteIndexAfterDays() {
        int days;

        try {
            days = Integer.parseInt(this.afterDays);
        } catch (NumberFormatException e) {
            days = 30;
        }

        log.info("Index patient - deleting patients after " + days + " days");
        schedulerService.setScheduledDeletionAfterDays(days);
    }
}
