package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.AppointmentDTO;
import de.hhu.covidtracer.models.Appointment;

import java.util.List;

public interface AppointmentService {
    List<AppointmentDTO> getAllDTOsById(long id);

    List<Appointment> getAllByTodaysOrFutureDateTime();

    List<Appointment> getAllByMissedAppointments();

    Appointment getById(long id);

    Appointment update(AppointmentDTO appointment, long appointmentId);

    void delete(Appointment appointmentId);

    AppointmentDTO getDTOById(long appointmentId);

    Appointment createFromDTO(AppointmentDTO appointmentForm);
}
