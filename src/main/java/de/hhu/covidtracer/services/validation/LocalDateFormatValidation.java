package de.hhu.covidtracer.services.validation;

import de.hhu.covidtracer.services.configs.DateFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class LocalDateFormatValidation implements ConstraintValidator<LocalDateFormat, String> {
    private static final List<String> SUPPORTED_FORMATS = DateFormat.getValues();
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = SUPPORTED_FORMATS
            .stream()
            .map(DateTimeFormatter::ofPattern)
            .collect(Collectors.toList());


    @Override
    public void initialize(LocalDateFormat constraint) {

    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return false;

        for (DateTimeFormatter dateTimeFormatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDate.parse(value, dateTimeFormatter);
                return true;
            } catch (DateTimeParseException ignored) {

            }
        }

        return false;
    }
}
