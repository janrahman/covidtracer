package de.hhu.covidtracer.controllers;

import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.dtos.ReportDTO;
import de.hhu.covidtracer.dtos.wrappers.IndexPersonFormWrapper;
import de.hhu.covidtracer.services.ContactPersonService;
import de.hhu.covidtracer.services.IndexPersonService;
import de.hhu.covidtracer.services.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Profile("local")
@Controller
@RequestMapping("/index")
@Slf4j
public class LocalIndexPersonController {
    private final IndexPersonService indexPersonService;
    private final ReportService reportService;
    private final ContactPersonService contactPersonService;

    @Autowired
    public LocalIndexPersonController(
            IndexPersonService indexPersonService,
            ReportService reportService,
            ContactPersonService contactPersonService) {
        this.indexPersonService = indexPersonService;
        this.reportService = reportService;
        this.contactPersonService = contactPersonService;
    }


    @GetMapping
    public String indexMainPage(
            Model model) {
        model.addAttribute(
                "indexPeople",
                indexPersonService.getAllDTOs());

        return "pages/indexpeople/index_indexpeople";
    }


    @GetMapping("/open")
    public String indexNotReportedToHealthDepartmentOrSupervisor(
            Model model) {
        List<IndexPersonDTO> notReportedToHealthDepartment = indexPersonService
                .getAllDTOsByNotReportedToHealthDepartment();
        List<IndexPersonDTO> notReportedToSupervisor = indexPersonService
                .getAllDTOsByNotReportedToSupervisor();

        model.addAttribute(
                "noHDReportedPeople",
                notReportedToHealthDepartment);
        model.addAttribute(
                "noMAReportedStaff",
                notReportedToSupervisor);

        return "pages/indexpeople/open_indexpeople";
    }


    @GetMapping("/open/healthdepartment")
    public String indexNotReportedToHealthDepartmentPage(
            Model model) {
        IndexPersonFormWrapper indexPersonFormWrapper =
                new IndexPersonFormWrapper();
        List<IndexPersonDTO> indexPersonDTOList = indexPersonService
                .getAllDTOsByNotReportedToHealthDepartment();

        indexPersonFormWrapper
                .setIndexPersonDTOList(indexPersonDTOList);

        model.addAttribute("people", indexPersonFormWrapper);

        return "pages/indexpeople/open_indexpeople_healthdepartment";
    }


    @PostMapping("/open/healthdepartment")
    public String indexNotReportedToHealthDepartmentTableForm(
            @ModelAttribute("people") IndexPersonFormWrapper indexPersonFormWrapper) {
        indexPersonService.updateBatchFromMultiForm(
                indexPersonFormWrapper.getIndexPersonDTOList());

        return "redirect:/index/open/healthdepartment";
    }


    @GetMapping("/open/staff")
    public String indexNotReportedToSupervisorPage(
            Model model) {
        IndexPersonFormWrapper indexPersonFormWrapper = new IndexPersonFormWrapper();
        List<IndexPersonDTO> indexPersonDTOList = indexPersonService
                .getAllDTOsByNotReportedToSupervisor();

        indexPersonFormWrapper
                .setIndexPersonDTOList(indexPersonDTOList);

        model.addAttribute("staff", indexPersonFormWrapper);

        return "pages/indexpeople/open_indexpeople_staff";
    }


    @PostMapping("/open/staff")
    public String indexNotReportedToSupervisorTableForm(
            @ModelAttribute("staff") IndexPersonFormWrapper indexPersonFormWrapper) {
        indexPersonService.updateBatchFromMultiForm(
                indexPersonFormWrapper.getIndexPersonDTOList());

        return "redirect:/index/open/staff";
    }


    @GetMapping("/open/quarantine")
    public String indexInQuarantine(
            Model model) {
        IndexPersonFormWrapper indexPersonFormWrapper = new IndexPersonFormWrapper();
        List<IndexPersonDTO> indexPersonDTOList = indexPersonService
                .getAllDTOsByQuarantineAndBooleansInverted();

        indexPersonFormWrapper
                .setIndexPersonDTOList(indexPersonDTOList);
        model.addAttribute("people", indexPersonFormWrapper);

        return "pages/indexpeople/open_indexpeople_quarantine";
    }


    @PostMapping("/open/quarantine")
    public String indexInQuarantineTableForm(
            @ModelAttribute("people") IndexPersonFormWrapper indexPersonFormWrapper) {
        indexPersonService.updateQuarantineBatchFromMultiForm(
                indexPersonFormWrapper.getIndexPersonDTOList());

        return "redirect:/index/open/quarantine";
    }


    @GetMapping("/{id}")
    public String indexDetailPage(
            @PathVariable long id,
            Model model) {
        List<ReportDTO> indexPersonReports = reportService
                .getReportDTOsByIndexId(id);
        ReportDTO firstReport = reportService
                .getFirstReportDTO(id);

        indexPersonReports.remove(firstReport);
        model.addAttribute(
                "indexPerson", indexPersonService.getDTOById(id));
        model.addAttribute(
                "reports", indexPersonReports);
        model.addAttribute(
                "firstReport", firstReport);
        model.addAttribute(
                "contacts",
                contactPersonService.getDTOsByIndexId(id));

        return "pages/indexpeople/show_indexperson";
    }


    @GetMapping("/{id}/edit")
    public String indexEditPage(
            @PathVariable long id,
            Model model) {
        model.addAttribute(
                "indexPerson",
                indexPersonService.getDTOById(id));

        return "pages/indexpeople/edit_indexperson";
    }


    @PostMapping("/{id}/edit")
    public String indexEdit(
            @ModelAttribute("indexPerson") IndexPersonDTO indexPersonDTO,
            @PathVariable long id) {
        indexPersonService.update(indexPersonDTO, id);

        return "redirect:/index/{id}";
    }


    @GetMapping("/{id}/delete")
    public String indexDelete(
            @PathVariable long id) {
        indexPersonService.remove(id);

        return "redirect:/index";
    }


    @GetMapping("{id}/delete/confirm")
    public String indexDeleteConfirm(
            @PathVariable long id,
            Model model) {
        model.addAttribute(
                "index",
                indexPersonService.getDTOById(id));

        return "pages/indexpeople/delete_indexperson";
    }
}
