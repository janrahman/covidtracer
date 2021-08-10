package de.hhu.covidtracer.models.associations;

import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.IndexPerson;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Data
@Entity
public class IndexContact {
    @EmbeddedId
    private IndexContactId indexContactId = new IndexContactId();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("indexId")
    private IndexPerson index;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("contactId")
    private ContactPerson contactPerson;

    private LocalDate contactDateStart;
    private LocalDate contactDateEnd;


    public void setIndex(IndexPerson newIndex) {
        if (isOldIndex(newIndex)) return;

        IndexPerson oldIndex = this.index;
        this.index = newIndex;

        if (oldIndex != null) oldIndex.removeIndexContact(this);
        if (newIndex != null) newIndex.addIndexContact(this);
    }


    public void setContactPerson(ContactPerson newContact) {
        if (isOldContact(newContact)) return;

        ContactPerson oldContact = this.contactPerson;
        this.contactPerson = newContact;

        if (oldContact != null) oldContact.removeIndexContact(this);
        if (newContact != null) newContact.addIndexContact(this);
    }


    private boolean isOldIndex(IndexPerson newIndex) {
        return Objects.equals(this.index, newIndex);
    }


    private boolean isOldContact(ContactPerson newContact) {
        return Objects.equals(this.contactPerson, newContact);
    }
}
