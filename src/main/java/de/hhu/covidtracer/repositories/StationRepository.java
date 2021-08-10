package de.hhu.covidtracer.repositories;

import de.hhu.covidtracer.models.Station;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StationRepository extends CrudRepository<Station, Long> {
    List<Station> findAll();

    Optional<Station> findById(long id);

    Optional<Station> findByContacts_Id(long contactPersonId);

    Optional<Station> findByName(String name);

    boolean existsByName(String name);
}
