package de.hhu.covidtracer.dtos;

import lombok.Value;

@Value
public class DataCollectionDTO {
    String localTimestamp;

    long countIndexStatusPatientsInQuarantine;
    long countIndexStatusStaffInQuarantine;
    long countPositiveContactStaff;
    long countIndexPersonStaffPauseQuarantine;
    long countNewPatientsInQuarantine;
    long countNewStaffInQuarantine;
}
