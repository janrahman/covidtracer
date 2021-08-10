package de.hhu.covidtracer.services.implementations;

import de.hhu.covidtracer.dtos.ReportDTO;
import de.hhu.covidtracer.exceptions.IndexPersonException;
import de.hhu.covidtracer.exceptions.ReportException;
import de.hhu.covidtracer.exceptions.ReportNotFoundException;
import de.hhu.covidtracer.exceptions.UploadException;
import de.hhu.covidtracer.mappers.ReportMapper;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.Report;
import de.hhu.covidtracer.repositories.ReportRepository;
import de.hhu.covidtracer.services.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("ReportService")
@Slf4j
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    public ReportServiceImpl(
            ReportRepository reportRepository,
            ReportMapper reportMapper) {
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
    }


    @Override
    public Report getById(long id) {
        return reportRepository
                .findById(id)
                .orElseThrow(
                        () -> new ReportNotFoundException(
                                "Report not found."));
    }


    @Override
    public Report update(ReportDTO reportDTO) {
        Report updateReport = getById(reportDTO.getId());

        reportMapper.updateModel(reportDTO, updateReport);

        return save(updateReport);
    }


    @Override
    public Report save(Report report) {
        return reportRepository.save(report);
    }


    @Override
    public void delete(Report report) {
        if (reportRepository.existsById(report.getId())) {
            reportRepository.delete(report);
        }
    }


    @Override
    public void deleteExceptFirstReportFromIndex(
            IndexPerson indexPerson,
            long reportId) {
        if (indexPerson == null) return;

        Report deleteReport = getById(reportId);

        if (!isAssociatedToIndex(deleteReport, indexPerson.getId())) {
            throw new ReportException("Report does not belong to index.");
        }

        Report firstReport = getFirstReport(indexPerson.getId());

        if (firstReport.getId() == reportId) {
            throw new ReportException("Cannot delete first report.");
        }

        indexPerson.removeReport(deleteReport);
        delete(deleteReport);
    }


    @Override
    public void deleteFromContact(ContactPerson contactPerson, long reportId) {
        if (contactPerson == null) return;

        Report deleteReport = getById(reportId);

        if (!isAssociatedToContact(deleteReport, contactPerson.getId())) {
            throw new ReportException("Report does not belong to contact.");
        }

        contactPerson.removeReport(deleteReport);
        delete(deleteReport);
    }


    @Override
    public Report getFirstReport(long indexId) {
        return reportRepository
                .findFirstByIndexPerson_IdOrderByIdAsc(indexId)
                .orElseThrow(() -> new ReportNotFoundException(
                        "First report not found."));
    }


    @Override
    public Optional<Report> getLatestReportByContactId(
            long contactId) {
        return reportRepository
                .findFirstByContactPerson_IdOrderByDateDescIdDesc(contactId);
    }


    @Override
    public void setFirstReportFromFile(
            IndexPerson indexPerson,
            Report newReport) {
        if (indexPerson == null) {
            throw new IndexPersonException("Index is null.");
        }
        if (newReport == null) {
            throw new ReportException("Report is null.");
        }
        if (newReport.getDate() == null) {
            throw new UploadException("Invalid report date format.");
        }

        Optional<Report> firstReport = reportRepository
                .findFirstByIndexPerson_IdOrderByIdAsc(indexPerson.getId());

        firstReport.ifPresent(report -> report.setDate(newReport.getDate()));

        if (!firstReport.isPresent()) {
            indexPerson.addReport(newReport);
        }
    }


    @Override
    public List<Report> getReportsByContactPersonId(
            long contactId) {
        return reportRepository
                .findAllByContactPerson_IdOrderByDateDescIdDesc(
                        contactId);
    }


    @Override
    public Report toReport(ReportDTO reportDTO) {
        return reportMapper.toReport(reportDTO);
    }


    @Override
    public ReportDTO getDTOById(long id) {
        return reportMapper.toReportDTO(getById(id));
    }


    @Override
    public List<ReportDTO> getReportDTOsByIndexId(long indexId) {
        return reportMapper.toReportDTOs(reportRepository
                .findAllByIndexPerson_IdOrderByDateDescIdDesc(indexId));
    }


    @Override
    public ReportDTO getFirstReportDTO(long indexId) {
        return reportMapper.toReportDTO(getFirstReport(indexId));
    }


    private boolean isAssociatedToIndex(Report report, long indexId) {
        if (report.getIndexPerson() == null) return false;
        return report.getIndexPerson().getId() == indexId;
    }


    private boolean isAssociatedToContact(Report report, long contactId) {
        if (report.getContactPerson() == null) return false;
        return report.getContactPerson().getId() == contactId;
    }

}
