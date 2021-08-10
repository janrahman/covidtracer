package de.hhu.covidtracer.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String date;
    @DateTimeFormat(pattern = "HH:mm")
    private String time;
    private boolean participated = false;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indexperson_id")
    private IndexPerson indexPerson;


    public void setIndexPerson(IndexPerson newIndexPerson) {
        if (isCurrentIndexPatient(newIndexPerson)) return;

        IndexPerson oldIndexPerson = this.indexPerson;
        this.indexPerson = newIndexPerson;

        if (oldIndexPerson != null) oldIndexPerson.removeAppointment(this);
        if (newIndexPerson != null) newIndexPerson.addAppointment(this);
    }


    private boolean isCurrentIndexPatient(IndexPerson newIndexPerson) {
        return Objects.equals(this.indexPerson, newIndexPerson);
    }
}
