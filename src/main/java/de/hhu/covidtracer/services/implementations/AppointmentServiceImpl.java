package de.hhu.covidtracer.services.implementations;

import de.hhu.covidtracer.dtos.AppointmentDTO;
import de.hhu.covidtracer.exceptions.AppointmentNotFoundException;
import de.hhu.covidtracer.mappers.AppointmentMapper;
import de.hhu.covidtracer.models.Appointment;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.repositories.AppointmentRepository;
import de.hhu.covidtracer.services.AppointmentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("AppointmentService")
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
    }


    @Override
    public List<AppointmentDTO> getAllDTOsById(long id) {
        return appointmentMapper
                .toAppointmentDTOs(appointmentRepository
                        .findAllByIndexPerson_Id(id));
    }


    @Override
    public List<Appointment> getAllByTodaysOrFutureDateTime() {
        return new ArrayList<>();
    }


    @Override
    public List<Appointment> getAllByMissedAppointments() {
        return new ArrayList<>();
    }


    @Override
    public Appointment getById(long appointmentId) {
        return appointmentRepository
                .findById(appointmentId)
                .orElseThrow(
                        () -> new AppointmentNotFoundException(
                                "Appointment not found."));
    }


    @Override
    public Appointment update(
            AppointmentDTO appointmentDTO,
            long appointmentId) {
        Appointment updateAppointment = getById(appointmentId);

        appointmentMapper.updateModel(appointmentDTO, updateAppointment);

        return appointmentRepository.save(updateAppointment);
    }


    @Override
    public void delete(Appointment appointment) {
        if (appointmentRepository.existsById(appointment.getId())) {
            if (appointment.getIndexPerson() != null) {
                IndexPerson indexPerson = appointment.getIndexPerson();

                indexPerson.removeAppointment(appointment);
            }

            appointmentRepository.delete(appointment);
        }
    }


    @Override
    public AppointmentDTO getDTOById(long appointmentId) {
        return appointmentMapper
                .toAppointmentDTO(getById(appointmentId));
    }


    @Override
    public Appointment createFromDTO(AppointmentDTO appointmentForm) {
        return appointmentMapper.toAppointment(appointmentForm);
    }
}
