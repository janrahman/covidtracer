package de.hhu.covidtracer.dtos;

import de.hhu.covidtracer.models.Status;
import de.hhu.covidtracer.services.validation.LocalDateFormat;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class IndexPersonDTO {
    private long id;
    @NotEmpty
    @Size(min = 2, max = 26)
    private String name;
    @NotEmpty
    @Size(min = 2, max = 26)
    private String firstName;
    @LocalDateFormat
    private String birthday;
    @Valid
    private AddressDTO address;
    private Status status;
    private boolean reportSupervisor;
    private boolean reportHealthDepartment;
    private boolean inQuarantine;
    private boolean visible;
    private String owner;

    private String entryDateTime;

}
