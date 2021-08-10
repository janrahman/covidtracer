package de.hhu.covidtracer.helper;

import com.github.javafaker.Faker;
import de.hhu.covidtracer.dtos.AddressDTO;
import de.hhu.covidtracer.models.Address;

import java.util.Locale;

public class AddressTestHelper {
    private static final Faker faker = new Faker(new Locale("de-DE"));

    public Address getAddress() {
        Address fakeAddress = new Address();

        fakeAddress.setStreet(faker.address().streetAddress());
        fakeAddress.setPostcode(faker.address().zipCode());
        fakeAddress.setCity(faker.address().city());

        return fakeAddress;
    }


    public AddressDTO getDTO() {
        AddressDTO addressDTO = new AddressDTO();

        addressDTO.setStreet(faker.address().streetAddress());
        addressDTO.setPostcode(faker.address().zipCode());
        addressDTO.setCity(faker.address().city());

        return addressDTO;
    }
}
