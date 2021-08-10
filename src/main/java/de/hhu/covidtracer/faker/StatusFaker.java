package de.hhu.covidtracer.faker;

import com.github.javafaker.Faker;
import de.hhu.covidtracer.models.Status;

import static de.hhu.covidtracer.models.Status.*;

public class StatusFaker {
    private final Faker faker;

    public StatusFaker(Faker faker) {
        this.faker = faker;
    }

    public Status randomIndexStatus() {
        boolean random = faker.bool().bool();
        if (random) return STAFF;
        return PATIENT;
    }


    public Status randomContactStatus() {
        int randomNumber = faker.number().numberBetween(1, 4);

        if (randomNumber == 1) return STAFF;
        if (randomNumber == 2) return PATIENT;
        return VISITOR;
    }
}
