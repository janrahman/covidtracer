package de.hhu.covidtracer.services;

public interface SchedulerService {
    void setScheduledQuarantineStatus(int days);

    void setScheduledDeletionAfterDays(int days);

    void setSurvey(int quarantineLapseDays);
}
