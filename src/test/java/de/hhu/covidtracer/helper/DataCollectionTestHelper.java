package de.hhu.covidtracer.helper;

import com.github.javafaker.Faker;
import de.hhu.covidtracer.dtos.DataCollectionDTO;
import de.hhu.covidtracer.models.DataCollection;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataCollectionTestHelper {
    private static final Faker FAKER = new Faker(new Locale("de-DE"));

    public DataCollection create(long id, Instant instant) {
        return DataCollection.builder()
                .id(id)
                .countNewPatientsInQuarantine(
                        FAKER.number().randomNumber())
                .countNewStaffInQuarantine(
                        FAKER.number().randomNumber())
                .countIndexStatusPatientsInQuarantine(
                        FAKER.number().randomNumber())
                .countIndexPersonStaffPauseQuarantine(
                        FAKER.number().randomNumber())
                .countIndexStatusStaffInQuarantine(
                        FAKER.number().randomNumber())
                .countPositiveContactStaff(
                        FAKER.number().randomNumber())
                .timestamp(instant)
                .build();
    }


    public DataCollectionDTO createDTO(
            long newPatients,
            long newStaff,
            Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

        return new DataCollectionDTO(
                formatter.format(instant),
                FAKER.number().randomNumber(),
                FAKER.number().randomNumber(),
                FAKER.number().randomNumber(),
                FAKER.number().randomNumber(),
                newPatients,
                newStaff);
    }

}
