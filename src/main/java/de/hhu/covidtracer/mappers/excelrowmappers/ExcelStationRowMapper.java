package de.hhu.covidtracer.mappers.excelrowmappers;

import de.hhu.covidtracer.dtos.StationDTO;
import de.hhu.covidtracer.services.helper.SpreadsheetHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

@Component
public class ExcelStationRowMapper {
    private final SpreadsheetHelper spreadsheetHelper;

    public ExcelStationRowMapper(SpreadsheetHelper spreadsheetHelper) {
        this.spreadsheetHelper = spreadsheetHelper;
    }


    public StationDTO readStationStaffRow(Row currentRow) {
        StationDTO stationDTO = new StationDTO();
        String cellData;

        int columnRange = 3;
        for (int i = 0; i < columnRange; i++) {
            Cell currentCell = currentRow.getCell(i);
            cellData = spreadsheetHelper
                    .getCellDataAsString(currentCell);

            if (i == 0) stationDTO.setHospitalName(cellData);
            if (i == 1) stationDTO.setName(cellData);
            if (i == 2) stationDTO.setDepartment(cellData);
        }

        return stationDTO;
    }


    public StationDTO readStationContactRow(Row currentRow) {
        StationDTO stationDTO = new StationDTO();
        String cellData = spreadsheetHelper
                .getCellDataAsString(currentRow.getCell(7));

        stationDTO.setName(cellData);
        stationDTO.setHospitalName("");
        stationDTO.setDepartment("");

        return stationDTO;
    }
}
