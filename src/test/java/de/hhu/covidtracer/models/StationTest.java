package de.hhu.covidtracer.models;

import de.hhu.covidtracer.helper.ContactPersonTestHelper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StationTest {
    private static final ContactPersonTestHelper CONTACT_PERSON_TEST_HELPER =
            new ContactPersonTestHelper();


    @Test
    public void emptyContact() {
        Station station = new Station();

        assertTrue(station.getContacts().isEmpty());
    }


    @Test
    public void addOneContactPerson() {
        Station station = new Station();
        ContactPerson contactPerson = new ContactPerson();

        station.addContact(contactPerson);

        assertEquals(1, station.getContacts().size());
        assertEquals(station, contactPerson.getStation());
    }


    @Test
    public void addOneStaffMember() {
        Station station = new Station();
        ContactPerson staffMember = new ContactPerson();

        station.addContact(staffMember);

        assertEquals(1, station.getContacts().size());
        assertEquals(station, staffMember.getStation());
    }


    @Test
    public void removeOneContactPerson() {
        Station station = new Station();
        ContactPerson contactPerson = new ContactPerson();

        station.addContact(contactPerson);
        station.removeContact(contactPerson);

        assertTrue(station.getContacts().isEmpty());
        assertNull(contactPerson.getStation());
    }


    @Test
    public void removeOneStaffMember() {
        Station station = new Station();
        ContactPerson staffMember = new ContactPerson();
        ContactPerson anotherPerson = new ContactPerson();

        staffMember.setId(1L);
        anotherPerson.setId(2L);

        station.addContact(staffMember);
        station.addContact(anotherPerson);
        station.removeContact(staffMember);

        assertEquals(1, station.getContacts().size());
        assertNull(staffMember.getStation());
    }


    @Test
    public void addTenContactPeople() {
        Station station = new Station();
        List<ContactPerson> contactPersonList = CONTACT_PERSON_TEST_HELPER
                .getContacts(1, 10, Status.VISITOR);

        for (ContactPerson contactPerson : contactPersonList) {
            station.addContact(contactPerson);
        }

        assertEquals(10, station.getContacts().size());
    }


    @Test
    public void addTenStaffMember() {
        int max = 10;
        Station station = new Station();
        List<ContactPerson> staffMemberList = CONTACT_PERSON_TEST_HELPER
                .getStaff(1, max);

        for (ContactPerson contactPerson : staffMemberList) {
            station.addContact(contactPerson);
        }

        assertEquals(max, station.getContacts().size());
    }
}
