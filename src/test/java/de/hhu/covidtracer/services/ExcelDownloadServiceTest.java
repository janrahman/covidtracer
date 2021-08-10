package de.hhu.covidtracer.services;

import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.helper.ContactPersonTestHelper;
import de.hhu.covidtracer.helper.IndexPersonTestHelper;
import de.hhu.covidtracer.services.implementations.ExcelDownloadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletResponse;

import static de.hhu.covidtracer.models.Status.STAFF;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ExcelDownloadServiceTest {
    private static final IndexPersonTestHelper INDEX_PERSON_TEST_HELPER =
            new IndexPersonTestHelper();
    private static final ContactPersonTestHelper CONTACT_PERSON_TEST_HELPER =
            new ContactPersonTestHelper();

    @Mock
    HttpServletResponse httpServletResponse;

    private ExcelDownloadService excelDownloadService;

    @BeforeEach
    public void setup() {
        excelDownloadService = new ExcelDownloadServiceImpl();
    }


    @Nested
    public class SetResponseHeader {
        @Test
        public void shouldSetInvalidNameHeader() {
            excelDownloadService.setResponseHeader(
                    httpServletResponse,
                    new IndexPersonDTO());

            verify(httpServletResponse).setContentType(
                    "application/octet-stream");
            verify(httpServletResponse).setHeader(
                    anyString(),
                    matches(".*" + "empty" + ".*"));
        }


        @Test
        public void shouldSetResponseHeader() {
            IndexPersonDTO indexPersonDTO = INDEX_PERSON_TEST_HELPER
                    .getDTO(1L, STAFF);
            excelDownloadService.setResponseHeader(
                    httpServletResponse,
                    indexPersonDTO);

            verify(httpServletResponse).setContentType(
                    eq("application/octet-stream"));
            verify(httpServletResponse).setHeader(
                    eq("Content-Disposition"),
                    matches(".*" +
                            indexPersonDTO.getFirstName().toLowerCase() +
                            ".*" +
                            indexPersonDTO.getName().toLowerCase() +
                            ".*"));
        }
    }

}
