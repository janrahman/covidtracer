package de.hhu.covidtracer.services.implementations;

import de.hhu.covidtracer.dtos.*;
import de.hhu.covidtracer.exceptions.UploadException;
import de.hhu.covidtracer.mappers.excelrowmappers.ExcelContactPersonRowMapper;
import de.hhu.covidtracer.mappers.excelrowmappers.ExcelIndexPersonRowMapper;
import de.hhu.covidtracer.mappers.excelrowmappers.ExcelReportRowMapper;
import de.hhu.covidtracer.mappers.excelrowmappers.ExcelStationRowMapper;
import de.hhu.covidtracer.models.*;
import de.hhu.covidtracer.models.associations.IndexContact;
import de.hhu.covidtracer.services.*;
import de.hhu.covidtracer.services.helper.SpreadsheetHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static de.hhu.covidtracer.models.Status.*;


@Service("ExcelUploadService")
@Slf4j
public class ExcelUploadServiceImpl implements ExcelUploadService {
    private static final String CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final List<String> SHEET_NAMES = Arrays.asList(
            "Index",
            "Mitarbeiter",
            "Patienten",
            "Besucher");
    private static final List<ContactPersonDTO> CONTACT_LIST =
            new ArrayList<>();
    private final SpreadsheetHelper spreadsheetHelper;
    private final StationService stationService;
    private final IndexPersonService indexPersonService;
    private final ContactPersonService contactPersonService;
    private final ReportService reportService;
    private final IndexContactService indexContactService;
    private final ExcelIndexPersonRowMapper excelIndexPersonRowMapper;
    private final ExcelContactPersonRowMapper excelContactPersonRowMapper;
    private final ExcelStationRowMapper excelStationRowMapper;
    private final ExcelReportRowMapper excelReportRowMapper;

    public ExcelUploadServiceImpl(
            SpreadsheetHelper spreadsheetHelper,
            StationService stationService,
            IndexPersonService indexPersonService,
            ContactPersonService contactPersonService,
            ReportService reportService,
            IndexContactService indexContactService,
            ExcelIndexPersonRowMapper excelIndexPersonRowMapper,
            ExcelContactPersonRowMapper excelContactPersonRowMapper,
            ExcelStationRowMapper excelStationRowMapper,
            ExcelReportRowMapper excelReportRowMapper) {
        this.spreadsheetHelper = spreadsheetHelper;
        this.stationService = stationService;
        this.indexPersonService = indexPersonService;
        this.contactPersonService = contactPersonService;
        this.reportService = reportService;
        this.indexContactService = indexContactService;
        this.excelIndexPersonRowMapper = excelIndexPersonRowMapper;
        this.excelContactPersonRowMapper = excelContactPersonRowMapper;
        this.excelStationRowMapper = excelStationRowMapper;
        this.excelReportRowMapper = excelReportRowMapper;
    }


    @Override
    public IndexPerson importFromExcel(
            MultipartFile file,
            String owner) throws IOException, UploadException {
        if (file.isEmpty()) throw new UploadException("File is empty.");
        if (!isContentType(file)) throw new UploadException(
                "Wrong file extension.");

        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        if (!checkSheets(workbook))
            throw new UploadException(
                    "Sheet names are not valid. " + SHEET_NAMES);

        Status status;
        ReportDTO reportDTO = new ReportDTO();
        IndexPersonDTO indexPersonDTO = new IndexPersonDTO();

        for (Sheet sheet : workbook) {
            if (SHEET_NAMES.get(0).equals(sheet.getSheetName())) {
                Row indexRow = sheet.getRow(4);

                if (spreadsheetHelper
                        .checkIfRowIsEmpty(indexRow, 7)) {
                    throw new UploadException("No index patient found.");
                }

                indexPersonDTO = getIndexPatientFromSheet(indexRow);
                reportDTO = getReportFromSheet(indexRow);
                continue;
            }

            status = getStatusSheet(sheet.getSheetName());

            readContacts(sheet, status);
        }

        workbook.close();
        removeDuplicatesFromContactList();

        IndexPerson indexPerson = indexPersonService
                .getFromImportFile(indexPersonDTO);
        Report report = reportService.toReport(reportDTO);

        indexPerson.setEntryDateTime(Instant.now());
        indexPerson.setOwner(owner);
        reportService.setFirstReportFromFile(indexPerson, report);

        indexPerson = saveAllRelations(indexPerson);

        CONTACT_LIST.clear();

        return indexPerson;
    }


