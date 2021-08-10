package de.hhu.covidtracer.faker;

import com.github.javafaker.Faker;
import de.hhu.covidtracer.models.Address;

import java.util.List;

public class AddressFaker {
    private final Faker faker;

    public AddressFaker(Faker faker) {
        this.faker = faker;
    }


    public Address create() {
        Address address = new Address();

        address.setStreet(faker.address().streetAddress());
        address.setPostcode(faker.address().zipCode());
        address.setCity(faker.address().city());

        return address;
    }


    public void addAddresses(List<Address> addresses, int count) {
        for (int i = 0; i < count; i++) {
            addresses.add(create());
        }
    }
}
