package de.hhu.covidtracer.repositories;

import de.hhu.covidtracer.models.DataCollection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DataCollectionRepository extends CrudRepository<DataCollection, Long> {
    List<DataCollection> findAll();

    List<DataCollection> findAllByTimestampGreaterThanEqualAndTimestampLessThanEqual(
            Instant start,
            Instant end);
}
