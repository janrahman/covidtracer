package de.hhu.covidtracer.services;

import de.hhu.covidtracer.exceptions.UploadException;
import de.hhu.covidtracer.models.IndexPerson;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelUploadService {
    IndexPerson importFromExcel(MultipartFile file, String owner) throws IOException, UploadException;
}
