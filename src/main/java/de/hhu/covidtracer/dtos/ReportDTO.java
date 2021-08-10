package de.hhu.covidtracer.dtos;

import de.hhu.covidtracer.services.validation.LocalDateFormat;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ReportDTO {
    private long id;
    @NotEmpty
    @LocalDateFormat
    private String date;
    private String comment;
}
