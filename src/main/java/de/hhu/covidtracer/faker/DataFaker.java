package de.hhu.covidtracer.faker;

import com.github.javafaker.Faker;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.Station;
import de.hhu.covidtracer.models.associations.IndexContact;
import de.hhu.covidtracer.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Locale;

@Profile({"dev"})
@Component
@Slf4j
public class DataFaker implements ServletContextInitializer {
    private final Faker faker;
    private final IndexPersonService indexPersonService;
    private final StationService stationService;
    private final ContactPersonService contactPersonService;
    private final DataCollectionService dataCollectionService;
    private final IndexContactService indexContactService;

    @Autowired
    public DataFaker(
            IndexPersonService indexPersonService,
            StationService stationService,
            ContactPersonService contactPersonService,
            DataCollectionService dataCollectionService,
            IndexContactService indexContactService) {
        this.indexPersonService = indexPersonService;
        this.stationService = stationService;
        this.contactPersonService = contactPersonService;
        this.dataCollectionService = dataCollectionService;
        this.indexContactService = indexContactService;
        faker = new Faker(Locale.GERMAN);
    }


    @Override
    @Transactional
    public void onStartup(ServletContext servletContext) {
        if (indexPersonService.count() != 0) {
            log.info("Database already initialized.");
            log.info("Skipping database faking");
            return;
        }

        List<IndexPerson> savedIndex;
        List<ContactPerson> savedContacts;

        int amountIndex = 30;
        int amountContactsPerIndex = 10;
        int amountStations = 15;

        log.info("Init Faker");
        IndexPersonFaker indexPersonFaker = new IndexPersonFaker(faker);
        AddressFaker addressFaker = new AddressFaker(faker);
        ReportFaker reportFaker = new ReportFaker(faker);
        StationFaker stationFaker = new StationFaker(faker);
        ContactPersonFaker contactPersonFaker = new ContactPersonFaker(faker);
        IndexContactFaker indexContactFaker = new IndexContactFaker(faker);
        StatusFaker statusFaker = new StatusFaker(faker);

        log.info("    Create index objects...");
        List<IndexPerson> fakeIndex = indexPersonFaker
                .getIndex(
                        amountIndex,
                        reportFaker,
                        addressFaker,
                        statusFaker);
        log.info("    Create station objects...");
        List<Station> fakeStations = stationFaker
                .getStations(amountStations);
        log.info("    Create contact objects...");
        List<ContactPerson> fakeContacts = contactPersonFaker
                .getContacts(
                        amountIndex * amountContactsPerIndex,
                        fakeStations,
                        addressFaker,
                        reportFaker,
                        statusFaker);

        log.info("    Persist stations, contacts and reports...");
        savedContacts = contactPersonService.saveAll(fakeContacts);
        stationService.saveAll(fakeStations);

        log.info("    Persist patients...");
        savedIndex = indexPersonService.saveAll(fakeIndex);

        log.info("    Create relationship index contact...");
        List<IndexContact> fakeRelations = indexContactFaker
                .setRelationships(
                        savedIndex,
                        savedContacts,
                        amountContactsPerIndex);
        indexContactService.saveAll(fakeRelations);

        log.info("    Create and persist survey");
        dataCollectionService.save(
                dataCollectionService
                        .createDataCollection(14));
        log.info("Done!");
    }
}
