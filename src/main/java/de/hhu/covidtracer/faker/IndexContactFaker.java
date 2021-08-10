package de.hhu.covidtracer.faker;

import com.github.javafaker.Faker;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.associations.IndexContact;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class IndexContactFaker {
    private final Faker faker;

    public IndexContactFaker(Faker faker) {
        this.faker = faker;
    }


    public IndexContact create(
            IndexPerson indexPerson,
            ContactPerson contactPerson) {
        IndexContact indexContact = new IndexContact();

        LocalDate contactDateEnd = LocalDate
                .now()
                .minusDays(faker
                        .number()
                        .numberBetween(0, 9));
        LocalDate contactDateStart = contactDateEnd
                .minusDays(faker
                        .number()
                        .numberBetween(3, 7));


        indexContact.setIndex(indexPerson);
        indexContact.setContactPerson(contactPerson);
        indexContact.setContactDateStart(contactDateStart);
        indexContact.setContactDateEnd(contactDateEnd);

        return indexContact;
    }


    public List<IndexContact> setRelationships(
            List<IndexPerson> indexPersonList,
            List<ContactPerson> contactPersonList,
            int amountContactsPerIndex) {
        List<IndexContact> indexContactList = new ArrayList<>();
        int count = 0;

        for (IndexPerson indexPerson : indexPersonList) {
            if (count >= contactPersonList.size()) count = 0;

            for (int i = 0; i < amountContactsPerIndex; i++) {
                indexContactList.add(create(
                        indexPerson,
                        contactPersonList.get(count++)));
            }
        }

        return indexContactList;
    }
}
