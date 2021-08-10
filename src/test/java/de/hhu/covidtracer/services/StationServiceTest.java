package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.StationDTO;
import de.hhu.covidtracer.exceptions.StationException;
import de.hhu.covidtracer.exceptions.StationNotFoundException;
import de.hhu.covidtracer.helper.ContactPersonTestHelper;
import de.hhu.covidtracer.helper.StationTestHelper;
import de.hhu.covidtracer.mappers.StationMapper;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.Station;
import de.hhu.covidtracer.repositories.StationRepository;
import de.hhu.covidtracer.services.implementations.StationServiceImpl;
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
import java.util.Optional;

import static de.hhu.covidtracer.models.Status.STAFF;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {
    private static final StationTestHelper STATION_TEST_HELPER =
            new StationTestHelper();
    private static final ContactPersonTestHelper CONTACT_PERSON_TEST_HELPER =
            new ContactPersonTestHelper();

    @Mock
    private StationRepository stationRepository;
    @Mock
    private ContactPersonService contactPersonService;
    @Mock
    private StationMapper stationMapper;

    private StationService stationService;


    @BeforeEach
    public void setup() {
        stationService = new StationServiceImpl(
                stationRepository,
                contactPersonService,
                stationMapper);
    }


    @Nested
    public class GetEntities {
        @Test
        public void shouldReturnList() {
            when(stationRepository.findAll()).thenReturn(new ArrayList<>());

            stationService.getAll();

            verify(stationRepository, times(1))
                    .findAll();
        }


        @Test
        public void shouldThrowErrorWhenIdNotFound() {
            assertThrows(
                    StationNotFoundException.class,
                    () -> stationService.getById(1L));
        }


        @Test
        public void shouldReturnEntityWhenIdFound() {
            when(stationRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new Station()));

            stationService.getById(1L);

            verify(stationRepository, times(1))
                    .findById(anyLong());
        }
    }


    @Nested
    public class GetDTOs {
        @Test
        public void shouldReturnDTOList() {
            when(stationRepository.findAll()).thenReturn(new ArrayList<>());
            when(stationMapper.toStationDTOs(any()))
                    .thenReturn(new ArrayList<>());

            stationService.getAllDTOs();

            verify(stationRepository, times(1))
                    .findAll();
            verify(stationMapper, times(1))
                    .toStationDTOs(any());
        }


        @Test
        public void shouldThrowErrorWhenIdNotFound() {
            assertThrows(
                    StationNotFoundException.class,
                    () -> stationService.getDTOById(1L));
        }


        @Test
        public void shouldReturnDTOWhenIdFound() {
            when(stationRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new Station()));
            when(stationMapper.toStationDTO(any()))
                    .thenReturn(new StationDTO());

            stationService.getDTOById(1L);

            verify(stationRepository, times(1))
                    .findById(anyLong());
            verify(stationMapper, times(1))
                    .toStationDTO(any());
        }
    }


    @Nested
    public class GetStationFromFile {
        @Test
        public void shouldReturnNullWhenNull() {
            assertNull(stationService.getStationFromFile(null));
        }


        @Test
        public void shouldReturnNullWhenNameIsNull() {
            assertNull(stationService.getStationFromFile(new StationDTO()));
        }


        @ParameterizedTest
        @ValueSource(strings = {"", " ", "         ", "\n", "\t", "\r", "\n\r\t"})
        public void shouldReturnNullWhenNameIsEmpty(String whitespaces) {
            StationDTO stationDTO = new StationDTO();

            stationDTO.setName(whitespaces);

            assertNull(stationService.getStationFromFile(stationDTO));
        }


        @Test
        public void shouldReturnStationWhenFoundInDatabase() {
            Station result;
            String name = "test";
            Station fromDatabase = STATION_TEST_HELPER.create(1L, name);
            StationDTO stationDTO = new StationDTO();

            stationDTO.setName(name);

            when(stationRepository.findByName(any()))
                    .thenReturn(Optional.of(fromDatabase));

            result = stationService.getStationFromFile(stationDTO);

            assertEquals(fromDatabase, result);
            verify(stationRepository, times(0))
                    .save(any());
            verify(stationMapper, times(1))
                    .toStation(any());
        }


        @Test
        public void shouldSaveFromDTO() {
            Station result;
            String name = "test";
            Station fromDTO = STATION_TEST_HELPER.create(0L, name);
            StationDTO stationDTO = new StationDTO();

            stationDTO.setName(name);

            when(stationRepository.findByName(any()))
                    .thenReturn(Optional.empty());
            when(stationMapper.toStation(any()))
                    .thenReturn(fromDTO);
            when(stationRepository.save(any()))
                    .thenReturn(fromDTO);

            result = stationService.getStationFromFile(stationDTO);

            assertEquals(fromDTO, result);
            verify(stationRepository, times(1))
                    .save(any());
            verify(stationMapper, times(1))
                    .toStation(any());
        }
    }


    @Nested
    public class Update {
        @Test
        public void shouldThrowErrorWhenIdNotFound() {
            assertThrows(
                    StationNotFoundException.class,
                    () -> stationService.update(new StationDTO()));
        }


        @Test
        public void shouldUpdateWhenIdFound() {
            when(stationRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new Station()));
            doNothing().when(stationMapper).updateModel(any(), any());
            when(stationRepository.save(any())).thenReturn(new Station());

            stationService.update(new StationDTO());

            verify(stationMapper, times(1))
                    .updateModel(any(), any());
            verify(stationRepository, times(1))
                    .save(any());
        }


        @Test
        public void shouldDoNothingWhenDTOIsNull() {
            stationService
                    .updateStationFromDTOAndContactId(null, 1L);

            verify(stationRepository, times(0))
                    .findById(anyLong());
            verify(stationRepository, times(0))
                    .findByContacts_Id(anyLong());
            verify(stationRepository, times(0))
                    .save(any());
        }


        @Test
        public void shouldThrowErrorWhenDTOIdNotFound() {
            assertThrows(
                    StationNotFoundException.class,
                    () -> stationService.updateStationFromDTOAndContactId(
                            new StationDTO(),
                            1L));
        }


        @Test
        public void shouldNotUpdateWhenOldStationEqualsNew() {
            Station station = STATION_TEST_HELPER.create(1L);

            when(stationRepository.findById(anyLong()))
                    .thenReturn(Optional.of(station));
            when(stationRepository.findByContacts_Id(anyLong()))
                    .thenReturn(Optional.of(station));
            when(contactPersonService.getById(anyLong()))
                    .thenReturn(new ContactPerson());

            stationService.updateStationFromDTOAndContactId(
                    new StationDTO(),
                    1L);

            verify(stationRepository, times(0))
                    .save(any());
        }


        @Test
        public void shouldUpdateWhenContactHasNoStation() {
            Station station = STATION_TEST_HELPER.create(1L);
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, STAFF);

            when(stationRepository.findById(anyLong()))
                    .thenReturn(Optional.of(station));
            when(stationRepository.findByContacts_Id(anyLong()))
                    .thenReturn(Optional.empty());
            when(contactPersonService.getById(anyLong()))
                    .thenReturn(contactPerson);

            stationService.updateStationFromDTOAndContactId(
                    new StationDTO(),
                    1L);

            assertEquals(station, contactPerson.getStation());
            assertTrue(station.getContacts().contains(contactPerson));
            verify(stationRepository, times(1))
                    .save(any());
        }


        @Test
        public void shouldReplaceOldStationOfContact() {
            Station station = STATION_TEST_HELPER.create(1L);
            Station oldStation = STATION_TEST_HELPER.create(2L);
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, STAFF);

            contactPerson.setStation(oldStation);

            when(stationRepository.findById(anyLong()))
                    .thenReturn(Optional.of(station));
            when(stationRepository.findByContacts_Id(anyLong()))
                    .thenReturn(Optional.of(oldStation));
            when(contactPersonService.getById(anyLong()))
                    .thenReturn(contactPerson);

            stationService.updateStationFromDTOAndContactId(
                    new StationDTO(),
                    1L);

            assertEquals(station, contactPerson.getStation());
            assertTrue(station.getContacts().contains(contactPerson));
            verify(stationRepository, times(2))
                    .save(any());
        }
    }


    @Nested
    public class Save {
        @ParameterizedTest
        @ValueSource(strings = {"ABC", "PO11", "1LLL", "!=!ASD"})
        public void shouldSaveWithoutNameFormat(String name) {
            Station result;
            Station station = STATION_TEST_HELPER.create(1L, name);
            when(stationRepository.save(any())).thenReturn(station);

            result = stationService.save(station);

            assertEquals(name, result.getName());
            verify(stationRepository, times(1))
                    .save(station);
        }


        @ParameterizedTest
        @ValueSource(strings = {"abc", "p o 11", "ll1ll l", "qweQQd !! ++"})
        public void shouldSaveWithNameFormat(String name) {
            Station result;
            Station station = STATION_TEST_HELPER.create(1L, name);
            when(stationRepository.save(any())).thenReturn(station);

            result = stationService.save(station);

            assertNotEquals(name, result.getName());
            verify(stationRepository, times(1))
                    .save(station);
        }


        @Test
        public void shouldSaveAll() {
            when(stationRepository.saveAll(any()))
                    .thenReturn(new ArrayList<>());

            stationService.saveAll(new ArrayList<>());

            verify(stationRepository, times(1))
                    .saveAll(any());
        }


        @Test
        public void shouldThrowErrorWhenDTOIsNull() {
            assertThrows(StationException.class, () -> stationService
                    .saveNewEntityFromDTO(null));
        }


        @Test
        public void shouldThrowErrorWhenStationAlreadyExists() {
            StationDTO stationDTO = new StationDTO();

            stationDTO.setName("test");
            when(stationRepository.existsByName(anyString())).thenReturn(true);

            assertThrows(StationException.class, () -> stationService
                    .saveNewEntityFromDTO(stationDTO));
        }


        @Test
        public void shouldSaveDTO() {
            Station result;
            Station toStation = STATION_TEST_HELPER.create(1L);

            when(stationRepository.existsByName(any())).thenReturn(false);
            when(stationMapper.toStation(any())).thenReturn(toStation);
            when(stationRepository.save(any())).thenReturn(toStation);

            result = stationService.saveNewEntityFromDTO(new StationDTO());

            assertEquals(toStation, result);
            verify(stationMapper, times(1))
                    .toStation(any());
            verify(stationRepository, times(1))
                    .save(any());
        }
    }


    @Nested
    public class Delete {
        @Test
        public void shouldThrowErrorWhenNull() {
            assertThrows(
                    StationException.class,
                    () -> stationService.remove(null));
        }


        @Test
        public void shouldRemoveStationWithoutContact() {
            Station station = new Station();

            stationService.remove(station);

            verify(stationRepository, times(1))
                    .delete(any());
            verify(contactPersonService, times(0))
                    .save(any());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100, 1000})
        public void shouldRemoveStationWithContacts(int size) {
            Station station = STATION_TEST_HELPER.create(1L);
            List<ContactPerson> contactPersonList = CONTACT_PERSON_TEST_HELPER
                    .getContacts(1, size, STAFF);

            contactPersonList.forEach(c -> c.setStation(station));

            stationService.remove(station);

            contactPersonList.forEach(c -> assertNull(c.getStation()));
            verify(stationRepository, times(1))
                    .delete(any());
            verify(contactPersonService, times(size))
                    .save(any());
        }
    }


    @Nested
    public class RemoveAssociation {
        @Test
        public void shouldDoNothingWhenAllNull() {
            stationService.removeOldStationFromContact(
                    null, null);

            verify(stationRepository, times(0))
                    .save(any());
        }


        @Test
        public void shouldDoNothingWhenStationIsNull() {
            stationService.removeOldStationFromContact(
                    null, new ContactPerson());

            verify(stationRepository, times(0))
                    .save(any());
        }


        @Test
        public void shouldDoNothingWhenContactIsNull() {
            stationService.removeOldStationFromContact(
                    new Station(), null);

            verify(stationRepository, times(0))
                    .save(any());
        }


        @Test
        public void shouldDoNothingContactHasNoStation() {
            stationService.removeOldStationFromContact(
                    new Station(), new ContactPerson());

            verify(stationRepository, times(0))
                    .save(any());
        }


        @Test
        public void shouldDoNothingWhenNewStationEqualsOldStation() {
            Station station = STATION_TEST_HELPER.create(1L);
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, STAFF);

            contactPerson.setStation(station);

            stationService.removeOldStationFromContact(station, contactPerson);

            assertEquals(station, contactPerson.getStation());
            verify(stationRepository, times(0))
                    .save(any());
        }


        @Test
        public void shouldThrowErrorWhenOldStationIdNotFound() {
            Station station = STATION_TEST_HELPER.create(1L);
            Station oldStation = STATION_TEST_HELPER.create(2L);
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, STAFF);

            contactPerson.setStation(oldStation);

            assertThrows(
                    StationNotFoundException.class,
                    () -> stationService.removeOldStationFromContact(
                            station,
                            contactPerson));
        }


        @Test
        public void shouldRemoveOldStation() {
            Station station = STATION_TEST_HELPER.create(1L);
            Station oldStation = STATION_TEST_HELPER.create(2L);
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, STAFF);

            contactPerson.setStation(oldStation);
            when(stationRepository.findById(2L))
                    .thenReturn(Optional.of(oldStation));
            when(stationRepository.save(oldStation)).thenReturn(oldStation);

            stationService.removeOldStationFromContact(station, contactPerson);

            assertNull(contactPerson.getStation());
            verify(stationRepository, times(1))
                    .save(any());
        }
    }


    @Nested
    public class FormatStationName {
        @Test
        public void shouldReturnEmptyStringWhenNull() {
            assertTrue(stationService.formatStationName(null).isEmpty());
        }


        @ParameterizedTest
        @ValueSource(strings = {"", " ", "         ", "\n", "\t", "\r", "\n\r\t"})
        public void shouldReturnEmptyStringWhenEmpty(String whitespace) {
            assertTrue(stationService.formatStationName(whitespace).isEmpty());
        }


        @ParameterizedTest
        @ValueSource(strings = {"upper", "test123", "asdfg"})
        public void shouldReturnUppercase(String name) {
            assertEquals(
                    name.toUpperCase(),
                    stationService.formatStationName(name));
        }


        @ParameterizedTest
        @ValueSource(strings = {"upper case", "     upper    case  ", "\n\t upper \n case", "\r upper case"})
        public void shouldRemoveWhitespacesAndUppercase(String name) {
            String expected = "UPPERCASE";
            assertEquals(expected, stationService.formatStationName(name));
        }
    }
}
