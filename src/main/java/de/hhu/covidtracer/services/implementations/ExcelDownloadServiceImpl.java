package de.hhu.covidtracer.services.implementations;

import de.hhu.covidtracer.dtos.ContactPersonDTO;
import de.hhu.covidtracer.dtos.IndexContactDTO;
import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.dtos.wrappers.PersonBasicInfoWrapper;
import de.hhu.covidtracer.services.ExcelDownloadService;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelDownloadServiceImpl implements ExcelDownloadService {
    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final static List<String> HEADER_TITLES = new ArrayList<>(
            Arrays.asList(
                    "",
                    "Anrede",
                    "Titel",
                    "Vorname",
                    "Name",
                    "Strasse",
                    "PLZ",
                    "Ort",
                    "Geburtsdatum",
                    "Telefon",
                    "Beruf",
                    "Kat. n Empfehlungen des RKI",
                    "letzter sozialer Kontakt am"
            ));
    private final static int HEADER_FONT_SIZE = 13;
    private final static int TEXT_FONT_SIZE = 12;

    public ExcelDownloadServiceImpl() {
        this.workbook = new XSSFWorkbook();
    }


    private void writeHeaderRow(
            Map<String, Integer> headerRowEntries) {
        Row row = sheet.createRow(0);
        CellStyle cellStyle = getHeaderCellStyle();

        headerRowEntries.forEach((v, k) -> createCells(row, k, v, cellStyle));
    }


    private void writeIndexData(
            IndexPersonDTO indexPerson,
            Map<String, Integer> headerRowEntries) {
        Row row = sheet.createRow(1);
        CellStyle firstRowCellStyle = getIndexFirstRowCellStyle();
        CellStyle mainCellStyle = getIndexValuesRowCellStyle();

        PersonBasicInfoWrapper basicIndexData = PersonBasicInfoWrapper.builder()
                .name(indexPerson.getName())
                .firstName(indexPerson.getFirstName())
                .address(indexPerson.getAddress())
                .build();

        headerRowEntries.forEach((v, k) -> createCells(
                row,
                k,
                "",
                mainCellStyle));
        createCells(
                row,
                headerRowEntries.get(""),
                "Index",
                firstRowCellStyle);
        createCellsBasicInfo(
                row,
                headerRowEntries,
                basicIndexData,
                mainCellStyle);
        createCells(
                row,
                headerRowEntries.get("Geburtsdatum"),
                indexPerson.getBirthday(),
                mainCellStyle);

    }


    private void writeContactData(
            List<IndexContactDTO> indexContactDTOList,
            Map<String, Integer> headerRowEntries) {
        PersonBasicInfoWrapper basicContactData;
        int rowCount = 2;
        CellStyle mainCellStyle = getContactRowCellStyle();
        CellStyle firstRowCellStyle = getContactFirstRowCellStyle();

        for (IndexContactDTO indexContactDTO : indexContactDTOList) {
            Row row = sheet.createRow(rowCount++);
            ContactPersonDTO contactPersonDTO = indexContactDTO
                    .getContactPerson();
            basicContactData = PersonBasicInfoWrapper.builder()
                    .firstName(contactPersonDTO.getFirstName())
                    .name(contactPersonDTO.getName())
                    .address(contactPersonDTO.getAddress())
                    .build();

            headerRowEntries.forEach(
                    (v, k) -> createCells(row, k, "", mainCellStyle));

            createCells(
                    row,
                    headerRowEntries.get(""),
                    "Kontaktpersonen",
                    firstRowCellStyle);
            createCellsBasicInfo(
                    row,
                    headerRowEntries,
                    basicContactData,
                    mainCellStyle);
            createCells(
                    row,
                    headerRowEntries.get("Telefon"),
                    contactPersonDTO.getPhone(),
                    mainCellStyle);
            createCells(
                    row,
                    headerRowEntries.get("Beruf"),
                    contactPersonDTO.getOccupationGroup(),
                    mainCellStyle);
            createCells(
                    row,
                    headerRowEntries.get("Kat. n Empfehlungen des RKI"),
                    contactPersonDTO.getCategory(),
                    mainCellStyle);
            createCells(
                    row,
                    headerRowEntries.get("letzter sozialer Kontakt am"),
                    indexContactDTO.getContactDateEnd(),
                    mainCellStyle);
        }

    }


    private void createCells(
            Row row,
            int columnIdx,
            String value,
            CellStyle cellStyle) {
        Cell cell = row.createCell(columnIdx);

        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        sheet.autoSizeColumn(columnIdx);
    }


    public void setResponseHeader(
            HttpServletResponse response,
            IndexPersonDTO indexPersonDTO) {
        String fullName = setFullName(indexPersonDTO);
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyyMMdd_HHmmss")
                .withZone(ZoneId.systemDefault());
        String fileName = fullName +
                "_" +
                formatter.format(Instant.now()) +
                ".xlsx";
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" +
                fileName;

        response.setContentType("application/octet-stream");
        response.setHeader(headerKey, headerValue);
    }


    private String setFullName(IndexPersonDTO indexPersonDTO) {
        String firstName = "empty";
        String name = "empty";

        if (indexPersonDTO.getFirstName() != null &&
                !indexPersonDTO.getFirstName().isEmpty()) {
            firstName = indexPersonDTO.getFirstName().toLowerCase();
        }

        if (indexPersonDTO.getName() != null &&
                !indexPersonDTO.getName().isEmpty()) {
            name = indexPersonDTO.getName().toLowerCase();
        }

        return firstName + "_" + name;
    }


    public void export(
            HttpServletResponse response,
            IndexPersonDTO indexPersonDTO,
            List<IndexContactDTO> indexContactDTOList,
            String password)
            throws IOException, GeneralSecurityException {
        POIFSFileSystem fs = new POIFSFileSystem();
        EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
        Encryptor enc = info.getEncryptor();

        if (password.isEmpty()) password = null;
        enc.confirmPassword(password);

        Map<String, Integer> headerRowEntries = createHashMap();
        sheet = workbook.createSheet("Addressen");

        writeHeaderRow(headerRowEntries);
        writeIndexData(indexPersonDTO, headerRowEntries);
        writeContactData(indexContactDTOList, headerRowEntries);

        OutputStream encos = enc.getDataStream(fs);

        workbook.write(encos);
        workbook.close();
        encos.close();

        ServletOutputStream outputStream = response.getOutputStream();

        fs.writeFilesystem(outputStream);
        response.flushBuffer();
        outputStream.close();
        fs.close();
    }


    private Map<String, Integer> createHashMap() {
        Map<String, Integer> headerMap = new HashMap<>();
        int counter = 0;

        for (String title : HEADER_TITLES) {
            headerMap.put(title, counter++);
        }
        return headerMap;
    }


    private CellStyle getHeaderCellStyle() {
        CellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();

        cellStyle.setFillForegroundColor(
                IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setWrapText(true);
        font.setFontHeightInPoints((short) HEADER_FONT_SIZE);
        font.setBold(true);
        cellStyle.setFont(font);

        return cellStyle;
    }


    private CellStyle getContactRowCellStyle() {
        CellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();

        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.TAN.getIndex());
        font.setFontHeightInPoints((short) TEXT_FONT_SIZE);
        cellStyle.setFont(font);

        return cellStyle;
    }


    private CellStyle getContactFirstRowCellStyle() {
        CellStyle cellStyle = getContactRowCellStyle();

        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return cellStyle;
    }


    private CellStyle getIndexRowCellStyle() {
        CellStyle cellStyle = workbook.createCellStyle();

        cellStyle.setFillForegroundColor(IndexedColors.TAN.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.ORANGE.getIndex());
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());

        return cellStyle;
    }


    private CellStyle getIndexValuesRowCellStyle() {
        CellStyle cellStyle = getIndexRowCellStyle();
        XSSFFont font = workbook.createFont();

        font.setFontHeightInPoints((short) TEXT_FONT_SIZE);
        cellStyle.setFont(font);

        return cellStyle;
    }


    private CellStyle getIndexFirstRowCellStyle() {
        CellStyle cellStyle = getIndexRowCellStyle();
        XSSFFont font = workbook.createFont();

        font.setColor(IndexedColors.RED.getIndex());
        font.setFontHeightInPoints((short) TEXT_FONT_SIZE);
        cellStyle.setFont(font);

        return cellStyle;
    }


    private void createCellsBasicInfo(
            Row row,
            Map<String, Integer> headerRowEntries,
            PersonBasicInfoWrapper personBasicInfoWrapper,
            CellStyle cellStyle) {
        createCells(
                row,
                headerRowEntries.get("Vorname"),
                personBasicInfoWrapper.getFirstName(),
                cellStyle);
        createCells(
                row,
                headerRowEntries.get("Name"),
                personBasicInfoWrapper.getName(),
                cellStyle);
        createCells(
                row,
                headerRowEntries.get("Strasse"),
                personBasicInfoWrapper.getAddress().getStreet(),
                cellStyle);
        createCells(
                row,
                headerRowEntries.get("PLZ"),
                personBasicInfoWrapper.getAddress().getPostcode(),
                cellStyle);
        createCells(
                row,
                headerRowEntries.get("Ort"),
                personBasicInfoWrapper.getAddress().getCity(),
                cellStyle);
    }
}
