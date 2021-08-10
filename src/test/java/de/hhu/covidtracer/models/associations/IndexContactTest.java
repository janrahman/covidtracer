package de.hhu.covidtracer.models.associations;

import de.hhu.covidtracer.helper.ContactPersonTestHelper;
import de.hhu.covidtracer.helper.IndexPersonTestHelper;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.IndexPerson;
import org.junit.jupiter.api.Test;

import static de.hhu.covidtracer.models.Status.*;
import static org.junit.jupiter.api.Assertions.*;

public class IndexContactTest {
    private static final IndexPersonTestHelper INDEX_PERSON_TEST_HELPER =
            new IndexPersonTestHelper();
    private static final ContactPersonTestHelper CONTACT_PERSON_TEST_HELPER =
            new ContactPersonTestHelper();


    @Test
    public void emptyRelations() {
        IndexContact indexContact = new IndexContact();

        assertNull(indexContact.getIndex());
        assertNull(indexContact.getContactPerson());
    }


    @Test
    public void addIndex() {
        IndexContact association = new IndexContact();
        IndexPerson index = INDEX_PERSON_TEST_HELPER
                .getIndexPerson(1L, PATIENT);

        association.setIndex(index);

        assertEquals(index, association.getIndex());
        assertTrue(index.getIndexContacts().contains(association));
    }


    @Test
    public void addContact() {
        IndexContact association = new IndexContact();
        ContactPerson contact = CONTACT_PERSON_TEST_HELPER
                .getContact(1L, PATIENT);

        association.setContactPerson(contact);

        assertEquals(contact, association.getContactPerson());
        assertTrue(contact.getIndexContacts().contains(association));
    }


    @Test
    public void addIndexAndContact() {
        IndexContact association = new IndexContact();
        ContactPerson contactPerson = CONTACT_PERSON_TEST_HELPER
                .getContact(1L, PATIENT);
        IndexPerson indexPerson = INDEX_PERSON_TEST_HELPER
                .getIndexPerson(1L, PATIENT);

        association.setContactPerson(contactPerson);
        association.setIndex(indexPerson);

        assertEquals(contactPerson, association.getContactPerson());
        assertEquals(indexPerson, association.getIndex());
        assertTrue(contactPerson.getIndexContacts().contains(association));
        assertTrue(contactPerson.getIndexContacts().contains(association));
    }


    @Test
    public void replaceIndex() {
        IndexContact association = new IndexContact();
        IndexPerson oldIndex = INDEX_PERSON_TEST_HELPER
                .getIndexPerson(1L, PATIENT);
        IndexPerson newIndex = INDEX_PERSON_TEST_HELPER
                .getIndexPerson(2L, STAFF);

        association.setIndex(oldIndex);
        association.setIndex(newIndex);

        assertEquals(newIndex, association.getIndex());
        assertTrue(oldIndex.getIndexContacts().isEmpty());
        assertTrue(newIndex.getIndexContacts().contains(association));
    }


    @Test
    public void replaceContact() {
        IndexContact association = new IndexContact();
        ContactPerson oldContact = CONTACT_PERSON_TEST_HELPER
                .getContact(1L, PATIENT);
        ContactPerson newContact = CONTACT_PERSON_TEST_HELPER
                .getContact(2L, STAFF);

        association.setContactPerson(oldContact);
        association.setContactPerson(newContact);

        assertEquals(newContact, association.getContactPerson());
        assertTrue(oldContact.getIndexContacts().isEmpty());
        assertTrue(newContact.getIndexContacts().contains(association));
    }


    @Test
    public void replaceIndexAndContact() {
        IndexContact association = new IndexContact();

        IndexPerson oldIndex = INDEX_PERSON_TEST_HELPER
                .getIndexPerson(1L, PATIENT);
        IndexPerson newIndex = INDEX_PERSON_TEST_HELPER
                .getIndexPerson(2L, STAFF);

        ContactPerson oldContact = CONTACT_PERSON_TEST_HELPER
                .getContact(1L, PATIENT);
        ContactPerson newContact = CONTACT_PERSON_TEST_HELPER
                .getContact(2L, STAFF);

        association.setIndex(oldIndex);
        association.setIndex(newIndex);
        association.setContactPerson(oldContact);
        association.setContactPerson(newContact);

        assertEquals(newIndex, association.getIndex());
        assertTrue(oldIndex.getIndexContacts().isEmpty());
        assertTrue(newIndex.getIndexContacts().contains(association));
        assertEquals(newContact, association.getContactPerson());
        assertTrue(oldContact.getIndexContacts().isEmpty());
        assertTrue(newContact.getIndexContacts().contains(association));
    }


    @Test
    public void removeIndexAssociation() {
        IndexContact association = new IndexContact();
        IndexPerson oldIndex = INDEX_PERSON_TEST_HELPER
                .getIndexPerson(1L, PATIENT);

        association.setIndex(oldIndex);
        association.setIndex(null);

        assertNull(association.getIndex());
        assertTrue(oldIndex.getIndexContacts().isEmpty());
    }


    @Test
    public void removeContactAssociation() {
        IndexContact association = new IndexContact();
        ContactPerson oldContact = CONTACT_PERSON_TEST_HELPER
                .getContact(1L, PATIENT);

        association.setContactPerson(oldContact);
        association.setContactPerson(null);

        assertNull(association.getContactPerson());
        assertTrue(oldContact.getIndexContacts().isEmpty());
    }
}
