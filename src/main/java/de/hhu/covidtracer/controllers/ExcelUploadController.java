package de.hhu.covidtracer.controllers;

import de.hhu.covidtracer.exceptions.UploadException;
import de.hhu.covidtracer.models.IndexPerson;
import de.hhu.covidtracer.services.ExcelUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/import")
@EnableTransactionManagement
public class ExcelUploadController {
    private final ExcelUploadService excelUploadService;

    @Autowired
    public ExcelUploadController(
            ExcelUploadService excelUploadService) {
        this.excelUploadService = excelUploadService;
    }


    @PostMapping
    public String importExcel(
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes)
            throws IOException, UploadException {
        String owner = Optional
                .ofNullable(authentication)
                .map(Authentication::getName)
                .orElse("admin");

        IndexPerson uploadedIndex = excelUploadService.importFromExcel(file, owner);

        redirectAttributes.addFlashAttribute(
                "messageSuccess",
                "You successfully uploaded " + StringUtils
                        .cleanPath(Objects
                                .requireNonNull(
                                        file.getOriginalFilename())) + ".");

        return "redirect:/index/" + uploadedIndex.getId() + "/validate";
    }
}
