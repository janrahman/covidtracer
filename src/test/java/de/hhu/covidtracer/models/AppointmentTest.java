package de.hhu.covidtracer.models;

import de.hhu.covidtracer.helper.AppointmentTestHelper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentTest {
    private final Appointment appointment = new Appointment();
    private final AppointmentTestHelper appointmentTestHelper = new AppointmentTestHelper();


    @Test
    public void emptyIndexPerson() {
        assertNull(appointment.getIndexPerson());
    }


    @Test
    public void addOneIndexPerson() {
        IndexPerson indexPerson = new IndexPerson();

        appointment.setIndexPerson(indexPerson);

        assertTrue(indexPerson.getAppointments().contains(appointment));
        assertEquals(indexPerson, appointment.getIndexPerson());
    }


    @Test
    public void removeOneIndexPerson() {
        IndexPerson indexPerson = new IndexPerson();

        appointment.setIndexPerson(indexPerson);
        appointment.setIndexPerson(null);

        assertNull(appointment.getIndexPerson());
        assertTrue(indexPerson.getAppointments().isEmpty());
    }


    @Test
    public void addTenAppointmentsToIndex() {
        int max = 10;
        IndexPerson indexPerson = new IndexPerson();
        List<Appointment> appointmentList = appointmentTestHelper.getAppointments(1, max);

        for (Appointment appointment : appointmentList) indexPerson.addAppointment(appointment);

        assertEquals(max, indexPerson.getAppointments().size());
    }
}
