package de.hhu.covidtracer.services.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class InstantLocalDateTimeConverter implements Converter<Instant, String> {
    private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ISO_LOCAL_DATE_TIME;


    @Override
    public String convert(Instant source) {
        return LocalDateTime
                .ofInstant(source, ZoneId.systemDefault())
                .format(DATE_TIME_FORMATTER);
    }
}
