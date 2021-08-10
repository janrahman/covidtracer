package de.hhu.covidtracer.repositories;

import de.hhu.covidtracer.models.Appointment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends CrudRepository<Appointment, Long> {
    List<Appointment> findAllByIndexPerson_Id(long id);

    //List<Appointment> findAllByStartGreaterThanEqual(LocalDateTime start);

    //List<Appointment> findAllByStartLessThan(LocalDateTime start);
}
