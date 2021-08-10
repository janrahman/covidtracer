package de.hhu.covidtracer.services.implementations;

import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.services.DataCollectionService;
import de.hhu.covidtracer.services.IndexPersonService;
import de.hhu.covidtracer.services.SchedulerService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service("SchedulerService")
public class SchedulerServiceImpl implements SchedulerService {
    private final IndexPersonService indexPersonService;
    private final DataCollectionService dataCollectionService;

    public SchedulerServiceImpl(
            IndexPersonService indexPersonService,
            DataCollectionService dataCollectionService) {
        this.indexPersonService = indexPersonService;
        this.dataCollectionService = dataCollectionService;
    }


    @Override
    public void setScheduledQuarantineStatus(int days) {
        Instant timestampMinusDays = Instant
                .now()
                .minus(Math.abs(days), ChronoUnit.DAYS);
        LocalDate dateMinusDays = timestampMinusDays
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        updateQuarantineStatusByEntryTime(timestampMinusDays);
        updateQuarantineStatusByFirstReport(dateMinusDays);
    }


    private void updateQuarantineStatusByFirstReport(LocalDate dateMinusDays) {
        indexPersonService
                .getAllByFirstReportDateBeforeLocalDate(dateMinusDays).stream()
                .filter(IndexPerson::isInQuarantine)
                .forEach(i -> {
                    i.setInQuarantine(false);
                    indexPersonService.save(i);
                });
    }


    private void updateQuarantineStatusByEntryTime(Instant instant) {
        indexPersonService
                .getAllByEntryDateTimeLessEqualsThan(instant).stream()
                .filter(IndexPerson::isInQuarantine)
                .forEach(i -> {
                    i.setInQuarantine(false);
                    indexPersonService.save(i);
                });
    }


    @Override
    public void setScheduledDeletionAfterDays(int days) {
        Instant timestampMinusDays = Instant
                .now()
                .minus(Math.abs(days), ChronoUnit.DAYS);
        LocalDate dateMinusDays = timestampMinusDays
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        deleteIndexByEntryDateTime(timestampMinusDays);
        deleteIndexByFirstReportDate(dateMinusDays);
    }


    private void deleteIndexByFirstReportDate(LocalDate dateMinusDays) {
        indexPersonService
                .getAllByFirstReportDateBeforeLocalDate(dateMinusDays)
                .forEach(i -> indexPersonService.remove(i.getId()));
    }


    private void deleteIndexByEntryDateTime(Instant timestampMinusDays) {
        indexPersonService
                .getAllByEntryDateTimeLessEqualsThan(timestampMinusDays)
                .forEach(i -> indexPersonService.remove(i.getId()));
    }


    @Override
    public void setSurvey(int quarantineLapseDays) {
        dataCollectionService.save(dataCollectionService
                .createDataCollection(Math.abs(quarantineLapseDays)));
    }
}
