package com.solar.api.tenant.service.contract.lookup;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.repository.CodeTypeRefMapRepository;
import com.solar.api.tenant.repository.DocuLibraryRepository;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Getter
@Component
public class AttachmentFactory implements ApplicationContextAware, CodeTypeRefMap {
    private ApplicationContext applicationContext;

    @Autowired
    private CodeTypeRefMapRepository codeTypeRefMapRepository;

    @Autowired
    private Utility utility;

    @Autowired
    private DocuLibraryRepository docuLibraryRepository;

    public String getRefTable(String refCode) {
        com.solar.api.tenant.model.extended.CodeTypeRefMap codeTypeRefMap = codeTypeRefMapRepository.findByRefCode(refCode);
        return codeTypeRefMap.getRefTable();
    }

    @Override
    public Object doPostAttachment(String refCode, List<MultipartFile> multipartFiles, String directoryString,
                                   String filePath, String codeRefType, String codeRefId, Object o)
            throws URISyntaxException, IOException, StorageException {
        switch (getRefTable(refCode)) {
            case "DOCU_LIBRARY": {
                List<DocuLibrary> docuLibraries = utility.uploadAndSaveFiles(multipartFiles, directoryString, filePath, codeRefType, codeRefId);
                for (DocuLibrary docuLibrary : docuLibraries) {
                    docuLibraryRepository.findById(docuLibrary.getDocuId()).orElseThrow(() -> new NotFoundException(DocuLibrary.class, docuLibrary.getDocuId()));
                    if (o instanceof Organization) {
                        docuLibrary.setOrganization((Organization) o);
                    }
                    if (o instanceof Entity) {
                        docuLibrary.setEntity((Entity) o);
                    }
                    if (o instanceof Contract) {
                        docuLibrary.setContract((Contract) o);
                    }
                    docuLibraryRepository.save(docuLibrary);
                }
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected value: " + getRefTable(refCode));
            }
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
