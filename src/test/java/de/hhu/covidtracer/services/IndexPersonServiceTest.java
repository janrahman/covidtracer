package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.exceptions.IndexPersonException;
import de.hhu.covidtracer.exceptions.IndexPersonNotFoundException;
import de.hhu.covidtracer.helper.IndexPersonTestHelper;
import de.hhu.covidtracer.mappers.IndexPersonMapper;
import de.hhu.covidtracer.models.*;
import de.hhu.covidtracer.models.associations.IndexContact;
import de.hhu.covidtracer.models.associations.IndexContactId;
import de.hhu.covidtracer.repositories.IndexPersonRepository;
import de.hhu.covidtracer.services.implementations.IndexPersonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.hhu.covidtracer.models.Status.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IndexPersonServiceTest {
    private static final IndexPersonTestHelper INDEX_PERSON_TEST_HELPER =
            new IndexPersonTestHelper();

    @Mock
    private IndexPersonRepository indexPersonRepository;
    @Mock
    private ContactPersonService contactPersonService;
    @Mock
    private IndexPersonMapper indexPersonMapper;
    @Mock
    private ReportService reportService;
    @Mock
    private IndexContactService indexContactService;

    private IndexPersonService indexPersonService;


    @BeforeEach
    public void setup() {
        indexPersonService = new IndexPersonServiceImpl(
                indexPersonRepository,
                indexPersonMapper,
                contactPersonService,
                reportService,
                indexContactService);
    }


    @Nested
    public class Count {
        @ParameterizedTest
        @ValueSource(ints = {0, 1, 10, 100, 1000})
        public void shouldCountAll(int size) {
            when(indexPersonRepository.count()).thenReturn((long) size);

            int countEntries = indexPersonService.count();

            assertEquals(size, countEntries);
            verify(indexPersonRepository, times(1))
                    .count();
        }


        @ParameterizedTest
        @EnumSource(Status.class)
        public void shouldCountAllByStatusAndInQuarantine(Status status) {
            long size = 0L;

            when(indexPersonRepository
                    .countByStatusAndInQuarantineIsTrue(status))
                    .thenReturn(size);

            long countEntries = indexPersonService
                    .countByStatusAndInQuarantine(status);

            assertEquals(size, countEntries);
            verify(indexPersonRepository, times(1))
                    .countByStatusAndInQuarantineIsTrue(any());
        }


        @Test
        public void shouldCountAllNotReportedToHealthDepartment() {
            long result;
            long expected = 1L;

            when(indexPersonRepository.countByReportHealthDepartmentIsFalse())
                    .thenReturn(expected);

            result = indexPersonService.countNotReportedToHealthDepartment();

            assertEquals(expected, result);
            verify(indexPersonRepository, times(1))
                    .countByReportHealthDepartmentIsFalse();
        }


        @Test
        public void shouldCountAllNotReportedToSupervisor() {
            long result;
            long expected = 1L;

            when(indexPersonRepository
                    .countByStatusAndReportSupervisorIsFalse(STAFF))
                    .thenReturn(expected);

            result = indexPersonService.countNotReportedToSupervisor();

            assertEquals(expected, result);
            verify(indexPersonRepository, times(1))
                    .countByStatusAndReportSupervisorIsFalse(any());
        }


        @Test
        public void shouldCountAllPatientsNotInQuarantine() {
            long result;
            long expected = 1L;

            when(indexPersonRepository
                    .countByInQuarantineIsFalse())
                    .thenReturn(expected);

            result = indexPersonService.countPatientsNotInQuarantine();

            assertEquals(expected, result);
            verify(indexPersonRepository, times(1))
                    .countByInQuarantineIsFalse();
        }
    }


    @Nested
    public class CountStaffAbortedQuarantine {
        @Test
        public void shouldReturnNoResult() {
            long result;
            int quarantineLapseDays = 14;
            Instant timestamp = LocalDate.of(2021, 2, 10)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant();
            List<IndexPerson> repoList = new ArrayList<>();

            when(indexPersonRepository
                    .findAllByStatusAndInQuarantine(
                            STAFF,
                            false))
                    .thenReturn(repoList);

            result = indexPersonService.countStaffAbortedQuarantine(
                    timestamp,
                    quarantineLapseDays);

            assertEquals(0L, result);
            verify(indexPersonRepository, times(1))
                    .findAllByStatusAndInQuarantine(any(), anyBoolean());
            verify(reportService, times((0))).getFirstReport(anyLong());
        }


        @Test
        public void shouldExcludeNull() {
            long result;
            int quarantineLapseDays = 14;
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, 1, STAFF);
            Instant timestamp = LocalDate.of(2021, 2, 10)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant();

            when(indexPersonRepository
                    .findAllByStatusAndInQuarantine(
                            STAFF,
                            false))
                    .thenReturn(repoList);
            when(reportService.getFirstReport(anyLong()))
                    .thenReturn(new Report());

            result = indexPersonService.countStaffAbortedQuarantine(
                    timestamp,
                    quarantineLapseDays);

            assertEquals(0L, result);
            verify(reportService, times(repoList.size()))
                    .getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldCountByFilter(int size) {
            long result;
            int quarantineLapseDays = 14;
            LocalDate date = LocalDate.of(2020, 1, 1);
            Instant timestamp = date.atStartOfDay(ZoneId
                    .systemDefault())
                    .toInstant();
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(1, size, STAFF, date);

            when(indexPersonRepository
                    .findAllByStatusAndInQuarantine(
                            STAFF,
                            false))
                    .thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService.countStaffAbortedQuarantine(
                    timestamp,
                    quarantineLapseDays);

            assertEquals(repoList.size(), result);
            verify(indexPersonRepository, times(1))
                    .findAllByStatusAndInQuarantine(any(), anyBoolean());
            verify(reportService, times(size)).getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldCountByFilterAndExcludeNull(int size) {
            long result;
            int quarantineLapseDays = 14;
            LocalDate date = LocalDate.of(2021, 1, 1);
            Instant timestamp = date.atStartOfDay(ZoneId
                    .systemDefault())
                    .toInstant();
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1,
                            size,
                            STAFF,
                            date);
            List<IndexPerson> repoListInvalidDate = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1 + size,
                            size,
                            STAFF,
                            null);

            repoList.addAll(repoListInvalidDate);

            when(indexPersonRepository
                    .findAllByStatusAndInQuarantine(
                            STAFF,
                            false))
                    .thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService.countStaffAbortedQuarantine(
                    timestamp,
                    quarantineLapseDays);

            assertEquals(size, result);
            verify(
                    reportService,
                    times(size * 2))
                    .getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldExcludeNotAbortedIndex(int size) {
            long result;
            int quarantineLapseDays = 14;
            LocalDate date = LocalDate.of(2021, 1, 1);
            Instant timestamp = date.atStartOfDay(ZoneId
                    .systemDefault())
                    .toInstant();
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1,
                            size,
                            STAFF,
                            date.minusDays(quarantineLapseDays));

            when(indexPersonRepository
                    .findAllByStatusAndInQuarantine(
                            STAFF,
                            false))
                    .thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService.countStaffAbortedQuarantine(
                    timestamp,
                    quarantineLapseDays);

            assertEquals(0, result);
            verify(reportService, times(size)).getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldExcludeNotAbortedIndexAndExcludeNull(
                int size) {
            long result;
            int quarantineLapseDays = 14;
            LocalDate date = LocalDate.of(2021, 1, 1);
            Instant timestamp = date.atStartOfDay(ZoneId
                    .systemDefault())
                    .toInstant();
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1,
                            size,
                            STAFF,
                            date.minusDays(quarantineLapseDays));
            List<IndexPerson> repoListNullDate = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1 + size,
                            size,
                            STAFF,
                            null);

            repoList.addAll(repoListNullDate);

            when(indexPersonRepository
                    .findAllByStatusAndInQuarantine(
                            STAFF,
                            false))
                    .thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService.countStaffAbortedQuarantine(
                    timestamp,
                    quarantineLapseDays);

            assertEquals(0, result);
            verify(reportService, times(size * 2))
                    .getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldOnlyCountByFilter(
                int validIndexListSize) {
            long result;
            int quarantineLapseDays = 14;
            int notAbortedIndexListSize = 10;
            int dateIsNullIndexListSize = 10;
            LocalDate date = LocalDate.of(2021, 1, 1);
            Instant timestamp = date.atStartOfDay(ZoneId
                    .systemDefault())
                    .toInstant();
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1,
                            validIndexListSize,
                            STAFF,
                            date.minusDays(quarantineLapseDays - 1));
            List<IndexPerson> repoListNotAbortedQuarantine =
                    INDEX_PERSON_TEST_HELPER.getIndexWithReportDate(
                            1 + validIndexListSize,
                            notAbortedIndexListSize,
                            STAFF,
                            date.minusDays(quarantineLapseDays));
            List<IndexPerson> repoListNull = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1 +
                                    validIndexListSize +
                                    dateIsNullIndexListSize,
                            dateIsNullIndexListSize,
                            STAFF,
                            null);

            repoList.addAll(repoListNotAbortedQuarantine);
            repoList.addAll(repoListNull);

            when(indexPersonRepository
                    .findAllByStatusAndInQuarantine(
                            STAFF,
                            false))
                    .thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService.countStaffAbortedQuarantine(
                    timestamp,
                    quarantineLapseDays);

            assertEquals(validIndexListSize, result);
            verify(
                    reportService,
                    times(
                            validIndexListSize +
                                    notAbortedIndexListSize +
                                    dateIsNullIndexListSize))
                    .getFirstReport(anyLong());
        }
    }


    @Nested
    public class CountIndexInQuarantineByStatusAndFirstReport {
        @Test
        public void shouldReturnNoResult() {
            long result;
            Instant timestamp = LocalDate.of(2021, 2, 10)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant();
            List<IndexPerson> repoList = new ArrayList<>();

            when(indexPersonRepository
                    .findAllByStatusAndInQuarantine(any(), anyBoolean()))
                    .thenReturn(repoList);
            result = indexPersonService
                    .countIndexInQuarantineByStatusAndFirstReportDate(
                            STAFF, timestamp);

            assertEquals(0L, result);
            verify(indexPersonRepository, times(1))
                    .findAllByStatusAndInQuarantine(any(), anyBoolean());
            verify(reportService, times((0))).getFirstReport(anyLong());
        }


        @Test
        public void shouldExcludeNull() {
            long result;
            Status status = STAFF;
            Instant timestamp = LocalDate.of(2021, 2, 10)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant();
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER.getIndexPeople(
                    1, 1, status);

            when(indexPersonRepository
                    .findAllByStatusAndInQuarantine(any(), anyBoolean()))
                    .thenReturn(repoList);
            when(reportService.getFirstReport(anyLong()))
                    .thenReturn(new Report());

            result = indexPersonService
                    .countIndexInQuarantineByStatusAndFirstReportDate(
                            status, timestamp);

            assertEquals(0L, result);
            verify(indexPersonRepository, times(1))
                    .findAllByStatusAndInQuarantine(any(), anyBoolean());
            verify(reportService, times((1))).getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldCountByFilter(int size) {
            long result;
            Status status = STAFF;
            LocalDate date = LocalDate.of(2021, 2, 10);
            Instant timestamp = date
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant();
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(1, size, status, date);

            when(indexPersonRepository
                    .findAllByStatusAndInQuarantine(any(), anyBoolean()))
                    .thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService
                    .countIndexInQuarantineByStatusAndFirstReportDate(
                            status,
                            timestamp);

            assertEquals(size, result);
            verify(indexPersonRepository, times(1))
                    .findAllByStatusAndInQuarantine(any(), anyBoolean());
            verify(reportService, times((size))).getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldCountByFilterAndExcludeNull(
                int size) {
            long result;
            Status status = STAFF;
            LocalDate date = LocalDate.of(2021, 2, 10);
            Instant timestamp = date
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant();
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1,
                            size,
                            status,
                            date);
            List<IndexPerson> repoListNull = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1 + size,
                            size,
                            status,
                            null);

            repoList.addAll(repoListNull);

            when(indexPersonRepository
                    .findAllByStatusAndInQuarantine(any(), anyBoolean()))
                    .thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService
                    .countIndexInQuarantineByStatusAndFirstReportDate(
                            status,
                            timestamp);

            assertEquals(size, result);
            verify(reportService, times(size * 2))
                    .getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldExcludeNotMatchingGivenTimestamp(int size) {
            long result;
            Status status = PATIENT;
            LocalDate date = LocalDate.of(2021, 2, 10);
            Instant timestamp = date
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant();
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1,
                            size,
                            status,
                            date.plusDays(1L));

            when(indexPersonRepository
                    .findAllByStatusAndInQuarantine(any(), anyBoolean()))
                    .thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService
                    .countIndexInQuarantineByStatusAndFirstReportDate(
                            status,
                            timestamp);

            assertEquals(0, result);
            verify(reportService, times(size))
                    .getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldExcludeNotMatchingGivenTimestampAndExcludeNull(
                int size) {
            long result;
            Status status = PATIENT;
            LocalDate date = LocalDate.of(2021, 2, 10);
            Instant timestamp = date
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant();
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1,
                            size,
                            status,
                            date.minusDays(1L));
            List<IndexPerson> repoListNull = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1 + size,
                            size,
                            status,
                            null);

            repoList.addAll(repoListNull);

            when(indexPersonRepository
                    .findAllByStatusAndInQuarantine(any(), anyBoolean()))
                    .thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService
                    .countIndexInQuarantineByStatusAndFirstReportDate(
                            status,
                            timestamp);

            assertEquals(0, result);
            verify(reportService, times(size * 2))
                    .getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldCountByFilterOnly(
                int size) {
            long result;
            Status status = PATIENT;
            LocalDate date = LocalDate.of(2021, 2, 10);
            Instant timestamp = date
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant();
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1,
                            size,
                            status,
                            date);
            List<IndexPerson> repoListNotGivenDate = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1 + size,
                            size,
                            status,
                            date.minusMonths(5L));
            List<IndexPerson> repoListNull = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1 + (size * 2),
                            size,
                            status,
                            null);

            repoList.addAll(repoListNotGivenDate);
            repoList.addAll(repoListNull);

            when(indexPersonRepository
                    .findAllByStatusAndInQuarantine(any(), anyBoolean()))
                    .thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService
                    .countIndexInQuarantineByStatusAndFirstReportDate(
                            status,
                            timestamp);

            assertEquals(size, result);
            verify(reportService, times(size * 3))
                    .getFirstReport(anyLong());
        }
    }


    @Nested
    public class Update {
        @Test
        public void shouldThrowError() {
            assertThrows(
                    IndexPersonNotFoundException.class,
                    () -> indexPersonService
                            .update(new IndexPersonDTO(), 0L));
        }


        @Test
        public void shouldUpdateIndex() {
            IndexPersonDTO indexPersonDTO = new IndexPersonDTO();
            IndexPerson indexPerson = new IndexPerson();

            when(indexPersonRepository.findById(anyLong()))
                    .thenReturn(Optional.of(indexPerson));
            doNothing().when(indexPersonMapper).updateModel(any(), any());
            when(indexPersonRepository.save(any())).thenReturn(indexPerson);

            indexPersonService.update(indexPersonDTO, 1L);

            verify(indexPersonRepository, times(1))
                    .findById(anyLong());
            verify(indexPersonRepository, times(1))
                    .save(any());
            verify(indexPersonMapper, times(1))
                    .updateModel(any(), any());
        }
    }


    @Nested
    public class Save {
        @Test
        public void shouldSaveIndex() {
            IndexPerson result;
            IndexPerson indexPerson = new IndexPerson();

            indexPerson.setId(1L);

            when(indexPersonRepository.save(any()))
                    .thenReturn(indexPerson);

            result = indexPersonService.save(indexPerson);

            assertEquals(indexPerson, result);
            verify(indexPersonRepository, times(1))
                    .save(any());
        }


        @Test
        public void shouldSaveIndexList() {
            List<IndexPerson> result;
            List<IndexPerson> indexPersonList = new ArrayList<>();

            when(indexPersonRepository.saveAll(any()))
                    .thenReturn(indexPersonList);

            result = indexPersonService.saveAll(indexPersonList);

            assertEquals(indexPersonList, result);
            verify(indexPersonRepository, times(1))
                    .saveAll(any());
        }


        @Test
        public void shouldThrowErrorBySavingReport() {
            assertThrows(
                    IndexPersonNotFoundException.class,
                    () -> indexPersonService
                            .saveReport(0L, null));
        }


        @Test
        public void shouldSaveReport() {
            IndexPerson result;
            long id = 1L;
            IndexPerson indexPerson = INDEX_PERSON_TEST_HELPER
                    .getIndexPerson(id, STAFF);
            Report report = new Report();

            report.setId(id);
            when(indexPersonRepository.findById(anyLong()))
                    .thenReturn(Optional.of(indexPerson));
            when(indexPersonRepository.save(any()))
                    .thenReturn(indexPerson);

            result = indexPersonService.saveReport(id, report);

            assertEquals(report.getIndexPerson(), result);
            assertEquals(id, result.getId());
            verify(indexPersonRepository, times(1))
                    .findById(anyLong());
            verify(indexPersonRepository, times(1))
                    .save(any());
        }


        @Test
        public void shouldThrowErrorBySavingAppointment() {
            assertThrows(
                    IndexPersonNotFoundException.class,
                    () -> indexPersonService
                            .saveAppointmentToIndex(
                                    null,
                                    0L));
        }


        @Test
        public void shouldSaveAppointment() {
            IndexPerson result;
            long id = 1L;
            IndexPerson indexPerson = INDEX_PERSON_TEST_HELPER
                    .getIndexPerson(id, PATIENT);
            Appointment appointment = new Appointment();

            appointment.setId(id);
            when(indexPersonRepository.findById(anyLong()))
                    .thenReturn(Optional.of(indexPerson));
            when(indexPersonRepository.save(any()))
                    .thenReturn(indexPerson);

            result = indexPersonService
                    .saveAppointmentToIndex(appointment, id);

            assertEquals(id, result.getId());
            assertEquals(appointment.getIndexPerson(), result);
            verify(indexPersonRepository, times(1))
                    .findById(anyLong());
            verify(indexPersonRepository, times(1))
                    .save(any());
        }
    }


    @Nested
    public class GetIndexFromFile {
        @Test
        public void shouldThrowErrorWhenNull() {
            assertThrows(
                    IndexPersonException.class,
                    () -> indexPersonService.getFromImportFile(
                            null));
        }


        @Test
        public void shouldReturnEmptyIndex() {
            IndexPerson result;
            IndexPerson dtoToIndex = new IndexPerson();
            IndexPersonDTO indexPersonDTO = new IndexPersonDTO();

            dtoToIndex.setName("name");
            when(indexPersonMapper.toIndex(any()))
                    .thenReturn(dtoToIndex);
            when(indexPersonRepository
                    .findByNameAndFirstNameAndAddressAndBirthday(
                            any(),
                            any(),
                            any(),
                            any()))
                    .thenReturn(Optional.empty());

            result = indexPersonService.getFromImportFile(indexPersonDTO);

            assertEquals(dtoToIndex.getName(), result.getName());
        }


        @Test
        public void shouldReturnIndexFromDatabase() {
            IndexPerson result;
            long id = 1L;
            IndexPerson dtoFromIndex = new IndexPerson();
            IndexPerson entityFromDatabase = INDEX_PERSON_TEST_HELPER
                    .getIndexPerson(id, STAFF);

            dtoFromIndex.setId(id);
            when(indexPersonMapper.toIndex(any()))
                    .thenReturn(dtoFromIndex);
            when(indexPersonRepository
                    .findByNameAndFirstNameAndAddressAndBirthday(
                            any(),
                            any(),
                            any(),
                            any()))
                    .thenReturn(Optional.of(entityFromDatabase));

            doNothing().when(indexPersonMapper).updateModel(any(), any());

            result = indexPersonService
                    .getFromImportFile(new IndexPersonDTO());

            assertEquals(
                    1L,
                    result.getId());
            assertEquals(
                    entityFromDatabase.getName(),
                    result.getName());
            assertEquals(
                    entityFromDatabase.getFirstName(),
                    result.getFirstName());
            assertEquals(
                    entityFromDatabase.getAddress(),
                    result.getAddress());
            assertEquals(
                    entityFromDatabase.getBirthday(),
                    result.getBirthday());
            verify(indexPersonMapper, times(1))
                    .updateModel(any(), any());
        }


        @Test
        public void shouldReturnNewIndexFromDTO() {
            long id = 1L;
            IndexPerson dtoToIndex = INDEX_PERSON_TEST_HELPER
                    .getIndexPerson(id, STAFF);
            IndexPersonDTO indexPersonDTO = new IndexPersonDTO();

            when(indexPersonMapper.toIndex(any()))
                    .thenReturn(dtoToIndex);
            when(indexPersonRepository
                    .findByNameAndFirstNameAndAddressAndBirthday(
                            any(),
                            any(),
                            any(),
                            any()))
                    .thenReturn(Optional.empty());

            IndexPerson entity = indexPersonService
                    .getFromImportFile(indexPersonDTO);

            assertEquals(id, entity.getId());
            assertEquals(dtoToIndex.getFirstName(), entity.getFirstName());
            assertEquals(dtoToIndex.getAddress(), entity.getAddress());
            assertEquals(dtoToIndex.getBirthday(), entity.getBirthday());
            verify(indexPersonMapper, times(0))
                    .updateModel(any(), any());
        }
    }


    @Nested
    public class Remove {
        @Test
        public void shouldThrowError() {
            assertThrows(
                    IndexPersonNotFoundException.class,
                    () -> indexPersonService.remove(0L));
        }


        @Test
        public void shouldRemoveIndex() {
            long id = 1L;
            IndexPerson indexPerson = INDEX_PERSON_TEST_HELPER
                    .getIndexPerson(id, STAFF);
            IndexContact indexContact = new IndexContact();
            ContactPerson contactPerson = new ContactPerson();

            contactPerson.setId(id);
            indexContact.setIndexContactId(new IndexContactId(id, id));
            indexContact.setContactPerson(contactPerson);
            indexContact.setIndex(indexPerson);

            when(indexPersonRepository.findById(anyLong()))
                    .thenReturn(Optional.of(indexPerson));
            doNothing().when(indexContactService).remove(anyLong(), anyLong());
            doNothing().when(indexPersonRepository).delete(any());
            doNothing().when(contactPersonService).removeOrphans(any());

            indexPersonService.remove(id);

            verify(indexContactService, times(1))
                    .remove(anyLong(), anyLong());
            verify(indexPersonRepository, times(1))
                    .delete(any());
            verify(contactPersonService, times(1))
                    .removeOrphans(any());
        }
    }


    @Nested
    public class GetEntities {
        @Test
        public void shouldThrowErrorWhenIdNotFound() {
            long id = 0L;

            assertThrows(
                    IndexPersonNotFoundException.class,
                    () -> indexPersonService.getById(id));
        }


        @Test
        public void shouldFindIndex() {
            IndexPerson result;
            long id = 1L;
            IndexPerson indexPerson = INDEX_PERSON_TEST_HELPER
                    .getIndexPerson(id, PATIENT);

            when(indexPersonRepository.findById(anyLong()))
                    .thenReturn(Optional.of(indexPerson));

            result = indexPersonService.getById(id);

            assertEquals(indexPerson, result);
        }


        @Test
        public void shouldReturnAll() {
            when(indexPersonRepository.findAll())
                    .thenReturn(new ArrayList<>());

            indexPersonService.getAll();

            verify(indexPersonRepository, times(1))
                    .findAll();
        }


        @Test
        public void shouldReturnAllByEntryDateTimeLessEqualsThan() {
            when(indexPersonRepository
                    .findAllByEntryDateTimeLessThanEqual(any()))
                    .thenReturn(new ArrayList<>());

            indexPersonService
                    .getAllByEntryDateTimeLessEqualsThan(Instant.now());

            verify(indexPersonRepository, times(1))
                    .findAllByEntryDateTimeLessThanEqual(any());
        }
    }


    @Nested
    public class GetDTOs {
        @Test
        public void shouldThrowErrorWhenIdNotFound() {
            long id = 0L;

            assertThrows(
                    IndexPersonNotFoundException.class,
                    () -> indexPersonService.getDTOById(id));
        }


        @Test
        public void shouldFindDTOById() {
            IndexPersonDTO result;
            long id = 1L;
            IndexPersonDTO dto = new IndexPersonDTO();

            dto.setId(id);

            when(indexPersonRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new IndexPerson()));
            when(indexPersonMapper.toIndexDTO(any())).thenReturn(dto);

            result = indexPersonService.getDTOById(id);

            assertEquals(dto, result);
        }


        @Test
        public void shouldReturnAllDTOs() {
            List<IndexPersonDTO> result;

            when(indexPersonRepository.findAll())
                    .thenReturn(new ArrayList<>());
            when(indexPersonMapper.toIndexDTOs(any()))
                    .thenReturn(new ArrayList<>());

            result = indexPersonService.getAllDTOs();

            assertTrue(result.isEmpty());
            verify(indexPersonRepository, times(1))
                    .findAll();
            verify(indexPersonMapper, times(1))
                    .toIndexDTOs(any());
        }


        @Test
        public void shouldReturnDTOsByStatus() {
            List<IndexPersonDTO> result;

            when(indexPersonRepository.findAllByStatus(any()))
                    .thenReturn(new ArrayList<>());
            when(indexPersonMapper.toIndexDTOs(any()))
                    .thenReturn(new ArrayList<>());

            result = indexPersonService.getAllDTOsByStatus(STAFF);

            assertTrue(result.isEmpty());
            verify(indexPersonRepository, times(1))
                    .findAllByStatus(any());
            verify(indexPersonMapper, times(1))
                    .toIndexDTOs(any());
        }


        @Test
        public void shouldReturnDTOsByNotReportedToHealthDepartment() {
            List<IndexPersonDTO> result;

            when(indexPersonRepository.findAllByReportHealthDepartmentIsFalse())
                    .thenReturn(new ArrayList<>());
            when(indexPersonMapper.toIndexDTOs(any()))
                    .thenReturn(new ArrayList<>());

            result = indexPersonService
                    .getAllDTOsByNotReportedToHealthDepartment();

            assertTrue(result.isEmpty());
            verify(indexPersonRepository, times(1))
                    .findAllByReportHealthDepartmentIsFalse();
            verify(indexPersonMapper, times(1))
                    .toIndexDTOs(any());
        }


        @Test
        public void shouldReturnDTOsByQuarantineAndBooleansInverted() {
            List<IndexPersonDTO> result;

            when(indexPersonRepository.findAllByInQuarantineIsTrue())
                    .thenReturn(new ArrayList<>());
            when(indexPersonMapper.toIndexDTOs(any()))
                    .thenReturn(new ArrayList<>());

            result = indexPersonService
                    .getAllDTOsByQuarantineAndBooleansInverted();

            assertTrue(result.isEmpty());
            verify(indexPersonRepository, times(1))
                    .findAllByInQuarantineIsTrue();
            verify(indexPersonMapper, times(1))
                    .toIndexDTOs(any());
        }


        @Test
        public void shouldReturnDTOsByVisibleOrByOwner() {
            List<IndexPersonDTO> result;
            String owner = "admin";

            when(indexPersonRepository
                    .findAllByVisibleIsTrueOrOwner(anyString()))
                    .thenReturn(new ArrayList<>());
            when(indexPersonMapper.toIndexDTOs(any()))
                    .thenReturn(new ArrayList<>());

            result = indexPersonService.getAllVisibleOrOwnerDTOs(owner);

            assertTrue(result.isEmpty());
            verify(indexPersonRepository, times(1))
                    .findAllByVisibleIsTrueOrOwner(anyString());
            verify(indexPersonMapper, times(1))
                    .toIndexDTOs(any());
        }


        @Test
        public void shouldReturnLastFiveDTOsByUploadDate() {
            List<IndexPersonDTO> result;

            when(indexPersonRepository.findFirst5ByOrderByIdDesc())
                    .thenReturn(new ArrayList<>());
            when(indexPersonMapper.toIndexDTOs(any()))
                    .thenReturn(new ArrayList<>());

            result = indexPersonService.getLastUploadedIndexPeople();

            assertTrue(result.isEmpty());
            verify(indexPersonRepository, times(1))
                    .findFirst5ByOrderByIdDesc();
            verify(indexPersonMapper, times(1))
                    .toIndexDTOs(any());
        }


        @Test
        public void shouldReturnLastUploadedIndexPeopleByOwner() {
            List<IndexPersonDTO> result;
            String owner = "admin";

            when(indexPersonRepository
                    .findFirst5ByOwnerOrderByIdDesc(anyString()))
                    .thenReturn(new ArrayList<>());
            when(indexPersonMapper.toIndexDTOs(any()))
                    .thenReturn(new ArrayList<>());

            result = indexPersonService
                    .getLastUploadedIndexPeopleByOwner(owner);

            assertTrue(result.isEmpty());
            verify(indexPersonRepository, times(1))
                    .findFirst5ByOwnerOrderByIdDesc(anyString());
            verify(indexPersonMapper, times(1))
                    .toIndexDTOs(any());
        }
    }


    @Nested
    public class UpdateBatch {
        @Test
        public void shouldReturnEmptyWhenInputIsNull() {
            assertTrue(indexPersonService
                    .updateBatchFromMultiForm(null)
                    .isEmpty());
            verify(indexPersonRepository, times(0))
                    .findById(anyLong());
            verify(indexPersonRepository, times(0))
                    .save(any());
        }


        @Test
        public void shouldReturnEmptyWhenInputListIsEmpty() {
            assertTrue(indexPersonService
                    .updateBatchFromMultiForm(new ArrayList<>())
                    .isEmpty());
            verify(indexPersonRepository, times(0))
                    .findById(anyLong());
            verify(indexPersonRepository, times(0))
                    .save(any());
        }


        @Test
        public void shouldThrowError() {
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, 1, PATIENT);

            assertThrows(
                    IndexPersonNotFoundException.class,
                    () -> indexPersonService
                            .updateBatchFromMultiForm(indexPersonDTOList));
        }


        @Test
        public void shouldNotChangeAnyBooleans() {
            List<IndexPerson> result;
            int size = 1;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);
            List<IndexPerson> indexPersonList = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, size, STAFF);

            indexPersonList.forEach(i -> {
                when(indexPersonRepository.findById(i.getId()))
                        .thenReturn(Optional.of(i));
                when(indexPersonRepository.save(i)).thenReturn(i);
            });

            result = indexPersonService
                    .updateBatchFromMultiForm(indexPersonDTOList);

            assertEquals(indexPersonDTOList.size(), result.size());
            result.forEach(i -> {
                assertFalse(i.isReportHealthDepartment());
                assertFalse(i.isReportSupervisor());
            });
            verify(indexPersonRepository, times(size))
                    .findById(anyLong());
            verify(indexPersonRepository, times(size))
                    .save(any());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldChangeHealthDepartmentBoolean(int size) {
            List<IndexPerson> result;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);
            List<IndexPerson> indexPersonList = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, size, STAFF);

            indexPersonDTOList.forEach(dto -> dto
                    .setReportHealthDepartment(true));
            indexPersonList.forEach(i -> {
                when(indexPersonRepository.findById(i.getId()))
                        .thenReturn(Optional.of(i));
                when(indexPersonRepository.save(i)).thenReturn(i);
            });

            result = indexPersonService
                    .updateBatchFromMultiForm(indexPersonDTOList);

            assertEquals(indexPersonDTOList.size(), result.size());
            assertEquals(size, result.stream()
                    .map(IndexPerson::isReportHealthDepartment)
                    .filter(b -> b)
                    .count());
            verify(indexPersonRepository, times(size))
                    .findById(anyLong());
            verify(indexPersonRepository, times(size))
                    .save(any());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldChangeSupervisorBoolean(int size) {
            List<IndexPerson> result;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);
            List<IndexPerson> indexPersonList = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, size, STAFF);

            indexPersonDTOList.forEach(dto -> dto.setReportSupervisor(true));

            indexPersonList.forEach(i -> {
                when(indexPersonRepository.findById(i.getId()))
                        .thenReturn(Optional.of(i));
                when(indexPersonRepository.save(i)).thenReturn(i);
            });

            result = indexPersonService
                    .updateBatchFromMultiForm(indexPersonDTOList);

            assertEquals(indexPersonDTOList.size(), result.size());
            assertEquals(size, result.stream()
                    .map(IndexPerson::isReportSupervisor)
                    .filter(b -> b)
                    .count());
            verify(indexPersonRepository, times(size))
                    .findById(anyLong());
            verify(indexPersonRepository, times(size))
                    .save(any());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldChangeHealthDepartmentAndSupervisor(int size) {
            List<IndexPerson> result;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);
            List<IndexPerson> indexPersonList = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, size, STAFF);

            indexPersonDTOList.forEach(dto -> {
                dto.setReportHealthDepartment(true);
                dto.setReportSupervisor(true);
            });
            indexPersonList.forEach(i -> {
                when(indexPersonRepository.findById(i.getId()))
                        .thenReturn(Optional.of(i));
                when(indexPersonRepository.save(i)).thenReturn(i);
            });

            result = indexPersonService
                    .updateBatchFromMultiForm(indexPersonDTOList);

            assertEquals(indexPersonDTOList.size(), result.size());
            assertEquals(size, result.stream()
                    .map(IndexPerson::isReportHealthDepartment)
                    .filter(b -> b)
                    .count());
            assertEquals(size, result.stream()
                    .map(IndexPerson::isReportSupervisor)
                    .filter(b -> b)
                    .count());
            verify(indexPersonRepository, times(size))
                    .findById(anyLong());
            verify(indexPersonRepository, times(size))
                    .save(any());
        }


        @Test
        public void shouldExcludeNull() {
            List<IndexPerson> result;
            int size = 1;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);
            List<IndexPerson> indexPersonList = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, size, STAFF);

            indexPersonDTOList.add(null);
            indexPersonList.forEach(i -> {
                when(indexPersonRepository.findById(i.getId()))
                        .thenReturn(Optional.of(i));
                when(indexPersonRepository.save(i)).thenReturn(i);
            });

            result = indexPersonService
                    .updateBatchFromMultiForm(indexPersonDTOList);

            assertNotEquals(indexPersonDTOList.size(), result.size());
            assertEquals(size, result.size());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldChangeHealthDepartmentAndSupervisorAndExcludeNull(
                int size) {
            List<IndexPerson> result;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);
            List<IndexPerson> indexPersonList = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, size, STAFF);

            indexPersonDTOList.forEach(dto -> {
                dto.setReportHealthDepartment(true);
                dto.setReportSupervisor(true);
            });

            for (int i = 0; i < size; i++) {
                indexPersonDTOList.add(null);
            }

            indexPersonList.forEach(i -> {
                when(indexPersonRepository.findById(i.getId()))
                        .thenReturn(Optional.of(i));
                when(indexPersonRepository.save(i)).thenReturn(i);
            });

            result = indexPersonService
                    .updateBatchFromMultiForm(indexPersonDTOList);

            assertNotEquals(indexPersonDTOList.size(), result.size());
            assertEquals(size, result.size());
            assertEquals(size, result.stream()
                    .map(IndexPerson::isReportHealthDepartment)
                    .filter(b -> b)
                    .count());
            assertEquals(size, result.stream()
                    .map(IndexPerson::isReportSupervisor)
                    .filter(b -> b)
                    .count());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldChangeHealthDepartmentAndSupervisorToFalse(
                int size) {
            List<IndexPerson> result;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);
            List<IndexPerson> indexPersonList = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, size, STAFF);

            indexPersonList.forEach(i -> {
                i.setReportHealthDepartment(true);
                i.setReportSupervisor(true);
                when(indexPersonRepository.findById(i.getId()))
                        .thenReturn(Optional.of(i));
                when(indexPersonRepository.save(i)).thenReturn(i);
            });

            result = indexPersonService
                    .updateBatchFromMultiForm(indexPersonDTOList);

            assertEquals(indexPersonDTOList.size(), result.size());
            assertEquals(0, result.stream()
                    .map(IndexPerson::isReportHealthDepartment)
                    .filter(b -> b)
                    .count());
            assertEquals(0, result.stream()
                    .map(IndexPerson::isReportSupervisor)
                    .filter(b -> b)
                    .count());
        }


        @Test
        public void shouldReturnEmptyWhenNullQuarantine() {
            assertTrue(indexPersonService
                    .updateQuarantineBatchFromMultiForm(null)
                    .isEmpty());
            verify(indexPersonRepository, times(0))
                    .findById(anyLong());
            verify(indexPersonRepository, times(0))
                    .save(any());
        }


        @Test
        public void shouldReturnEmptyWhenInputListEmptyQuarantine() {
            assertTrue(indexPersonService
                    .updateQuarantineBatchFromMultiForm(new ArrayList<>())
                    .isEmpty());
            verify(indexPersonRepository, times(0))
                    .findById(anyLong());
            verify(indexPersonRepository, times(0))
                    .save(any());
        }


        @Test
        public void shouldThrowErrorQuarantine() {
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, 1, PATIENT);

            assertThrows(
                    IndexPersonNotFoundException.class,
                    () -> indexPersonService.updateQuarantineBatchFromMultiForm(
                            indexPersonDTOList));
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldSetTrueQuarantine(int size) {
            List<IndexPerson> result;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);
            List<IndexPerson> indexPersonList = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, size, STAFF);

            indexPersonList.forEach(i -> {
                when(indexPersonRepository.findById(i.getId()))
                        .thenReturn(Optional.of(i));
                when(indexPersonRepository.save(i)).thenReturn(i);
            });

            result = indexPersonService.updateQuarantineBatchFromMultiForm(
                    indexPersonDTOList);

            assertEquals(indexPersonDTOList.size(), result.size());
            result.forEach(i -> assertTrue(i.isInQuarantine()));
            verify(indexPersonRepository, times(size))
                    .findById(anyLong());
            verify(indexPersonRepository, times(size))
                    .save(any());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldSetFalseQuarantine(int size) {
            List<IndexPerson> result;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);
            List<IndexPerson> indexPersonList = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, size, STAFF);

            indexPersonDTOList.forEach(dto -> dto.setInQuarantine(true));
            indexPersonList.forEach(i -> {
                when(indexPersonRepository.findById(i.getId()))
                        .thenReturn(Optional.of(i));
                when(indexPersonRepository.save(i)).thenReturn(i);
            });

            result = indexPersonService.updateQuarantineBatchFromMultiForm(
                    indexPersonDTOList);

            assertEquals(indexPersonDTOList.size(), result.size());
            result.forEach(i -> assertFalse(i.isInQuarantine()));
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldNotChangeWhenDTOIsFalseAndIndexIsTrueQuarantine(
                int size) {
            List<IndexPerson> result;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);
            List<IndexPerson> indexPersonList = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, size, STAFF);

            indexPersonList.forEach(i -> {
                i.setInQuarantine(true);
                when(indexPersonRepository.findById(i.getId()))
                        .thenReturn(Optional.of(i));
                when(indexPersonRepository.save(i)).thenReturn(i);
            });

            result = indexPersonService.updateQuarantineBatchFromMultiForm(
                    indexPersonDTOList);

            assertEquals(indexPersonDTOList.size(), result.size());
            result.forEach(i -> assertTrue(i.isInQuarantine()));
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldNotChangeWhenDTOIsTrueAndIndexIsFalseQuarantine(
                int size) {
            List<IndexPerson> result;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);
            List<IndexPerson> indexPersonList = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, size, STAFF);

            indexPersonDTOList.forEach(dto -> dto.setInQuarantine(true));
            indexPersonList.forEach(i -> {
                when(indexPersonRepository.findById(i.getId()))
                        .thenReturn(Optional.of(i));
                when(indexPersonRepository.save(i)).thenReturn(i);
            });

            result = indexPersonService.updateQuarantineBatchFromMultiForm(
                    indexPersonDTOList);

            assertEquals(indexPersonDTOList.size(), result.size());
            result.forEach(i -> assertFalse(i.isInQuarantine()));
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldUpdateAndExcludeNullQuarantine(
                int size) {
            List<IndexPerson> result;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);
            List<IndexPerson> indexPersonList = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, size, STAFF);

            indexPersonDTOList.forEach(dto -> dto.setInQuarantine(false));
            indexPersonDTOList.add(null);
            indexPersonList.forEach(i -> {
                when(indexPersonRepository.findById(i.getId()))
                        .thenReturn(Optional.of(i));
                when(indexPersonRepository.save(i)).thenReturn(i);
            });

            result = indexPersonService.updateQuarantineBatchFromMultiForm(
                    indexPersonDTOList);

            assertNotEquals(indexPersonDTOList.size(), result.size());
            assertEquals(size, result.size());
            assertEquals(size, result.stream()
                    .map(IndexPerson::isInQuarantine)
                    .filter(b -> b)
                    .count());
        }
    }


    @Nested
    public class FilterDTOListByVisibleOrOwnerNoResult {
        @Test
        public void shouldReturnEmptyWhenListIsEmptyAndOwnerNull() {
            assertTrue(indexPersonService
                    .filterDTOListByVisibleOrOwner(
                            new ArrayList<>(),
                            null)
                    .isEmpty());
        }


        @Test
        public void shouldReturnEmptyWhenInputsAreNull() {
            assertTrue(indexPersonService
                    .filterDTOListByVisibleOrOwner(
                            null,
                            null)
                    .isEmpty());
        }


        @ParameterizedTest
        @ValueSource(ints = {0, 1, 10})
        public void shouldReturnOneVisible(int size) {
            List<IndexPersonDTO> result;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);
            IndexPersonDTO indexPersonDTO = INDEX_PERSON_TEST_HELPER
                    .getDTO(1L + size, STAFF);

            indexPersonDTO.setVisible(true);
            indexPersonDTOList.add(indexPersonDTO);

            result = indexPersonService.filterDTOListByVisibleOrOwner(
                    indexPersonDTOList, "admin");

            assertTrue(result.contains(indexPersonDTO));
        }


        @ParameterizedTest
        @ValueSource(ints = {0, 1, 10})
        public void shouldReturnAllVisible(int size) {
            List<IndexPersonDTO> result;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);

            indexPersonDTOList.forEach(dto -> dto.setVisible(true));

            result = indexPersonService.filterDTOListByVisibleOrOwner(
                    indexPersonDTOList, null);

            assertEquals(size, result.size());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldReturnVisibleAndExcludeNotVisible(int size) {
            List<IndexPersonDTO> result;
            int sizeNotVisible = size * 2;
            List<IndexPersonDTO> dtoList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, sizeNotVisible, STAFF);
            List<IndexPersonDTO> visibleDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1 + sizeNotVisible, size, VISITOR);

            visibleDTOList.forEach(dto -> dto.setVisible(true));
            dtoList.addAll(visibleDTOList);

            result = indexPersonService.filterDTOListByVisibleOrOwner(
                    dtoList, "admin");

            assertEquals(size, result.size());
            visibleDTOList.forEach(dto -> assertTrue(result.contains(dto)));
        }


        @ParameterizedTest
        @ValueSource(strings = {"user", "admin"})
        public void shouldReturnOneByOwner(String owner) {
            List<IndexPersonDTO> result;
            int size = 1;
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);
            IndexPersonDTO indexPersonDTO = INDEX_PERSON_TEST_HELPER
                    .getDTO(1L + size, STAFF);

            indexPersonDTO.setOwner(owner);
            indexPersonDTOList.add(indexPersonDTO);

            result = indexPersonService.filterDTOListByVisibleOrOwner(
                    indexPersonDTOList, owner);

            assertTrue(result.contains(indexPersonDTO));
        }


        @ParameterizedTest
        @ValueSource(ints = {0, 1, 10})
        public void shouldReturnAllByOwner(int size) {
            List<IndexPersonDTO> result;
            String owner = "admin";
            List<IndexPersonDTO> indexPersonDTOList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);

            indexPersonDTOList.forEach(dto -> dto.setOwner(owner));

            result = indexPersonService.filterDTOListByVisibleOrOwner(
                    indexPersonDTOList, owner);

            assertEquals(size, result.size());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldReturnByOwnerAndExcludeNotOwned(int size) {
            List<IndexPersonDTO> result;
            int sizeNotVisible = size * 2;
            String owner = "admin";
            List<IndexPersonDTO> dtoList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, sizeNotVisible, STAFF);
            List<IndexPersonDTO> ownershipDTOs = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1 + sizeNotVisible, size, VISITOR);

            ownershipDTOs.forEach(dto -> dto.setOwner(owner));
            dtoList.addAll(ownershipDTOs);

            result = indexPersonService.filterDTOListByVisibleOrOwner(
                    dtoList, owner);

            assertEquals(size, result.size());
            ownershipDTOs.forEach(dto -> assertTrue(result.contains(dto)));
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldReturnByOwnerAndVisible(int size) {
            List<IndexPersonDTO> result;
            int sizeNotVisible = size * 2;
            String owner = "admin";
            List<IndexPersonDTO> compareList = new ArrayList<>();
            List<IndexPersonDTO> dtoList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, sizeNotVisible, STAFF);
            List<IndexPersonDTO> ownershipDTOs = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1 + sizeNotVisible, size, PATIENT);
            List<IndexPersonDTO> visibleDTOs = INDEX_PERSON_TEST_HELPER
                    .getDTOList(
                            1 + sizeNotVisible + size,
                            size,
                            PATIENT);

            dtoList.forEach(dto -> dto.setOwner("guest"));
            ownershipDTOs.forEach(dto -> dto.setOwner(owner));
            visibleDTOs.forEach(dto -> dto.setVisible(true));
            dtoList.addAll(ownershipDTOs);
            dtoList.addAll(visibleDTOs);
            compareList.addAll(ownershipDTOs);
            compareList.addAll(visibleDTOs);

            result = indexPersonService.filterDTOListByVisibleOrOwner(
                    dtoList, owner);

            assertEquals(size * 2, result.size());
            compareList.forEach(dto -> assertTrue(result.contains(dto)));
        }


        @Test
        public void shouldShowVisibleWhenNotOwner() {
            List<IndexPersonDTO> result;
            int size = 10;
            String owner = "admin";
            List<IndexPersonDTO> dtoList = INDEX_PERSON_TEST_HELPER
                    .getDTOList(1, size, STAFF);

            dtoList.forEach(dto -> {
                dto.setOwner("user");
                dto.setVisible(true);
            });

            result = indexPersonService.filterDTOListByVisibleOrOwner(
                    dtoList, owner);

            assertEquals(size, result.size());
            dtoList.forEach(dto -> assertTrue(result.contains(dto)));
        }
    }


    @Nested
    public class GetAllByFirstReportDateBeforeLocalDate {
        @Test
        public void shouldReturnEmptyListWhenNoResult() {
            List<IndexPerson> result;
            when(indexPersonRepository.findAll()).thenReturn(new ArrayList<>());

            result = indexPersonService
                    .getAllByFirstReportDateBeforeLocalDate(LocalDate.now());

            assertTrue(result.isEmpty());
            verify(indexPersonRepository, times(1))
                    .findAll();
            verify(reportService, times(0))
                    .getById(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldReturnEmptyWhenInputIsNullAndFRNotNull(int size) {
            List<IndexPerson> result;
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1,
                            size,
                            STAFF,
                            LocalDate.of(2021, 2, 2));

            when(indexPersonRepository.findAll()).thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService
                    .getAllByFirstReportDateBeforeLocalDate(null);

            assertTrue(result.isEmpty());
            verify(indexPersonRepository, times(1))
                    .findAll();
            verify(reportService, times(size))
                    .getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldReturnFirstReportDatesNull(int size) {
            List<IndexPerson> result;
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1,
                            size,
                            STAFF,
                            null);

            when(indexPersonRepository.findAll()).thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService
                    .getAllByFirstReportDateBeforeLocalDate(null);

            assertEquals(size, result.size());
            verify(indexPersonRepository, times(1))
                    .findAll();
            verify(reportService, times(size))
                    .getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldReturnListByFirstReportDateBeforeGivenDate(
                int size) {
            List<IndexPerson> result;
            LocalDate firstReportDate = LocalDate.of(2021, 2, 10);
            LocalDate dateAfterFirstReport = firstReportDate.plusDays(1L);
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1,
                            size,
                            STAFF,
                            firstReportDate);

            when(indexPersonRepository.findAll()).thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService.getAllByFirstReportDateBeforeLocalDate(
                    dateAfterFirstReport);

            assertEquals(size, result.size());
            verify(indexPersonRepository, times(1))
                    .findAll();
            verify(reportService, times(size))
                    .getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldReturnEmptyByFirstReportDateAfterGivenDate(
                int size) {
            List<IndexPerson> result;
            LocalDate firstReportDate = LocalDate.of(2021, 2, 10);
            LocalDate dateBeforeFirstReport = firstReportDate.minusDays(1L);
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1,
                            size,
                            STAFF,
                            firstReportDate);

            when(indexPersonRepository.findAll()).thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService.getAllByFirstReportDateBeforeLocalDate(
                    dateBeforeFirstReport);

            assertTrue(result.isEmpty());
            verify(indexPersonRepository, times(1))
                    .findAll();
            verify(reportService, times(size))
                    .getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldReturnEmptyByFirstReportDateEqualsGivenDate(
                int size) {
            List<IndexPerson> result;
            LocalDate date = LocalDate.of(2021, 2, 10);
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1,
                            size,
                            STAFF,
                            date);

            when(indexPersonRepository.findAll()).thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService.getAllByFirstReportDateBeforeLocalDate(
                    date);

            assertTrue(result.isEmpty());
            verify(indexPersonRepository, times(1))
                    .findAll();
            verify(reportService, times(size))
                    .getFirstReport(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldOnlyReturnIndexByFirstReportDateBeforeGivenDate(
                int size) {
            List<IndexPerson> result;
            int verifyListSize = 1;
            LocalDate date = LocalDate.of(2021, 2, 10);
            LocalDate validFirstReportDate = date.minusDays(1L);
            List<IndexPerson> repoList = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1,
                            verifyListSize,
                            STAFF,
                            validFirstReportDate);
            List<IndexPerson> indexListEqualsInputDate = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1 + verifyListSize,
                            size,
                            STAFF,
                            date);
            List<IndexPerson> indexListAfterInputDate = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1 + verifyListSize + size,
                            size,
                            STAFF,
                            date.plusDays(1L));
            List<IndexPerson> indexListNull = INDEX_PERSON_TEST_HELPER
                    .getIndexWithReportDate(
                            1 + verifyListSize + (size * 2),
                            size,
                            STAFF,
                            null);

            repoList.addAll(indexListEqualsInputDate);
            repoList.addAll(indexListAfterInputDate);
            repoList.addAll(indexListNull);

            when(indexPersonRepository.findAll()).thenReturn(repoList);
            repoList.forEach(i -> when(reportService.getFirstReport(i.getId()))
                    .thenReturn(i.getReports().get(0)));

            result = indexPersonService.getAllByFirstReportDateBeforeLocalDate(
                    date);

            assertEquals(verifyListSize, result.size());
            assertEquals(repoList.get(0), result.get(0));
        }
    }
}
