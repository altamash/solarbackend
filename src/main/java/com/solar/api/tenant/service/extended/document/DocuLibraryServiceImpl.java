package com.solar.api.tenant.service.extended.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.model.attribute.PortalAttributeSAAS;
import com.solar.api.saas.model.attribute.PortalAttributeValueSAAS;
import com.solar.api.saas.repository.PortalAttributeRepository;
import com.solar.api.saas.repository.PortalAttributeValueRepository;
import com.solar.api.saas.service.StorageService;
import com.solar.api.tenant.mapper.extended.document.DocuMapper;
import com.solar.api.tenant.mapper.extended.document.DocumentDTO;
import com.solar.api.tenant.mapper.extended.document.SaveDocumentDTO;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.extended.project.ProjectLevelDoc;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.DocuLibraryRepository;
import com.solar.api.tenant.repository.UserRepository;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.contract.UserLevelPrivilegeService;
import com.solar.api.tenant.service.extended.project.ProjectLevelDocService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
//@Transactional("tenantTransactionManager")
public class DocuLibraryServiceImpl implements DocuLibraryService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private DocuLibraryRepository docuLibraryRepository;
    @Autowired
    private ProjectLevelDocService projectLevelDocService;
    @Autowired
    private StorageService storageService;
    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private Utility utility;
    @Autowired
    private PortalAttributeValueRepository portalAttributeValueRepository;
    @Autowired
    private PortalAttributeRepository portalAttributeRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;


    @Override
    public List<DocuLibrary> saveDocument(List<MultipartFile> multipartFiles, String level, String levelId, String documentName, String documentType,
                                          String codeRefType, String codeRefId, String notes, String tags, String format, String status, Boolean visibilityKey, Long compKey)
            throws StorageException, IOException, URISyntaxException {
        if (level != null) {
            return projectDocument(multipartFiles, level, levelId, documentName, documentType, notes, tags, format, status, visibilityKey, compKey);
        } else if (codeRefType != null) {
            return referenceDocument(multipartFiles, codeRefType, codeRefId, documentName, documentType, notes, tags, format, status, visibilityKey, compKey);
        }
        return null;
    }

    private List<DocuLibrary> referenceDocument(List<MultipartFile> multipartFiles, String codeRefType, String codeRefId, String documentName, String documentType,
                                                String notes, String tags, String format, String status, Boolean visibilityKey, Long compKey)
            throws URISyntaxException, IOException, StorageException {
        List<DocuLibrary> docuLibraryList = new ArrayList<>();

        User loggedInUser = userService.getLoggedInUser();
        Long acctId = loggedInUser.getAcctId();
        UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.userLevelPrivilegeByAccountId(acctId);

        for (MultipartFile multipartFile : multipartFiles) {
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());


            String mimeType = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            if (documentName == null) {
                documentName = multipartFile.getOriginalFilename() != null ?
                        multipartFile.getOriginalFilename().replaceAll("\\s", "_") : codeRefType + codeRefId + documentType + "DOC";
            } else {
                documentName = documentName + "." + mimeType;
            }

            String uri = storageService.storeInContainer(multipartFile, appProfile, "tenant/"
                            + compKey + AppConstants.PROJECT_DOCUMENT_PATH,
                    timeStamp + "-" + documentName, compKey
                    , false);
            Double fileSizeInKB = Double.valueOf(multipartFile.getSize() / 1024);
            Double fileSizeInMB = fileSizeInKB / 1024;
            DocuLibrary docuLibrary = saveOrUpdate(DocuLibrary.builder()
                    .docuName(documentName)
                    .codeRefType(codeRefType)
                    .codeRefId(codeRefId)
                    .notes(notes)
                    .tags(tags)
                    .format(format)
                    .status(status)
                    .visibilityKey(visibilityKey)
                    .uri(uri)
                    .size(fileSizeInMB + "MB")
                    .entity(userLevelPrivilege != null && userLevelPrivilege.getEntity() != null ? userLevelPrivilege.getEntity() : null)
                    .docuType(documentType)
                    .visibilityKey(true)
                    .referenceTime(timeStamp)
                    .build());
            //TODO:CodeRefTypeMap builder
            docuLibraryList.add(docuLibrary);
        }
        return docuLibraryList;
    }

    private List<DocuLibrary> projectDocument(List<MultipartFile> multipartFiles, String level, String levelId, String documentName, String documentType,
                                              String notes, String tags, String format, String status, Boolean visibilityKey, Long compKey)
            throws URISyntaxException, IOException, StorageException {
        List<DocuLibrary> docuLibraryList = new ArrayList<>();

        User loggedInUser = userService.getLoggedInUser();
        Long acctId = loggedInUser.getAcctId();
        UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.userLevelPrivilegeByAccountId(acctId);

        for (MultipartFile multipartFile : multipartFiles) {

            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());

            if (documentName == null) {
                documentName = multipartFile.getOriginalFilename() != null ?
                        multipartFile.getOriginalFilename().replaceAll("\\s", "_") : level + levelId + documentType + "DOC";
            }

            String uri = storageService.storeInContainer(multipartFile, appProfile, "tenant/"
                            + compKey + AppConstants.PROJECT_DOCUMENT_PATH,
                    timeStamp + "-" + documentName, compKey, false);
            Double fileSizeInKB = Double.valueOf(multipartFile.getSize() / 1024);
            Double fileSizeInMB = fileSizeInKB / 1024;
            DocuLibrary docuLibrary = saveOrUpdate(DocuLibrary.builder()
                    .docuName(documentName)
                    .notes(notes)
                    .tags(tags)
                    .format(format)
                    .status(status)
                    .visibilityKey(visibilityKey)
                    .uri(uri)
                    .docuType(documentType)
                    .visibilityKey(true)
                    .size(fileSizeInMB + "MB")
                    .entity(userLevelPrivilege != null && userLevelPrivilege.getEntity() != null ? userLevelPrivilege.getEntity() : null)
                    .referenceTime(timeStamp)
                    .build());
            projectLevelDocService.save(ProjectLevelDoc.builder()
                    .docId(docuLibrary.getDocuId())
                    .level(level)
                    .levelId(String.valueOf(levelId))
                    .build());
            docuLibraryList.add(docuLibrary);
        }
        return docuLibraryList;
    }

    @Override
    public DocuLibrary saveOrUpdate(DocuLibrary docuLibrary) {
        if (docuLibrary.getDocuId() != null) {
            DocuLibrary docuLibraryData = findById(docuLibrary.getDocuId());
            if (docuLibraryData == null) {
                throw new NotFoundException(CompanyPreference.class, docuLibrary.getDocuId());
            }
            docuLibraryData = DocuMapper.toUpdatedDocuLibrary(docuLibraryData,
                    docuLibrary);
            return docuLibraryRepository.save(docuLibraryData);
        }
        return docuLibraryRepository.save(docuLibrary);
    }

    @Override
    public DocuLibrary update(DocuLibrary docuLibrary) {
        return docuLibraryRepository.save(docuLibrary);
    }

    @Override
    public DocuLibrary findById(Long id) {
        return docuLibraryRepository.findById(id).orElseThrow(() -> new NotFoundException(DocuLibrary.class, id));
    }

    @Override
    public List<DocuLibrary> findByLevelAndLevelId(String level, String levelId) {

        List<DocuLibrary> docuLibraryList = new ArrayList<>();
        List<ProjectLevelDoc> projectLevelDocs = projectLevelDocService.findByLevelAndLevelId(level, levelId);

        for (ProjectLevelDoc projectLevelDoc : projectLevelDocs) {
            DocuLibrary docuLibrary = findById(projectLevelDoc.getDocId());
            if (docuLibrary.getVisibilityKey()) {
                docuLibraryList.add(docuLibrary);
            }
        }
        return docuLibraryList;
    }

    @Override
    public List<DocuLibrary> findAll() {
        return docuLibraryRepository.findAll();
    }

    @Override
    public Boolean deleteProjectDocument(Long id, Long compKey) {
        DocuLibrary docuLibrary = findById(id);
        Boolean file = moveDocumentToDeleteStorage(docuLibrary, compKey);
        if (!file) {
            return false;
        }

        //        ProjectLevelDoc projectLevelDoc = projectLevelDocService.findByDocId(id);
//        if(projectLevelDoc != null) {
//            projectLevelDocService.delete(projectLevelDoc.getId());
//        }
        docuLibrary.setVisibilityKey(false);
//        delete(id);
        saveOrUpdate(docuLibrary);
        return true;
    }

    private Boolean moveDocumentToDeleteStorage(DocuLibrary docuLibrary, Long compKey) {
        String fileName = docuLibrary.getReferenceTime() != null ?
                docuLibrary.getReferenceTime() + "-" + docuLibrary.getDocuName() : docuLibrary.getDocuName();
        File file = storageService.getBlob(appProfile,
                "tenant/" + compKey + AppConstants.PROJECT_DOCUMENT_PATH, fileName);
        if (file == null) {
            return false;
        }
        try {
            MultipartFile multipartFile = storageService.convertFileToMultipart(file);
            storageService.storeInContainer(multipartFile, appProfile, "tenant/"
                            + compKey + AppConstants.PROJECT_DELETED_DOCUMENT_PATH,
                    fileName, compKey
                    , false);
            storageService.deleteBlob(appProfile, fileName, compKey, AppConstants.PROJECT_DOCUMENT_PATH);
        } catch (StorageException | URISyntaxException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public void delete(Long id) {
        docuLibraryRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        docuLibraryRepository.deleteAll();
    }

    @Override
    public List<DocuLibrary> saveSliderResources(List<MultipartFile> multipartFiles, boolean isDefault, Long resourceInterval, String status)
            throws URISyntaxException, IOException, StorageException {
        List<DocuLibrary> docuLibraryList = new ArrayList<>();
        String uri = "";
        for (MultipartFile multipartFile : multipartFiles) {
            DocuLibrary docuLibrary = new DocuLibrary();
            AtomicInteger counter = new AtomicInteger(0);
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
            String fileNameWithOutExt = FilenameUtils.removeExtension(multipartFile.getOriginalFilename());
            String fileExt = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            if (isDefault) {
                //  delete previous files
                uri = storageService.storeInContainer(multipartFile, AppConstants.DEV_PUBLIC_CONTAINER, "carousel/default",
                        timeStamp + "-" + fileNameWithOutExt + "." + fileExt, utility.getCompKey()
                        , true);
                PortalAttributeValueSAAS portalAttributeValueSAAS = new PortalAttributeValueSAAS();
                portalAttributeValueSAAS.setAttributeValue(uri);
                portalAttributeValueSAAS.setDescription(multipartFile.getOriginalFilename());
                portalAttributeValueSAAS.setSequenceNumber(counter.incrementAndGet());
                portalAttributeValueSAAS.setResourceInterval(resourceInterval);
                portalAttributeValueSAAS.setStatus(status);
                PortalAttributeSAAS portalAttributeSAAS = portalAttributeRepository.findByName("Carousel")
                        .orElseThrow(() ->
                                new NotFoundException(PortalAttributeSAAS.class, (Long) null));
                portalAttributeValueSAAS.setAttribute(portalAttributeSAAS);
                portalAttributeValueSAAS = portalAttributeValueRepository.save(portalAttributeValueSAAS);
                docuLibrary.setDocuId(portalAttributeValueSAAS.getId());
                docuLibrary.setDocuName(portalAttributeValueSAAS.getDescription());
                docuLibrary.setUri(portalAttributeValueSAAS.getAttributeValue());
                docuLibrary.setResourceInterval(portalAttributeValueSAAS.getResourceInterval());
                docuLibrary.setStatus(portalAttributeValueSAAS.getStatus());
                docuLibrary.setReferenceTime(timeStamp);
            } else {
                //  delete previous files
                uri = storageService.storeInContainer(multipartFile, AppConstants.DEV_PUBLIC_CONTAINER, "carousel/" + utility.getCompKey(),
                        fileNameWithOutExt + "_" + timeStamp + "." + fileExt, utility.getCompKey()
                        , true);
                docuLibrary.setDocuName(multipartFile.getName());
                docuLibrary.setUri(uri);
                docuLibrary.setDocuType(multipartFile.getContentType());
                docuLibrary.setVisibilityKey(true);
                docuLibrary.setCompKey(utility.getCompKey());
                docuLibrary.setResourceInterval(resourceInterval);
                docuLibrary.setStatus(status);
                docuLibrary.setReferenceTime(timeStamp);
                docuLibrary = docuLibraryRepository.save(docuLibrary);
            }
            docuLibraryList.add(docuLibrary);
        }
        return docuLibraryList;
    }

    @Override
    public List<DocuLibrary> findAllSliderResources(Long compKey) {
        List<DocuLibrary> docuLibraryList = new ArrayList<>();
        if (compKey != null) {
            docuLibraryList = docuLibraryRepository.findAllByCompKey(compKey);
        } else {
            PortalAttributeSAAS portalAttributeSAAS = portalAttributeRepository.findByName("Carousel")
                    .orElseThrow(() ->
                            new NotFoundException(PortalAttributeSAAS.class, (Long) null));
            List<PortalAttributeValueSAAS> portalAttributeValueSAASList = portalAttributeValueRepository.findByAttribute(portalAttributeSAAS);
            if (!CollectionUtils.isEmpty(portalAttributeValueSAASList)) {
                for (PortalAttributeValueSAAS portalAttributeValueSAAS : portalAttributeValueSAASList) {
                    DocuLibrary docuLibrary = new DocuLibrary();
                    docuLibrary.setUri(portalAttributeValueSAAS.getAttributeValue());
                    docuLibraryList.add(docuLibrary);
                }
            }
        }
        return docuLibraryList;
    }

    @Override
    public List<DocuLibrary> findByCodeRefId(String codeRefId) {
        return docuLibraryRepository.findAllByCodeRefId(codeRefId);//docuLibraryRepository.findAllByCodeRefIdAndCodeRefTypeAndVisibilityKey(codeRefId, "ORG_MNG", true);
    }

    @Override
    public List<DocuLibrary> saveDocumentForOrg(String multiPartsFileDto, String codeRefId, Long compKey, String codeRefType) throws URISyntaxException, IOException, StorageException {

        List<DocuLibrary> docuLibraryList = new ArrayList<>();
        MultipartFile multipartFile = null;
        List<SaveDocumentDTO> docuDtoList = null;

        try {
            docuDtoList = Arrays.asList(new ObjectMapper().readValue(multiPartsFileDto, SaveDocumentDTO[].class));
            for (SaveDocumentDTO docuDto : docuDtoList) {

                multipartFile = convertFileBase64ToMultipart(docuDto.getFileBase64(), docuDto.getName());
                String documentName = docuDto.getName();

                if (documentName == null || documentName.isEmpty()) {

                    documentName = multipartFile.getOriginalFilename() != null ?
                            multipartFile.getOriginalFilename().replaceAll("\\s", "_") : codeRefType + codeRefId + docuDto.getType() + "DOC";
                }
                String uri = storageService.storeInContainer(multipartFile, appProfile, "tenant/"
                                + compKey + AppConstants.PROJECT_DOCUMENT_PATH,
                        documentName, compKey
                        , false);
                long fileSizeInKB = multipartFile.getSize() / 1024;
                long fileSizeInMB = fileSizeInKB / 1024;
                DocuLibrary docuLibrary = saveOrUpdate(DocuLibrary.builder()
                        .docuName(documentName)
                        .codeRefType(codeRefType)
                        .codeRefId(codeRefId)
                        .visibilityKey(true)
                        .uri(uri)
                        .size(fileSizeInMB + "MB")
                        .docuType(docuDto.getType())
                        .visibilityKey(true)
                        .build());

                docuLibraryList.add(docuLibrary);
            }
        } catch (Exception ex) {
            throw new IOException("error,cannot save document ");
        }

        return docuLibraryList;
    }


    private MultipartFile convertFileBase64ToMultipart(String base64String, String fileName) {
        MockMultipartFile mockMultipartFile = null;
        try {
            byte[] decoderBytes = Base64.getDecoder().decode(base64String);
            mockMultipartFile = new MockMultipartFile(fileName, decoderBytes);
        } catch (Exception ex) {
//            LOGGER.error(ex.getMessage());
            throw new IllegalArgumentException("ILegalBase64" + fileName);
        }
        return mockMultipartFile;
    }

    @Override
    public List<DocuLibrary> findByCodeRefIdAndCodeRefType(String codeRefId, String codeRefType) {
        return docuLibraryRepository.findAllByCodeRefIdAndCodeRefTypeAndVisibilityKey(codeRefId, codeRefType, true);
    }

    @Override
    public List<DocuLibrary> findByCodeRefTypeAndNotes(String codeRefType, String notes) {
        return docuLibraryRepository.findAllByCodeRefTypeAndNotesAndVisibilityKey(codeRefType, notes, true);
    }

    @Override
    public DocuLibrary findByCodeRefIdAndCodeRefTypeAndNotes(String codeRefId, String codeRefType, String notes) {
        return docuLibraryRepository.findByCodeRefIdAndCodeRefTypeAndAndNotesAndVisibilityKey(codeRefId, codeRefType, notes, true);
    }

    @Override
    public void saveAll(List<DocuLibrary> docuLibraries) {
        docuLibraryRepository.saveAll(docuLibraries);
    }

    @Override
    public List<DocumentDTO> findAllDocumentDTOByCodeRefIdAndCodeRefType(String codeRefId, String codeRefType) {
        return docuLibraryRepository.findAllDocumentDTOByCodeRefIdAndCodeRefType(codeRefId, codeRefType);
    }
}
