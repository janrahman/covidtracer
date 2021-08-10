package de.hhu.covidtracer.services.converters;

import de.hhu.covidtracer.models.Status;

import javax.persistence.AttributeConverter;

public class StatusAttributeConverter implements AttributeConverter<Status, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Status attribute) {
        return attribute.getId();
    }


    @Override
    public Status convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;

        Status status = Status.getStatus(dbData);

        if (status == null) throw new IllegalArgumentException("Invalid status id");

        return status;
    }
}
