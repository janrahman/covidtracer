package de.hhu.covidtracer.services.converters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class LocalDateStringConverter implements Converter<LocalDate, String> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE;


    @Override
    public String convert(LocalDate source) {
        return source.format(DATE_TIME_FORMATTER);
    }
}
