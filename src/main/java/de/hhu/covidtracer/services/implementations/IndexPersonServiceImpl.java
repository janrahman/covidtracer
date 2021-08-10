package de.hhu.covidtracer.services.implementations;

import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.exceptions.IndexPersonException;
import de.hhu.covidtracer.exceptions.IndexPersonNotFoundException;
import de.hhu.covidtracer.mappers.IndexPersonMapper;
import de.hhu.covidtracer.models.Appointment;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.Report;
import de.hhu.covidtracer.models.Status;
import de.hhu.covidtracer.models.associations.IndexContact;
import de.hhu.covidtracer.repositories.IndexPersonRepository;
import de.hhu.covidtracer.services.ContactPersonService;
import de.hhu.covidtracer.services.IndexContactService;
import de.hhu.covidtracer.services.IndexPersonService;
import de.hhu.covidtracer.services.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static de.hhu.covidtracer.models.Status.STAFF;

@Service("IndexPersonService")
@Validated
public class IndexPersonServiceImpl implements IndexPersonService {
    private final IndexPersonRepository indexPersonRepository;
    private final IndexPersonMapper indexPersonMapper;
    private final ContactPersonService contactPersonService;
    private final ReportService reportService;
    private final IndexContactService indexContactService;

    public IndexPersonServiceImpl(
            IndexPersonRepository indexPersonRepository,
            IndexPersonMapper indexPersonMapper,
            ContactPersonService contactPersonService,
            ReportService reportService,
            IndexContactService indexContactService) {
        this.indexPersonRepository = indexPersonRepository;
        this.indexPersonMapper = indexPersonMapper;
        this.contactPersonService = contactPersonService;
        this.reportService = reportService;
        this.indexContactService = indexContactService;
    }


    @Override
    public IndexPerson getById(long id) {
        return indexPersonRepository
                .findById(id)
                .orElseThrow(
                        () -> new IndexPersonNotFoundException(
                                "Index not found."));
    }


    @Override
    public IndexPersonDTO getDTOById(long id) {
        return indexPersonMapper
                .toIndexDTO(getById(id));
    }


    @Override
    public int count() {
        return (int) indexPersonRepository.count();
    }


    @Override
    public long countByStatusAndInQuarantine(Status status) {
        return indexPersonRepository.countByStatusAndInQuarantineIsTrue(status);
    }


    @Override
    public long countStaffAbortedQuarantine(
            Instant dateTime,
            int quarantineLapse) {
        List<IndexPerson> staffNotInQuarantine = indexPersonRepository
                .findAllByStatusAndInQuarantine(STAFF, false);
        LocalDate dateFromTimestamp = dateTime
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return staffNotInQuarantine.stream()
                .map(i -> reportService.getFirstReport(i.getId()).getDate())
                .filter(Objects::nonNull)
                .filter(firstReportDate -> firstReportDate
                        .plusDays(quarantineLapse)
                        .isAfter(dateFromTimestamp))
                .count();
    }


    @Override
    public long countNotReportedToHealthDepartment() {
        return indexPersonRepository
                .countByReportHealthDepartmentIsFalse();
    }


    @Override
    public long countNotReportedToSupervisor() {
        return indexPersonRepository
                .countByStatusAndReportSupervisorIsFalse(STAFF);
    }


    @Override
    public long countPatientsNotInQuarantine() {
        return indexPersonRepository
                .countByInQuarantineIsFalse();
    }


    @Override
    public long countIndexInQuarantineByStatusAndFirstReportDate(
            Status status,
            Instant timestamp) {
        LocalDate dateFromTimestamp = timestamp
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        List<IndexPerson> indexPersonList = indexPersonRepository
                .findAllByStatusAndInQuarantine(status, true);

        return indexPersonList.stream()
                .map(i -> reportService.getFirstReport(i.getId()).getDate())
                .filter(Objects::nonNull)
                .filter(firstReportDate -> firstReportDate
                        .isEqual(dateFromTimestamp))
                .count();
    }


