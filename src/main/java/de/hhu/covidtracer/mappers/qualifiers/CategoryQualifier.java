package de.hhu.covidtracer.mappers.qualifiers;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class CategoryQualifier {
    private final List<String> UNITS = Arrays
            .asList("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX");

    @Named("convertDigitToRoman")
    public String fromDTOString(String source) {
        if (source == null) return null;
        if (UNITS.contains(source)) return source;

        try {
            int number = Integer.parseInt(source);

            if (number > 0 && number < 10) return UNITS.get(number);
        } catch (NumberFormatException e) {
            log.info(e.getMessage());
        }

        return "0";
    }
}
