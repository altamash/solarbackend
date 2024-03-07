package com.solar.api.tenant.controller.v1.extended;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.tenant.mapper.extended.document.DocuHistoryDTO;
import com.solar.api.tenant.mapper.extended.document.DocuLibraryDTO;
import com.solar.api.tenant.mapper.extended.document.DocumentDTO;
import com.solar.api.tenant.model.extended.document.DocuHistory;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.repository.DocuHistoryRepository;
import com.solar.api.tenant.repository.DocuLibraryRepository;
import com.solar.api.tenant.service.extended.document.DocuHistoryService;
import com.solar.api.tenant.service.extended.document.DocuLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.solar.api.helper.Utility;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.solar.api.tenant.mapper.extended.document.DocuMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("DocumentController")
@RequestMapping(value = "/document")
public class DocumentController {

    @Autowired
    private DocuHistoryService docuHistoryService;
    @Autowired
    private DocuLibraryService docuLibraryService;
    @Autowired
    private DocuHistoryRepository docuHistoryRepository;
    @Autowired
    private DocuLibraryRepository docuLibraryRepository;
    @Autowired
    private MasterTenantService masterTenantService;
    @Autowired
    private Utility utility;


    // DocuHistory ////////////////////////////////////////
    @PostMapping("/history")
    public DocuHistoryDTO addDocuHistory(@RequestBody DocuHistoryDTO docuHistoryDTO) {
        return toDocuHistoryDTO(docuHistoryService.save(toDocuHistory(docuHistoryDTO)));
    }

    @PutMapping("/history")
    public DocuHistoryDTO updateDocuHistory(@RequestBody DocuHistoryDTO docuHistoryDTO) {
        DocuHistory docuHistory = docuHistoryRepository.findById(docuHistoryDTO.getDocuId()).orElse(null);
        return toDocuHistoryDTO(docuHistory == null ? docuHistory :
                docuHistoryService.save(toUpdatedDocuHistory(docuHistory, toDocuHistory(docuHistoryDTO))));
    }

    @GetMapping("/history/{id}")
    public DocuHistoryDTO findDocuHistoryById(@PathVariable Long id) {
        return toDocuHistoryDTO(docuHistoryService.findById(id));
    }

