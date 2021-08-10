package de.hhu.covidtracer.helper;

import com.github.javafaker.Faker;
import de.hhu.covidtracer.dtos.AddressDTO;
import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.models.Address;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.Report;
import de.hhu.covidtracer.models.Status;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IndexPersonTestHelper {
    private static final Faker FAKER = new Faker(new Locale("de-DE"));
    private static final AddressTestHelper ADDRESS_TEST_HELPER =
            new AddressTestHelper();
    private static final ReportTestHelper REPORT_TEST_HELPER =
            new ReportTestHelper();


    public IndexPerson getIndexPerson(long id, Status status) {
        Address fakeAddress = ADDRESS_TEST_HELPER.getAddress();

        LocalDate fakeBirthday = FAKER.date().birthday()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return IndexPerson.builder()
                .id(id)
                .firstName(FAKER.name().firstName())
                .name(FAKER.name().lastName())
                .birthday(fakeBirthday)
                .address(fakeAddress)
                .status(status)
                .build();
    }


    public List<IndexPerson> getIndexPeople(
            int startId, int size,
            Status status) {
        List<IndexPerson> indexPersonList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            indexPersonList.add(getIndexPerson(startId++, status));
        }

        return indexPersonList;
    }


    public List<IndexPerson> getIndexWithReportDate(
            int startId,
            int size,
            Status status,
            LocalDate localDate) {
        IndexPerson indexPerson;
        Report report;
        List<IndexPerson> indexPersonList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            indexPerson = getIndexPerson(startId, status);
            report = REPORT_TEST_HELPER.createReportWithDate(startId, localDate);

            indexPerson.addReport(report);
            indexPersonList.add(indexPerson);
            startId++;
        }

        return indexPersonList;
    }


    public IndexPersonDTO getDTO(long id, Status status) {
        AddressDTO fakeAddress = ADDRESS_TEST_HELPER.getDTO();

        LocalDate fakeBirthday = FAKER.date().birthday()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        IndexPersonDTO indexPersonDTO = new IndexPersonDTO();

        indexPersonDTO.setId(id);
        indexPersonDTO.setName(FAKER.name().lastName());
        indexPersonDTO.setFirstName(FAKER.name().firstName());
        indexPersonDTO.setBirthday(fakeBirthday
                .format(DateTimeFormatter.ISO_DATE));
        indexPersonDTO.setStatus(status);
        indexPersonDTO.setAddress(fakeAddress);

        return indexPersonDTO;
    }


    public List<IndexPersonDTO> getDTOList(
            int startId,
            int size,
            Status status) {
        List<IndexPersonDTO> indexPersonList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            indexPersonList.add(getDTO(startId++, status));
        }

        return indexPersonList;
    }
}
