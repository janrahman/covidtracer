package de.hhu.covidtracer.mappers.excelrowmappers;

import de.hhu.covidtracer.dtos.ReportDTO;
import de.hhu.covidtracer.exceptions.UploadException;
import de.hhu.covidtracer.services.ValidationService;
import de.hhu.covidtracer.services.helper.SpreadsheetHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

@Component
public class ExcelReportRowMapper {
    private final SpreadsheetHelper spreadsheetHelper;
    private final ValidationService validationService;

    public ExcelReportRowMapper(
            SpreadsheetHelper spreadsheetHelper,
            ValidationService validationService) {
        this.spreadsheetHelper = spreadsheetHelper;
        this.validationService = validationService;
    }


    public ReportDTO readReportIndexRow(Row currentRow) {
        Cell currentCell = currentRow.getCell(6);

        ReportDTO reportDTO = new ReportDTO();

        reportDTO.setDate(spreadsheetHelper.getCellDataAsString(currentCell));
        reportDTO.setComment("Erstbefund");

        validateDate(reportDTO);

        return reportDTO;
    }


    private void validateDate(ReportDTO reportDTO) {
        if (validationService.validate(reportDTO).size() > 0) {
            throw new UploadException("Invalid report date format.");
        }
    }
}

