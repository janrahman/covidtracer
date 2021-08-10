package de.hhu.covidtracer.dtos;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class DataCollectionDateFilterDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate start;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate end;
}
