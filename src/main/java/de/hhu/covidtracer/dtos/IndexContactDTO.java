package de.hhu.covidtracer.dtos;

import de.hhu.covidtracer.services.validation.LocalDateFormat;
import lombok.Value;

@Value
public class IndexContactDTO {
    IndexPersonDTO index;
    ContactPersonDTO contactPerson;
    @LocalDateFormat
    String contactDateStart;
    @LocalDateFormat
    String contactDateEnd;
}
