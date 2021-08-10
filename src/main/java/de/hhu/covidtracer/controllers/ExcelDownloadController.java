package de.hhu.covidtracer.controllers;

import de.hhu.covidtracer.dtos.IndexContactDTO;
import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.services.ExcelDownloadService;
import de.hhu.covidtracer.services.IndexContactService;
import de.hhu.covidtracer.services.IndexPersonService;
import de.hhu.covidtracer.services.implementations.ExcelDownloadServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Controller
@RequestMapping("/export")
public class ExcelDownloadController {
    private final IndexPersonService indexPersonService;
    private final IndexContactService indexContactService;

    @Value("${custom.config.excel.export.password}")
    private String password;

    @Autowired
    public ExcelDownloadController(
            IndexPersonService indexPersonService,
            IndexContactService indexContactService) {
        this.indexPersonService = indexPersonService;
        this.indexContactService = indexContactService;
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isOwner(authentication, #id)")
    public void exportContactsToExcel(
            Authentication authentication,
            @PathVariable long id,
            HttpServletResponse response)
            throws IOException, GeneralSecurityException {
        IndexPersonDTO indexPersonDTO = indexPersonService.getDTOById(id);
        List<IndexContactDTO> indexContactDTOList = indexContactService
                .getDTOsFromIndexId(id);
        ExcelDownloadService excelDownloadService = new ExcelDownloadServiceImpl();

        excelDownloadService.setResponseHeader(response, indexPersonDTO);
        excelDownloadService.export(
                response,
                indexPersonDTO,
                indexContactDTOList,
                password);
    }
}
