package de.hhu.covidtracer.models;

import de.hhu.covidtracer.helper.ReportTestHelper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReportTest {
    private static final ReportTestHelper REPORT_TEST_HELPER =
            new ReportTestHelper();


    @Test
    public void emptyRelations() {
        Report report = new Report();

        assertNull(report.getIndexPerson());
        assertNull(report.getContactPerson());
    }


    @Test
    public void addOneIndexPerson() {
        Report report = new Report();
        IndexPerson indexPerson = new IndexPerson();

        report.setIndexPerson(indexPerson);

        assertEquals(indexPerson, report.getIndexPerson());
        assertTrue(indexPerson.getReports().contains(report));
    }


    @Test
    public void addOneContactPerson() {
        Report report = new Report();
        ContactPerson contactPerson = new ContactPerson();

        report.setContactPerson(contactPerson);

        assertEquals(contactPerson, report.getContactPerson());
        assertTrue(contactPerson.getReports().contains(report));
    }


    @Test
    public void onlyContactPersonIsAllowed() {
        Report report = new Report();
        IndexPerson indexPerson = new IndexPerson();
        ContactPerson contactPerson = new ContactPerson();

        report.setContactPerson(contactPerson);
        report.setIndexPerson(indexPerson);

        assertEquals(contactPerson, report.getContactPerson());
        assertTrue(contactPerson.getReports().contains(report));
        assertNull(report.getIndexPerson());
        assertTrue(indexPerson.getReports().isEmpty());
    }


    @Test
    public void onlyIndexPersonIsAllowed() {
        Report report = new Report();
        IndexPerson indexPerson = new IndexPerson();
        ContactPerson contactPerson = new ContactPerson();

        report.setIndexPerson(indexPerson);
        report.setContactPerson(contactPerson);

        assertEquals(indexPerson, report.getIndexPerson());
        assertTrue(indexPerson.getReports().contains(report));
        assertTrue(contactPerson.getReports().isEmpty());
        assertNull(report.getContactPerson());
    }


    @Test
    public void removeOneIndexPerson() {
        Report report = new Report();
        IndexPerson indexPerson = new IndexPerson();

        report.setIndexPerson(indexPerson);
        report.setIndexPerson(null);

        assertTrue(indexPerson.getReports().isEmpty());
    }


    @Test
    public void replaceContactPerson() {
        Report report = new Report();
        ContactPerson contactPerson1 = new ContactPerson();
        ContactPerson contactPerson2 = new ContactPerson();

        contactPerson1.setId(1L);
        contactPerson2.setId(2L);
        report.setContactPerson(contactPerson1);
        report.setContactPerson(contactPerson2);

        assertEquals(contactPerson2, report.getContactPerson());
        assertTrue(contactPerson2.getReports().contains(report));
        assertTrue(contactPerson1.getReports().isEmpty());
    }


    @Test
    public void replaceReportContactPerson() {
        Report report = new Report();
        ContactPerson contactPerson = new ContactPerson();
        Report report1 = new Report();

        report1.setId(1L);
        report.setContactPerson(contactPerson);
        report1.setContactPerson(contactPerson);

        assertEquals(contactPerson, report1.getContactPerson());
        assertTrue(contactPerson.getReports().contains(report1));
        assertEquals(contactPerson, report.getContactPerson());
    }


    @Test
    public void addTenReportsToIndex() {
        int max = 10;
        IndexPerson indexPerson = new IndexPerson();
        List<Report> reportList = REPORT_TEST_HELPER.getReports(1, max);

        for (Report report : reportList) report.setIndexPerson(indexPerson);

        assertEquals(max, indexPerson.getReports().size());
    }


    @Test
    public void addTenReportsToContactPerson() {
        int max = 10;
        ContactPerson contactPerson = new ContactPerson();
        List<Report> reportList = REPORT_TEST_HELPER.getReports(1, max);

        for (Report report : reportList) report.setContactPerson(contactPerson);

        assertEquals(max, contactPerson.getReports().size());
    }
}