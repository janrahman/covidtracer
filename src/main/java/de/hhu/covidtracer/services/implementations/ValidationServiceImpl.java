package de.hhu.covidtracer.services.implementations;

import de.hhu.covidtracer.dtos.wrappers.ValidationWrapper;
import de.hhu.covidtracer.services.ValidationService;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

@Service("ValidationService")
public class ValidationServiceImpl implements ValidationService {
    private final Validator validator;

    public ValidationServiceImpl(Validator validator) {
        this.validator = validator;
    }


    @Override
    public <T> List<ValidationWrapper<T>> validateList(
            List<T> list) {
        List<ValidationWrapper<T>> validationWrappers = new ArrayList<>();

        if (list != null && !list.isEmpty()) {
            validationWrappers = list.stream()
                    .map(this::createWrapper)
                    .collect(Collectors.toList());
        }

        return validationWrappers;
    }


    private <T> ValidationWrapper<T> createWrapper(T model) {
        return ValidationWrapper.<T>builder()
                .model(model)
                .errorRecords(validate(model))
                .build();
    }


    @Override
    public <T> Map<String, Integer> validate(T model) {
        Set<ConstraintViolation<T>> violations = validator
                .validate(model);
        Map<String, Integer> errorRecords = new HashMap<>();

        for (ConstraintViolation<T> constraintViolation : violations) {
            errorRecords.computeIfPresent(
                    constraintViolation.getPropertyPath().toString(),
                    (k, v) -> v + 1);
            errorRecords.putIfAbsent(
                    constraintViolation.getPropertyPath().toString(), 1);
        }

        return errorRecords;
    }
}
