package de.hhu.covidtracer.repositories;

import de.hhu.covidtracer.models.associations.IndexContact;
import de.hhu.covidtracer.models.associations.IndexContactId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexContactRepository extends CrudRepository<IndexContact, IndexContactId> {
    List<IndexContact> findAllByIndexContactId_ContactId(long contactId);

    List<IndexContact> findAllByIndexContactId_IndexId(long indexId);

}
