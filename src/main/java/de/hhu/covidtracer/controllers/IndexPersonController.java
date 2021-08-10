package de.hhu.covidtracer.controllers;

import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.dtos.ReportDTO;
import de.hhu.covidtracer.dtos.wrappers.IndexPersonFormWrapper;
import de.hhu.covidtracer.models.Status;
import de.hhu.covidtracer.services.ContactPersonService;
import de.hhu.covidtracer.services.IndexPersonService;
import de.hhu.covidtracer.services.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO: Apply changes to LocalController
@Profile({"prod", "dev", "postgres"})
@Controller
@RequestMapping("/index")
@Slf4j
public class IndexPersonController {
    private final IndexPersonService indexPersonService;
    private final ReportService reportService;
    private final ContactPersonService contactPersonService;

    @Autowired
    public IndexPersonController(
            IndexPersonService indexPersonService,
            ReportService reportService,
            ContactPersonService contactPersonService) {
        this.indexPersonService = indexPersonService;
        this.reportService = reportService;
        this.contactPersonService = contactPersonService;
    }


    @GetMapping
    public String indexMainPage(
            Authentication authentication,
            Model model,
            HttpServletRequest request) {
        List<IndexPersonDTO> indexPersonDTOList = new ArrayList<>();
        String role = "ROLE_ADMIN";

        if (request.isUserInRole(role)) {
            indexPersonDTOList = indexPersonService.getAllDTOs();
        }
        if (!request.isUserInRole(role)) {
            indexPersonDTOList = indexPersonService
                    .getAllVisibleOrOwnerDTOs(
                            authentication.getName());
        }

        model.addAttribute(
                "indexPeople",
                indexPersonDTOList);

        return "pages/indexpeople/index_indexpeople";
    }


    @GetMapping("/open")
    public String indexNotReportedToHealthDepartmentOrSupervisor(
            HttpServletRequest request,
            Authentication authentication,
            Model model) {
        List<IndexPersonDTO> notReportedToHealthDepartment = indexPersonService
                .getAllDTOsByNotReportedToHealthDepartment();
        List<IndexPersonDTO> notReportedToSupervisor = indexPersonService
                .getAllDTOsByNotReportedToSupervisor();

        if (!request.isUserInRole("ROLE_ADMIN")) {
            notReportedToHealthDepartment = indexPersonService
                    .filterDTOListByVisibleOrOwner(
                            notReportedToHealthDepartment,
                            authentication.getName());

            notReportedToSupervisor = indexPersonService
                    .filterDTOListByVisibleOrOwner(
                            notReportedToSupervisor,
                            authentication.getName());
        }

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
            Authentication authentication,
            HttpServletRequest request,
            Model model) {
        IndexPersonFormWrapper indexPersonFormWrapper =
                new IndexPersonFormWrapper();
        List<IndexPersonDTO> indexPersonDTOList = indexPersonService
                .getAllDTOsByNotReportedToHealthDepartment();

        if (request.isUserInRole("ROLE_ADMIN")) {
            indexPersonFormWrapper.setIndexPersonDTOList(indexPersonDTOList);
        } else {
            indexPersonFormWrapper.setIndexPersonDTOList(indexPersonService
                    .filterDTOListByVisibleOrOwner(
                            indexPersonDTOList,
                            authentication.getName()));
        }

        model.addAttribute("people", indexPersonFormWrapper);

        return "pages/indexpeople/open_indexpeople_healthdepartment";
    }


