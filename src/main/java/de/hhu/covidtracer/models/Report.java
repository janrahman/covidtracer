package de.hhu.covidtracer.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Data
@Entity
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDate date;
    private String comment;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indexperson_id")
    private IndexPerson indexPerson;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staffmember_id")
    private ContactPerson contactPerson;


    public void setIndexPerson(IndexPerson newIndexPerson) {
        if (isCurrentIndex(newIndexPerson)) return;
        if (this.contactPerson != null) return;

        IndexPerson oldIndexPerson = this.indexPerson;
        this.indexPerson = newIndexPerson;

        if (oldIndexPerson != null) oldIndexPerson.removeReport(this);
        if (newIndexPerson != null) newIndexPerson.addReport(this);
    }


    public void setContactPerson(ContactPerson newStaff) {
        if (isCurrentContactPerson(newStaff)) return;
        if (this.indexPerson != null) return;

        ContactPerson oldStaff = this.contactPerson;
        this.contactPerson = newStaff;

        if (oldStaff != null) oldStaff.removeReport(this);
        if (newStaff != null) newStaff.addReport(this);
    }


    private boolean isCurrentIndex(IndexPerson newIndexPerson) {
        return Objects.equals(this.indexPerson, newIndexPerson);
    }


    private boolean isCurrentContactPerson(ContactPerson newStaff) {
        return Objects.equals(this.contactPerson, newStaff);
    }
}
