package de.hhu.covidtracer.mappers;

import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.mappers.qualifiers.LocalTimeQualifier;
import de.hhu.covidtracer.models.IndexPerson;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {LocalTimeQualifier.class, AddressMapper.class})
public interface IndexPersonMapper {
    @Mappings({
            @Mapping(
                    target = "entryDateTime",
                    qualifiedByName = "convertToInstant"),
            @Mapping(target = "birthday", qualifiedByName = "convertLocalDateToString")
    })
    IndexPersonDTO toIndexDTO(IndexPerson indexPerson);

    @Mapping(target = "birthday", qualifiedByName = "convertStringToLocalDate")
    IndexPerson toIndex(IndexPersonDTO indexPersonDTO);

    List<IndexPersonDTO> toIndexDTOs(List<IndexPerson> indexPersonList);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "birthday", qualifiedByName = "convertStringToLocalDate")
    })
    void updateModel(
            IndexPersonDTO indexPersonDTO,
            @MappingTarget IndexPerson indexPerson);
}
