package de.hhu.covidtracer.controllers;

import de.hhu.covidtracer.dtos.ContactPersonDTO;
import de.hhu.covidtracer.dtos.wrappers.StationListFormWrapper;
import de.hhu.covidtracer.services.ContactPersonService;
import de.hhu.covidtracer.services.IndexContactService;
import de.hhu.covidtracer.services.ReportService;
import de.hhu.covidtracer.services.StationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

@Controller
@RequestMapping("/contacts")
@Slf4j
public class ContactPersonController {
    private final ContactPersonService contactPersonService;
    private final ReportService reportService;
    private final StationService stationService;
    private final IndexContactService indexContactService;

    @Autowired
    public ContactPersonController(
            ContactPersonService contactPersonService,
            ReportService reportService,
            StationService stationService,
            IndexContactService indexContactService) {
        this.contactPersonService = contactPersonService;
        this.reportService = reportService;
        this.stationService = stationService;
        this.indexContactService = indexContactService;
    }


    @GetMapping("/{contactId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isContactOwner(authentication, #contactId)")
    public String contactPersonDetailPage(
            Authentication authentication,
            @PathVariable long contactId,
            Model model) {
        model.addAttribute(
                "contact",
                contactPersonService
                        .getDTOById(contactId));
        model.addAttribute(
                "indexContacts",
                indexContactService.getDTOsFromContactPersonId(contactId));
        model.addAttribute(
                "reports",
                reportService.getReportsByContactPersonId(contactId));

        return "pages/contacts/show_contact";
    }


    @GetMapping("/{contactId}/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isContactOwner(authentication, #contactId)")
    public String contactPersonEditPage(
            Authentication authentication,
            @PathVariable long contactId,
            Model model) {

        model.addAttribute(
                "contact",
                contactPersonService.getDTOById(contactId));
        model.addAttribute(
                "stations",
                StationListFormWrapper.builder()
                        .stationDTOList(stationService.getAllDTOs())
                        .build());

        return "pages/contacts/edit_contact";
    }


    @PostMapping("/{contactId}/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isContactOwner(authentication, #contactId)")
    public String contactPersonEdit(
            Authentication authentication,
            @PathVariable long contactId,
            @ModelAttribute("stations") StationListFormWrapper stationListFormWrapper,
            @Valid @ModelAttribute("contact") ContactPersonDTO contactPersonDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StationListFormWrapper copy = StationListFormWrapper.builder()
                    .stationDTOList(stationService.getAllDTOs())
                    .build();
            BeanUtils.copyProperties(
                    copy,
                    stationListFormWrapper);

            return "pages/contacts/edit_contact";
        }

        contactPersonService.update(contactId, contactPersonDTO);

        if (contactPersonDTO.getStation() != null) {
            stationService.updateStationFromDTOAndContactId(
                    contactPersonDTO.getStation(),
                    contactId);
        }

        return "redirect:/contacts/" + contactId;
    }


    @GetMapping("/{contactId}/delete/confirm")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isContactOwner(authentication, #contactId)")
    public String contactPersonDeleteConfirm(
            Authentication authentication,
            @PathVariable long contactId,
            Model model) {
        model.addAttribute(
                "contact",
                contactPersonService.getDTOById(contactId));

        return "pages/contacts/delete_contact";
    }


    @GetMapping("/{contactId}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isContactOwner(authentication, #contactId)")
    public String contactPersonDelete(
            Authentication authentication,
            @PathVariable long contactId) {
        contactPersonService.removeOrphans(
                Collections.singletonList(contactId));

        return "redirect:/";
    }
}
