package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.IndexContactDTO;
import de.hhu.covidtracer.dtos.IndexPersonDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface ExcelDownloadService {
    void setResponseHeader(
            HttpServletResponse response,
            IndexPersonDTO indexPersonDTO);

    void export(
            HttpServletResponse response,
            IndexPersonDTO indexPersonDTO,
            List<IndexContactDTO> indexContactDTOList,
            String password) throws IOException, GeneralSecurityException;
}