    @PostMapping("/open/healthdepartment")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isBatchFormOwner(authentication, #indexFormWrapper)")
    public String indexNotReportedToHealthDepartmentTableForm(
            Authentication authentication,
            @ModelAttribute("people") IndexPersonFormWrapper indexFormWrapper) {
        indexPersonService.updateBatchFromMultiForm(
                indexFormWrapper.getIndexPersonDTOList());

        return "redirect:/index/open/healthdepartment";
    }


    @GetMapping("/open/staff")
    public String indexNotReportedToSupervisorPage(
            Authentication authentication,
            HttpServletRequest request,
            Model model) {
        IndexPersonFormWrapper indexPersonFormWrapper =
                new IndexPersonFormWrapper();
        List<IndexPersonDTO> indexPersonDTOList = indexPersonService
                .getAllDTOsByNotReportedToSupervisor();

        if (request.isUserInRole("ROLE_ADMIN")) {
            indexPersonFormWrapper
                    .setIndexPersonDTOList(indexPersonDTOList);
        } else {
            indexPersonFormWrapper.setIndexPersonDTOList(
                    indexPersonService
                            .filterDTOListByVisibleOrOwner(
                                    indexPersonDTOList,
                                    authentication.getName()));
        }

        model.addAttribute("staff", indexPersonFormWrapper);

        return "pages/indexpeople/open_indexpeople_staff";
    }


    @PostMapping("/open/staff")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isBatchFormOwner(authentication, #indexFormWrapper)")
    public String indexNotReportedToSupervisorTableForm(
            Authentication authentication,
            @ModelAttribute("staff") IndexPersonFormWrapper indexFormWrapper) {
        indexPersonService.updateBatchFromMultiForm(
                indexFormWrapper.getIndexPersonDTOList());

        return "redirect:/index/open/staff";
    }


    @GetMapping("/open/quarantine")
    public String indexInQuarantine(
            Authentication authentication,
            HttpServletRequest request,
            Model model) {
        IndexPersonFormWrapper indexPersonFormWrapper =
                new IndexPersonFormWrapper();
        List<IndexPersonDTO> indexPersonDTOList = indexPersonService
                .getAllDTOsByQuarantineAndBooleansInverted();

        if (request.isUserInRole("ROLE_ADMIN")) {
            indexPersonFormWrapper
                    .setIndexPersonDTOList(indexPersonDTOList);
        } else {
            indexPersonFormWrapper.setIndexPersonDTOList(
                    indexPersonService
                            .filterDTOListByVisibleOrOwner(
                                    indexPersonDTOList,
                                    authentication.getName()));
        }

        model.addAttribute(
                "people",
                indexPersonFormWrapper);

        return "pages/indexpeople/open_indexpeople_quarantine";
    }


    @PostMapping("/open/quarantine")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isBatchFormOwner(authentication, #indexFormWrapper)")
    public String indexInQuarantineTableForm(
            Authentication authentication,
            @ModelAttribute("people") IndexPersonFormWrapper indexFormWrapper) {
        indexPersonService.updateQuarantineBatchFromMultiForm(
                indexFormWrapper.getIndexPersonDTOList());

        return "redirect:/index/open/quarantine";
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #id)")
    public String indexDetailPage(
            Authentication authentication,
            @PathVariable long id,
            Model model) {
        List<ReportDTO> indexReports = reportService
                .getReportDTOsByIndexId(id);
        ReportDTO firstReport = reportService
                .getFirstReportDTO(id);

        indexReports.remove(firstReport);
        model.addAttribute(
                "indexPerson", indexPersonService.getDTOById(id));
        model.addAttribute(
                "reports", indexReports);
        model.addAttribute(
                "firstReport", firstReport);
        model.addAttribute(
                "contacts",
                contactPersonService.getDTOsByIndexId(id));

        return "pages/indexpeople/show_indexperson";
    }


    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #id)")
    public String indexEditPage(
            Authentication authentication,
            @PathVariable long id,
            Model model) {
        model.addAttribute(
                "indexPerson",
                indexPersonService.getDTOById(id));

        return "pages/indexpeople/edit_indexperson";
    }


    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #id)")
    public String indexEdit(
            Authentication authentication,
            @Valid @ModelAttribute("indexPerson") IndexPersonDTO indexPersonDTO,
            BindingResult bindingResult,
            @PathVariable long id) {
        if (bindingResult.hasErrors()) {
            return "pages/indexpeople/edit_indexperson";
        }

        indexPersonService.update(indexPersonDTO, id);
        return "redirect:/index/" + id;
    }


    @GetMapping("/{id}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #id)")
    public String indexDelete(
            Authentication authentication,
            @PathVariable long id) {
        indexPersonService.remove(id);

        return "redirect:/index";
    }


    @GetMapping("/{id}/delete/confirm")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #id)")
    public String indexDeleteConfirm(
            Authentication authentication,
            @PathVariable long id,
            Model model) {
        model.addAttribute(
                "index",
                indexPersonService.getDTOById(id));

        return "pages/indexpeople/delete_indexperson";
    }


    @GetMapping("/{indexId}/contacts")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #indexId)")
    public String contactList(
            Authentication authentication,
            @PathVariable long indexId,
            @RequestParam("status") Optional<Status> status,
            Model model) {
        if (!status.isPresent()) return "redirect:/" + indexId;

        status.ifPresent(s -> {
            model.addAttribute("status", s);
            model.addAttribute(
                    "index",
                    indexPersonService.getDTOById(indexId));
            model.addAttribute(
                    "contacts",
                    contactPersonService
                            .getDTOsByIndexIdAndStatus(indexId, s));
        });

        return "pages/contacts/list_contacts";
    }
}
