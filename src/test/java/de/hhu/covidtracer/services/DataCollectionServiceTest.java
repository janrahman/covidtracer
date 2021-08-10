package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.DataCollectionDTO;
import de.hhu.covidtracer.exceptions.DataCollectionException;
import de.hhu.covidtracer.helper.DataCollectionTestHelper;
import de.hhu.covidtracer.mappers.DataCollectionMapper;
import de.hhu.covidtracer.models.DataCollection;
import de.hhu.covidtracer.repositories.DataCollectionRepository;
import de.hhu.covidtracer.services.implementations.DataCollectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataCollectionServiceTest {
    private static final DataCollectionTestHelper DATA_COLLECTION_TEST_HELPER =
            new DataCollectionTestHelper();
    @Mock
    private DataCollectionRepository dataCollectionRepository;
    @Mock
    private IndexPersonService indexPersonService;
    @Mock
    private ContactPersonService contactPersonService;
    @Mock
    private DataCollectionMapper dataCollectionMapper;

    private DataCollectionService dataCollectionService;


    @BeforeEach
    public void setup() {
        dataCollectionService = new DataCollectionServiceImpl(
                dataCollectionRepository,
                indexPersonService,
                contactPersonService,
                dataCollectionMapper);
    }


    @Nested
    public class GetEntity {
        @Test
        public void shouldReturnObject() {
            DataCollection result;

            when(indexPersonService.countByStatusAndInQuarantine(any()))
                    .thenReturn(1L);
            when(indexPersonService.countByStatusAndInQuarantine(any()))
                    .thenReturn(1L);
            when(contactPersonService.countStaffContacts()).thenReturn(1L);
            when(indexPersonService
                    .countStaffAbortedQuarantine(any(), anyInt()))
                    .thenReturn(1L);
            when(indexPersonService
                    .countIndexInQuarantineByStatusAndFirstReportDate(
                            any(),
                            any()))
                    .thenReturn(1L);

            result = dataCollectionService
                    .createDataCollection(14);

            assertEquals(1L, result
                    .getCountIndexPersonStaffPauseQuarantine());
            assertEquals(1L, result
                    .getCountIndexStatusPatientsInQuarantine());
            assertEquals(1L, result
                    .getCountIndexStatusStaffInQuarantine());
            assertEquals(1L, result
                    .getCountNewPatientsInQuarantine());
            assertEquals(1L, result
                    .getCountNewStaffInQuarantine());
            assertEquals(1L, result
                    .getCountPositiveContactStaff());
        }
    }


    @Nested
    public class GetDTOs {
        @Test
        public void shouldReturnAllDTOs() {
            when(dataCollectionRepository.findAll())
                    .thenReturn(new ArrayList<>());
            when(dataCollectionMapper.toDataCollectionDTOs(any()))
                    .thenReturn(new ArrayList<>());

            dataCollectionService.getAllDTOs();

            verify(dataCollectionRepository, times(1))
                    .findAll();
            verify(dataCollectionMapper, times(1))
                    .toDataCollectionDTOs(any());
        }


        @Test
        public void shouldThrowErrorWhenInputsAreNull() {
            assertThrows(
                    DataCollectionException.class,
                    () -> dataCollectionService
                            .getDTOsBetweenDates(null, null));
        }


        @Test
        public void shouldThrowErrorWhenStartDateIsNull() {
            assertThrows(
                    DataCollectionException.class,
                    () -> dataCollectionService
                            .getDTOsBetweenDates(null, LocalDate.now()));
        }


        @Test
        public void shouldThrowErrorWhenEndDateIsNull() {
            assertThrows(
                    DataCollectionException.class,
                    () -> dataCollectionService
                            .getDTOsBetweenDates(LocalDate.now(), null));
        }


        @Test
        public void shouldThrowErrorWhenStartDateIsAfterEndDate() {
            LocalDate startDate = LocalDate.of(2020, 10, 12);
            LocalDate endDate = startDate.minusDays(14);
            assertThrows(
                    DataCollectionException.class,
                    () -> dataCollectionService
                            .getDTOsBetweenDates(startDate, endDate));
        }


        @Test
        public void shouldReturnListWhenStartDateAndEndDateAreValid() {
            LocalDate startDate = LocalDate.of(2020, 10, 12);
            LocalDate endDate = startDate.plusDays(14);

            when(dataCollectionRepository
                    .findAllByTimestampGreaterThanEqualAndTimestampLessThanEqual(
                            any(),
                            any()))
                    .thenReturn(new ArrayList<>());
            when(dataCollectionMapper.toDataCollectionDTOs(any()))
                    .thenReturn(new ArrayList<>());

            dataCollectionService.getDTOsBetweenDates(startDate, endDate);

            verify(dataCollectionRepository, times(1))
                    .findAllByTimestampGreaterThanEqualAndTimestampLessThanEqual(
                            any(),
                            any());
            verify(dataCollectionMapper, times(1))
                    .toDataCollectionDTOs(any());
        }
    }


    @Nested
    public class Save {
        @Test
        public void shouldReturnSavedEntity() {
            DataCollection result;
            DataCollection dataCollection = DATA_COLLECTION_TEST_HELPER
                    .create(1L, Instant.now());

            when(dataCollectionRepository.save(dataCollection))
                    .thenReturn(dataCollection);

            result = dataCollectionService.save(dataCollection);

            assertEquals(dataCollection.getId(), result.getId());
            verify(dataCollectionRepository, times(1))
                    .save(any());
        }
    }


    @Nested
    public class Count {
        @Test
        public void shouldReturnZeroNewPatientsWhenListIsNull() {
            long result;
            long expectedCount = 0L;

            result = dataCollectionService.countNewPatients(null);

            assertEquals(expectedCount, result);
        }


        @Test
        public void shouldReturnZeroNewPatientsWhenListIsEmpty() {
            long result;
            long expectedCount = 0L;

            result = dataCollectionService.countNewPatients(new ArrayList<>());

            assertEquals(expectedCount, result);
        }


        @ParameterizedTest
        @ValueSource(longs = {1L, 10L, 100L})
        public void shouldReturnNewPatientsOfOneDataCollection(
                long expectedCount) {
            long result;
            DataCollectionDTO dataCollectionDTO = DATA_COLLECTION_TEST_HELPER
                    .createDTO(expectedCount, 10L, Instant.now());

            result = dataCollectionService
                    .countNewPatients(
                            Collections.singletonList(dataCollectionDTO));

            assertEquals(expectedCount, result);
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100, 1000})
        public void shouldReturnSumNewPatientsFromList(int size) {
            long result;
            long newPatientCount = 10L;
            long expectedCount = newPatientCount * size;

            List<DataCollectionDTO> dataCollectionDTOList = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                dataCollectionDTOList.add(DATA_COLLECTION_TEST_HELPER.createDTO(
                        newPatientCount,
                        1L,
                        Instant.now().plusSeconds(i)));
            }

            result = dataCollectionService.countNewPatients(dataCollectionDTOList);

            assertEquals(expectedCount, result);
        }


        @Test
        public void shouldReturnZeroNewStaffWhenListIsNull() {
            long result;
            long expectedCount = 0L;

            result = dataCollectionService.countNewStaff(null);

            assertEquals(expectedCount, result);
        }


        @Test
        public void shouldReturnZeroNewStaffWhenListIsEmpty() {
            long result;
            long expectedCount = 0L;

            result = dataCollectionService.countNewStaff(new ArrayList<>());

            assertEquals(expectedCount, result);
        }


        @ParameterizedTest
        @ValueSource(longs = {1L, 10L, 100L})
        public void shouldReturnNewStaffCountOfOneDataCollection(
                long expectedCount) {
            long result;
            DataCollectionDTO dataCollectionDTO = DATA_COLLECTION_TEST_HELPER
                    .createDTO(0L, expectedCount, Instant.now());

            result = dataCollectionService
                    .countNewStaff(
                            Collections.singletonList(dataCollectionDTO));

            assertEquals(expectedCount, result);
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100, 1000})
        public void shouldReturnNewStaffSumFromList(int size) {
            long result;
            long newStaffCount = 10L;
            long expectedCount = newStaffCount * size;

            List<DataCollectionDTO> dataCollectionDTOList = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                dataCollectionDTOList.add(DATA_COLLECTION_TEST_HELPER.createDTO(
                        0L,
                        newStaffCount,
                        Instant.now().plusSeconds(i)));
            }

            result = dataCollectionService.countNewStaff(dataCollectionDTOList);

            assertEquals(expectedCount, result);
        }
    }
}
