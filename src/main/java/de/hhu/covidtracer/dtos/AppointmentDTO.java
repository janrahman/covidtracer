package de.hhu.covidtracer.dtos;

import de.hhu.covidtracer.services.validation.LocalDateFormat;
import lombok.Data;

@Data
public class AppointmentDTO {
    long id;
    @LocalDateFormat
    private String date;
    @LocalDateFormat
    private String time;
    private boolean participated;
}
