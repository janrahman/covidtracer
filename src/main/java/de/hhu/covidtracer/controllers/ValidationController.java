package de.hhu.covidtracer.controllers;

import de.hhu.covidtracer.dtos.ContactPersonDTO;
import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.dtos.ReportDTO;
import de.hhu.covidtracer.services.ContactPersonService;
import de.hhu.covidtracer.services.IndexPersonService;
import de.hhu.covidtracer.services.ReportService;
import de.hhu.covidtracer.services.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ValidationController {
    private final ValidationService validationService;
    private final IndexPersonService indexPersonService;
    private final ReportService reportService;
    private final ContactPersonService contactPersonService;

    @Autowired
    public ValidationController(
            ValidationService validationService,
            IndexPersonService indexPersonService,
            ReportService reportService,
            ContactPersonService contactPersonService) {
        this.validationService = validationService;
        this.indexPersonService = indexPersonService;
        this.reportService = reportService;
        this.contactPersonService = contactPersonService;
    }

    @GetMapping("/index/{id}/validate")
    public String indexValidation(
            @PathVariable long id,
            Model model,
            HttpServletRequest request) {
//        Optional<String> previousUrl = Optional.ofNullable(
//                request.getHeader(
//                        "Referer")).map(url -> "redirect:" + url);

        IndexPersonDTO indexPersonDTO = indexPersonService
                .getDTOById(id);
        List<ReportDTO> reportDTOList = reportService
                .getReportDTOsByIndexId(id);
        List<ContactPersonDTO> contactPersonDTOList = contactPersonService
                .getDTOsByIndexId(id);

        model.addAttribute(
                "indexPerson",
                indexPersonDTO);
        model.addAttribute(
                "indexErrors",
                validationService.validate(indexPersonDTO));
        model.addAttribute(
                "reportErrorsWrapper",
                validationService.validateList(reportDTOList));
        model.addAttribute(
                "contactErrorsWrapper",
                validationService.validateList(contactPersonDTOList));

        return "pages/indexpeople/validate_indexperson";
    }
}
