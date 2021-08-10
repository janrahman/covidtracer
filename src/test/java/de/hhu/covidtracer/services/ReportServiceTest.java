package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.ReportDTO;
import de.hhu.covidtracer.exceptions.IndexPersonException;
import de.hhu.covidtracer.exceptions.ReportException;
import de.hhu.covidtracer.exceptions.ReportNotFoundException;
import de.hhu.covidtracer.exceptions.UploadException;
import de.hhu.covidtracer.helper.ContactPersonTestHelper;
import de.hhu.covidtracer.helper.IndexPersonTestHelper;
import de.hhu.covidtracer.helper.ReportTestHelper;
import de.hhu.covidtracer.mappers.ReportMapper;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.Report;
import de.hhu.covidtracer.repositories.ReportRepository;
import de.hhu.covidtracer.services.implementations.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static de.hhu.covidtracer.models.Status.STAFF;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    private static final ReportTestHelper REPORT_TEST_HELPER =
            new ReportTestHelper();
    private static final IndexPersonTestHelper INDEX_PERSON_TEST_HELPER =
            new IndexPersonTestHelper();
    private static final ContactPersonTestHelper CONTACT_PERSON_TEST_HELPER =
            new ContactPersonTestHelper();

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportMapper reportMapper;

    private ReportService reportService;


    @BeforeEach
    public void setup() {
        reportService = new ReportServiceImpl(reportRepository, reportMapper);
    }


    @Nested
    public class GetEntities {
        @Test
        public void shouldThrowErrorWhenIdNotFound() {
            assertThrows(
                    ReportNotFoundException.class,
                    () -> reportService.getById(1L));
        }


        @Test
        public void shouldReturnWhenIdFound() {
            when(reportRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new Report()));

            reportService.getById(1L);

            verify(reportRepository, times(1))
                    .findById(anyLong());
        }


        @Test
        public void shouldThrowErrorOnFirstReportWhenIdNotFound() {
            assertThrows(
                    ReportNotFoundException.class,
                    () -> reportService.getFirstReport(1L));
        }


        @Test
        public void shouldReturnFirstReportWhenIdFound() {
            when(reportRepository
                    .findFirstByIndexPerson_IdOrderByIdAsc(anyLong()))
                    .thenReturn(Optional.of(new Report()));

            reportService.getFirstReport(1L);

            verify(reportRepository, times(1))
                    .findFirstByIndexPerson_IdOrderByIdAsc(anyLong());
        }


        @Test
        public void shouldReturnOptionalWhenContactIdIsGiven() {
            when(reportRepository
                    .findFirstByContactPerson_IdOrderByDateDescIdDesc(
                            anyLong()))
                    .thenReturn(Optional.empty());

            reportService.getLatestReportByContactId(1L);

            verify(reportRepository, times(1))
                    .findFirstByContactPerson_IdOrderByDateDescIdDesc(
                            anyLong());
        }


        @Test
        public void shouldReturnListWhenContactIdIsGiven() {
            when(reportRepository
                    .findAllByContactPerson_IdOrderByDateDescIdDesc(
                            anyLong()))
                    .thenReturn(new ArrayList<>());

            reportService.getReportsByContactPersonId(1L);

            verify(reportRepository, times(1))
                    .findAllByContactPerson_IdOrderByDateDescIdDesc(
                            anyLong());
        }


        @Test
        public void shouldReturnEntityWhenConvertFromDTO() {
            when(reportMapper.toReport(any())).thenReturn(new Report());

            reportService.toReport(new ReportDTO());

            verify(reportMapper, times(1))
                    .toReport(any());
        }
    }


    @Nested
    public class GetDTOs {
        @Test
        public void shouldThrowErrorWhenReportIdNotFound() {
            assertThrows(
                    ReportNotFoundException.class,
                    () -> reportService.getDTOById(1L));
        }


        @Test
        public void shouldReturnDTOWhenEntityFound() {
            when(reportRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new Report()));
            when(reportMapper.toReportDTO(any())).thenReturn(new ReportDTO());

            reportService.getDTOById(1L);

            verify(reportRepository, times(1))
                    .findById(anyLong());
            verify(reportMapper, times(1))
                    .toReportDTO(any());
        }


        @Test
        public void shouldReturnDTOListWhenIndexIdIsGiven() {
            when(reportRepository
                    .findAllByIndexPerson_IdOrderByDateDescIdDesc(anyLong()))
                    .thenReturn(new ArrayList<>());
            when(reportMapper.toReportDTOs(any()))
                    .thenReturn(new ArrayList<>());

            reportService.getReportDTOsByIndexId(1L);

            verify(reportRepository, times(1))
                    .findAllByIndexPerson_IdOrderByDateDescIdDesc(anyLong());
            verify(reportMapper, times(1))
                    .toReportDTOs(any());
        }


        @Test
        public void shouldThrowErrorWhenIndexIdNotFound() {
            assertThrows(
                    ReportNotFoundException.class,
                    () -> reportService.getFirstReportDTO(1L));
        }


        @Test
        public void shouldReturnFirstReportDTOWhenIndexIdIsGiven() {
            when(reportRepository
                    .findFirstByIndexPerson_IdOrderByIdAsc(anyLong()))
                    .thenReturn(Optional.of(new Report()));
            when(reportMapper.toReportDTO(any()))
                    .thenReturn(new ReportDTO());

            reportService.getFirstReportDTO(1L);

            verify(reportRepository, times(1))
                    .findFirstByIndexPerson_IdOrderByIdAsc(anyLong());
            verify(reportMapper, times(1))
                    .toReportDTO(any());
        }
    }


    @Nested
    public class SetFirstReportFromFile {
        @Test
        public void shouldAThrowErrorWhenIndexIsNull() {
            assertThrows(
                    IndexPersonException.class,
                    () -> reportService.setFirstReportFromFile(
                            null,
                            null));
        }


        @Test
        public void shouldAThrowErrorWhenReportIsNull() {
            assertThrows(
                    ReportException.class,
                    () -> reportService.setFirstReportFromFile(
                            new IndexPerson(),
                            null));
        }


        @Test
        public void shouldAThrowErrorWhenReportDateIsNull() {
            assertThrows(
                    UploadException.class,
                    () -> reportService.setFirstReportFromFile(
                            new IndexPerson(),
                            new Report()));
        }


        @Test
        public void shouldAddReportWhenNotFoundInDatabase() {
            Report report = REPORT_TEST_HELPER.createReport(0L);
            IndexPerson indexPerson = INDEX_PERSON_TEST_HELPER
                    .getIndexPerson(1L, STAFF);

            when(reportRepository
                    .findFirstByIndexPerson_IdOrderByIdAsc(anyLong()))
                    .thenReturn(Optional.empty());

            reportService.setFirstReportFromFile(indexPerson, report);

            assertTrue(indexPerson.getReports().contains(report));
            assertEquals(indexPerson, report.getIndexPerson());
        }


        @Test
        public void shouldUpdateDateWhenFirstReportFoundInDatabase() {
            Report report = REPORT_TEST_HELPER.createReport(1L);
            Report reportFromFile = REPORT_TEST_HELPER.createReport(2L);

            IndexPerson indexPerson = INDEX_PERSON_TEST_HELPER
                    .getIndexPerson(1L, STAFF);

            indexPerson.addReport(report);

            when(reportRepository
                    .findFirstByIndexPerson_IdOrderByIdAsc(anyLong()))
                    .thenReturn(Optional.of(report));

            reportService.setFirstReportFromFile(indexPerson, report);

            assertTrue(indexPerson.getReports().contains(report));
            assertEquals(indexPerson, report.getIndexPerson());
            assertEquals(reportFromFile.getDate(), report.getDate());
            assertNull(reportFromFile.getContactPerson());
            assertFalse(indexPerson.getReports().contains(reportFromFile));
        }
    }


    @Nested
    public class Update {
        @Test
        public void shouldThrowErrorWhenIdNotFound() {
            assertThrows(
                    ReportNotFoundException.class,
                    () -> reportService.update(new ReportDTO()));
        }


        @Test
        public void shouldUpdateWhenFound() {
            Report report = REPORT_TEST_HELPER.createReport(1L);
            ReportDTO reportDTO = new ReportDTO();

            when(reportRepository.findById(anyLong()))
                    .thenReturn(Optional.of(report));
            doNothing().when(reportMapper).updateModel(reportDTO, report);
            when(reportRepository.save(any())).thenReturn(report);

            reportService.update(reportDTO);

            verify(reportMapper, times(1))
                    .updateModel(any(), any());
            verify(reportRepository, times(1))
                    .save(any());
        }
    }


    @Nested
    public class Save {
        @Test
        public void shouldSave() {
            when(reportRepository.save(any())).thenReturn(new Report());

            reportService.save(new Report());

            verify(reportRepository, times(1))
                    .save(any());
        }
    }


    @Nested
    public class Remove {
        @Test
        public void shouldNotRemoveWhenReportDoesNotExist() {
            when(reportRepository.existsById(anyLong())).thenReturn(false);

            reportService.delete(new Report());

            verify(reportRepository, times(1))
                    .existsById(any());
            verify(reportRepository, times(0))
                    .delete(any());
        }


        @Test
        public void shouldRemoveWhenReportExists() {
            when(reportRepository.existsById(anyLong())).thenReturn(true);
            doNothing().when(reportRepository).delete(any());

            reportService.delete(new Report());

            verify(reportRepository, times(1))
                    .existsById(any());
            verify(reportRepository, times(1))
                    .delete(any());
        }


        @Test
        public void shouldThrowErrorWhenReportIdNotFoundIndex() {
            assertThrows(
                    ReportNotFoundException.class,
                    () -> reportService.deleteExceptFirstReportFromIndex(
                            new IndexPerson(),
                            1L));
        }


        @Test
        public void shouldNotRemoveReportWhenIndexIsNull() {
            reportService.deleteExceptFirstReportFromIndex(
                    null,
                    1L);

            verify(reportRepository, times(0))
                    .existsById(any());
            verify(reportRepository, times(0))
                    .delete(any());
            verify(reportRepository, times(0))
                    .findFirstByIndexPerson_IdOrderByIdAsc(anyLong());
        }


        @Test
        public void shouldThrowErrorWhenIndexHasNoFirstReport() {
            long reportId = 1L;
            Report report = new Report();
            IndexPerson indexPerson = new IndexPerson();

            report.setId(reportId);
            indexPerson.setId(1L);

            when(reportRepository.findById(anyLong()))
                    .thenReturn(Optional.of(report));

            assertThrows(
                    ReportException.class,
                    () -> reportService.deleteExceptFirstReportFromIndex(
                            indexPerson,
                            reportId));
        }


        @Test
        public void shouldRemoveReportWhenAssociatedToIndex() {
            long reportId = 2L;
            IndexPerson indexPerson = INDEX_PERSON_TEST_HELPER
                    .getIndexPerson(1L, STAFF);
            Report firstReport = REPORT_TEST_HELPER.createReport(1L);
            Report report = REPORT_TEST_HELPER.createReport(reportId);

            indexPerson.addReport(firstReport);
            indexPerson.addReport(report);

            when(reportRepository.findById(anyLong()))
                    .thenReturn(Optional.of(report));
            when(reportRepository
                    .findFirstByIndexPerson_IdOrderByIdAsc(anyLong()))
                    .thenReturn(Optional.of(firstReport));
            when(reportRepository.existsById(anyLong())).thenReturn(true);
            doNothing().when(reportRepository).delete(any());

            reportService.deleteExceptFirstReportFromIndex(
                    indexPerson,
                    reportId);

            assertEquals(1, indexPerson.getReports().size());
            assertTrue(indexPerson.getReports().contains(firstReport));
            assertNull(report.getIndexPerson());
            assertFalse(indexPerson.getReports().contains(report));
            verify(reportRepository, times(1))
                    .existsById(any());
            verify(reportRepository, times(1))
                    .delete(any());
            verify(reportRepository, times(1))
                    .findFirstByIndexPerson_IdOrderByIdAsc(anyLong());
        }


        @Test
        public void shouldThrowErrorWhenNotAssociatedToIndex() {
            long reportId = 2L;
            IndexPerson indexPerson = INDEX_PERSON_TEST_HELPER
                    .getIndexPerson(1L, STAFF);
            IndexPerson owner = INDEX_PERSON_TEST_HELPER
                    .getIndexPerson(2L, STAFF);
            Report firstReport = REPORT_TEST_HELPER.createReport(1L);
            Report report = REPORT_TEST_HELPER.createReport(reportId);

            indexPerson.addReport(firstReport);
            owner.addReport(report);

            when(reportRepository.findById(anyLong()))
                    .thenReturn(Optional.of(report));

            assertThrows(
                    ReportException.class,
                    () -> reportService.deleteExceptFirstReportFromIndex(
                            indexPerson,
                            reportId));

        }


        @Test
        public void shouldThrowErrorWhenFirstReport() {
            long reportId = 2L;
            IndexPerson indexPerson = INDEX_PERSON_TEST_HELPER
                    .getIndexPerson(1L, STAFF);
            Report firstReport = REPORT_TEST_HELPER.createReport(reportId);

            indexPerson.addReport(firstReport);

            when(reportRepository.findById(anyLong()))
                    .thenReturn(Optional.of(firstReport));
            when(reportRepository
                    .findFirstByIndexPerson_IdOrderByIdAsc(anyLong()))
                    .thenReturn(Optional.of(firstReport));

            assertThrows(
                    ReportException.class,
                    () -> reportService.deleteExceptFirstReportFromIndex(
                            indexPerson,
                            reportId));
            verify(reportRepository, times(1))
                    .findFirstByIndexPerson_IdOrderByIdAsc(anyLong());
        }


        @Test
        public void shouldThrowErrorWhenReportIdNotFoundContact() {
            assertThrows(
                    ReportNotFoundException.class,
                    () -> reportService.deleteFromContact(
                            new ContactPerson(),
                            1L));
        }


        @Test
        public void shoulNotRemoveReportWhenContactIsNull() {
            reportService.deleteFromContact(
                    null,
                    1L);

            verify(reportRepository, times(0))
                    .existsById(any());
            verify(reportRepository, times(0))
                    .delete(any());
        }


        @Test
        public void shouldRemoveReportWhenAssociatedToContact() {
            long reportId = 2L;
            ContactPerson contact = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, STAFF);
            Report report = REPORT_TEST_HELPER.createReport(reportId);

            contact.addReport(report);

            when(reportRepository.findById(anyLong()))
                    .thenReturn(Optional.of(report));
            when(reportRepository.existsById(anyLong())).thenReturn(true);
            doNothing().when(reportRepository).delete(any());

            reportService.deleteFromContact(
                    contact,
                    reportId);

            assertTrue(contact.getReports().isEmpty());
            assertNull(report.getIndexPerson());
            verify(reportRepository, times(1))
                    .existsById(any());
            verify(reportRepository, times(1))
                    .delete(any());
        }


        @Test
        public void shouldThrowErrorWhenNotAssociatedToContact() {
            long reportId = 2L;
            ContactPerson contact = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, STAFF);
            ContactPerson owner = CONTACT_PERSON_TEST_HELPER
                    .getContact(2L, STAFF);
            Report firstReport = REPORT_TEST_HELPER.createReport(1L);
            Report report = REPORT_TEST_HELPER.createReport(reportId);

            contact.addReport(firstReport);
            owner.addReport(report);

            when(reportRepository.findById(anyLong()))
                    .thenReturn(Optional.of(report));

            assertThrows(
                    ReportException.class,
                    () -> reportService.deleteFromContact(
                            contact,
                            reportId));

        }


        @Test
        public void shouldThrowErrorWhenReportContactIsNull() {
            when(reportRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new Report()));

            assertThrows(
                    ReportException.class,
                    () -> reportService.deleteFromContact(
                            new ContactPerson(),
                            1L));
        }
    }
}
