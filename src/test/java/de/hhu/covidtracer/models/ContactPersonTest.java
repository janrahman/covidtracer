package de.hhu.covidtracer.models;

import de.hhu.covidtracer.helper.IndexPersonTestHelper;
import de.hhu.covidtracer.helper.ReportTestHelper;
import de.hhu.covidtracer.models.associations.IndexContact;
import de.hhu.covidtracer.models.associations.IndexContactId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.hhu.covidtracer.models.Status.STAFF;
import static org.junit.jupiter.api.Assertions.*;

public class ContactPersonTest {
    private static final ReportTestHelper REPORT_TEST_HELPER =
            new ReportTestHelper();
    private static final IndexPersonTestHelper INDEX_PERSON_TEST_HELPER =
            new IndexPersonTestHelper();


    @Test
    public void emptyIndexContact() {
        ContactPerson contactPerson = new ContactPerson();
        IndexContact indexContact = new IndexContact();

        contactPerson.removeIndexContact(indexContact);

        assertTrue(contactPerson.getIndexContacts().isEmpty());
    }


    @Test
    public void addOneIndexContact() {
        ContactPerson contactPerson = new ContactPerson();
        IndexContact indexContact = new IndexContact();

        contactPerson.addIndexContact(indexContact);

        assertEquals(1, contactPerson.getIndexContacts().size());
        assertEquals(contactPerson, indexContact.getContactPerson());
    }


    @Test
    public void removeOneIndexContact() {
        ContactPerson contactPerson = new ContactPerson();
        IndexContact indexContact = new IndexContact();

        contactPerson.addIndexContact(indexContact);
        contactPerson.removeIndexContact(indexContact);

        assertTrue(contactPerson.getIndexContacts().isEmpty());
    }


    @Test
    public void addTenIndexContacts() {
        IndexContact indexContact;
        ContactPerson contactPerson = new ContactPerson();

        int max = 10;
        List<IndexPerson> indexPersonList = INDEX_PERSON_TEST_HELPER
                .getIndexPeople(1, max, STAFF);

        contactPerson.setId(1L);

        for (IndexPerson indexPerson : indexPersonList) {
            indexContact = new IndexContact();

            indexContact
                    .setIndexContactId(
                            new IndexContactId(
                                    indexPerson.getId(),
                                    contactPerson.getId()));

            indexContact.setIndex(indexPerson);
            indexContact.setContactPerson(contactPerson);
        }

        assertEquals(max, contactPerson.getIndexContacts().size());
    }


    @Test
    public void addOneStation() {
        ContactPerson contactPerson = new ContactPerson();
        Station station = new Station();

        contactPerson.setStation(station);

        assertEquals(station, contactPerson.getStation());
        assertTrue(station.getContacts().contains(contactPerson));
    }


    @Test
    public void removeOneStation() {
        ContactPerson contactPerson = new ContactPerson();
        Station station = new Station();

        contactPerson.setStation(station);
        contactPerson.setStation(null);

        assertNull(contactPerson.getStation());
        assertTrue(station.getContacts().isEmpty());
    }


    @Test
    public void reassignNewStation() {
        ContactPerson contactPerson = new ContactPerson();
        Station station1 = new Station();
        Station station2 = new Station();

        contactPerson.setStation(station1);
        station1.removeContact(contactPerson);
        contactPerson.setStation(station2);

        assertEquals(station2, contactPerson.getStation());
        assertTrue(station1.getContacts().isEmpty());
    }


    @Test
    public void addOneReport() {
        ContactPerson contactPerson = new ContactPerson();
        Report report = new Report();

        contactPerson.addReport(report);

        assertTrue(contactPerson.getReports().contains(report));
        assertEquals(contactPerson, report.getContactPerson());
    }


    @Test
    public void addTenReports() {
        int max = 10;
        ContactPerson contactPerson = new ContactPerson();
        List<Report> reportList = REPORT_TEST_HELPER.getReports(1, max);

        for (Report report : reportList) {
            contactPerson.addReport(report);
        }

        assertEquals(max, contactPerson.getReports().size());
    }


    @Test
    public void removeOneReport() {
        ContactPerson contactPerson = new ContactPerson();
        Report report1 = REPORT_TEST_HELPER.createReport(1L);

        contactPerson.addReport(report1);
        contactPerson.removeReport(report1);

        assertNull(report1.getContactPerson());
        assertTrue(contactPerson.getReports().isEmpty());
    }


    @Test
    public void removeFirstReport() {
        ContactPerson contactPerson = new ContactPerson();
        Report report1 = REPORT_TEST_HELPER.createReport(1L);
        Report report2 = REPORT_TEST_HELPER.createReport(2L);

        contactPerson.addReport(report1);
        contactPerson.addReport(report2);
        contactPerson.removeReport(report1);

        assertEquals(1, contactPerson.getReports().size());
        assertNull(report1.getContactPerson());
        assertEquals(contactPerson, report2.getContactPerson());
        assertTrue(contactPerson.getReports().contains(report2));
    }
}