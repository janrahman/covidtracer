package de.hhu.covidtracer.services;

import de.hhu.covidtracer.exceptions.IndexContactNotFoundException;
import de.hhu.covidtracer.helper.IndexContactTestHelper;
import de.hhu.covidtracer.mappers.IndexContactMapper;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.associations.IndexContact;
import de.hhu.covidtracer.models.associations.IndexContactId;
import de.hhu.covidtracer.repositories.IndexContactRepository;
import de.hhu.covidtracer.services.implementations.IndexContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IndexContactServiceTest {
    private static final IndexContactTestHelper INDEX_CONTACT_TEST_HELPER =
            new IndexContactTestHelper();

    private DateTimeFormatter dateTimeFormatter;

    @Mock
    private IndexContactRepository indexContactRepository;

    @Mock
    private IndexContactMapper indexContactMapper;

    @Mock
    private ConversionService conversionService;

    private IndexContactService indexContactService;


    @BeforeEach
    public void setup() {
        indexContactService = new IndexContactServiceImpl(
                indexContactRepository,
                indexContactMapper,
                conversionService);
    }


    @Nested
    public class Save {
        @Test
        public void shouldSaveIndexContact() {
            IndexContact indexContact = new IndexContact();

            when(indexContactRepository.save(any())).thenReturn(indexContact);

            indexContactService.save(indexContact);

            verify(indexContactRepository, times(1))
                    .save(any());
        }


        @Test
        public void shouldSaveList() {
            List<IndexContact> indexContactList = new ArrayList<>();

            when(indexContactRepository.saveAll(any()))
                    .thenReturn(indexContactList);

            indexContactService.saveAll(indexContactList);

            verify(indexContactRepository, times(1))
                    .saveAll(any());
        }
    }


    @Nested
    public class Update {
        @BeforeEach
        public void setupDateTimeFormatter() {
            dateTimeFormatter = DateTimeFormatter.ISO_DATE;
        }


        @Test
        public void shouldThrowErrorWhenIndexContactNotFound() {
            assertThrows(IndexContactNotFoundException.class,
                    () -> indexContactService.update(
                            1L,
                            1L,
                            null,
                            null));
        }


        @Test
        public void shouldNotUpdateWhenBothDatesAreNull() {
            IndexContact result;
            IndexContact indexContact = INDEX_CONTACT_TEST_HELPER
                    .create(1L, 1L);

            when(indexContactRepository.findById(any()))
                    .thenReturn(Optional.of(indexContact));
            when(conversionService.convert(any(), eq(LocalDate.class)))
                    .thenReturn(null);
            when(indexContactRepository.save(indexContact))
                    .thenReturn(indexContact);

            result = indexContactService.update(
                    1L,
                    1L,
                    null,
                    null
            );

            assertNotNull(result.getContactDateStart());
            assertNotNull(result.getContactDateEnd());
            verify(conversionService, times(2))
                    .convert(any(), eq(LocalDate.class));
        }


        @Test
        public void shouldOnlyUpdateContactDateStart() {
            IndexContact result;

            LocalDate updateContactDateStart = LocalDate
                    .of(2020, 12, 3);
            LocalDate existingContactDateStart = LocalDate
                    .of(2021, 1, 1);
            String updateContactDateToString = updateContactDateStart
                    .format(dateTimeFormatter);

            IndexContact indexContact = INDEX_CONTACT_TEST_HELPER
                    .createWithContactDates(
                            1L,
                            1L,
                            existingContactDateStart,
                            existingContactDateStart.plusDays(14));

            when(indexContactRepository.findById(any()))
                    .thenReturn(Optional.of(indexContact));
            when(conversionService
                    .convert(eq(updateContactDateToString), eq(LocalDate.class)))
                    .thenReturn(updateContactDateStart);
            when(indexContactRepository.save(indexContact))
                    .thenReturn(indexContact);

            result = indexContactService.update(
                    1L,
                    1L,
                    updateContactDateToString,
                    null
            );

            assertEquals(
                    indexContact.getIndexContactId(),
                    result.getIndexContactId());
            assertEquals(
                    updateContactDateStart,
                    result.getContactDateStart());
            assertEquals(
                    indexContact.getContactDateEnd(),
                    result.getContactDateEnd());
            verify(conversionService, times(2))
                    .convert(any(), eq(LocalDate.class));
        }


        @Test
        public void shouldOnlyUpdateContactDateEnd() {
            IndexContact result;

            LocalDate updateContactDateEnd = LocalDate
                    .of(2020, 12, 3);
            LocalDate existingContactDateEnd = LocalDate
                    .of(2021, 1, 1);
            String updateContactDateToString = updateContactDateEnd
                    .format(dateTimeFormatter);

            IndexContact indexContact = INDEX_CONTACT_TEST_HELPER
                    .createWithContactDates(
                            1L,
                            1L,
                            existingContactDateEnd.minusDays(14),
                            existingContactDateEnd);

            when(indexContactRepository.findById(any()))
                    .thenReturn(Optional.of(indexContact));
            when(conversionService
                    .convert(eq(null), eq(LocalDate.class)))
                    .thenReturn(null);
            when(conversionService
                    .convert(
                            eq(updateContactDateToString),
                            eq(LocalDate.class)))
                    .thenReturn(updateContactDateEnd);
            when(indexContactRepository.save(indexContact))
                    .thenReturn(indexContact);

            result = indexContactService.update(
                    1L,
                    1L,
                    null,
                    updateContactDateToString);

            assertEquals(
                    indexContact.getIndexContactId(),
                    result.getIndexContactId());
            assertEquals(
                    updateContactDateEnd,
                    result.getContactDateEnd());
            assertEquals(
                    indexContact.getContactDateStart(),
                    result.getContactDateStart());
            verify(conversionService, times(2))
                    .convert(any(), eq(LocalDate.class));
        }


        @Test
        public void shouldUpdateAllDates() {
            IndexContact result;

            LocalDate updateContactDateStart = LocalDate
                    .of(2020, 12, 3);
            LocalDate updateContactDateEnd = updateContactDateStart
                    .plusDays(14L);
            LocalDate existingContactDateStart = updateContactDateStart
                    .plusDays(30L);
            LocalDate existingContactDateEnd = existingContactDateStart
                    .plusDays(14L);

            String updateContactDateStartToString = updateContactDateStart
                    .format(dateTimeFormatter);
            String updateContactDateEndToString = updateContactDateEnd
                    .format(dateTimeFormatter);

            IndexContact indexContact = INDEX_CONTACT_TEST_HELPER
                    .createWithContactDates(
                            1L,
                            1L,
                            existingContactDateStart,
                            existingContactDateEnd);

            when(indexContactRepository.findById(any()))
                    .thenReturn(Optional.of(indexContact));
            when(conversionService
                    .convert(
                            eq(updateContactDateStartToString),
                            eq(LocalDate.class)))
                    .thenReturn(updateContactDateStart);
            when(conversionService
                    .convert(
                            eq(updateContactDateEndToString),
                            eq(LocalDate.class)))
                    .thenReturn(updateContactDateEnd);
            when(indexContactRepository.save(indexContact))
                    .thenReturn(indexContact);

            result = indexContactService.update(
                    1L,
                    1L,
                    updateContactDateStartToString,
                    updateContactDateEndToString);

            assertEquals(
                    indexContact.getIndexContactId(),
                    result.getIndexContactId());
            assertEquals(
                    updateContactDateEnd,
                    result.getContactDateEnd());
            assertEquals(
                    updateContactDateStart,
                    result.getContactDateStart());
            assertNotEquals(
                    existingContactDateStart,
                    result.getContactDateStart());
            assertNotEquals(
                    existingContactDateEnd,
                    result.getContactDateEnd());
        }
    }


    @Nested
    public class Remove {
        @Test
        public void shouldThrowErrorWhenIndexContactNotFound() {
            assertThrows(IndexContactNotFoundException.class,
                    () -> indexContactService.remove(
                            1L,
                            1L));
        }


        @Test
        public void shouldRemoveIndexContact() {
            IndexContact indexContact = INDEX_CONTACT_TEST_HELPER
                    .create(1L, 1L);

            when(indexContactRepository.findById(any()))
                    .thenReturn(Optional.of(indexContact));
            doNothing().when(indexContactRepository).delete(any());

            indexContactService.remove(1L, 1L);

            assertNull(indexContact.getIndex());
            assertNull(indexContact.getContactPerson());
            verify(indexContactRepository, times(1))
                    .delete(any());
        }
    }


    @Nested
    public class GetIndexIdsFromContactId {
        @Test
        public void shouldReturnEmtpyListWhenIndexContactsNotFound() {
            List<Long> result;

            when(indexContactRepository
                    .findAllByIndexContactId_ContactId(anyLong()))
                    .thenReturn(new ArrayList<>());

            result = indexContactService.getIndexIdsFromContactId(0L);

            assertTrue(result.isEmpty());
            verify(indexContactRepository, times(1))
                    .findAllByIndexContactId_ContactId(anyLong());
        }


        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100})
        public void shouldReturnListWhenIndexContactsAreFound(int size) {
            List<Long> result;
            List<IndexContact> indexContactList = INDEX_CONTACT_TEST_HELPER
                    .createList(1L, 1L, size);
            when(indexContactRepository
                    .findAllByIndexContactId_ContactId(anyLong()))
                    .thenReturn(indexContactList);

            result = indexContactService.getIndexIdsFromContactId(0L);

            assertEquals(size, result.size());
            indexContactList.stream()
                    .map(ic -> ic.getIndexContactId().getIndexId())
                    .forEach(id -> assertTrue(result.contains(id)));

            verify(indexContactRepository, times(1))
                    .findAllByIndexContactId_ContactId(anyLong());
        }
    }


    @Nested
    public class GetDTOs {
        @Test
        public void shouldReturnListOrEmptyWhenIndexIdGiven() {
            when(indexContactRepository
                    .findAllByIndexContactId_IndexId(anyLong()))
                    .thenReturn(new ArrayList<>());
            when(indexContactMapper.toIndexContactDTOs(any()))
                    .thenReturn(new ArrayList<>());

            indexContactService.getDTOsFromIndexId(anyLong());

            verify(indexContactRepository, times(1))
                    .findAllByIndexContactId_IndexId(anyLong());
            verify(indexContactMapper, times(1))
                    .toIndexContactDTOs(any());
        }


        @Test
        public void shouldReturnListOrEmptyWhenContactIdGiven() {
            when(indexContactRepository
                    .findAllByIndexContactId_ContactId(anyLong()))
                    .thenReturn(new ArrayList<>());
            when(indexContactMapper.toIndexContactDTOs(any()))
                    .thenReturn(new ArrayList<>());

            indexContactService.getDTOsFromContactPersonId(anyLong());

            verify(indexContactRepository, times(1))
                    .findAllByIndexContactId_ContactId(anyLong());
            verify(indexContactMapper, times(1))
                    .toIndexContactDTOs(any());
        }
    }


    @Nested
    public class GetEntities {
        @Test
        public void shouldReturnListWhenIndexIdGiven() {
            when(indexContactRepository.findAllByIndexContactId_IndexId(anyLong()))
                    .thenReturn(new ArrayList<>());

            indexContactService.getByIndexId(1L);

            verify(indexContactRepository, times(1))
                    .findAllByIndexContactId_IndexId(anyLong());
        }


        @Test
        public void shouldThrowErrorWhenIndexContactIdNotFound() {
            assertThrows(
                    IndexContactNotFoundException.class,
                    () -> indexContactService
                            .getByIndexContactId(1L, 1L));
        }


        @Test
        public void shouldReturnIndexContactByIndexContactId() {
            IndexContact result;
            IndexContact indexContact = INDEX_CONTACT_TEST_HELPER
                    .create(1L, 1L);

            when(indexContactRepository
                    .findById(new IndexContactId(1L, 1L)))
                    .thenReturn(Optional.of(indexContact));

            result = indexContactService
                    .getByIndexContactId(1L, 1L);

            assertEquals(indexContact, result);
            verify(indexContactRepository, times(1))
                    .findById(any());
        }
    }


    @Nested
    public class GetFromFile {
        @BeforeEach
        public void setUpDatetimeFormatter() {
            dateTimeFormatter = DateTimeFormatter.ISO_DATE;
        }


        @Test
        public void shouldThrowErrorWhenAllNull() {
            assertThrows(
                    IndexContactNotFoundException.class,
                    () -> indexContactService.getFromFile(
                            null,
                            null,
                            null,
                            null));
        }


        @Test
        public void shouldThrowErrorWhenContactIsNull() {
            assertThrows(
                    IndexContactNotFoundException.class,
                    () -> indexContactService.getFromFile(
                            new IndexPerson(),
                            null,
                            null,
                            null));
        }


        @Test
        public void shouldThrowErrorWhenIndexIsNull() {
            assertThrows(
                    IndexContactNotFoundException.class,
                    () -> indexContactService.getFromFile(
                            null,
                            new ContactPerson(),
                            null,
                            null));
        }


        @Test
        public void shouldReturnNewIndexContactWhenNotInDatabase() {
            IndexContact result;


            LocalDate contactDateStart = LocalDate.of(2020, 12, 1);
            LocalDate contactDateEnd = LocalDate.of(2020, 12, 14);
            String contactDateStartToString = contactDateStart
                    .format(dateTimeFormatter);
            String contactDateEndToString = contactDateEnd
                    .format(dateTimeFormatter);

            IndexContact indexContact = INDEX_CONTACT_TEST_HELPER
                    .createWithContactDates(
                            1L,
                            1L,
                            contactDateStart,
                            contactDateEnd);

            when(indexContactRepository
                    .findById(any()))
                    .thenReturn(Optional.empty());
            when(conversionService
                    .convert(eq(contactDateStartToString), eq(LocalDate.class)))
                    .thenReturn(contactDateStart);
            when(conversionService
                    .convert(eq(contactDateEndToString), eq(LocalDate.class)))
                    .thenReturn(contactDateEnd);

            result = indexContactService.getFromFile(
                    indexContact.getIndex(),
                    indexContact.getContactPerson(),
                    contactDateStartToString,
                    contactDateEndToString);

            assertEquals(
                    indexContact.getIndex(),
                    result.getIndex());
            assertEquals(
                    indexContact.getContactPerson(),
                    result.getContactPerson());
            assertEquals(contactDateStart, result.getContactDateStart());
            assertEquals(contactDateEnd, result.getContactDateEnd());
            assertEquals(
                    0L,
                    result.getIndexContactId().getIndexId());
            assertEquals(
                    0L,
                    result.getIndexContactId().getContactId());
        }


        @Test
        public void shouldReturnIndexContactFromDatabase() {
            IndexContact result;

            LocalDate contactDateStart = LocalDate.of(2020, 12, 1);
            LocalDate contactDateEnd = LocalDate.of(2020, 12, 14);
            String contactDateStartToString = contactDateStart
                    .format(dateTimeFormatter);
            String contactDateEndToString = contactDateEnd
                    .format(dateTimeFormatter);

            IndexContact indexContact = INDEX_CONTACT_TEST_HELPER
                    .createWithContactDates(
                            1L,
                            1L,
                            contactDateStart.plusDays(10L),
                            contactDateEnd.plusDays(10L));

            when(indexContactRepository
                    .findById(any()))
                    .thenReturn(Optional.of(indexContact));

            result = indexContactService.getFromFile(
                    indexContact.getIndex(),
                    indexContact.getContactPerson(),
                    contactDateStartToString,
                    contactDateEndToString);

            assertEquals(
                    indexContact.getIndex(),
                    result.getIndex());
            assertEquals(
                    indexContact.getContactPerson(),
                    result.getContactPerson());
            assertNotEquals(
                    contactDateStart,
                    result.getContactDateStart());
            assertNotEquals(
                    contactDateEnd,
                    result.getContactDateEnd());
        }
    }
}
