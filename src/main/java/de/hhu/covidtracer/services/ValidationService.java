package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.wrappers.ValidationWrapper;

import java.util.List;
import java.util.Map;

public interface ValidationService {
    <T> Map<String, Integer> validate(T model);

    <T> List<ValidationWrapper<T>> validateList(
            List<T> list);
}
