package de.hhu.covidtracer.services.implementations;

import de.hhu.covidtracer.dtos.DataCollectionDTO;
import de.hhu.covidtracer.exceptions.DataCollectionException;
import de.hhu.covidtracer.mappers.DataCollectionMapper;
import de.hhu.covidtracer.models.DataCollection;
import de.hhu.covidtracer.models.Status;
import de.hhu.covidtracer.repositories.DataCollectionRepository;
import de.hhu.covidtracer.services.ContactPersonService;
import de.hhu.covidtracer.services.IndexPersonService;
import de.hhu.covidtracer.services.DataCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service("DataCollectionService")
@Slf4j
public class DataCollectionServiceImpl implements DataCollectionService {
    private final DataCollectionRepository dataCollectionRepository;
    private final IndexPersonService indexPersonService;
    private final ContactPersonService contactPersonService;
    private final DataCollectionMapper dataCollectionMapper;

    public DataCollectionServiceImpl(
            DataCollectionRepository dataCollectionRepository,
            IndexPersonService indexPersonService,
            ContactPersonService contactPersonService,
            DataCollectionMapper dataCollectionMapper) {
        this.dataCollectionRepository = dataCollectionRepository;
        this.indexPersonService = indexPersonService;
        this.contactPersonService = contactPersonService;
        this.dataCollectionMapper = dataCollectionMapper;
    }


    @Override
    public List<DataCollectionDTO> getAllDTOs() {
        return dataCollectionMapper
                .toDataCollectionDTOs(dataCollectionRepository.findAll());
    }


    @Override
    public DataCollection save(DataCollection dataCollection) {
        return dataCollectionRepository.save(dataCollection);

    }


    @Override
    public DataCollection createDataCollection(int quarantineLapse) {
        Instant timestamp = Instant.now();

        return DataCollection.builder()
                .timestamp(timestamp)
                .countIndexStatusPatientsInQuarantine(indexPersonService
                        .countByStatusAndInQuarantine(
                                Status.PATIENT))
                .countIndexStatusStaffInQuarantine(indexPersonService
                        .countByStatusAndInQuarantine(
                                Status.STAFF))
                .countPositiveContactStaff(contactPersonService
                        .countStaffContacts())
                .countIndexPersonStaffPauseQuarantine(indexPersonService
                        .countStaffAbortedQuarantine(
                                timestamp,
                                quarantineLapse))
                .countNewPatientsInQuarantine(indexPersonService
                        .countIndexInQuarantineByStatusAndFirstReportDate(
                                Status.PATIENT,
                                timestamp))
                .countNewStaffInQuarantine(indexPersonService
                        .countIndexInQuarantineByStatusAndFirstReportDate(
                                Status.STAFF,
                                timestamp))
                .build();
    }


    @Override
    public List<DataCollectionDTO> getDTOsBetweenDates(
            LocalDate start,
            LocalDate end) {
        if (start == null || end == null) {
            throw new DataCollectionException("Invalid date input.");
        }

        if (start.isAfter(end)) {
            throw new DataCollectionException("Start date is after end date.");
        }

        Instant startInstant = start
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant();
        Instant endInstant = end
                .atTime(LocalTime.MAX)
                .atZone(ZoneId.systemDefault())
                .toInstant();

        return dataCollectionMapper.toDataCollectionDTOs(dataCollectionRepository
                .findAllByTimestampGreaterThanEqualAndTimestampLessThanEqual(
                        startInstant,
                        endInstant));
    }


    @Override
    public long countNewPatients(
            List<DataCollectionDTO> dataCollectionList) {
        if (dataCollectionList == null) return 0L;

        return dataCollectionList
                .stream()
                .mapToLong(DataCollectionDTO::getCountNewPatientsInQuarantine)
                .sum();
    }


    @Override
    public long countNewStaff(
            List<DataCollectionDTO> dataCollectionList) {
        if (dataCollectionList == null) return 0L;

        return dataCollectionList
                .stream()
                .mapToLong(DataCollectionDTO::getCountNewStaffInQuarantine)
                .sum();
    }
}
