package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.DataCollectionDTO;
import de.hhu.covidtracer.models.DataCollection;

import java.time.LocalDate;
import java.util.List;

public interface DataCollectionService {
    List<DataCollectionDTO> getAllDTOs();

    DataCollection save(DataCollection dataCollection);

    DataCollection createDataCollection(int quarantineLapse);

    List<DataCollectionDTO> getDTOsBetweenDates(LocalDate start, LocalDate end);

    long countNewPatients(List<DataCollectionDTO> dataCollectionList);

    long countNewStaff(List<DataCollectionDTO> dataCollectionList);
}
