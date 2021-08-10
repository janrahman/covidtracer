package de.hhu.covidtracer.dtos.wrappers;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class ValidationWrapper<T> {
    T model;
    Map<String, Integer> errorRecords;
}
