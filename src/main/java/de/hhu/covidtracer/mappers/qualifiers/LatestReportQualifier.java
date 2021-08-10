package de.hhu.covidtracer.mappers.qualifiers;

import de.hhu.covidtracer.models.Report;
import de.hhu.covidtracer.services.ReportService;
import org.mapstruct.Named;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LatestReportQualifier {
    private final ReportService reportService;
    private final ConversionService conversionService;

    public LatestReportQualifier(
            ReportService reportService,
            ConversionService conversionService) {
        this.reportService = reportService;
        this.conversionService = conversionService;
    }


    @Named("latestReportDate")
    public String getLatestReportDate(long contactId) {
        Optional<Report> latestReport = reportService
                .getLatestReportByContactId(contactId);

        return latestReport
                .map(report -> conversionService.convert(
                        report.getDate(),
                        String.class))
                .orElse("No reports");
    }
}
