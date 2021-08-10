package de.hhu.covidtracer.models;

import de.hhu.covidtracer.helper.AppointmentTestHelper;
import de.hhu.covidtracer.helper.ContactPersonTestHelper;
import de.hhu.covidtracer.helper.ReportTestHelper;
import de.hhu.covidtracer.models.associations.IndexContact;
import de.hhu.covidtracer.models.associations.IndexContactId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static de.hhu.covidtracer.models.Status.VISITOR;
import static org.junit.jupiter.api.Assertions.*;

public class IndexPersonTest {
    private static final ContactPersonTestHelper CONTACT_PERSON_TEST_HELPER =
            new ContactPersonTestHelper();
    private static final ReportTestHelper REPORT_TEST_HELPER =
            new ReportTestHelper();
    private static final AppointmentTestHelper APPOINTMENT_TEST_HELPER =
            new AppointmentTestHelper();


    @Test
    public void emptyRelations() {
        IndexPerson indexPerson = new IndexPerson();

        assertTrue(indexPerson.getIndexContacts().isEmpty());
        assertTrue(indexPerson.getReports().isEmpty());
        assertTrue(indexPerson.getAppointments().isEmpty());
    }


    @Test
    public void addOneIndexContact() {
        IndexPerson indexPerson = new IndexPerson();
        IndexContact indexContact = new IndexContact();

        indexPerson.addIndexContact(indexContact);

        assertTrue(indexPerson.getIndexContacts().contains(indexContact));
        assertEquals(indexPerson, indexContact.getIndex());
    }


    @Test
    public void addOneReport() {
        IndexPerson indexPerson = new IndexPerson();
        Report report = new Report();

        indexPerson.addReport(report);

        assertEquals(indexPerson, report.getIndexPerson());
        assertTrue(indexPerson.getReports().contains(report));
    }


    @Test
    public void addOneAppointment() {
        IndexPerson indexPerson = new IndexPerson();
        Appointment appointment = new Appointment();

        indexPerson.addAppointment(appointment);

        assertEquals(indexPerson, appointment.getIndexPerson());
        assertTrue(indexPerson.getAppointments().contains(appointment));
    }


    @Test
    public void addOneIndexContactOneReportOneAppointment() {
        IndexPerson indexPerson = new IndexPerson();
        IndexContact indexContact = new IndexContact();
        Report report = new Report();
        Appointment appointment = new Appointment();

        indexPerson.addIndexContact(indexContact);
        indexPerson.addReport(report);
        indexPerson.addAppointment(appointment);

        assertTrue(indexPerson.getIndexContacts().contains(indexContact));
        assertTrue(indexPerson.getReports().contains(report));
        assertTrue(indexPerson.getAppointments().contains(appointment));
    }


    @Test
    public void removeOneIndexContact() {
        IndexPerson indexPerson = new IndexPerson();
        List<ContactPerson> contactPersonList = CONTACT_PERSON_TEST_HELPER
                .getContacts(1, 2, VISITOR);
        IndexContact indexContact1 = new IndexContact();
        IndexContact indexContact2 = new IndexContact();

        indexPerson.setId(1L);
        indexContact1.setContactPerson(contactPersonList.get(0));
        indexContact2.setContactPerson(contactPersonList.get(1));

        indexContact1
                .setIndexContactId(
                        new IndexContactId(
                                indexPerson.getId(),
                                contactPersonList.get(0).getId()));
        indexContact2
                .setIndexContactId(
                        new IndexContactId(
                                indexPerson.getId(),
                                contactPersonList.get(1).getId()));

        indexPerson.addIndexContact(indexContact1);
        indexPerson.addIndexContact(indexContact2);
        indexPerson.removeIndexContact(indexContact1);

        assertTrue(indexPerson.getIndexContacts().contains(indexContact2));
        assertFalse(indexPerson.getIndexContacts().contains(indexContact1));
        assertNull(indexContact1.getIndex());
        assertEquals(indexPerson, indexContact2.getIndex());
    }


    @Test
    public void removeOneReport() {
        IndexPerson indexPerson = new IndexPerson();
        Report report = new Report();

        indexPerson.addReport(report);
        indexPerson.removeReport(report);

        assertTrue(indexPerson.getReports().isEmpty());
        assertNull(report.getIndexPerson());
    }


    @Test
    public void removeOneAppointment() {
        IndexPerson indexPerson = new IndexPerson();
        Appointment appointment = new Appointment();

        indexPerson.addAppointment(appointment);
        indexPerson.removeAppointment(appointment);

        assertTrue(indexPerson.getAppointments().isEmpty());
        assertNull(appointment.getIndexPerson());
    }


    @Test
    public void addTenContactPeople() {
        IndexContact indexContact;

        int max = 10;
        IndexPerson indexPerson = new IndexPerson();
        List<ContactPerson> contactPersonList = CONTACT_PERSON_TEST_HELPER
                .getStaff(1, max);
        List<IndexContact> indexContactsList = new ArrayList<>();

        indexPerson.setId(1L);

        for (ContactPerson contactPerson : contactPersonList) {
            indexContact = new IndexContact();

            indexContact
                    .setIndexContactId(
                            new IndexContactId(
                                    indexPerson.getId(),
                                    contactPerson.getId()));
            indexContact.setIndex(indexPerson);
            indexContact.setContactPerson(contactPerson);
            indexContactsList.add(indexContact);
        }

        assertEquals(max, indexPerson.getIndexContacts().size());
    }


    @Test
    public void addTenReports() {
        int max = 10;
        IndexPerson indexPerson = new IndexPerson();
        List<Report> reportList = REPORT_TEST_HELPER.getReports(1, max);

        for (Report report : reportList) indexPerson.addReport(report);

        assertEquals(max, indexPerson.getReports().size());
    }


    @Test
    public void addTenAppointments() {
        int max = 10;
        IndexPerson indexPerson = new IndexPerson();
        List<Appointment> appointmentList = APPOINTMENT_TEST_HELPER.getAppointments(1, max);

        for (Appointment appointment : appointmentList) indexPerson.addAppointment(appointment);

        assertEquals(max, indexPerson.getAppointments().size());
    }
}
