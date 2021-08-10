package de.hhu.covidtracer.dtos.wrappers;

import de.hhu.covidtracer.dtos.IndexPersonDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IndexPersonFormWrapper {
    private List<IndexPersonDTO> indexPersonDTOList;
}
