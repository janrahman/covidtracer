package de.hhu.covidtracer.mappers.qualifiers;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;

@Component
@Slf4j
public class LocalTimeQualifier {
    private final ConversionService conversionService;

    public LocalTimeQualifier(ConversionService conversionService) {
        this.conversionService = conversionService;
    }


    @Named("convertToInstant")
    public String fromInstant(Instant instant) {
        return conversionService.convert(instant, String.class);
    }


    @Named("convertLocalDateToString")
    public String fromLocalDate(LocalDate date) {
        return conversionService.convert(date, String.class);
    }


    @Named("convertStringToLocalDate")
    public LocalDate fromString(String date) {
        return conversionService.convert(date, LocalDate.class);
    }
}
