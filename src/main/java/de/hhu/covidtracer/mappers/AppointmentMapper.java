package de.hhu.covidtracer.mappers;

import de.hhu.covidtracer.dtos.AppointmentDTO;
import de.hhu.covidtracer.models.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentMapper {
    AppointmentDTO toAppointmentDTO(Appointment appointment);

    Appointment toAppointment(AppointmentDTO appointmentDTO);

    List<AppointmentDTO> toAppointmentDTOs(List<Appointment> appointmentList);

    @Mapping(target = "id", ignore = true)
    void updateModel(
            AppointmentDTO appointmentDTO,
            @MappingTarget Appointment appointment);
}
