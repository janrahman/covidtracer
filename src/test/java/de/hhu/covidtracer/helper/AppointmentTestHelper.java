package de.hhu.covidtracer.helper;

import de.hhu.covidtracer.dtos.AppointmentDTO;
import de.hhu.covidtracer.mappers.AppointmentMapper;
import de.hhu.covidtracer.models.Appointment;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentTestHelper {
    private final AppointmentMapper appointmentMapper = Mappers
            .getMapper(AppointmentMapper.class);

    public List<Appointment> getAppointments(
            int startId,
            int size) {
        List<Appointment> appointmentList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Appointment appointment = createAppointment(startId++);

            appointmentList.add(appointment);
        }

        return appointmentList;
    }


    public Appointment createAppointment(long id) {
        return createAppointment(
                id,
                LocalDate
                        .now()
                        .toString(),
                LocalTime
                        .now()
                        .toString());
    }


    public Appointment createAppointment(
            long id,
            String date,
            String time) {
        Appointment appointment = new Appointment();

        appointment.setId(id);
        appointment.setDate(date);
        appointment.setTime(time);

        return appointment;
    }


    public AppointmentDTO createAppointmentDTO(
            long id,
            String date,
            String time) {
        return appointmentMapper
                .toAppointmentDTO(
                        createAppointment(
                                id,
                                date,
                                time));
    }


    public List<AppointmentDTO> toAppointmentDTOs(List<Appointment> appointmentList) {
        return appointmentMapper.toAppointmentDTOs(appointmentList);
    }
}
