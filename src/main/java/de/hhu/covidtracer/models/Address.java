package de.hhu.covidtracer.models;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class Address {
    private String street, postcode, city;
}
