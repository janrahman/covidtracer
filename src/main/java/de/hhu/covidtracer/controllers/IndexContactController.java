package de.hhu.covidtracer.controllers;

import de.hhu.covidtracer.dtos.ContactPersonDTO;
import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.models.ContactPerson;
import de.hhu.covidtracer.services.ContactPersonService;
import de.hhu.covidtracer.services.IndexContactService;
import de.hhu.covidtracer.services.IndexPersonService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.Collections;

@Controller
public class IndexContactController {
    private final IndexContactService indexContactService;
    private final IndexPersonService indexPersonService;
    private final ContactPersonService contactPersonService;

    public IndexContactController(
            IndexContactService indexContactService,
            IndexPersonService indexPersonService,
            ContactPersonService contactPersonService) {
        this.indexContactService = indexContactService;
        this.indexPersonService = indexPersonService;
        this.contactPersonService = contactPersonService;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #indexId)")
    @GetMapping("/index/{indexId}/contacts/{contactId}/edit")
    public String indexContactEditPage(
            @PathVariable long indexId,
            @PathVariable long contactId,
            Model model) {
        model.addAttribute(
                "index",
                indexPersonService.getDTOById(indexId));
        model.addAttribute(
                "contact",
                contactPersonService
                        .getDTOByIndexContactId(indexId, contactId));
        return "pages/index_contacts/edit_indexcontact";
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #indexId)")
    @PostMapping("/index/{indexId}/contacts/{contactId}/edit")
    public String indexContactEditPage(
            @PathVariable long indexId,
            @PathVariable long contactId,
            @ModelAttribute("index") IndexPersonDTO indexPersonDTO,
            @ModelAttribute("contact")ContactPersonDTO contactPersonDTO,
            BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            BeanUtils.copyProperties(
                    indexPersonService.getDTOById(indexId),
                    indexPersonDTO);
            BeanUtils.copyProperties(
                    contactPersonService
                            .getDTOByIndexContactId(indexId, contactId),
                    contactPersonDTO);
            return "pages/index_contacts/edit_indexcontact";
        }

        indexContactService.update(
                indexId,
                contactId,
                contactPersonDTO.getDatePeriodStart(),
                contactPersonDTO.getDatePeriodEnd());

        return "redirect:/index/" + indexId;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #indexId)")
    @GetMapping("/index/{indexId}/contacts/{contactId}/delete")
    public String indexContactDeletePage(
            @PathVariable long indexId,
            @PathVariable long contactId) {
        indexContactService.remove(indexId, contactId);
        contactPersonService
                .removeOrphans(Collections.singletonList(contactId));

        return "redirect:/index/" + indexId;
    }
}
