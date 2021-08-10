package de.hhu.covidtracer.repositories;

import de.hhu.covidtracer.models.Address;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactPersonRepository extends CrudRepository<ContactPerson, Long> {

    Optional<ContactPerson> findById(long id);

    Optional<ContactPerson> findByNameAndFirstNameAndAddress(
            String name,
            String firstName,
            Address address);

    long countByStatus(Status status);

    boolean existsByIdAndStatus(long id, Status status);
}
