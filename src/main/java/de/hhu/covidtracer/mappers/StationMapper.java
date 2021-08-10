package de.hhu.covidtracer.mappers;

import de.hhu.covidtracer.dtos.StationDTO;
import de.hhu.covidtracer.models.Station;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ContactPersonMapper.class})
public interface StationMapper {
    StationDTO toStationDTO(Station station);

    Station toStation(StationDTO stationDTO);

    List<StationDTO> toStationDTOs(List<Station> stationList);

    @Mapping(target = "id", ignore = true)
    void updateModel(StationDTO stationDTO, @MappingTarget Station station);
}
