package de.hhu.covidtracer.faker;

import com.github.javafaker.Faker;
import de.hhu.covidtracer.models.Address;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.Status;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class IndexPersonFaker {
    private final Faker faker;

    public IndexPersonFaker(Faker faker) {
        this.faker = faker;
    }


    private IndexPerson createIndexByStatus(
            Status status,
            Address address,
            ReportFaker reportFaker) {
        IndexPerson person = IndexPerson.builder()
                .name(faker.name().lastName())
                .firstName(faker.name().firstName())
                .birthday(faker.date().birthday()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .address(address)
                .status(status)
                .visible(true)
                .build();

        reportFaker.createFirstReport(person);

        return person;
    }


    public List<IndexPerson> getIndex(
            int amountIndex,
            ReportFaker reportFaker,
            AddressFaker addressFaker,
            StatusFaker statusFaker) {
        List<IndexPerson> indexPersonList = new ArrayList<>();

        for (int i = 0; i < amountIndex; i++) {
            indexPersonList.add(
                    createIndexByStatus(
                            statusFaker.randomIndexStatus(),
                            addressFaker.create(),
                            reportFaker));
        }

        return indexPersonList;
    }
}
