package de.hhu.covidtracer.mappers;

import de.hhu.covidtracer.dtos.DataCollectionDTO;
import de.hhu.covidtracer.mappers.qualifiers.LocalTimeQualifier;
import de.hhu.covidtracer.models.DataCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {LocalTimeQualifier.class})
public interface DataCollectionMapper {
    @Mapping(
            target = "localTimestamp",
            source = "timestamp",
            qualifiedByName = "convertToInstant")
    DataCollectionDTO toDataCollectionDTO(DataCollection dataCollection);

    List<DataCollectionDTO> toDataCollectionDTOs(
            List<DataCollection> dataCollectionList);
}
