package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.models.Appointment;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.Report;
import de.hhu.covidtracer.models.Status;

import javax.validation.Valid;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface IndexPersonService {
    IndexPerson getById(long id);

    IndexPersonDTO getDTOById(long id);

    int count();

    long countByStatusAndInQuarantine(Status status);

    long countStaffAbortedQuarantine(Instant dateTime, int quarantineLapse);

    long countNotReportedToHealthDepartment();

    long countNotReportedToSupervisor();

    long countPatientsNotInQuarantine();

    long countIndexInQuarantineByStatusAndFirstReportDate(
            Status status,
            Instant dateTime);

    IndexPerson update(@Valid IndexPersonDTO indexPersonDTO, long id);

    IndexPerson save(IndexPerson indexPerson);

    List<IndexPerson> saveAll(List<IndexPerson> indexPersonList);

    IndexPerson saveReport(long indexId, Report report);

    IndexPerson saveAppointmentToIndex(
            @Valid Appointment appointment,
            long indexId);

    IndexPerson getFromImportFile(IndexPersonDTO indexPersonDTO);

    void remove(long id);

    List<IndexPersonDTO> getAllDTOs();

    List<IndexPersonDTO> getAllDTOsByStatus(Status status);

    List<IndexPersonDTO> getAllDTOsByNotReportedToHealthDepartment();

    List<IndexPersonDTO> getAllDTOsByNotReportedToSupervisor();

    List<IndexPersonDTO> getAllDTOsByQuarantineAndBooleansInverted();

    List<IndexPersonDTO> getAllVisibleOrOwnerDTOs(String owner);

    List<IndexPerson> updateBatchFromMultiForm(List<IndexPersonDTO> indexPersonDTOList);

    List<IndexPerson> updateQuarantineBatchFromMultiForm(List<IndexPersonDTO> indexPersonDTOList);

    List<IndexPersonDTO> filterDTOListByVisibleOrOwner(
            List<IndexPersonDTO> indexPersonDTOList,
            String owner);

    List<IndexPersonDTO> getLastUploadedIndexPeople();

    List<IndexPersonDTO> getLastUploadedIndexPeopleByOwner(String owner);

    List<IndexPerson> getAllByEntryDateTimeLessEqualsThan(Instant instant);

    List<IndexPerson> getAll();

    List<IndexPerson> getAllByFirstReportDateBeforeLocalDate(LocalDate localDate);
}
