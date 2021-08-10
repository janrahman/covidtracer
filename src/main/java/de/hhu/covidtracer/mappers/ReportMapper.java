package de.hhu.covidtracer.mappers;

import de.hhu.covidtracer.dtos.ReportDTO;
import de.hhu.covidtracer.mappers.qualifiers.LocalTimeQualifier;
import de.hhu.covidtracer.models.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {LocalTimeQualifier.class})
public interface ReportMapper {
    @Mapping(
            target = "date",
            qualifiedByName = "convertStringToLocalDate")
    Report toReport(ReportDTO report);

    @Mapping(
            target = "date",
            qualifiedByName = "convertLocalDateToString")
    ReportDTO toReportDTO(Report report);

    List<ReportDTO> toReportDTOs(List<Report> reportList);

    @Mapping(
            target = "date",
            qualifiedByName = "convertStringToLocalDate")
    void updateModel(ReportDTO reportDTO, @MappingTarget Report report);
}
