package de.hhu.covidtracer.mappers.excelrowmappers;

import de.hhu.covidtracer.dtos.AddressDTO;
import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.exceptions.UploadException;
import de.hhu.covidtracer.models.Status;
import de.hhu.covidtracer.services.helper.SpreadsheetHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

@Component
public class ExcelIndexPersonRowMapper {
    private final SpreadsheetHelper spreadsheetHelper;

    public ExcelIndexPersonRowMapper(SpreadsheetHelper spreadsheetHelper) {
        this.spreadsheetHelper = spreadsheetHelper;
    }


    public IndexPersonDTO readIndexPersonRow(
            Row row) throws UploadException {
        IndexPersonDTO indexPersonDTO = new IndexPersonDTO();
        AddressDTO addressDTO = new AddressDTO();

        int cellIdx = 0;

        for (Cell currentCell : row) {
            String cellData = spreadsheetHelper
                    .getCellDataAsString(currentCell);
            switch (cellIdx) {
                case 0:
                    indexPersonDTO.setName(cellData);
                    break;
                case 1:
                    indexPersonDTO.setFirstName(cellData);
                    break;
                case 2:
                    indexPersonDTO.setBirthday(cellData);
                    break;
                case 3:
                    addressDTO.setStreet(cellData);
                    break;
                case 4:
                    addressDTO.setPostcode(cellData);
                    break;
                case 5:
                    addressDTO.setCity(cellData);
                    break;
                case 7:
                    indexPersonDTO.setStatus(
                            spreadsheetHelper.getCellStatus(cellData));
                default:
                    break;
            }

            cellIdx++;
        }

        indexPersonDTO.setAddress(addressDTO);
        indexPersonDTO.setInQuarantine(true);

        return indexPersonDTO;
    }
}
