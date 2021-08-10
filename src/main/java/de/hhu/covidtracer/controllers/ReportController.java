package de.hhu.covidtracer.controllers;

import de.hhu.covidtracer.dtos.ContactPersonDTO;
import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.dtos.ReportDTO;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.models.Report;
import de.hhu.covidtracer.services.ContactPersonService;
import de.hhu.covidtracer.services.IndexPersonService;
import de.hhu.covidtracer.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Controller
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reportService;
    private final IndexPersonService indexPersonService;
    private final ContactPersonService contactPersonService;

    @Autowired
    public ReportController(
            ReportService reportService,
            IndexPersonService indexPersonService,
            ContactPersonService contactPersonService) {
        this.reportService = reportService;
        this.indexPersonService = indexPersonService;
        this.contactPersonService = contactPersonService;
    }


    @GetMapping("/index/{indexId}/new")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #indexId)")
    public String reportAddNewIndexPage(
            Authentication authentication,
            @PathVariable long indexId,
            Model model) {
        model.addAttribute(
                "person",
                indexPersonService.getDTOById(indexId));
        model.addAttribute(
                "reportForm",
                new ReportDTO());

        return "pages/reports/add_report";
    }


    @PostMapping("/index/{indexId}/new")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #indexId)")
    public String reportAddNewIndex(
            Authentication authentication,
            @PathVariable long indexId,
            @ModelAttribute("person") IndexPersonDTO person,
            @Valid @ModelAttribute("reportForm") ReportDTO reportForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "pages/reports/add_report";
        reportForm.setId(0L);
        Report report = reportService.toReport(reportForm);

        indexPersonService.saveReport(indexId, report);

        return "redirect:/index/" + indexId;
    }


    @GetMapping("/{id}/index/{indexId}/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #indexId)")
    public String reportEditIndexPage(
            Authentication authentication,
            @PathVariable long indexId,
            @PathVariable long id,
            Model model) {
        model.addAttribute(
                "index",
                indexPersonService.getDTOById(indexId));
        model.addAttribute(
                "reportForm",
                reportService.getDTOById(id));

        return "pages/reports/edit_report";
    }


    @PostMapping("/{id}/index/{indexId}/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #indexId)")
    public String reportEditIndex(
            Authentication authentication,
            @PathVariable long indexId,
            @PathVariable long id,
            @Valid @ModelAttribute("reportForm") ReportDTO reportForm,
            @ModelAttribute("index") IndexPersonDTO indexPersonDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "pages/reports/edit_report";

        reportForm.setId(id);
        reportService.update(reportForm);

        return "redirect:/index/" + indexId;
    }


    @GetMapping("/{id}/index/{indexId}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #indexId)")
    public String reportDeleteIndex(
            Authentication authentication,
            @PathVariable long indexId,
            @PathVariable long id) {
        IndexPerson indexPerson = indexPersonService.getById(indexId);

        reportService.deleteExceptFirstReportFromIndex(indexPerson, id);

        return "redirect:/index/" + indexId;
    }


    @GetMapping("/{id}/index/{indexId}/delete/confirm")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #indexId)")
    public String reportDeleteIndexConfirm(
            Authentication authentication,
            @PathVariable long indexId,
            @PathVariable long id,
            Model model) {
        Report report = reportService.getById(id);

        model.addAttribute("report", report);
        model.addAttribute("indexId", indexId);

        return "pages/reports/delete_report";
    }


    @GetMapping("/contacts/{contactId}/new")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isContactOwner(authentication, #contactId)")
    public String reportAddNewContactPage(
            Authentication authentication,
            @PathVariable long contactId,
            Model model) {
        if (contactPersonService.isVisitor(contactId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Visitors cannot have any reports");
        }

        model.addAttribute(
                "contact",
                contactPersonService.getDTOById(contactId));
        model.addAttribute(
                "reportForm",
                new ReportDTO());

        return "pages/reports/add_report_contact";
    }


    @PostMapping("/contacts/{contactId}/new")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isContactOwner(authentication, #contactId)")
    public String reportAddNewContact(
            Authentication authentication,
            @PathVariable long contactId,
            @ModelAttribute("contact")ContactPersonDTO contactPersonDTO,
            @ModelAttribute("reportForm") ReportDTO reportDTO) {
        if (contactPersonService.isVisitor(contactId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Visitors cannot have any reports");
        }
        reportDTO.setId(0L);

        Report report = reportService.toReport(reportDTO);

        contactPersonService.saveReport(report, contactId);

        return "redirect:/contacts/" + contactId;
    }


    @GetMapping("/{id}/contacts/{contactId}/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isContactOwner(authentication, #contactId)")
    public String reportEditContactPage(
            Authentication authentication,
            @PathVariable long contactId,
            @PathVariable long id,
            Model model) {
        if (contactPersonService.isVisitor(contactId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Visitors cannot have any reports");
        }

        model.addAttribute(
                "contact",
                contactPersonService.getDTOById(contactId));
        model.addAttribute(
                "reportForm",
                reportService.getDTOById(id));

        return "/pages/reports/edit_report_contact";
    }


    @PostMapping("/{id}/contacts/{contactId}/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isContactOwner(authentication, #contactId)")
    public String reportEditContact(
            Authentication authentication,
            @PathVariable long contactId,
            @PathVariable long id,
            @ModelAttribute("contact") ContactPersonDTO contactPersonDTO,
            @Valid @ModelAttribute("reportForm") ReportDTO reportDTO,
            BindingResult bindingResult) {
        if (contactPersonService.isVisitor(contactId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Visitors cannot have any reports");
        }
        if (bindingResult.hasErrors()) return "/pages/reports/edit_report_contact";

        reportDTO.setId(id);
        reportService.update(reportDTO);

        return "redirect:/contacts/" + contactId;
    }


    @GetMapping("/{id}/contacts/{contactId}/delete/confirm")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isContactOwner(authentication, #contactId)")
    public String deleteReportContactConfirm(
            Authentication authentication,
            @PathVariable long contactId,
            @PathVariable long id,
            Model model) {
        if (contactPersonService.isVisitor(contactId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Visitors cannot have any reports");
        }

        model.addAttribute("contact", contactPersonService.getDTOById(contactId));
        model.addAttribute("report", reportService.getById(id));

        return "/pages/reports/delete_report_contact";
    }


    @GetMapping("/{id}/contacts/{contactId}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isContactOwner(authentication, #contactId)")
    public String deleteReportContact(
            Authentication authentication,
            @PathVariable long contactId,
            @PathVariable long id) {
        if (contactPersonService.isVisitor(contactId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Visitors cannot have any reports");
        }

        ContactPerson contactPerson = contactPersonService.getById(contactId);

        reportService.deleteFromContact(contactPerson, id);

        return "redirect:/contacts/" + contactId;
    }
}
