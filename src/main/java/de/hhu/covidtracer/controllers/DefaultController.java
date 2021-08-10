package de.hhu.covidtracer.controllers;

import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.services.IndexPersonService;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Profile({"prod", "dev", "postgres"})
@Controller
@RequestMapping("/")
public class DefaultController {
    private final IndexPersonService indexPersonService;

    public DefaultController(
            IndexPersonService indexPersonService) {
        this.indexPersonService = indexPersonService;
    }

    @GetMapping
    public String mainPage(
            Authentication authentication,
            HttpServletRequest request,
            Model model) {
        List<IndexPersonDTO> recentIndexPersonDTOS;

        if (request.isUserInRole("ROLE_ADMIN")) {
            recentIndexPersonDTOS = indexPersonService
                    .getLastUploadedIndexPeople();
        } else {
            recentIndexPersonDTOS = indexPersonService
                    .getLastUploadedIndexPeopleByOwner(
                            authentication.getName());
        }

        model.addAttribute(
                "lastIndexPeople",
                recentIndexPersonDTOS);
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
