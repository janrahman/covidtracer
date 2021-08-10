package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.ContactPersonDTO;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.Report;
import de.hhu.covidtracer.models.Status;
import de.hhu.covidtracer.models.associations.IndexContact;

import java.util.List;
import java.util.Set;

public interface ContactPersonService {

    ContactPerson getById(long id);

    ContactPerson update(long id, ContactPersonDTO contactDTO);

    ContactPerson save(ContactPerson contactPerson);

    void remove(ContactPerson contactPerson);

    void removeOrphans(List<Long> contactIds);

    List<ContactPerson> saveAll(List<ContactPerson> contactPersonList);

    ContactPerson getContactFromFile(ContactPersonDTO contactPersonDTO);

    long countStaffContacts();

    ContactPersonDTO getDTOById(long id);

    ContactPerson saveReport(Report report, long contactId);

    boolean isVisitor(long contactId);

    List<ContactPersonDTO> getDTOsByIndexId(long indexId);

    List<ContactPersonDTO> getDTOsByIndexIdAndStatus(
            long indexId,
            Status status);

    ContactPersonDTO getDTOByIndexContactId(long indexId, long contactId);
}
