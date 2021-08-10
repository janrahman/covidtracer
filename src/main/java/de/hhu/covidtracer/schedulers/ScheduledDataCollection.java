package de.hhu.covidtracer.schedulers;

import de.hhu.covidtracer.services.DataCollectionService;
import de.hhu.covidtracer.services.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledDataCollection {
    private final SchedulerService schedulerService;

    @Autowired
    public ScheduledDataCollection(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }


    @Scheduled(cron = "@daily")
    public void saveDailySurvey() {
        int quarantineLapseDays = 14;

        log.info("DataCollection - Saving today's entries.");
        schedulerService.setSurvey(quarantineLapseDays);
    }
}
