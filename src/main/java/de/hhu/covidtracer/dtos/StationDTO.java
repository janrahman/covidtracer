package de.hhu.covidtracer.dtos;

import de.hhu.covidtracer.services.validation.UniqueStationName;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@UniqueStationName
public class StationDTO {
    private long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String department;
    @NotEmpty
    private String hospitalName;
}
