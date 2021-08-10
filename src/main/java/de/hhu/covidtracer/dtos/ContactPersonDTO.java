package de.hhu.covidtracer.dtos;

import de.hhu.covidtracer.models.Status;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ContactPersonDTO {
    private long id;
    @NotEmpty
    @Size(min = 2, max = 26)
    private String name;
    @NotEmpty
    @Size(min = 2, max = 26)
    private String firstName;
    @Valid
    private AddressDTO address;
    private String phone;
    private String email;
    private String occupationGroup;
    private String category;
    private Status status;

    private String latestReportDate;
    private String datePeriodStart;
    private String datePeriodEnd;

    @NotNull
    private StationDTO station;
}
