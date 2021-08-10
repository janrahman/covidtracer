package de.hhu.covidtracer.security;

import de.hhu.covidtracer.dtos.IndexPersonDTO;
import de.hhu.covidtracer.dtos.wrappers.IndexPersonFormWrapper;
import de.hhu.covidtracer.services.IndexContactService;
import de.hhu.covidtracer.services.IndexPersonService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("userSecurity")
public class UserSecurity {
    private final IndexPersonService indexPersonService;
    private final IndexContactService indexContactService;

    public UserSecurity(
            IndexPersonService indexPersonService,
            IndexContactService indexContact) {
        this.indexPersonService = indexPersonService;
        this.indexContactService = indexContact;
    }


    public boolean isOwner(
            Authentication authentication,
            long indexId) {
        IndexPersonDTO indexPersonDTO = indexPersonService
                .getDTOById(indexId);

        return authentication
                .getName()
                .equals(indexPersonDTO.getOwner()) ||
                indexPersonDTO.isVisible();
    }


    public boolean isBatchFormOwner(
            Authentication authentication,
            IndexPersonFormWrapper indexPersonFormWrapper) {
        for (IndexPersonDTO indexPersonDTO : indexPersonFormWrapper
                .getIndexPersonDTOList()) {
            if (!indexPersonDTO.isVisible() && !authentication
                    .getName()
                    .equals(indexPersonDTO.getOwner())) {
                return false;
            }
        }

        return true;
    }


    public boolean isContactOwner(
            Authentication authentication,
            long contactId) {
        List<Long> contactOwnersIds = indexContactService
                .getIndexIdsFromContactId(contactId);

        return contactOwnersIds.stream()
                .anyMatch(indexId -> isOwner(authentication, indexId));
    }
}
