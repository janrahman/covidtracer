package de.hhu.covidtracer.mappers;

import de.hhu.covidtracer.dtos.ContactPersonDTO;
import de.hhu.covidtracer.mappers.qualifiers.CategoryQualifier;
import de.hhu.covidtracer.mappers.qualifiers.LatestReportQualifier;
import de.hhu.covidtracer.mappers.qualifiers.LocalTimeQualifier;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.associations.IndexContact;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {
                StationMapper.class,
                LatestReportQualifier.class,
                CategoryQualifier.class,
                LocalTimeQualifier.class,
                AddressMapper.class})
public interface ContactPersonMapper {
    @Mappings({
            @Mapping(
                    source = "id",
                    target = "latestReportDate",
                    qualifiedByName = "latestReportDate"),
            @Mapping(
                    target = "category",
                    qualifiedByName = "convertDigitToRoman")})
    ContactPersonDTO toContactPersonDTO(ContactPerson contactPerson);

    @Mappings({
            @Mapping(target = "station", ignore = true),
            @Mapping(
                    target = "category",
                    qualifiedByName = "convertDigitToRoman")
    })
    ContactPerson toContactPerson(ContactPersonDTO contactPersonDTO);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "station", ignore = true)
    })
    void updateModel(
            ContactPersonDTO contactPersonDTO,
            @MappingTarget ContactPerson contactPerson);

    @Mappings({
            @Mapping(
                    source = "contactPerson.id",
                    target = "latestReportDate",
                    qualifiedByName = "latestReportDate"),
            @Mapping(
                    source = "indexContact.contactDateStart",
                    target = "datePeriodStart",
                    qualifiedByName = "convertLocalDateToString"),
            @Mapping(
                    source = "indexContact.contactDateEnd",
                    target = "datePeriodEnd",
                    qualifiedByName = "convertLocalDateToString"),
            @Mapping(
                    target = "category",
                    qualifiedByName = "convertDigitToRoman")
    })
    ContactPersonDTO toContactPersonDTO(
            ContactPerson contactPerson,
            IndexContact indexContact);
}
