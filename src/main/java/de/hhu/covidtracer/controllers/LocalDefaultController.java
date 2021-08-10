package de.hhu.covidtracer.controllers;

import de.hhu.covidtracer.services.IndexPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Profile({"local"})
@Controller
@RequestMapping("/")
public class LocalDefaultController {
    private final IndexPersonService indexPersonService;

    @Autowired
    public LocalDefaultController(IndexPersonService indexPersonService) {
        this.indexPersonService = indexPersonService;
    }

    @GetMapping
    public String mainPage(Model model) {
        model.addAttribute("lastIndexPeople", indexPersonService
                .getLastUploadedIndexPeople());
        model.addAttribute(
                "countNotReportedToHealthDepartment",
                indexPersonService.countNotReportedToHealthDepartment());
        model.addAttribute(
                "countStaffNotReportedToSupervisor",
                indexPersonService.countNotReportedToSupervisor());
        model.addAttribute(
                "countPatientsNotInQuarantine",
                indexPersonService.countPatientsNotInQuarantine());

        return "index";
    }
}
