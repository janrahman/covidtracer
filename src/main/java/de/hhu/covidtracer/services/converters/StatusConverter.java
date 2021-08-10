package de.hhu.covidtracer.services.converters;

import de.hhu.covidtracer.models.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StatusConverter implements Converter<String, Status> {

    @Override
    public Status convert(String source) {
        try {
            return Status.valueOf(source.toUpperCase());
        } catch(IllegalArgumentException e) {
            log.error(e.getMessage());
        }

        return null;
    }
}
