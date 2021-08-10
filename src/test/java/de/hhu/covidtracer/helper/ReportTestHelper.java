package de.hhu.covidtracer.helper;

import de.hhu.covidtracer.models.Report;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportTestHelper {
    public List<Report> getReports(int startId, int size) {
        Report report;
        List<Report> reportList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            report = new Report();

            report.setId(startId++);
            reportList.add(report);
        }

        return reportList;
    }


    public Report createReport(long id) {
        Report report = new Report();

        report.setId(id);
        report.setDate(LocalDate.now());
        report.setComment("Comment no.: #" + id);

        return report;
    }


    public Report createReportWithDate(long id, LocalDate localDate) {
        Report report = createReport(id);
        report.setDate(localDate);

        return report;
    }
}
