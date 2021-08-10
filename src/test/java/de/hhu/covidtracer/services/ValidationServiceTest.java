package de.hhu.covidtracer.services;

import de.hhu.covidtracer.models.IndexPerson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ValidationServiceTest {
    @Autowired
    private ValidationService validationService;


    @Test
    public void shouldReturnHashSetWhenNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validate(null));
    }


    @Test
    public void shouldReturnEmptyHashMapWhenNoValidations() {
        assertTrue(validationService.validate(new IndexPerson()).isEmpty());
    }
}
