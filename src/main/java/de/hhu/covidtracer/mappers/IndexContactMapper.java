package de.hhu.covidtracer.mappers;

import de.hhu.covidtracer.dtos.IndexContactDTO;
import de.hhu.covidtracer.mappers.qualifiers.LocalTimeQualifier;
import de.hhu.covidtracer.models.associations.IndexContact;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                IndexPersonMapper.class,
                ContactPersonMapper.class,
                LocalTimeQualifier.class})
public interface IndexContactMapper {
    IndexContactDTO toIndexContactDTO(IndexContact indexContact);

    IndexContact toIndexContact(IndexContactDTO indexContactDTO);

    List<IndexContactDTO> toIndexContactDTOs(
            List<IndexContact> indexContactList);
}
