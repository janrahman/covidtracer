package de.hhu.covidtracer.services;

import de.hhu.covidtracer.helper.IndexPersonTestHelper;
import de.hhu.covidtracer.models.DataCollection;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.services.implementations.SchedulerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static de.hhu.covidtracer.models.Status.PATIENT;
import static de.hhu.covidtracer.models.Status.STAFF;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SchedulerServiceTest {
    private static final IndexPersonTestHelper INDEX_PERSON_TEST_HELPER =
            new IndexPersonTestHelper();

    @Mock
    private IndexPersonService indexPersonService;
    @Mock
    private DataCollectionService dataCollectionService;

    private SchedulerService schedulerService;


    @BeforeEach
    public void setup() {
        schedulerService = new SchedulerServiceImpl(
                indexPersonService,
                dataCollectionService);
    }


    @Nested
    public class Setter {
        @Test
        public void shouldNotUpdateQuarantineStatusWhenListsAreEmpty() {
            when(indexPersonService
                    .getAllByFirstReportDateBeforeLocalDate(any()))
                    .thenReturn(new ArrayList<>());
            when(indexPersonService
                    .getAllByEntryDateTimeLessEqualsThan(any()))
                    .thenReturn(new ArrayList<>());

            schedulerService.setScheduledQuarantineStatus(0);

            verify(indexPersonService, times(0))
                    .save(any());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100, 1000})
        public void shouldUpdateQuarantineStatusWhenListByEntryDateTimeFound(
                int size) {
            List<IndexPerson> allByEntryDateTimeLessEqualsThan =
                    INDEX_PERSON_TEST_HELPER
                            .getIndexPeople(1, size, STAFF);


            when(indexPersonService
                    .getAllByFirstReportDateBeforeLocalDate(any()))
                    .thenReturn(new ArrayList<>());
            when(indexPersonService
                    .getAllByEntryDateTimeLessEqualsThan(any()))
                    .thenReturn(allByEntryDateTimeLessEqualsThan);

            schedulerService.setScheduledQuarantineStatus(0);

            verify(indexPersonService, times(size))
                    .save(any());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100, 1000})
        public void shouldUpdateQSWhenEDTFoundAndFilterInQuarantineOnly(
                int size) {
            List<IndexPerson> all = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, 10, PATIENT);
            List<IndexPerson> inQuarantine =
                    INDEX_PERSON_TEST_HELPER
                            .getIndexPeople(1, size, STAFF);

            all.forEach(i -> i.setInQuarantine(false));
            all.addAll(inQuarantine);

            when(indexPersonService
                    .getAllByFirstReportDateBeforeLocalDate(any()))
                    .thenReturn(new ArrayList<>());
            when(indexPersonService
                    .getAllByEntryDateTimeLessEqualsThan(any()))
                    .thenReturn(all);

            schedulerService.setScheduledQuarantineStatus(0);

            all.forEach(i -> assertFalse(i.isInQuarantine()));
            verify(indexPersonService, times(size))
                    .save(any());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100, 1000})
        public void shouldUpdateQuarantineStatusWhenListByLocalDateFound(
                int size) {
            List<IndexPerson> allByFirstReportDateBeforeLocalDate =
                    INDEX_PERSON_TEST_HELPER
                            .getIndexPeople(1, size, STAFF);


            when(indexPersonService
                    .getAllByFirstReportDateBeforeLocalDate(any()))
                    .thenReturn(allByFirstReportDateBeforeLocalDate);
            when(indexPersonService
                    .getAllByEntryDateTimeLessEqualsThan(any()))
                    .thenReturn(new ArrayList<>());

            schedulerService.setScheduledQuarantineStatus(0);

            verify(indexPersonService, times(size))
                    .save(any());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100, 1000})
        public void shouldUpdateQSWhenLDFoundAndFilterInQuarantineOnly(
                int size) {
            List<IndexPerson> all = INDEX_PERSON_TEST_HELPER
                    .getIndexPeople(1, 10, PATIENT);
            List<IndexPerson> inQuarantine =
                    INDEX_PERSON_TEST_HELPER
                            .getIndexPeople(1, size, STAFF);

            all.forEach(i -> i.setInQuarantine(false));
            all.addAll(inQuarantine);

            when(indexPersonService
                    .getAllByFirstReportDateBeforeLocalDate(any()))
                    .thenReturn(all);
            when(indexPersonService
                    .getAllByEntryDateTimeLessEqualsThan(any()))
                    .thenReturn(new ArrayList<>());

            schedulerService.setScheduledQuarantineStatus(0);

            all.forEach(i -> assertFalse(i.isInQuarantine()));
            verify(indexPersonService, times(size))
                    .save(any());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100, 1000})
        public void shouldUpdateOnceWhenFoundInBothLists(int size) {
            List<IndexPerson> inQuarantine =
                    INDEX_PERSON_TEST_HELPER
                            .getIndexPeople(1, size, STAFF);

            when(indexPersonService
                    .getAllByFirstReportDateBeforeLocalDate(any()))
                    .thenReturn(inQuarantine);
            when(indexPersonService
                    .getAllByEntryDateTimeLessEqualsThan(any()))
                    .thenReturn(inQuarantine);

            schedulerService.setScheduledQuarantineStatus(0);

            inQuarantine.forEach(i -> assertFalse(i.isInQuarantine()));
            verify(indexPersonService, times(size))
                    .save(any());
        }


        @Test
        public void shouldSaveDataCollectionObject() {
            when(dataCollectionService.createDataCollection(anyInt()))
                    .thenReturn(new DataCollection());
            when(dataCollectionService.save(any()))
                    .thenReturn(new DataCollection());

            schedulerService.setSurvey(0);

            verify(dataCollectionService, times(1))
                    .createDataCollection(anyInt());
            verify(dataCollectionService, times(1))
                    .save(any());
        }


        @Test
        public void shouldSaveDataCollectionAndConvertNegativeDays() {
            int negativeDays = -14;
            int expected = -negativeDays;
            when(dataCollectionService.createDataCollection(anyInt()))
                    .thenReturn(new DataCollection());
            when(dataCollectionService.save(any()))
                    .thenReturn(new DataCollection());

            schedulerService.setSurvey(negativeDays);

            verify(dataCollectionService, times(1))
                    .createDataCollection(expected);
            verify(dataCollectionService, times(1))
                    .save(any());
        }
    }


    @Nested
    public class Delete {
        @Test
        public void shouldNotDeleteWhenListsAreEmpty() {
            when(indexPersonService
                    .getAllByFirstReportDateBeforeLocalDate(any()))
                    .thenReturn(new ArrayList<>());
            when(indexPersonService
                    .getAllByEntryDateTimeLessEqualsThan(any()))
                    .thenReturn(new ArrayList<>());

            schedulerService.setScheduledDeletionAfterDays(0);

            verify(indexPersonService, times(0))
                    .save(any());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100, 1000})
        public void shouldDeleteWhenListByEntryDateTimeFound(
                int size) {
            List<IndexPerson> allByEntryDateTimeLessEqualsThan =
                    INDEX_PERSON_TEST_HELPER
                            .getIndexPeople(1, size, STAFF);

            when(indexPersonService
                    .getAllByFirstReportDateBeforeLocalDate(any()))
                    .thenReturn(new ArrayList<>());
            when(indexPersonService
                    .getAllByEntryDateTimeLessEqualsThan(any()))
                    .thenReturn(allByEntryDateTimeLessEqualsThan);

            schedulerService.setScheduledDeletionAfterDays(0);

            verify(indexPersonService, times(size))
                    .remove(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100, 1000})
        public void shouldUpdateQuarantineStatusWhenListByLocalDateFound(
                int size) {
            List<IndexPerson> allByFirstReportDateBeforeLocalDate =
                    INDEX_PERSON_TEST_HELPER
                            .getIndexPeople(1, size, STAFF);

            when(indexPersonService
                    .getAllByFirstReportDateBeforeLocalDate(any()))
                    .thenReturn(allByFirstReportDateBeforeLocalDate);
            when(indexPersonService
                    .getAllByEntryDateTimeLessEqualsThan(any()))
                    .thenReturn(new ArrayList<>());

            schedulerService.setScheduledDeletionAfterDays(0);

            verify(indexPersonService, times(size))
                    .remove(anyLong());
        }
    }
}
