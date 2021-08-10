package de.hhu.covidtracer.services.helper;

import de.hhu.covidtracer.models.Status;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static de.hhu.covidtracer.models.Status.PATIENT;
import static de.hhu.covidtracer.models.Status.STAFF;
import static org.apache.poi.ss.usermodel.CellType.BLANK;

@Component
public class SpreadsheetHelper {
    private final DataFormatter dataFormatter = new DataFormatter();


    public String getCellDataAsString(Cell cell) throws DateTimeParseException {
        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell
                            .getDateCellValue()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE);
                }
            case STRING:
                return dataFormatter.formatCellValue(cell);
            default:
                return "";
        }
    }


    public Status getCellStatus(String compare) {
        if (!compare.isEmpty()) {
            String format = compare
                    .replaceAll("\\s+", "")
                    .toLowerCase();
            if (format.startsWith("m") ||
                    format.startsWith("s")) return STAFF;
        }

        return PATIENT;
    }


    public boolean checkIfRowIsEmpty(Row row, int lastColumn) {
        if (row == null) return true;
        if (row.getLastCellNum() <= 0) return true;

        for (int i = 0; i < lastColumn; i++) {
            Cell currentCell = row.getCell(i);
            Optional<Cell> cellInContainer = Optional.ofNullable(currentCell);

            if (cellInContainer.isPresent()) {
                if (currentCell.getCellType() != BLANK &&
                        !dataFormatter
                                .formatCellValue(currentCell).isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }
}
