package de.hhu.covidtracer.services.converters;

import de.hhu.covidtracer.services.configs.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StringLocalDateConverter implements Converter<String, LocalDate> {
    private static final List<String> SUPPORTED_FORMATS = DateFormat
            .getValues();
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS =
            SUPPORTED_FORMATS
                    .stream()
                    .map(DateTimeFormatter::ofPattern)
                    .collect(Collectors.toList());


    @Override
    public LocalDate convert(String source) {
        if (!isValidFormat(source)) return null;

        for (DateTimeFormatter dateTimeFormatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDate.parse(source, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                log.info(e.getMessage());
            }
        }

        return null;
    }


    private boolean isValidFormat(String source) {
        return source != null && hasValidSize(source) && !hasLetter(source);
    }


    private boolean hasLetter(String source) {
        return source
                .chars()
                .anyMatch(Character::isLetter);
    }


    private boolean hasValidSize(String source) {
        return source.length() > 5 && source.length() < 11;
    }
}
