package de.hhu.covidtracer.repositories;

import de.hhu.covidtracer.models.Report;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends CrudRepository<Report, Long> {
    List<Report> findAllByContactPerson_IdOrderByDateDescIdDesc(
            long contactPersonId);

    List<Report> findAllByIndexPerson_IdOrderByDateDescIdDesc(long indexId);

    Optional<Report> findFirstByContactPerson_IdOrderByDateDescIdDesc(
            long contactId);

    Optional<Report> findFirstByIndexPerson_IdOrderByIdAsc(long id);
}
