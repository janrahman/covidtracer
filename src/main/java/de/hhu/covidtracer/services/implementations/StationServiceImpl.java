package de.hhu.covidtracer.services.implementations;

import de.hhu.covidtracer.dtos.StationDTO;
import de.hhu.covidtracer.exceptions.StationException;
import de.hhu.covidtracer.exceptions.StationNotFoundException;
import de.hhu.covidtracer.mappers.StationMapper;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.Station;
import de.hhu.covidtracer.repositories.StationRepository;
import de.hhu.covidtracer.services.ContactPersonService;
import de.hhu.covidtracer.services.StationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("StationService")
public class StationServiceImpl implements StationService {
    private final StationRepository stationRepository;
    private final ContactPersonService contactPersonService;
    private final StationMapper stationMapper;

    public StationServiceImpl(
            StationRepository stationRepository,
            ContactPersonService contactPersonService,
            StationMapper stationMapper) {
        this.stationRepository = stationRepository;
        this.contactPersonService = contactPersonService;
        this.stationMapper = stationMapper;
    }


    @Override
    public List<Station> getAll() {
        return stationRepository.findAll();
    }


    @Override
    public List<StationDTO> getAllDTOs() {
        return stationMapper.toStationDTOs(getAll());
    }


    @Override
    public Station getById(long id) {
        return stationRepository
                .findById(id)
                .orElseThrow(
                        () -> new StationNotFoundException(
                                "Station not found."));
    }


    @Override
    public StationDTO getDTOById(long id) {
        return stationMapper.toStationDTO(getById(id));
    }


    @Override
    public Station update(StationDTO stationForm) {
        Station updateStation = getById(stationForm.getId());

        stationMapper.updateModel(stationForm, updateStation);

        return save(updateStation);
    }


    @Override
    public Station save(Station station) {
        station.setName(formatStationName(station.getName()));
        return stationRepository.save(station);
    }


    @Override
    public List<Station> saveAll(List<Station> stationList) {
        return (List<Station>) stationRepository.saveAll(stationList);
    }


    @Override
    public void remove(Station station) {
        if (station == null) {
            throw new StationException("Station is null.");
        }

        List<ContactPerson> contactPersonList = new ArrayList<>(
                station.getContacts());

        contactPersonList.forEach(c -> {
            station.removeContact(c);
            contactPersonService.save(c);
        });

        stationRepository.delete(station);
    }


    @Override
    public Station getStationFromFile(StationDTO stationDto) {
        if (stationDto == null ||
                stationDto.getName() == null ||
                stationDto.getName().trim().isEmpty()) {
            return null;
        }

        stationDto.setName(formatStationName(stationDto.getName()));

        Station station = stationRepository
                .findByName(stationDto.getName())
                .orElse(stationMapper.toStation(stationDto));

        if (station.getId() == 0L) return save(station);

        return station;
    }


    @Override
    public void updateStationFromDTOAndContactId(
            StationDTO newStation,
            long contactId) {
        if (newStation == null) return;

        ContactPerson contactPerson = contactPersonService.getById(contactId);
        Station entityStation = getById(newStation.getId());
        Station oldStation = stationRepository
                .findByContacts_Id(contactId)
                .orElse(new Station());

        if (entityStation == oldStation) return;
        if (oldStation.getId() > 0L) {
            oldStation.removeContact(contactPerson);
            save(oldStation);
        }

        entityStation.addContact(contactPerson);
        save(entityStation);
    }


    @Override
    public void removeOldStationFromContact(
            Station station,
            ContactPerson contactPerson) {
        if (station == null ||
                contactPerson == null ||
                contactPerson.getStation() == null)  {
            return;
        }

        long contactStationId = contactPerson.getStation().getId();

        if (station.getId() != contactStationId) {
            Station oldStation = getById(contactStationId);

            contactPerson.setStation(null);
            save(oldStation);
        }
    }


    @Override
    public Station saveNewEntityFromDTO(StationDTO stationForm) {
        if (stationForm == null) {
            throw new StationException("The given station is null.");
        }

        if (stationRepository.existsByName(stationForm.getName())) {
            throw new StationException("Station already exists.");
        }

        Station station = stationMapper.toStation(stationForm);

        return save(station);

    }


    @Override
    public String formatStationName(String name) {
        if (name == null || name.isEmpty()) return "";
        return name
                .replaceAll("\\s+", "")
                .toUpperCase();
    }


}
