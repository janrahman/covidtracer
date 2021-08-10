package de.hhu.covidtracer.helper;

import com.github.javafaker.Faker;
import de.hhu.covidtracer.models.Station;

import java.util.Locale;

public class StationTestHelper {
    private static final Faker FAKER = new Faker(new Locale("de-DE"));

    public Station create(long id) {
        return create(
                id,
                FAKER.letterify("#####"),
                FAKER.letterify("#######"),
                FAKER.medical().hospitalName());
    }


    public Station create(long id, String name) {
        return create(
                id,
                name,
                FAKER.letterify("#######"),
                FAKER.medical().hospitalName());
    }


    public Station create(
            long id,
            String name,
            String department,
            String hospitalName) {
        Station station = new Station(name, department, hospitalName);

        station.setId(id);

        return station;
    }
}
