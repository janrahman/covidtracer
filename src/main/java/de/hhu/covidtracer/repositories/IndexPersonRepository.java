package de.hhu.covidtracer.repositories;

import de.hhu.covidtracer.models.Address;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IndexPersonRepository extends CrudRepository<IndexPerson, Long> {
    List<IndexPerson> findAll();

    List<IndexPerson> findAllByReportHealthDepartmentIsFalse();

    List<IndexPerson> findAllByStatusAndReportSupervisorIsFalse(Status status);

    List<IndexPerson> findAllByStatus(Status status);

    List<IndexPerson> findAllByEntryDateTimeLessThanEqual(Instant dateTime);

    List<IndexPerson> findAllByInQuarantineIsTrue();

    List<IndexPerson> findAllByStatusAndInQuarantine(
            Status status,
            boolean inQuarantine);

    List<IndexPerson> findAllByVisibleIsTrueOrOwner(String owner);

    List<IndexPerson> findFirst5ByOrderByIdDesc();

    List<IndexPerson> findFirst5ByOwnerOrderByIdDesc(String owner);

    Optional<IndexPerson> findById(long id);

    Optional<IndexPerson> findByNameAndFirstNameAndAddressAndBirthday(
            String name,
            String firstName,
            Address address,
            LocalDate birthday);

    long countByStatusAndInQuarantineIsTrue(Status status);

    long countByReportHealthDepartmentIsFalse();

    long countByStatusAndReportSupervisorIsFalse(Status status);

    long countByInQuarantineIsFalse();
}
