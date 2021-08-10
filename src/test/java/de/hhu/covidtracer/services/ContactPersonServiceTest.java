package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.ContactPersonDTO;
import de.hhu.covidtracer.exceptions.ContactPersonException;
import de.hhu.covidtracer.exceptions.ContactPersonNotFoundException;
import de.hhu.covidtracer.exceptions.ReportException;
import de.hhu.covidtracer.helper.ContactPersonTestHelper;
import de.hhu.covidtracer.mappers.ContactPersonMapper;
import de.hhu.covidtracer.models.*;
import de.hhu.covidtracer.models.associations.IndexContact;
import de.hhu.covidtracer.models.associations.IndexContactId;
import de.hhu.covidtracer.repositories.ContactPersonRepository;
import de.hhu.covidtracer.services.implementations.ContactPersonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static de.hhu.covidtracer.models.Status.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactPersonServiceTest {
    private static final ContactPersonTestHelper CONTACT_PERSON_TEST_HELPER =
            new ContactPersonTestHelper();

    @Mock
    private ContactPersonRepository contactPersonRepository;

    @Mock
    private IndexContactService indexContactService;

    @Mock
    private ContactPersonMapper contactPersonMapper;

    private ContactPersonService contactPersonService;


    @BeforeEach
    public void setup() {
        contactPersonService = new ContactPersonServiceImpl(
                contactPersonRepository,
                contactPersonMapper,
                indexContactService);
    }


    @Nested
    public class GetEntities {
        @Test
        public void shouldThrowExceptionWhenIdNotFound() {
            long id = 0L;

            assertThrows(
                    ContactPersonNotFoundException.class,
                    () -> contactPersonService.getById(id));
        }


        @Test
        public void shouldReturnContact() {
            long id = 1L;
            ContactPerson result;
            Optional<ContactPerson> contactPersonOptional = Optional
                    .of(new ContactPerson());

            when(contactPersonRepository.findById(id))
                    .thenReturn(contactPersonOptional);

            result = contactPersonService.getById(id);

            assertEquals(contactPersonOptional.get(), result);
        }
    }


    @Nested
    public class GetContactFromFile {
        @Test
        public void shouldThrowErrorWhenNull() {
            assertThrows(
                    ContactPersonException.class,
                    () -> contactPersonService.getContactFromFile(
                            null));
        }


        @Test
        public void shouldReturnEmptyContact() {
            ContactPerson result;
            ContactPerson dtoToContact = new ContactPerson();
            ContactPersonDTO contactPersonDTO = new ContactPersonDTO();

            when(contactPersonMapper.toContactPerson(any()))
                    .thenReturn(dtoToContact);
            when(contactPersonRepository.findByNameAndFirstNameAndAddress(
                    any(),
                    any(),
                    any()))
                    .thenReturn(Optional.empty());

            result = contactPersonService.getContactFromFile(contactPersonDTO);

            assertEquals(dtoToContact, result);
        }


        @Test
        public void shouldReturnContactFromDatabase() {
            ContactPerson result;
            long id = 1L;
            ContactPerson dtoToContact = new ContactPerson();
            ContactPerson entityFromDatabase = CONTACT_PERSON_TEST_HELPER
                    .getContact(id, STAFF);

            dtoToContact.setId(1L);
            when(contactPersonMapper.toContactPerson(any()))
                    .thenReturn(dtoToContact);
            when(contactPersonRepository.findByNameAndFirstNameAndAddress(
                    any(),
                    any(),
                    any()))
                    .thenReturn(Optional.of(entityFromDatabase));
            doNothing().when(contactPersonMapper).updateModel(any(), any());

            result = contactPersonService
                    .getContactFromFile(new ContactPersonDTO());

            assertEquals(entityFromDatabase.getId(), result.getId());
            assertEquals(entityFromDatabase.getName(), result.getName());
            assertEquals(entityFromDatabase.getFirstName(), result.getFirstName());
            assertEquals(entityFromDatabase.getAddress(), result.getAddress());
            verify(contactPersonMapper, times(1))
                    .updateModel(any(), any());
        }


        @Test
        public void shouldReturnNewContactFromDTO() {
            ContactPerson result;
            long id = 1L;
            ContactPerson dtoToContact = CONTACT_PERSON_TEST_HELPER
                    .getContact(id, STAFF);

            dtoToContact.setId(1L);
            when(contactPersonMapper.toContactPerson(any()))
                    .thenReturn(dtoToContact);
            when(contactPersonRepository.findByNameAndFirstNameAndAddress(
                    any(),
                    any(),
                    any()))
                    .thenReturn(Optional.empty());

            result = contactPersonService
                    .getContactFromFile(new ContactPersonDTO());

            assertEquals(dtoToContact.getId(), result.getId());
            assertEquals(dtoToContact.getName(), result.getName());
            assertEquals(dtoToContact.getFirstName(), result.getFirstName());
            assertEquals(dtoToContact.getAddress(), result.getAddress());
            verify(contactPersonMapper, times(0))
                    .updateModel(any(), any());
        }
    }


    @Nested
    public class GetDTOs {
        @Test
        public void shouldThrowErrorWhenIdNotFound() {
            long id = 0L;

            assertThrows(
                    ContactPersonNotFoundException.class,
                    () -> contactPersonService.getDTOById(id));
            verify(contactPersonRepository, times(1))
                    .findById(anyLong());
        }


        @Test
        public void shouldReturnDTOWhenIdFound() {
            long id = 1L;
            ContactPersonDTO result;
            ContactPersonDTO indexToDTO = CONTACT_PERSON_TEST_HELPER
                    .getDTO(1L, STAFF);
            Optional<ContactPerson> contactPersonOptional = Optional
                    .of(new ContactPerson());

            when(contactPersonRepository.findById(id))
                    .thenReturn(contactPersonOptional);
            when(contactPersonMapper.toContactPersonDTO(any()))
                    .thenReturn(indexToDTO);

            result = contactPersonService.getDTOById(id);

            assertEquals(indexToDTO, result);
        }


        @Test
        public void shouldThrowErrorWhenContactIdNotFound() {
            long contactId = 0L;
            long indexId = 1L;

            assertThrows(
                    ContactPersonNotFoundException.class,
                    () -> contactPersonService
                            .getDTOByIndexContactId(indexId, contactId));
            verify(contactPersonRepository, times(1))
                    .findById(anyLong());
            verify(indexContactService, times(0))
                    .getByIndexContactId(anyLong(), anyLong());
            verify(contactPersonMapper, times(0))
                    .toContactPersonDTO(any(), any());
        }


        @Test
        public void shouldReturnDTOWhenContactAndIndexContactFound() {
            long contactId = 1L;
            long indexId = 1L;
            ContactPersonDTO result;
            ContactPersonDTO contactToDTO = CONTACT_PERSON_TEST_HELPER
                    .getDTO(contactId, STAFF);

            when(contactPersonRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new ContactPerson()));
            when(indexContactService.getByIndexContactId(anyLong(), anyLong()))
                    .thenReturn(new IndexContact());
            when(contactPersonMapper.toContactPersonDTO(any(), any()))
                    .thenReturn(contactToDTO);

            result = contactPersonService
                    .getDTOByIndexContactId(indexId, contactId);

            assertEquals(contactToDTO.getId(), result.getId());
            assertEquals(contactToDTO.getName(), result.getName());
            assertEquals(contactToDTO.getFirstName(), result.getFirstName());
            assertEquals(contactToDTO.getAddress(), result.getAddress());
            assertEquals(contactToDTO.getStatus(), result.getStatus());
        }
    }


    @Nested
    public class GetDTOsByIndexId {
        @Test
        public void shouldReturnEmptyWhenNoRelationship() {
            List<ContactPersonDTO> result;

            when(indexContactService.getByIndexId(anyLong()))
                    .thenReturn(new ArrayList<>());

            result = contactPersonService.getDTOsByIndexId(1L);

            assertTrue(result.isEmpty());
        }


        @Test
        public void shouldReturnEmptyWhenNoContact() {
            List<ContactPersonDTO> result;
            List<IndexContact> indexContactList = new ArrayList<>();

            indexContactList.add(new IndexContact());

            when(indexContactService.getByIndexId(anyLong()))
                    .thenReturn(indexContactList);

            result = contactPersonService.getDTOsByIndexId(1L);

            assertTrue(result.isEmpty());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldReturnDTOList(int size) {
            List<ContactPersonDTO> result;
            IndexContact indexContact;
            List<IndexContact> indexContactList = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                indexContact = new IndexContact();

                indexContact.setContactPerson(new ContactPerson());
                indexContactList.add(indexContact);

                when(contactPersonMapper
                        .toContactPersonDTO(
                                indexContact.getContactPerson(),
                                indexContact))
                        .thenReturn(new ContactPersonDTO());
            }

            when(indexContactService.getByIndexId(anyLong()))
                    .thenReturn(indexContactList);

            result = contactPersonService.getDTOsByIndexId(1L);

            assertEquals(size, result.size());
            verify(indexContactService, times(1))
                    .getByIndexId(anyLong());
            verify(contactPersonMapper, times(size))
                    .toContactPersonDTO(any(), any());
        }


        @Test
        public void shouldReturnEmptyWhenGetByIdxIdIsEmpty() {
            List<ContactPersonDTO> result;

            when(indexContactService.getByIndexId(anyLong()))
                    .thenReturn(new ArrayList<>());

            result = contactPersonService
                    .getDTOsByIndexIdAndStatus(1L, VISITOR);

            assertTrue(result.isEmpty());
        }


        @Test
        public void shouldReturnEmptyWhenStatusNotInList() {
            List<ContactPersonDTO> result;
            List<IndexContact> indexContactList = new ArrayList<>();
            IndexContact indexContact = new IndexContact();
            ContactPerson contactPerson = new ContactPerson();
            ContactPersonDTO contactToDTO = new ContactPersonDTO();

            contactToDTO.setStatus(STAFF);

            indexContact.setContactPerson(contactPerson);
            indexContactList.add(indexContact);

            when(indexContactService.getByIndexId(anyLong()))
                    .thenReturn(indexContactList);
            when(contactPersonMapper.toContactPersonDTO(any(), any()))
                    .thenReturn(contactToDTO);

            result = contactPersonService
                    .getDTOsByIndexIdAndStatus(1L, PATIENT);

            assertTrue(result.isEmpty());
        }


        @Test
        public void shouldReturnListWhenStatusEqualsArg() {
            List<ContactPersonDTO> result;
            Status status = VISITOR;
            ContactPersonDTO contactToDTO = CONTACT_PERSON_TEST_HELPER
                    .getDTO(1L, status);
            List<IndexContact> indexContactList = new ArrayList<>();
            IndexContact indexContact = new IndexContact();

            indexContact.setContactPerson(new ContactPerson());
            indexContactList.add(indexContact);

            when(indexContactService.getByIndexId(anyLong()))
                    .thenReturn(indexContactList);
            when(contactPersonMapper.toContactPersonDTO(any(), any()))
                    .thenReturn(contactToDTO);

            result = contactPersonService
                    .getDTOsByIndexIdAndStatus(1L, status);

            assertEquals(1, result.size());
            assertTrue(result.contains(contactToDTO));
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldReturnOnlyValidStatus(int size) {
            List<ContactPersonDTO> result;
            IndexContact indexContact;
            ContactPerson contact;

            long indexId = 1L;
            Status status = PATIENT;
            int falseDTOsSize = 20;

            List<ContactPersonDTO> contactPersonDTOList =
                    CONTACT_PERSON_TEST_HELPER.getDTOs(1L, size, status);
            List<ContactPersonDTO> filteredStatusDTOs = CONTACT_PERSON_TEST_HELPER
                    .getDTOs(1L + size, falseDTOsSize, VISITOR);
            List<IndexContact> indexContactList = new ArrayList<>();

            contactPersonDTOList.addAll(filteredStatusDTOs);

            for (ContactPersonDTO c : contactPersonDTOList) {
                indexContact = new IndexContact();
                contact = new ContactPerson();

                contact.setId(c.getId());
                indexContact.setIndexContactId(
                        new IndexContactId(indexId, c.getId()));
                indexContact.setContactPerson(contact);
                indexContactList.add(indexContact);

                when(contactPersonMapper
                        .toContactPersonDTO(contact, indexContact))
                        .thenReturn(c);
            }

            when(indexContactService.getByIndexId(anyLong()))
                    .thenReturn(indexContactList);

            result = contactPersonService
                    .getDTOsByIndexIdAndStatus(indexId, status);

            assertEquals(size, result.size());
            result.forEach(dto -> {
                assertEquals(status, dto.getStatus());
                assertTrue(contactPersonDTOList.contains(dto));
            });
        }
    }


    @Nested
    public class Update {
        @Test
        public void shouldThrowErrorWhenIdNotFound() {
            long id = 0L;
            assertThrows(
                    ContactPersonNotFoundException.class,
                    () -> contactPersonService.update(id, new ContactPersonDTO()));
        }


        @Test
        public void shouldReturnUpdatedEntity() {
            ContactPerson result;
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, STAFF);

            when(contactPersonRepository
                    .findById(anyLong()))
                    .thenReturn(Optional.of(contactPerson));
            doNothing()
                    .when(contactPersonMapper)
                    .updateModel(any(), any());
            when(contactPersonRepository.save(any())).thenReturn(contactPerson);

            result = contactPersonService
                    .update(contactPerson.getId(), null);

            assertEquals(contactPerson.getId(), result.getId());
            assertEquals(contactPerson.getFirstName(), result.getFirstName());
            assertEquals(contactPerson.getName(), result.getName());
            verify(contactPersonMapper, times(1))
                    .updateModel(any(), any());
            verify(contactPersonRepository, times(1))
                    .save(any());
        }
    }


    @Nested
    public class Save {
        @Test
        public void shouldReturnSavedEntity() {
            ContactPerson result;
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, PATIENT);

            when(contactPersonRepository.save(contactPerson))
                    .thenReturn(contactPerson);

            result = contactPersonService.save(contactPerson);

            assertEquals(contactPerson, result);
            verify(contactPersonRepository, times(1))
                    .save(any());
        }


        @Test
        public void shouldReturnListWhenSaveAll() {
            List<ContactPerson> result;
            List<ContactPerson> contactPersonList = CONTACT_PERSON_TEST_HELPER
                    .getContacts(1, 10, PATIENT);

            when(contactPersonRepository.saveAll(any()))
                    .thenReturn(contactPersonList);

            result = contactPersonService.saveAll(contactPersonList);

            assertEquals(contactPersonList, result);
            verify(contactPersonRepository, times(1))
                    .saveAll(any());
        }


        @Test
        public void shouldThrowErrorWhenContactIdNotFoundOnReportSave() {
            assertThrows(
                    ContactPersonNotFoundException.class,
                    () -> contactPersonService.saveReport(
                            new Report(),
                            1L));
        }


        @Test
        public void shouldSaveReport() {
            ContactPerson result;
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, STAFF);
            Report report = new Report();

            when(contactPersonRepository.findById(anyLong()))
                    .thenReturn(Optional.of(contactPerson));
            when(contactPersonRepository.save(any()))
                    .thenReturn(contactPerson);

            result = contactPersonService
                    .saveReport(report, contactPerson.getId());

            assertTrue(result.getReports().contains(report));
            assertEquals(contactPerson.getId(), result.getId());
            assertEquals(contactPerson.getFirstName(), result.getFirstName());
            assertEquals(contactPerson.getName(), result.getName());
            verify(contactPersonRepository, times(1))
                    .save(any());
        }


        @Test
        public void shouldNotSaveWhenReportIsNull() {
            ContactPerson result;
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, STAFF);

            when(contactPersonRepository.findById(anyLong()))
                    .thenReturn(Optional.of(contactPerson));

            result = contactPersonService
                    .saveReport(null, contactPerson.getId());

            assertTrue(result.getReports().isEmpty());
            assertEquals(contactPerson.getId(), result.getId());
            assertEquals(contactPerson.getFirstName(), result.getFirstName());
            assertEquals(contactPerson.getName(), result.getName());
            verify(contactPersonRepository, times(0))
                    .save(any());
        }


        @Test
        public void shouldNotSaveWhenContactIsVisitor() {
            ContactPerson result;
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, VISITOR);

            when(contactPersonRepository.findById(anyLong()))
                    .thenReturn(Optional.of(contactPerson));

            result = contactPersonService
                    .saveReport(new Report(), contactPerson.getId());

            assertEquals(contactPerson.getId(), result.getId());
            assertEquals(contactPerson.getFirstName(), result.getFirstName());
            assertEquals(contactPerson.getName(), result.getName());
            assertEquals(VISITOR, result.getStatus());
            verify(contactPersonRepository, times(0))
                    .save(any());
        }


        @Test
        public void shouldThrowErrorWhenReportBelongsIndex() {
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, STAFF);
            Report report = new Report();

            report.setIndexPerson(new IndexPerson());

            when(contactPersonRepository.findById(anyLong()))
                    .thenReturn(Optional.of(contactPerson));

            assertThrows(
                    ReportException.class,
                    () -> contactPersonService
                            .saveReport(report, contactPerson.getId()));
        }


        @Test
        public void shouldReplaceOldContactInReport() {
            ContactPerson result;
            ContactPerson oldEntry = CONTACT_PERSON_TEST_HELPER
                    .getContact(2L, PATIENT);
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, STAFF);
            Report report = new Report();

            report.setContactPerson(oldEntry);

            when(contactPersonRepository.findById(anyLong()))
                    .thenReturn(Optional.of(contactPerson));
            when(contactPersonRepository.save(any()))
                    .thenReturn(contactPerson);

            result = contactPersonService
                    .saveReport(report, contactPerson.getId());

            assertTrue(result.getReports().contains(report));
            assertTrue(oldEntry.getReports().isEmpty());
            assertEquals(report.getContactPerson(), result);
            assertEquals(contactPerson.getId(), result.getId());
            assertEquals(contactPerson.getFirstName(), result.getFirstName());
            assertEquals(contactPerson.getName(), result.getName());
        }
    }


    @Nested
    public class Remove {
        @Test
        public void shouldDeleteWhenRelationshipsAreNull() {
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, VISITOR);

            doNothing().when(contactPersonRepository).delete(any());

            contactPersonService.remove(contactPerson);

            verify(contactPersonRepository, times(1))
                    .delete(any());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldRemoveReportsOnDelete(int sizeReports) {
            Report report;
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, VISITOR);

            for (int i = 0; i < sizeReports; i++) {
                report = new Report();
                report.setId(i);
                contactPerson.addReport(report);
            }

            doNothing().when(contactPersonRepository).delete(any());

            contactPersonService.remove(contactPerson);

            assertTrue(contactPerson.getReports().isEmpty());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldRemoveIndexContactsOnDelete(int sizeIndexContacts) {
            IndexContact indexContact;
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, VISITOR);

            for (int i = 0; i < sizeIndexContacts; i++) {
                indexContact = new IndexContact();

                indexContact.setIndexContactId(
                        new IndexContactId(i, contactPerson.getId()));
                contactPerson.addIndexContact(indexContact);
            }

            doNothing().when(contactPersonRepository).delete(any());

            contactPersonService.remove(contactPerson);

            assertTrue(contactPerson.getIndexContacts().isEmpty());
        }


        @Test
        public void shouldRemoveStationOnDelete() {
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, VISITOR);

            contactPerson.setStation(new Station());

            doNothing().when(contactPersonRepository).delete(any());

            contactPersonService.remove(contactPerson);

            assertNull(contactPerson.getStation());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldRemoveAllOnDelete(int size) {
            IndexContact indexContact;
            Report report;
            ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                    .getContact(1L, VISITOR);

            for (int i = 0; i < size; i++) {
                report = new Report();
                indexContact = new IndexContact();

                report.setId(i);
                indexContact.setIndexContactId(
                        new IndexContactId(i, contactPerson.getId()));

                contactPerson.addReport(report);
                contactPerson.addIndexContact(indexContact);
            }

            contactPerson.setStation(new Station());

            doNothing().when(contactPersonRepository).delete(any());

            contactPersonService.remove(contactPerson);

            assertTrue(contactPerson.getReports().isEmpty());
            assertTrue(contactPerson.getIndexContacts().isEmpty());
            assertNull(contactPerson.getStation());
        }


        @Test
        public void shouldDoNothingWhenSetIsNull() {
            contactPersonService.removeOrphans(null);

            verify(contactPersonRepository, times(0))
                    .delete(any());
        }


        @Test
        public void shouldDoNotDeleteOrphansWhenListIsEmpty() {
            contactPersonService.removeOrphans(new ArrayList<>());

            verify(contactPersonRepository, times(0))
                    .delete(any());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldDeleteOrphans(int size) {
            List<ContactPerson> contactPersonList = CONTACT_PERSON_TEST_HELPER
                    .getContacts(1, size, VISITOR);

            for (ContactPerson c : contactPersonList) {
                when(contactPersonRepository.findById(c.getId()))
                        .thenReturn(Optional.of(c));
            }

            doNothing().when(contactPersonRepository).delete(any());

            contactPersonService.removeOrphans(contactPersonList.stream()
                    .map(ContactPerson::getId)
                    .collect(Collectors.toList()));

            verify(contactPersonRepository, times(size)).delete(any());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldDeleteOrphansOnly(int size) {
            IndexContact indexContact;
            int notOrphansSize = 20;
            List<ContactPerson> contactPersonList = CONTACT_PERSON_TEST_HELPER
                    .getContacts(1, size, VISITOR);
            List<ContactPerson> contactsInRelationship = CONTACT_PERSON_TEST_HELPER
                    .getContacts(1 + size, notOrphansSize, STAFF);

            for (ContactPerson c : contactsInRelationship) {
                indexContact = new IndexContact();

                indexContact.setIndexContactId(
                        new IndexContactId(1L, c.getId()));
                indexContact.setContactPerson(c);
                contactPersonList.add(c);
            }

            for (ContactPerson c : contactPersonList) {
                when(contactPersonRepository.findById(c.getId()))
                        .thenReturn(Optional.of(c));
            }

            doNothing().when(contactPersonRepository).delete(any());

            contactPersonService.removeOrphans(contactPersonList.stream()
                    .map(ContactPerson::getId)
                    .collect(Collectors.toList()));

            verify(contactPersonRepository, times(size)).delete(any());
            verify(contactPersonRepository, times(
                    contactPersonList.size()))
                    .findById(anyLong());
        }
    }


    @Nested
    public class CountStaffContacts {
        @Test
        public void shouldCountAll() {
            when(contactPersonRepository.countByStatus(any())).thenReturn(1L);

            contactPersonService.countStaffContacts();

            verify(contactPersonRepository, times(1)).countByStatus(any());
        }
    }


    @Nested
    public class isVisitor {
        @Test
        public void shouldReturnBoolean() {
            when(contactPersonRepository
                    .existsByIdAndStatus(anyLong(), any()))
                    .thenReturn(true);

            contactPersonService.isVisitor(1L);

            verify(contactPersonRepository, times(1))
                    .existsByIdAndStatus(anyLong(), any());
        }
    }
}
