package de.hhu.covidtracer.mappers.excelrowmappers;

import de.hhu.covidtracer.dtos.AddressDTO;
import de.hhu.covidtracer.dtos.ContactPersonDTO;
import de.hhu.covidtracer.models.Status;
import de.hhu.covidtracer.services.helper.SpreadsheetHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import static de.hhu.covidtracer.models.Status.STAFF;

@Component
public class ExcelContactPersonRowMapper {
    private final SpreadsheetHelper spreadsheetHelper;

    public ExcelContactPersonRowMapper(SpreadsheetHelper spreadsheetHelper) {
        this.spreadsheetHelper = spreadsheetHelper;
    }


    public ContactPersonDTO readContactStaffRow(
            Row row) {
        AddressDTO addressDTO = new AddressDTO();
        ContactPersonDTO staffMemberDTO = new ContactPersonDTO();
        int cellIdx = 0;

        for (Cell currentCell : row) {
            String cellData = spreadsheetHelper
                    .getCellDataAsString(currentCell);
            switch (cellIdx) {
                case 3:
                    staffMemberDTO
                            .setOccupationGroup(cellData);
                    break;
                case 4:
                    staffMemberDTO
                            .setName(cellData);
                    break;
                case 5:
                    staffMemberDTO
                            .setFirstName(cellData);
                    break;
                case 6:
                    addressDTO
                            .setStreet(cellData);
                    break;
                case 7:
                    addressDTO
                            .setPostcode(cellData);
                    break;
                case 8:
                    addressDTO
                            .setCity(cellData);
                    break;
                case 9:
                    staffMemberDTO
                            .setPhone(cellData);
                    break;
                case 10:
                    staffMemberDTO
                            .setEmail(cellData);
                    break;
                case 11:
                    staffMemberDTO
                            .setCategory(cellData);
                    break;
                case 12:
                    staffMemberDTO
                            .setDatePeriodStart(cellData);
                    break;
                case 13:
                    staffMemberDTO
                            .setDatePeriodEnd(cellData);
                default:
                    break;
            }

            cellIdx++;
        }

        staffMemberDTO.setAddress(addressDTO);
        staffMemberDTO.setStatus(STAFF);
        return staffMemberDTO;
    }


    public ContactPersonDTO readContactRow(
            Row row,
            Status status) {
        AddressDTO addressDTO = new AddressDTO();
        ContactPersonDTO contactPersonDTO = new ContactPersonDTO();

        int cellIdx = 0;

        for (Cell currentCell : row) {
            String cellData = spreadsheetHelper
                    .getCellDataAsString(currentCell);
            switch (cellIdx) {
                case 0:
                    contactPersonDTO
                            .setName(cellData);
                    break;
                case 1:
                    contactPersonDTO
                            .setFirstName(cellData);
                    break;
                case 2:
                    addressDTO
                            .setStreet(cellData);
                    break;
                case 3:
                    addressDTO
                            .setPostcode(cellData);
                    break;
                case 4:
                    addressDTO
                            .setCity(cellData);
                    break;
                case 5:
                    contactPersonDTO
                            .setPhone(cellData);
                    break;
                case 6:
                    contactPersonDTO
                            .setEmail(cellData);
                    break;
                case 8:
                    contactPersonDTO
                            .setDatePeriodStart(cellData);
                    break;
                case 9:
                    contactPersonDTO
                            .setDatePeriodEnd(cellData);
                default:
                    break;
            }

            cellIdx++;
        }

        contactPersonDTO.setAddress(addressDTO);
        contactPersonDTO.setStatus(status);

        return contactPersonDTO;
    }
}
