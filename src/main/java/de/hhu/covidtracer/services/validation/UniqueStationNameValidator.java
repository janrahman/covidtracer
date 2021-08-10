package de.hhu.covidtracer.services.validation;

import de.hhu.covidtracer.dtos.StationDTO;
import de.hhu.covidtracer.models.Station;
import de.hhu.covidtracer.repositories.StationRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class UniqueStationNameValidator implements ConstraintValidator<UniqueStationName, StationDTO> {
    private final StationRepository stationRepository;

    public UniqueStationNameValidator(
            StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }


    @Override
    public void initialize(
            UniqueStationName constraintAnnotation) {

    }


    @Override
    public boolean isValid(
            StationDTO stationDTO,
            ConstraintValidatorContext context) {
        Optional<Station> stationOptional = stationRepository
                .findByName(stationDTO.getName());

        return !stationOptional
                .filter(station -> station.getId() != stationDTO.getId())
                .isPresent();
    }
}
