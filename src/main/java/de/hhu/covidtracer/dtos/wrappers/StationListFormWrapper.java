package de.hhu.covidtracer.dtos.wrappers;

import de.hhu.covidtracer.dtos.StationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class StationListFormWrapper {
    List<StationDTO> stationDTOList;
}
