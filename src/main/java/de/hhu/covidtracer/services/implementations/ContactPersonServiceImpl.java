package de.hhu.covidtracer.services.implementations;

import de.hhu.covidtracer.dtos.ContactPersonDTO;
import de.hhu.covidtracer.exceptions.ContactPersonException;
import de.hhu.covidtracer.exceptions.ContactPersonNotFoundException;
import de.hhu.covidtracer.exceptions.ReportException;
import de.hhu.covidtracer.mappers.ContactPersonMapper;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.Report;
import de.hhu.covidtracer.models.Status;
import de.hhu.covidtracer.models.associations.IndexContact;
import de.hhu.covidtracer.repositories.ContactPersonRepository;
import de.hhu.covidtracer.services.ContactPersonService;
import de.hhu.covidtracer.services.IndexContactService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static de.hhu.covidtracer.models.Status.STAFF;
import static de.hhu.covidtracer.models.Status.VISITOR;

@Service("ContactPersonService")
public class ContactPersonServiceImpl implements ContactPersonService {
    private final ContactPersonRepository contactPersonRepository;
    private final ContactPersonMapper contactPersonMapper;
    private final IndexContactService indexContactService;

    public ContactPersonServiceImpl(
            ContactPersonRepository contactPersonRepository,
            ContactPersonMapper contactPersonMapper,
            IndexContactService indexContactService) {
        this.contactPersonRepository = contactPersonRepository;
        this.contactPersonMapper = contactPersonMapper;
        this.indexContactService = indexContactService;
    }


    @Override
    public ContactPerson getById(long id) {
        return contactPersonRepository
                .findById(id)
                .orElseThrow(
                        () -> new ContactPersonNotFoundException(
                                "Contact not found."));
    }


    @Override
    public ContactPerson update(
            long id,
            ContactPersonDTO contactPersonDTO) {
        ContactPerson query = getById(id);
        contactPersonMapper.updateModel(contactPersonDTO, query);
        return contactPersonRepository.save(query);
    }


    @Override
    public ContactPerson save(ContactPerson contactPerson) {
        return contactPersonRepository.save(contactPerson);
    }


    @Override
    public void remove(ContactPerson contactPerson) {
        Set<IndexContact> indexContacts = new HashSet<>(
                contactPerson.getIndexContacts());
        List<Report> reports = new ArrayList<>(
                contactPerson.getReports());

        indexContacts.forEach(indexContact -> indexContact
                .setContactPerson(null));
        reports.forEach(contactPerson::removeReport);
        contactPerson.setStation(null);
        contactPersonRepository.delete(contactPerson);
    }


    @Override
    public void removeOrphans(List<Long> contactIds) {
        if (contactIds == null || contactIds.isEmpty()) return;

        List<ContactPerson> contactPersonList = contactIds.stream()
                .filter(Objects::nonNull)
                .map(this::getById)
                .collect(Collectors.toList());

        contactPersonList.stream()
                .filter(contactPerson -> contactPerson
                        .getIndexContacts()
                        .isEmpty())
                .forEach(this::remove);
    }


    @Override
    public List<ContactPerson> saveAll(List<ContactPerson> contactPersonList) {
        return (List<ContactPerson>) contactPersonRepository
                .saveAll(contactPersonList);
    }


    @Override
    public ContactPerson getContactFromFile(ContactPersonDTO contactPersonDTO) {
        if (contactPersonDTO == null) {
            throw new ContactPersonException("Contact from file is null.");
        }

        ContactPerson newContact = contactPersonMapper
                .toContactPerson(contactPersonDTO);

        Optional<ContactPerson> contactPerson = contactPersonRepository
                .findByNameAndFirstNameAndAddress(
                        newContact.getName(),
                        newContact.getFirstName(),
                        newContact.getAddress());

        return contactPerson
                .map(c -> {
                    contactPersonMapper.updateModel(contactPersonDTO, c);
                    return c;
                })
                .orElse(newContact);
    }


    @Override
    public long countStaffContacts() {
        return contactPersonRepository.countByStatus(STAFF);
    }


    @Override
    public ContactPersonDTO getDTOById(long id) {
        return contactPersonMapper.toContactPersonDTO(getById(id));
    }


    @Override
    public ContactPerson saveReport(Report report, long contactId) {
        ContactPerson contactPerson = getById(contactId);

        if (report == null || contactPerson.getStatus() == VISITOR) {
            return contactPerson;
        }

        if (report.getIndexPerson() != null) {
            throw new ReportException(
                    "Report belongs to index");
        }

        contactPerson.addReport(report);

        return save(contactPerson);
    }


    @Override
    public boolean isVisitor(long contactId) {
        return contactPersonRepository
                .existsByIdAndStatus(contactId, VISITOR);
    }


    @Override
    public List<ContactPersonDTO> getDTOsByIndexId(long indexId) {
        List<IndexContact> indexContactList = indexContactService
                .getByIndexId(indexId);

        return indexContactList.stream()
                .filter(ic -> ic.getContactPerson() != null)
                .map(ic -> contactPersonMapper
                        .toContactPersonDTO(ic.getContactPerson(), ic))
                .collect(Collectors.toList());
    }


    @Override
    public List<ContactPersonDTO> getDTOsByIndexIdAndStatus(
            long indexId,
            Status status) {
        return getDTOsByIndexId(indexId).stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }


    @Override
    public ContactPersonDTO getDTOByIndexContactId(
            long indexId,
            long contactId) {
        ContactPerson contactPerson = getById(contactId);
        IndexContact indexContact = indexContactService
                .getByIndexContactId(indexId, contactId);

        return contactPersonMapper
                .toContactPersonDTO(contactPerson, indexContact);
    }
}
