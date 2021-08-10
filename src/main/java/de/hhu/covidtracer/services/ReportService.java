package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.ReportDTO;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.Report;

import java.util.List;
import java.util.Optional;

public interface ReportService {
    Report getById(long id);

    Report update(ReportDTO reportDTO);

    Report save(Report report);

    void delete(Report report);

    void deleteExceptFirstReportFromIndex(
            IndexPerson indexPerson,
            long reportId);

    void deleteFromContact(ContactPerson contactPerson, long reportId);

    Report getFirstReport(long indexId);

    Optional<Report> getLatestReportByContactId(long contactId);

    void setFirstReportFromFile(IndexPerson indexPerson, Report report);

    List<Report> getReportsByContactPersonId(long contactId);

    Report toReport(ReportDTO reportDTO);

    ReportDTO getDTOById(long id);

    List<ReportDTO> getReportDTOsByIndexId(long id);

    ReportDTO getFirstReportDTO(long indexId);
}
