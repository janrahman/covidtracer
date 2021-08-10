package de.hhu.covidtracer.services.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LocalDateSpanWrapperFormatValidation.class)
public @interface LocalDateSpanWrapperFormat {
    String message() default "{de.hhu.covidtracer.LocalDateSpanWrapperFormat.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
