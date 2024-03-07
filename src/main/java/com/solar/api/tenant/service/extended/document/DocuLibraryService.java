package com.solar.api.tenant.service.extended.document;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.tenant.mapper.extended.document.DocumentDTO;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface DocuLibraryService {

    //    file, level, levelId, docuName, docuType, codeRefType, codeRefId, notes, tags, format, status, visibilityKey
    List<DocuLibrary> saveDocument(List<MultipartFile> multipartFiles, String level, String levelId, String documentName, String documentType,
                                   String codeRefType, String codeRefId, String notes, String tags, String format, String status, Boolean visibilityKey, Long compKey)
            throws StorageException, IOException, URISyntaxException;

    DocuLibrary saveOrUpdate(DocuLibrary docuLibrary);

    DocuLibrary update(DocuLibrary docuLibrary);

    DocuLibrary findById(Long id);

    List<DocuLibrary> findByLevelAndLevelId(String level, String levelId);

    List<DocuLibrary> findAll();

    Boolean deleteProjectDocument(Long id, Long compKey);

    void delete(Long id);

    void deleteAll();

    List<DocuLibrary> saveSliderResources(List<MultipartFile> multipartFiles, boolean isDefault, Long interval, String status)
            throws URISyntaxException, IOException, StorageException;

    List<DocuLibrary> findAllSliderResources(Long compKey);

    List<DocuLibrary> findByCodeRefId(String codeRefId);

    List<DocuLibrary> saveDocumentForOrg(String multiPartsFileDto,
                                         String codeRefId, Long compKey, String codeRefType) throws URISyntaxException, IOException, StorageException;

    List<DocuLibrary> findByCodeRefIdAndCodeRefType(String codeRefId, String codeRefType);

    List<DocuLibrary> findByCodeRefTypeAndNotes(String codeRefType, String notes);

    DocuLibrary findByCodeRefIdAndCodeRefTypeAndNotes(String codeRefId, String codeRefType, String notes);
    void saveAll(List<DocuLibrary> docuLibraries);

    List<DocumentDTO> findAllDocumentDTOByCodeRefIdAndCodeRefType(String codeRefId, String codeRefType);
}