    @Transactional
    IndexPerson saveAllRelations(IndexPerson indexPerson) {
        ContactPerson contactPerson;
        Station station;
        IndexContact indexContact;
        List<IndexContact> indexContactList = new ArrayList<>();

        indexPerson = indexPersonService.save(indexPerson);

        for (ContactPersonDTO contactPersonDTO : CONTACT_LIST) {
            contactPerson = contactPersonService
                    .getContactFromFile(contactPersonDTO);
            station = stationService
                    .getStationFromFile(contactPersonDTO.getStation());

            if (station != null) {
                stationService.removeOldStationFromContact(
                        station,
                        contactPerson
                );
                contactPerson.setStation(station);
            }

            contactPerson = contactPersonService.save(contactPerson);
            indexContact = indexContactService.getFromFile(
                    indexPerson,
                    contactPerson,
                    contactPersonDTO.getDatePeriodStart(),
                    contactPersonDTO.getDatePeriodEnd()
            );

            indexContactList.add(indexContact);
        }

        indexContactService.saveAll(indexContactList);

        return indexPerson;
    }


    private Status getStatusSheet(String sheetName) {
        if (SHEET_NAMES.get(1).equals(sheetName)) return STAFF;
        if (SHEET_NAMES.get(2).equals(sheetName)) return PATIENT;
        if (SHEET_NAMES.get(3).equals(sheetName)) return VISITOR;

        throw new UploadException(
                "Sheet name is not valid. (" + sheetName + ")");
    }


    private ReportDTO getReportFromSheet(Row indexPatientRow) {
        return excelReportRowMapper
                .readReportIndexRow(indexPatientRow);
    }


    private void readContacts(
            Sheet sheet,
            Status status) {
        int rowIndex = 0;
        StationDTO stationDTO;
        ContactPersonDTO contactPersonDTO;

        for (Row currentRow : sheet) {
            // skip header
            if (rowIndex < 5) {
                rowIndex++;
                continue;
            }

            if (spreadsheetHelper.checkIfRowIsEmpty(currentRow, 2))
                break;

            if (status == STAFF) {
                contactPersonDTO = excelContactPersonRowMapper
                        .readContactStaffRow(currentRow);
                stationDTO = excelStationRowMapper
                        .readStationStaffRow(currentRow);
            } else {
                contactPersonDTO = excelContactPersonRowMapper
                        .readContactRow(currentRow, status);
                stationDTO = excelStationRowMapper
                        .readStationContactRow(currentRow);
            }

            contactPersonDTO.setStation(stationDTO);
            CONTACT_LIST.add(contactPersonDTO);
        }
    }


    private IndexPersonDTO getIndexPatientFromSheet(Row row) {
        return excelIndexPersonRowMapper.readIndexPersonRow(row);
    }


    private boolean isContentType(MultipartFile file) {
        return CONTENT_TYPE.equals(file.getContentType());
    }


    private boolean checkSheets(Workbook workbook) {
        for (Sheet sheet : workbook)
            if (!SHEET_NAMES.contains(sheet.getSheetName())) return false;
        return SHEET_NAMES.size() == workbook.getNumberOfSheets();
    }


    private void removeDuplicatesFromContactList() {
        HashSet<Object> contactDuplicates = new HashSet<>();

        CONTACT_LIST.removeIf(
                c -> !contactDuplicates.add(Arrays.asList(
                        c.getName(),
                        c.getFirstName(),
                        c.getEmail(),
                        c.getPhone())));
    }
}
