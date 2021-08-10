package de.hhu.covidtracer.helper;

import com.github.javafaker.Faker;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.associations.IndexContact;
import de.hhu.covidtracer.models.associations.IndexContactId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static de.hhu.covidtracer.models.Status.STAFF;

public class IndexContactTestHelper {
    private static final IndexPersonTestHelper INDEX_PERSON_TEST_HELPER =
            new IndexPersonTestHelper();
    private static final ContactPersonTestHelper CONTACT_PERSON_TEST_HELPER =
            new ContactPersonTestHelper();
    private static final Faker FAKER = new Faker(Locale.GERMAN);


    public IndexContact create(long indexId, long contactId) {
        IndexContact indexContact = new IndexContact();
        IndexPerson indexPerson = INDEX_PERSON_TEST_HELPER
                .getIndexPerson(indexId, STAFF);
        ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                .getContact(contactId, STAFF);

        indexContact.setIndexContactId(new IndexContactId(indexId, contactId));
        indexContact.setContactPerson(contactPerson);
        indexContact.setIndex(indexPerson);
        indexContact.setContactDateStart(
                LocalDate.of(
                        2020,
                        FAKER.number().numberBetween(1, 13),
                        23));
        indexContact.setContactDateEnd(indexContact
                .getContactDateStart().plusDays(7L));

        return indexContact;
    }


    public IndexContact createWithContactDates(
            long indexId,
            long contactId,
            LocalDate contactDateStart,
            LocalDate contactDateEnd) {
        IndexContact indexContact = new IndexContact();
        IndexPerson indexPerson = INDEX_PERSON_TEST_HELPER
                .getIndexPerson(indexId, STAFF);
        ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                .getContact(contactId, STAFF);

        indexContact.setIndexContactId(new IndexContactId(indexId, contactId));
        indexContact.setContactPerson(contactPerson);
        indexContact.setIndex(indexPerson);
        indexContact.setContactDateStart(contactDateStart);
        indexContact.setContactDateEnd(contactDateEnd);

        return indexContact;
    }


    public List<IndexContact> createList(
            long indexStartId,
            long contactStartId,
            int size) {
        List<IndexContact> indexContactList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            indexContactList.add(create(indexStartId++, contactStartId++));
        }

        return indexContactList;
    }
}
