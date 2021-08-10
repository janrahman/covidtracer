package de.hhu.covidtracer.models;

import de.hhu.covidtracer.models.associations.IndexContact;
import de.hhu.covidtracer.services.converters.StatusAttributeConverter;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "index")
public class IndexPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name, firstName;
    LocalDate birthday;

    @Embedded
    private Address address;

    @Convert(converter = StatusAttributeConverter.class)
    private Status status;

    private boolean reportSupervisor, reportHealthDepartment;
    @Builder.Default
    private boolean inQuarantine = true;
    @Builder.Default
    private Instant entryDateTime = Instant.now();

    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "indexPerson",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "index",
            cascade = {CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = true)
    private Set<IndexContact> indexContacts = new LinkedHashSet<>();

    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "indexPerson",
            cascade = CascadeType.ALL)
    @OrderBy("date DESC, time DESC")
    private List<Appointment> appointments = new ArrayList<>();

    private boolean visible;
    private String owner;


    public void addReport(Report report) {
        if (this.reports.contains(report)) return;
        if (report.getContactPerson() != null) return;

        this.reports.add(report);
        report.setIndexPerson(this);
    }


    public void removeReport(Report report) {
        if (!this.reports.contains(report)) return;

        this.reports.remove(report);
        report.setIndexPerson(null);
    }


    public void addIndexContact(IndexContact indexContact) {
        if (this.indexContacts.contains(indexContact)) return;

        this.indexContacts.add(indexContact);
        indexContact.setIndex(this);
    }


    public void removeIndexContact(IndexContact indexContact) {
        if (!this.indexContacts.contains(indexContact)) return;

        this.indexContacts.remove(indexContact);
        indexContact.setIndex(null);
    }


    public void addAppointment(Appointment appointment) {
        if (this.appointments.contains(appointment)) return;

        this.appointments.add(appointment);
        appointment.setIndexPerson(this);
    }


    public void removeAppointment(Appointment appointment) {
        if (!this.appointments.contains(appointment)) return;

        this.appointments.remove(appointment);
        appointment.setIndexPerson(null);
    }
}
