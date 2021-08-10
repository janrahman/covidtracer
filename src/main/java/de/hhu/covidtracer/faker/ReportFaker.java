package de.hhu.covidtracer.faker;

import com.github.javafaker.Faker;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.Report;
import de.hhu.covidtracer.models.ContactPerson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReportFaker {
    private final Faker faker;

    public ReportFaker(Faker faker) {
        this.faker = faker;
    }


    public void createFirstReport(IndexPerson indexPerson) {
        Report report = new Report();

        report.setDate(
                LocalDate.now());
        report.setComment("Erstbefund");

        indexPerson.addReport(report);

    }


    public void createContactReport(ContactPerson staffMember) {
        Report report = new Report();

        report.setDate(
                LocalDate.now());
        report.setComment("Befund: " + report.getDate()
                .format(DateTimeFormatter.ISO_DATE));

        staffMember.addReport(report);
    }
}
