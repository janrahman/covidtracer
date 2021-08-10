package de.hhu.covidtracer.dtos;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class AddressDTO {
    @NotEmpty
    private String street;
    @NotEmpty
    @Pattern(regexp = "\\d{5}")
    private String postcode;
    @NotEmpty
    private String city;
}