    @Override
    public IndexPerson update(@Valid IndexPersonDTO indexPersonDTO, long id) {
        IndexPerson updateEntry = getById(id);

        indexPersonMapper.updateModel(indexPersonDTO, updateEntry);

        return save(updateEntry);
    }


    @Override
    public IndexPerson save(IndexPerson indexPerson) {
        return indexPersonRepository.save(indexPerson);
    }


    @Override
    public List<IndexPerson> saveAll(List<IndexPerson> indexPersonList) {
        return (List<IndexPerson>) indexPersonRepository
                .saveAll(indexPersonList);
    }


    @Override
    public IndexPerson saveReport(long indexId, Report report) {
        IndexPerson indexPerson = getById(indexId);

        indexPerson.addReport(report);

        return save(indexPerson);
    }


    @Override
    public IndexPerson saveAppointmentToIndex(
            @Valid Appointment appointment,
            long indexId) {
        IndexPerson indexPerson = getById(indexId);

        indexPerson.addAppointment(appointment);

        return save(indexPerson);
    }


    @Override
    public IndexPerson getFromImportFile(
            IndexPersonDTO indexPersonDTO) {
        if (indexPersonDTO == null) {
            throw new IndexPersonException("Index from file is null.");
        }

        IndexPerson newIndex = indexPersonMapper.toIndex(indexPersonDTO);

        Optional<IndexPerson> entry = indexPersonRepository
                .findByNameAndFirstNameAndAddressAndBirthday(
                        newIndex.getName(),
                        newIndex.getFirstName(),
                        newIndex.getAddress(),
                        newIndex.getBirthday());

        return entry
                .map(entryIndex -> {
                    indexPersonMapper.updateModel(indexPersonDTO, entryIndex);
                    return entryIndex;
                })
                .orElse(newIndex);
    }


    @Override
    public void remove(long id) {
        IndexPerson removeEntry = getById(id);

        removeIndexEntity(removeEntry);
    }


    @Override
    public List<IndexPersonDTO> getAllDTOs() {
        return indexPersonMapper
                .toIndexDTOs(getAll());
    }


    @Override
    public List<IndexPersonDTO> getAllDTOsByStatus(Status status) {
        return indexPersonMapper
                .toIndexDTOs(indexPersonRepository
                        .findAllByStatus(status));
    }


    @Override
    public List<IndexPersonDTO> getAllDTOsByNotReportedToHealthDepartment() {
        return indexPersonMapper
                .toIndexDTOs(indexPersonRepository
                        .findAllByReportHealthDepartmentIsFalse());
    }


    @Override
    public List<IndexPersonDTO> getAllDTOsByNotReportedToSupervisor() {
        return indexPersonMapper
                .toIndexDTOs(indexPersonRepository
                        .findAllByStatusAndReportSupervisorIsFalse(STAFF));
    }


    @Override
    public List<IndexPersonDTO> getAllDTOsByQuarantineAndBooleansInverted() {
        List<IndexPersonDTO> indexPersonDTOList = indexPersonMapper
                .toIndexDTOs(indexPersonRepository
                        .findAllByInQuarantineIsTrue());

        invertQuarantineFlags(indexPersonDTOList);
        return indexPersonDTOList;
    }


    @Override
    public List<IndexPersonDTO> getAllVisibleOrOwnerDTOs(String owner) {
        return indexPersonMapper
                .toIndexDTOs(indexPersonRepository
                        .findAllByVisibleIsTrueOrOwner(owner));
    }


    @Override
    public List<IndexPerson> updateBatchFromMultiForm(
            List<IndexPersonDTO> indexPersonDTOList) {
        List<IndexPerson> updateEntries = new ArrayList<>();

        if (indexPersonDTOList != null) {
            indexPersonDTOList.stream()
                    .filter(Objects::nonNull)
                    .forEach(indexPersonDTO -> {
                        IndexPerson entry = getById(indexPersonDTO.getId());
                        entry.setReportHealthDepartment(
                                indexPersonDTO.isReportHealthDepartment());
                        entry.setReportSupervisor(
                                indexPersonDTO.isReportSupervisor());
                        updateEntries.add(save(entry));
                    });
        }

        return updateEntries;
    }


