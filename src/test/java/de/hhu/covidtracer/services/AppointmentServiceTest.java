package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.AppointmentDTO;
import de.hhu.covidtracer.helper.AppointmentTestHelper;
import de.hhu.covidtracer.mappers.AppointmentMapper;
import de.hhu.covidtracer.models.Appointment;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.repositories.AppointmentRepository;
import de.hhu.covidtracer.services.implementations.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {
    private final AppointmentTestHelper appointmentTestHelper = new AppointmentTestHelper();

    @Mock
    private AppointmentRepository appointmentRepository;

    private AppointmentService appointmentService;


    @BeforeEach
    public void setup() {
        AppointmentMapper appointmentMapper = Mappers
                .getMapper(AppointmentMapper.class);
        appointmentService = new AppointmentServiceImpl(
                appointmentRepository,
                appointmentMapper);
    }


    @Test
    public void getEmptyAppointmentsByIndexId() {
        long indexId = 0L;
        ArgumentCaptor<Long> indexIdArg = ArgumentCaptor
                .forClass(Long.class);
        List<AppointmentDTO> appointmentDTOList = appointmentService
                .getAllDTOsById(indexId);

        verify(
                appointmentRepository,
                times(1))
                .findAllByIndexPerson_Id(indexIdArg.capture());
        assertTrue(appointmentDTOList.isEmpty());
    }


    @Test
    public void getOneAppointmentByIndexId() {
        long indexId = 1L;
        List<Appointment> appointmentList = new ArrayList<>();
        Appointment appointment = new Appointment();

        appointmentList.add(appointment);
        when(appointmentRepository
                .findAllByIndexPerson_Id(indexId))
                .thenReturn(appointmentList);

        assertEquals(
                appointmentTestHelper
                        .toAppointmentDTOs(
                                appointmentList),
                appointmentService.getAllDTOsById(indexId));
    }


    @Test
    public void appointmentNotFoundById() {
        long id = 0L;

        assertThrows(
                ResponseStatusException.class,
                () -> appointmentService.getById(id));
    }


    @Test
    public void appointmentFoundById() {
        Appointment result;
        long id = 1L;
        Optional<Appointment> appointment = Optional.of(new Appointment());

        when(appointmentRepository.findById(id))
                .thenReturn(appointment);

        result = appointmentService.getById(id);

        assertEquals(appointment.get(), result);
    }


    @Test
    public void updateAppointmentNotFound() {
        long id = 0L;

        assertThrows(
                ResponseStatusException.class,
                () -> appointmentService
                        .update(new AppointmentDTO(), id));
    }


    @ParameterizedTest
    @ValueSource(strings = {"2021-01-24", "24.01.2021", "12-03-2021", "test", ""})
    public void updateDate(String updateDate) {
        long id = 1L;
        String time = LocalTime
                .of(13, 0)
                .toString();
        Appointment appointment = appointmentTestHelper
                .createAppointment(
                        id,
                        LocalDate
                                .of(2021, 1, 1)
                                .toString(),
                        time);
        AppointmentDTO appointmentDTO = appointmentTestHelper
                .createAppointmentDTO(
                        id,
                        updateDate,
                        time);

        when(appointmentRepository.findById(id))
                .thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment))
                .thenReturn(appointment);

        Appointment updatedAppointment = appointmentService
                .update(appointmentDTO, id);

        assertEquals(id, updatedAppointment.getId());
        assertEquals(time, updatedAppointment.getTime());
        assertEquals(
                updateDate,
                updatedAppointment.getDate());
    }


    @ParameterizedTest
    @ValueSource(strings = {"13:00", "13:14", "14:15PM", "test", ""})
    public void updateTime(String updateTime) {
        long id = 1L;
        String date = LocalDate
                .of(2021, 1, 12)
                .toString();
        Appointment appointment = appointmentTestHelper
                .createAppointment(
                        id,
                        date,
                        LocalTime
                                .of(12, 0)
                                .toString());
        AppointmentDTO appointmentDTO = appointmentTestHelper
                .createAppointmentDTO(
                        id,
                        date,
                        updateTime);

        when(appointmentRepository.findById(id))
                .thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment))
                .thenReturn(appointment);

        Appointment updatedAppointment = appointmentService
                .update(appointmentDTO, id);

        assertEquals(id, updatedAppointment.getId());
        assertEquals(date, updatedAppointment.getDate());
        assertEquals(
                updateTime,
                updatedAppointment.getTime());
    }


    @ParameterizedTest
    @ValueSource(longs = {2L, 3L, 4L, 5L, 312L, 4012L})
    public void updateIdShouldBeIgnored(long updateId) {
        long id = 1L;
        String date = LocalDate
                .of(2021, 2, 12)
                .toString();
        String time = LocalTime
                .of(12, 0)
                .toString();
        Appointment appointment = appointmentTestHelper
                .createAppointment(
                        id,
                        date,
                        time);
        AppointmentDTO appointmentDTO = appointmentTestHelper
                .createAppointmentDTO(
                        updateId,
                        date,
                        time);

        when(appointmentRepository.findById(id))
                .thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment))
                .thenReturn(appointment);

        Appointment updatedAppointment = appointmentService
                .update(appointmentDTO, id);

        assertEquals(id, updatedAppointment.getId());
        assertNotEquals(updateId, updatedAppointment.getId());
    }


    @Test
    public void noBehaviourOnDeleteIfAppointmentNotFound() {
        Appointment appointment = appointmentTestHelper
                .createAppointment(0L);
        ArgumentCaptor<Appointment> appointmentArgumentCaptor = ArgumentCaptor
                .forClass(Appointment.class);

        appointmentService.delete(appointment);

        verify(
                appointmentRepository,
                times(1))
                .existsById(anyLong());
        verify(
                appointmentRepository,
                times(0))
                .delete(appointmentArgumentCaptor.capture());
        assertNotNull(appointment);
    }


    @Test
    public void deleteAppointment() {
        long id = 1L;
        Appointment appointment = appointmentTestHelper
                .createAppointment(id);

        when(
                appointmentRepository
                        .existsById(id))
                .thenReturn(true);

        appointmentService.delete(appointment);

        verify(
                appointmentRepository,
                times(1))
                .delete(appointment);
    }


    @Test
    public void deleteAppointmentAndRemoveRelationshipIndex() {
        long id = 1L;
        Appointment appointment = appointmentTestHelper.createAppointment(id);
        IndexPerson indexPerson = new IndexPerson();

        appointment.setIndexPerson(indexPerson);
        when(appointmentRepository.existsById(id)).thenReturn(true);

        appointmentService.delete(appointment);

        verify(
                appointmentRepository,
                times(1))
                .delete(appointment);
        assertTrue(indexPerson.getAppointments().isEmpty());
        assertNull(appointment.getIndexPerson());
    }
}
