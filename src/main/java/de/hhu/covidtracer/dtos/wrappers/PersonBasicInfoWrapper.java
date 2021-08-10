package de.hhu.covidtracer.dtos.wrappers;

import de.hhu.covidtracer.dtos.AddressDTO;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PersonBasicInfoWrapper {
    String name;
    String firstName;
    AddressDTO address;
}