    @Override
    public List<IndexPerson> updateQuarantineBatchFromMultiForm(
            List<IndexPersonDTO> indexPersonDTOList) {
        List<IndexPerson> updateEntries = new ArrayList<>();


        if (indexPersonDTOList != null) {
            invertQuarantineFlags(indexPersonDTOList);
            indexPersonDTOList.stream()
                    .filter(Objects::nonNull)
                    .forEach(dto -> {
                        IndexPerson entry = getById(dto.getId());

                        entry.setInQuarantine(dto.isInQuarantine());
                        updateEntries.add(save(entry));
                    });
        }

        return updateEntries;
    }


    @Override
    public List<IndexPersonDTO> filterDTOListByVisibleOrOwner(
            List<IndexPersonDTO> indexPersonDTOList,
            String owner) {
        if (indexPersonDTOList == null) {
            return new ArrayList<>();
        }
        if (owner == null) owner = "";

        String finalOwner = owner;

        return indexPersonDTOList
                .stream()
                .filter(dto -> dto.isVisible() ||
                        (dto.getOwner() != null &&
                                dto.getOwner().equals(finalOwner)))
                .collect(Collectors.toList());
    }


    @Override
    public List<IndexPersonDTO> getLastUploadedIndexPeople() {
        return indexPersonMapper.toIndexDTOs(
                indexPersonRepository.findFirst5ByOrderByIdDesc());
    }


    @Override
    public List<IndexPersonDTO> getLastUploadedIndexPeopleByOwner(
            String owner) {
        return indexPersonMapper.toIndexDTOs(
                indexPersonRepository.findFirst5ByOwnerOrderByIdDesc(owner));
    }


    @Override
    public List<IndexPerson> getAllByEntryDateTimeLessEqualsThan(
            Instant instant) {
        return indexPersonRepository
                .findAllByEntryDateTimeLessThanEqual(instant);
    }


    @Override
    public List<IndexPerson> getAll() {
        return indexPersonRepository.findAll();
    }


    @Override
    public List<IndexPerson> getAllByFirstReportDateBeforeLocalDate(
            LocalDate localDate) {
        List<IndexPerson> indexPersonList = indexPersonRepository.findAll();

        if (localDate == null) {
            return indexPersonList.stream()
                    .filter(i -> reportService
                            .getFirstReport(i.getId())
                            .getDate() == null)
                    .collect(Collectors.toList());
        }

        List<IndexPerson> result = new ArrayList<>();

        indexPersonList.forEach(i -> {
            LocalDate firstReportDate = reportService
                    .getFirstReport(i.getId())
                    .getDate();

            if (firstReportDate != null &&
                    firstReportDate.isBefore(localDate)) {
                result.add(i);
            }
        });

        return result;
    }


    private void removeIndexEntity(IndexPerson indexPerson) {
        Set<IndexContact> entryContacts = new HashSet<>(
                indexPerson.getIndexContacts());

        List<Long> associatedContactIds = entryContacts.stream()
                .map(ic -> ic.getIndexContactId().getContactId())
                .collect(Collectors.toList());

        entryContacts.forEach(ic -> indexContactService.remove(
                ic.getIndexContactId().getIndexId(),
                ic.getIndexContactId().getContactId()));
        indexPersonRepository.delete(indexPerson);
        contactPersonService.removeOrphans(associatedContactIds);
    }


    private void invertQuarantineFlags(
            List<IndexPersonDTO> indexPersonDTOList) {
        indexPersonDTOList.stream()
                .filter(Objects::nonNull)
                .forEach(dto -> dto.setInQuarantine(!dto.isInQuarantine()));
    }
}
