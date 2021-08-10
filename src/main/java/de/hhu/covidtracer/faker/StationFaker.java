package de.hhu.covidtracer.faker;

import com.github.javafaker.Faker;
import de.hhu.covidtracer.models.Station;

import java.util.ArrayList;
import java.util.List;

public class StationFaker {
    private final Faker faker;

    public StationFaker(Faker faker) {
        this.faker = faker;
    }


    public Station create() {
        Station station = new Station();

        station.setName(faker.bothify("???##_station"));
        station.setDepartment(faker.bothify("???##_department"));
        station.setHospitalName(faker.medical().hospitalName());

        return station;
    }


    public Station createUKD() {
        Station ukd = new Station();

        ukd.setName(faker.bothify("???##_station"));
        ukd.setDepartment(faker.bothify("???##_department"));
        ukd.setHospitalName("UKD");

        return ukd;
    }


    public List<Station> getStations(int amountStations) {
        List<Station> stationList = new ArrayList<>();

        for (int i = 0; i < amountStations; i++) {
            boolean random = faker.bool().bool();
            if (random) stationList.add(create());
            if (!random) stationList.add(createUKD());
        }

        return stationList;
    }
}
