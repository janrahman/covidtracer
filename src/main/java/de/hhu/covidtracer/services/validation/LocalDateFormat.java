package de.hhu.covidtracer.services.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LocalDateFormatValidation.class)
public @interface LocalDateFormat {
    String message() default "{de.hhu.covidtracer.LocalDateFormat.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
