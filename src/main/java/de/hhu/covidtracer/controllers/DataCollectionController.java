package de.hhu.covidtracer.controllers;

import de.hhu.covidtracer.dtos.DataCollectionDTO;
import de.hhu.covidtracer.dtos.wrappers.LocalDateSpanWrapper;
import de.hhu.covidtracer.services.DataCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/statistics")
@Slf4j
public class DataCollectionController {
    private final DataCollectionService dataCollectionService;

    @Autowired
    public DataCollectionController(
            DataCollectionService dataCollectionService) {
        this.dataCollectionService = dataCollectionService;
    }


    @GetMapping
    public String dataCollectionMainPage(
            Model model) {
        model.addAttribute(
                "current",
                dataCollectionService.createDataCollection(14));
        model.addAttribute(
                "dataCollections",
                dataCollectionService.getAllDTOs());
        model.addAttribute(
                "filterDate",
                new LocalDateSpanWrapper());
        return "pages/datacollections/index_datacollection";
    }

    @GetMapping("/search")
    public String dataCollectionGetMapping(
            @ModelAttribute("filterDate") @Valid LocalDateSpanWrapper localDateSpanWrapper,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) return "redirect:/statistics";

        List<DataCollectionDTO> resultList = dataCollectionService
                .getDTOsBetweenDates(
                        localDateSpanWrapper.getStart(),
                        localDateSpanWrapper.getEnd());

        model.addAttribute("dates", localDateSpanWrapper);
        model.addAttribute(
                "dataCollectionResults", resultList);

        model.addAttribute(
                "totalScorePatients",
                dataCollectionService.countNewPatients(resultList));
        model.addAttribute(
                "totalScoreStaff",
                dataCollectionService.countNewStaff(resultList));

        return "pages/datacollections/show_datacollectionresults";
    }
}
