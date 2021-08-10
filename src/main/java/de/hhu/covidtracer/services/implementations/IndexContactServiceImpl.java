package de.hhu.covidtracer.services.implementations;

import de.hhu.covidtracer.dtos.IndexContactDTO;
import de.hhu.covidtracer.exceptions.IndexContactNotFoundException;
import de.hhu.covidtracer.mappers.IndexContactMapper;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.associations.IndexContact;
import de.hhu.covidtracer.models.associations.IndexContactId;
import de.hhu.covidtracer.repositories.IndexContactRepository;
import de.hhu.covidtracer.services.IndexContactService;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service("IndexContactService")
public class IndexContactServiceImpl implements IndexContactService {
    private final IndexContactRepository indexContactRepository;
    private final IndexContactMapper indexContactMapper;
    private final ConversionService conversionService;

    public IndexContactServiceImpl(
            IndexContactRepository indexContactRepository,
            IndexContactMapper indexContactMapper,
            ConversionService conversionService) {
        this.indexContactRepository = indexContactRepository;
        this.indexContactMapper = indexContactMapper;
        this.conversionService = conversionService;
    }


    @Override
    public IndexContact save(IndexContact indexContact) {
        return indexContactRepository.save(indexContact);
    }

    @Override
    public void saveAll(List<IndexContact> indexContactList) {
        indexContactRepository.saveAll(indexContactList);
    }


    @Override
    public List<Long> getIndexIdsFromContactId(long contactId) {
        List<IndexContact> indexContactList = indexContactRepository
                .findAllByIndexContactId_ContactId(contactId);

        return indexContactList.stream()
                .map(e -> e.getIndexContactId().getIndexId())
                .collect(Collectors.toList());
    }


    @Override
    public List<IndexContactDTO> getDTOsFromIndexId(long indexId) {
        return indexContactMapper.toIndexContactDTOs(indexContactRepository
                .findAllByIndexContactId_IndexId(indexId));
    }


    @Override
    public List<IndexContactDTO> getDTOsFromContactPersonId(long id) {
        return indexContactMapper.toIndexContactDTOs(indexContactRepository
                .findAllByIndexContactId_ContactId(id));
    }


    @Override
    public List<IndexContact> getByIndexId(long indexId) {
        return indexContactRepository.findAllByIndexContactId_IndexId(indexId);
    }


    @Override
    public IndexContact getFromFile(
            IndexPerson indexPerson,
            ContactPerson contactPerson,
            String datePeriodStart,
            String datePeriodEnd) {
        if (indexPerson == null || contactPerson == null) {
            throw new IndexContactNotFoundException(
                    "Index or/and contact is/are null.");
        }

        return indexContactRepository
                .findById(new IndexContactId(
                        indexPerson.getId(),
                        contactPerson.getId()))
                .orElseGet(() -> {
                    IndexContact newIndexContact = new IndexContact();

                    newIndexContact.setIndex(indexPerson);
                    newIndexContact.setContactPerson(contactPerson);
                    newIndexContact.setContactDateStart(
                            convertLocalDate(datePeriodStart));
                    newIndexContact.setContactDateEnd(
                            convertLocalDate(datePeriodEnd));

                    return newIndexContact;
                });
    }


    @Override
    public IndexContact getByIndexContactId(long indexId, long contactId) {
        return indexContactRepository
                .findById(new IndexContactId(indexId, contactId))
                .orElseThrow(() -> new IndexContactNotFoundException(
                        "Index contact relationship not found."));
    }


    @Override
    public IndexContact update(
            long indexId,
            long contactId,
            String datePeriodStart,
            String datePeriodEnd) {
        IndexContact indexContact = getByIndexContactId(indexId, contactId);
        LocalDate contactDateStart = convertLocalDate(datePeriodStart);
        LocalDate contactDateEnd = convertLocalDate(datePeriodEnd);

        if (contactDateStart != null) {
            indexContact.setContactDateStart(contactDateStart);
        }

        if (contactDateEnd != null) {
            indexContact.setContactDateEnd(contactDateEnd);
        }

        return save(indexContact);
    }


    @Override
    public void remove(long indexId, long contactId) {
        IndexContact indexContact = getByIndexContactId(indexId, contactId);

        indexContact.setContactPerson(null);
        indexContact.setIndex(null);

        indexContactRepository.delete(indexContact);

    }


    private LocalDate convertLocalDate(String source) {
        return conversionService.convert(source, LocalDate.class);
    }
}