    @GetMapping("/history")
    public List<DocuHistoryDTO> findAllDocuHistories() {
        return toDocuHistoryDTOs(docuHistoryService.findAll());
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity deleteDocuHistory(@PathVariable Long id) {
        docuHistoryService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/history")
    public ResponseEntity deleteAllDocuHistories() {
        docuHistoryService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // DocuLibrary ////////////////////////////////////////
    @PostMapping("/library")
    public List<DocuLibraryDTO> addDocumentLibrary(@RequestParam(value = "file") List<MultipartFile> file,
                                                   @RequestParam(value = "level", required = false) String level,
                                                   @RequestParam(value = "levelId", required = false) String levelId,
                                                   @RequestParam(value = "docuName", required = false) String docuName,
                                                   @RequestParam(value = "docuType") String docuType,
                                                   @RequestParam(value = "codeRefType", required = false) String codeRefType,
                                                   @RequestParam(value = "codeRefId", required = false) Long codeRefId,
                                                   @RequestParam(value = "notes", required = false) String notes,
                                                   @RequestParam(value = "tags", required = false) String tags,
                                                   @RequestParam(value = "format", required = false) String format,
                                                   @RequestParam(value = "status", required = false) String status,
                                                   @RequestParam(value = "visibilityKey", required = false) Boolean visibilityKey,
                                                   @RequestHeader("Comp-Key") Long compKey) throws StorageException, IOException, URISyntaxException {
        return toDocuLibraryDTOs(docuLibraryService.saveDocument(file, level, levelId, docuName, docuType, codeRefType, String.valueOf(codeRefId), notes, tags, format,
                status, visibilityKey, compKey));
    }

    @PutMapping("/library")
    public DocuLibraryDTO updateDocuLibrary(@RequestBody DocuLibraryDTO docuLibraryDTO) {
        DocuLibrary docuLibrary = docuLibraryRepository.findById(docuLibraryDTO.getDocuId()).orElse(null);
        return toDocuLibraryDTO(docuLibrary == null ? docuLibrary :
                docuLibraryService.saveOrUpdate(toUpdatedDocuLibrary(docuLibrary, toDocuLibrary(docuLibraryDTO))));
    }

    @GetMapping("/library/{id}")
    public DocuLibraryDTO findDocuLibraryById(@PathVariable Long id) {
        DocuLibrary docuLibrary = docuLibraryService.findById(id);
        if (docuLibrary.getVisibilityKey()) {
            return toDocuLibraryDTO(docuLibrary);
        }
        throw new NotFoundException(DocuLibrary.class, id);
    }

    @GetMapping("/library/findDocumentsByLevel/{level}/{levelId}")
    public List<DocuLibraryDTO> findDocumentsByLevel(@PathVariable String level, @PathVariable String levelId) {
        return toDocuLibraryDTOs(docuLibraryService.findByLevelAndLevelId(level, levelId));
    }

    @GetMapping("/library")
    public List<DocuLibraryDTO> findAllDocuLibraries() {
        return toDocuLibraryDTOs(docuLibraryService.findAll());
    }


    @DeleteMapping("/library/{id}")
    public ResponseEntity deleteDocuLibrary(@RequestHeader("Comp-Key") Long compKey, @PathVariable Long id) {
        Boolean response = docuLibraryService.deleteProjectDocument(id, compKey);
        if (!response) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/library")
    public ResponseEntity deleteAllDocuLibraries() {
        docuLibraryService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/slider/resources")
    public List<DocuLibraryDTO> addSliderResourcesInDocuLibrary(@RequestParam(value = "multipartFiles") List<MultipartFile> multipartFiles,
                                                                @RequestParam(value = "isDefault") Boolean isDefault,
                                                                @RequestParam(value = "interval") Long interval,
                                                                @RequestParam(value = "status") String status)
            throws StorageException, IOException, URISyntaxException {
        return toDocuLibraryDTOs(docuLibraryService.saveSliderResources(multipartFiles, isDefault, interval, status));
    }

    @GetMapping("/slider/resources")
    public List<DocuLibraryDTO> findAllDocuLibraries(@RequestHeader(value = "Comp-Key", required = false) Long compKey) {
        if (compKey != null) {
            MasterTenant masterTenant = masterTenantService.findByCompanyKey(compKey);
            DBContextHolder.setTenantName(masterTenant.getDbName());
        }
        return toDocuLibraryDTOs(docuLibraryService.findAllSliderResources(compKey));
    }

//    @GetMapping("/findOrgDocuments/{id}")
//    public List<DocumentDTO> findOrgDocuments(@RequestHeader(value = "Comp-Key", required = false) Long compKey,
//                                              @PathVariable Long id) {
//        if (compKey != null) {
//            MasterTenant masterTenant = masterTenantService.findByCompanyKey(compKey);
//            DBContextHolder.setTenantName(masterTenant.getDbName());
//        }
//        return toDocumentDTOs(docuLibraryService.findByCodeRefIdAndCodeRefType(String.valueOf(id),"ORG_MNG"));
//    }

    // DocuLibrary This method is created to take mongo codeRefId (which is string earlier we have same this method with long codeRefId for mysql coderefId (i.e: org, user)
    @PostMapping("/library/mongo")
    public List<DocuLibraryDTO> addDocumentLibrary(@RequestParam(value = "file") List<MultipartFile> file,
                                                   @RequestParam(value = "level", required = false) String level,
                                                   @RequestParam(value = "levelId", required = false) String levelId,
                                                   @RequestParam(value = "docuName", required = false) String docuName,
                                                   @RequestParam(value = "docuType") String docuType,
                                                   @RequestParam(value = "codeRefType", required = false) String codeRefType,
                                                   @RequestParam(value = "codeRefId", required = false) String codeRefId,
                                                   @RequestParam(value = "notes", required = false) String notes,
                                                   @RequestParam(value = "tags", required = false) String tags,
                                                   @RequestParam(value = "format", required = false) String format,
                                                   @RequestParam(value = "status", required = false) String status,
                                                   @RequestParam(value = "visibilityKey", required = false) Boolean visibilityKey,
                                                   @RequestHeader("Comp-Key") Long compKey) throws StorageException, IOException, URISyntaxException {
        return toDocuLibraryDTOs(docuLibraryService.saveDocument(file, level, levelId, docuName, docuType, codeRefType, String.valueOf(codeRefId), notes, tags, format,
                status, visibilityKey, compKey));
    }


    @GetMapping("/library/findByCodeRefIdAndCodeRefType/{codeRefId}/{codeRefType}")
    public List<DocumentDTO> findByCodeRefIdAndCodeRefType(@RequestHeader(value = "Comp-Key", required = false) Long compKey,
                                                           @PathVariable String codeRefId, @PathVariable String codeRefType) {
        if (compKey != null) {
            MasterTenant masterTenant = masterTenantService.findByCompanyKey(compKey);
            DBContextHolder.setTenantName(masterTenant.getDbName());
        }
        int rounding = utility.getCompanyPreference().getRounding();
        return applyRoundingToDocumentDTOList(docuLibraryService.findAllDocumentDTOByCodeRefIdAndCodeRefType(codeRefId,codeRefType),rounding);
    }
    @GetMapping("/library/findByCodeRefTypeAndNotes")
    public List<DocumentDTO> findByCodeRefTypeAndNotes(@RequestHeader(value = "Comp-Key", required = false) Long compKey,
                                                           @RequestParam("codeRefType") String codeRefType, @RequestParam("notes") String notes) {
        if (compKey != null) {
            MasterTenant masterTenant = masterTenantService.findByCompanyKey(compKey);
            DBContextHolder.setTenantName(masterTenant.getDbName());
        }
        int rounding = utility.getCompanyPreference().getRounding();
        return toDocumentDTOsRounding(docuLibraryService.findByCodeRefTypeAndNotes(codeRefType,notes),rounding);
    }
}
