package de.hhu.covidtracer.models;

import de.hhu.covidtracer.models.associations.IndexContact;
import de.hhu.covidtracer.services.converters.StatusAttributeConverter;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contact")
public class ContactPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String firstName;

    @Embedded
    private Address address;

    private String phone;
    private String email;
    private String occupationGroup;
    private String category;

    @Convert(converter = StatusAttributeConverter.class)
    private Status status;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", referencedColumnName = "id")
    private Station station;

    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "contactPerson",
            cascade = {CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = true)
    private Set<IndexContact> indexContacts = new LinkedHashSet<>();

    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "contactPerson",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();


    public void setStation(Station newStation) {
        if (isOldStation(newStation)) return;

        Station oldStation = this.station;
        this.station = newStation;

        if (oldStation != null) oldStation.removeContact(this);
        if (newStation != null) newStation.addContact(this);
    }


    public void addIndexContact(IndexContact indexContact) {
        if (this.indexContacts.contains(indexContact)) return;

        this.indexContacts.add(indexContact);
        indexContact.setContactPerson(this);
    }


    public void removeIndexContact(IndexContact indexContact) {
        if (!this.indexContacts.contains(indexContact)) return;

        this.indexContacts.remove(indexContact);
        indexContact.setContactPerson(null);
    }


    public void addReport(Report newReport) {
        if (this.reports.contains(newReport)) return;
        if (newReport.getIndexPerson() != null) return;

        this.reports.add(newReport);
        newReport.setContactPerson(this);
    }


    public void removeReport(Report report) {
        if (!this.reports.contains(report)) return;

        this.reports.remove(report);
        report.setContactPerson(null);
    }


    private boolean isOldStation(Station newStation) {
        return Objects.equals(this.station, newStation);
    }
}
