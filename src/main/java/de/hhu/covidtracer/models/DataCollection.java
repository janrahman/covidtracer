package de.hhu.covidtracer.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "survey")
public class DataCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    Instant timestamp;
    long countIndexStatusPatientsInQuarantine;
    long countIndexStatusStaffInQuarantine;
    long countPositiveContactStaff;
    long countIndexPersonStaffPauseQuarantine;
    long countNewPatientsInQuarantine;
    long countNewStaffInQuarantine;
}
