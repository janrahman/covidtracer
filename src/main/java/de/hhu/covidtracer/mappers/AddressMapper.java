package de.hhu.covidtracer.mappers;

import de.hhu.covidtracer.dtos.AddressDTO;
import de.hhu.covidtracer.models.Address;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {
    AddressDTO toAdressDTO(Address address);

    Address toAddress(AddressDTO addressDTO);
}
