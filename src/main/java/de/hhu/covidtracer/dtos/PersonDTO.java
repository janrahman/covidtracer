package de.hhu.covidtracer.dtos;

import de.hhu.covidtracer.models.Address;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PersonDTO {
    String name;
    String firstName;
    Address address;
}
