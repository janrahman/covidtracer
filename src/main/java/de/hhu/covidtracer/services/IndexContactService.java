package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.IndexContactDTO;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.associations.IndexContact;

import java.util.List;

public interface IndexContactService {
    IndexContact save(IndexContact indexContact);

    void saveAll(List<IndexContact> indexContactList);

    List<Long> getIndexIdsFromContactId(long contactId);

    List<IndexContactDTO> getDTOsFromIndexId(long id);

    List<IndexContactDTO> getDTOsFromContactPersonId(long id);

    List<IndexContact> getByIndexId(long indexId);

    IndexContact getFromFile(
            IndexPerson indexPerson,
            ContactPerson contactPerson,
            String datePeriodStart,
            String datePeriodEnd);

    IndexContact getByIndexContactId(long indexId, long contactId);

    IndexContact update(
            long indexId,
            long contactId,
            String datePeriodStart,
            String datePeriodEnd);

    void remove(
            long indexPerson,
            long contactPerson);
}
