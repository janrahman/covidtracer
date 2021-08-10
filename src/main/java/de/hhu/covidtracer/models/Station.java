package de.hhu.covidtracer.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "station")
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotEmpty
    @Column(unique=true)
    private String name;
    private String department;
    private String hospitalName;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "station",
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.DETACH},
            orphanRemoval = true)
    List<ContactPerson> contacts = new ArrayList<>();

    public Station(String name, String department, String hospitalName) {
        this.name = name;
        this.department = department;
        this.hospitalName = hospitalName;
    }


    public void addContact(ContactPerson contactPerson) {
        if (this.contacts.contains(contactPerson)) return;
        this.contacts.add(contactPerson);
        contactPerson.setStation(this);
    }


    public void removeContact(ContactPerson contactPerson) {
        if (!this.contacts.contains(contactPerson)) return;
        this.contacts.remove(contactPerson);
        contactPerson.setStation(null);
    }
}
