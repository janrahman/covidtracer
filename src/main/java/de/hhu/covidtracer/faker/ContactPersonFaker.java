package de.hhu.covidtracer.faker;

import com.github.javafaker.Faker;
import de.hhu.covidtracer.models.*;

import java.util.ArrayList;
import java.util.List;

import static de.hhu.covidtracer.models.Status.*;

public class ContactPersonFaker {
    private final static String UKD_EMAIL_DOMAIN="@ukd.org";
    private final Faker faker;

    public ContactPersonFaker(Faker faker) {
        this.faker = faker;
    }

    public ContactPerson create(
            Address address,
            Station station,
            ReportFaker reportFaker,
            StatusFaker statusFaker) {
        String fakeName = faker.name().lastName();
        String fakeFirstName = faker.name().firstName();
        String fakeMail = fakeFirstName + fakeName + "@example.org";

        ContactPerson contactPerson = ContactPerson.builder()
                .name(fakeName)
                .firstName(fakeFirstName)
                .address(address)
                .phone(faker.phoneNumber().phoneNumber())
                .email(fakeMail.toLowerCase())
                .status(statusFaker.randomContactStatus())
                .build();

        if (contactPerson.getStatus() != VISITOR) {
            reportFaker.createContactReport(contactPerson);
        }
        if (contactPerson.getStatus() == STAFF) {
            contactPerson.setEmail(
                    replaceToUkdMail(
                            contactPerson.getEmail()));
        }

        station.addContact(contactPerson);

        return contactPerson;
    }


    private String replaceToUkdMail(String email) {
        return email
                .replace(email
                        .substring(email.indexOf("@")), UKD_EMAIL_DOMAIN);
    }


    public List<ContactPerson> getContacts(
            int amount,
            List<Station> fakeStations,
            AddressFaker addressFaker,
            ReportFaker reportFaker,
            StatusFaker statusFaker) {
        List<ContactPerson> contactPersonList = new ArrayList<>();
        int randomStation = faker
                .number()
                .numberBetween(0, fakeStations.size() - 1);

        for (int i = 0; i < amount; i++) {
            contactPersonList
                    .add(create(
                            addressFaker.create(),
                            fakeStations.get(randomStation),
                            reportFaker,
                            statusFaker));
        }

        return contactPersonList;
    }
}
