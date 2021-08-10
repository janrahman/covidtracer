package de.hhu.covidtracer.services.validation;

import de.hhu.covidtracer.dtos.wrappers.LocalDateSpanWrapper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LocalDateSpanWrapperFormatValidation implements ConstraintValidator<LocalDateSpanWrapperFormat, LocalDateSpanWrapper> {
    @Override
    public void initialize(LocalDateSpanWrapperFormat constraintAnnotation) {

    }

    @Override
    public boolean isValid(
            LocalDateSpanWrapper value,
            ConstraintValidatorContext context) {
        return value != null &&
                value.getStart() != null &&
                value.getEnd() != null &&
                value.getStart().isBefore(value.getEnd());
    }
}
