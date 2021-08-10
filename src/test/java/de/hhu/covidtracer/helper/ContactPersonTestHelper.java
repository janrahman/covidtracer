package de.hhu.covidtracer.helper;

import com.github.javafaker.Faker;
import de.hhu.covidtracer.dtos.ContactPersonDTO;
import de.hhu.covidtracer.models.Status;
import de.hhu.covidtracer.models.ContactPerson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactPersonTestHelper {
    private static final Faker FAKER = new Faker(new Locale("de-DE"));
    private static final AddressTestHelper ADDRESS_TEST_HELPER =
            new AddressTestHelper();


    public ContactPerson getContact(long id, Status status) {
        ContactPerson contactPerson = new ContactPerson();

        contactPerson.setId(id);
        contactPerson.setName(FAKER.name().lastName());
        contactPerson.setFirstName(FAKER.name().firstName());
        contactPerson.setAddress(ADDRESS_TEST_HELPER.getAddress());
        contactPerson.setPhone(FAKER.phoneNumber().phoneNumber());
        contactPerson.setEmail(contactPerson.getName() + "@example.org");
        contactPerson.setStatus(status);

        if (status == Status.STAFF) {
            contactPerson.setOccupationGroup(status.getName());
            contactPerson.setCategory("I");
        }

        return contactPerson;
    }


    public List<ContactPerson> getContacts(int startIndex, int number, Status status) {
        ContactPerson contactPerson;
        List<ContactPerson> contacts = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            contactPerson = new ContactPerson();
            contactPerson.setStatus(status);
            contactPerson.setId(startIndex + i);
            contacts.add(contactPerson);
        }
        return contacts;
    }


    public List<ContactPerson> getStaff(int startIndex, int number) {
        return getContacts(startIndex, number, Status.STAFF);
    }


    public ContactPersonDTO getDTO(long id, Status status) {
        ContactPersonDTO dto = new ContactPersonDTO();

        dto.setId(id);
        dto.setName(FAKER.name().lastName());
        dto.setFirstName(FAKER.name().firstName());
        dto.setAddress(ADDRESS_TEST_HELPER.getDTO());
        dto.setPhone(FAKER.phoneNumber().phoneNumber());
        dto.setEmail(dto.getName() + "@example.org");
        dto.setStatus(status);

        if (status == Status.STAFF) {
            dto.setOccupationGroup(status.getName());
            dto.setCategory("I");
        }

        return dto;
    }


    public List<ContactPersonDTO> getDTOs(long id, int size, Status status) {
        List<ContactPersonDTO> contactPersonDTOList = new ArrayList<>();

        for (int i = 0; i < size; i++) contactPersonDTOList
                .add(getDTO(id++, status));

        return contactPersonDTOList;
    }
}
