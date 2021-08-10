package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.StationDTO;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.Station;

import java.util.List;

public interface StationService {
    List<Station> getAll();

    List<StationDTO> getAllDTOs();

    Station getById(long id);

    StationDTO getDTOById(long id);

    Station update(StationDTO stationForm);

    Station save(Station station);

    List<Station> saveAll(List<Station> stationList);

    void remove(Station station);

    Station getStationFromFile(StationDTO station);

    void updateStationFromDTOAndContactId(StationDTO station, long contactId);

    void removeOldStationFromContact(
            Station station,
            ContactPerson contactPerson);

    Station saveNewEntityFromDTO(StationDTO stationForm);

    String formatStationName(String name);

}
