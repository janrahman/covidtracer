package de.hhu.covidtracer.dtos.wrappers;

import de.hhu.covidtracer.services.validation.LocalDateSpanWrapperFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@LocalDateSpanWrapperFormat
public class LocalDateSpanWrapper {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate start;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate end;
}
