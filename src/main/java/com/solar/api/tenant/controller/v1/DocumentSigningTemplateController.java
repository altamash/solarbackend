package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.solar.api.saas.service.integration.docuSign.DataExchangeDocuSign;
import com.solar.api.saas.service.integration.docuSign.dto.request.TemplateData;
import com.solar.api.saas.service.integration.docuSign.dto.response.AccessTokenResponse;
import com.solar.api.saas.service.integration.docuSign.dto.response.CreateTemplateResponse;
import com.solar.api.tenant.mapper.DocumentSigningTemplateDTO;
import com.solar.api.tenant.mapper.DocumentSigningTemplateMapper;
import com.solar.api.tenant.mapper.tiles.DocSigningTemplateTile;
import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.docuSign.DocumentSigningTemplate;
import com.solar.api.tenant.service.DocumentSigningTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileDescriptor;
import java.util.List;

import static com.solar.api.tenant.mapper.DocumentSigningTemplateMapper.toDocumentSigningTemplate;
import static com.solar.api.tenant.mapper.DocumentSigningTemplateMapper.toDocumentSigningTemplateDTO;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("DocumentSigningTemplateController")
@RequestMapping(value = "/docSigningTemplate")
public class DocumentSigningTemplateController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private DocumentSigningTemplateService service;
    @Autowired
    private DataExchangeDocuSign dataExchangeDocuSign;

    @PostMapping
    public APIResponse addDocumentSigningTemplate(@RequestParam("contractDocument") String contractDocument,
                                                     @RequestParam("file") MultipartFile file,
                                                     @RequestHeader("Comp-Key") Long compKey) {
        DocumentSigningTemplateDTO template = null;
        String format = "pdf";
        try {
            template = new ObjectMapper().readValue(contractDocument, DocumentSigningTemplateDTO.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return APIResponse.builder()
                .data(toDocumentSigningTemplateDTO(service.add(toDocumentSigningTemplate(template),
                        template.getOrganizationId(), null, null, null, file, compKey, format)))
                .build();
    }
    @PostMapping("/addWithoutDeactivateFunctionality")
    public APIResponse addDocumentSigningTemplateWithoutInactiveFunctionality(@RequestParam("contractDocument") String contractDocument,
                                                  @RequestParam("file") MultipartFile file,
                                                  @RequestHeader("Comp-Key") Long compKey) {
        DocumentSigningTemplateDTO template = null;
        String format = "pdf";
        try {
            template = new ObjectMapper().readValue(contractDocument, DocumentSigningTemplateDTO.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return APIResponse.builder()
                .data(toDocumentSigningTemplateDTO(service.addWithoutDeactivateFunctionality(toDocumentSigningTemplate(template),
                        template.getOrganizationId(), null, null, null, file, compKey, format)))
                .build();
    }

    @PutMapping
    public APIResponse updateDocumentSigningTemplate(@RequestParam("contractDocument") String contractDocument,
                                                     @RequestParam(value = "file", required = false) MultipartFile file,
                                                     @RequestHeader("Comp-Key") Long compKey) {
        DocumentSigningTemplateDTO template = null;
        String format ="pdf";
        try {
            template = new ObjectMapper().readValue(contractDocument, DocumentSigningTemplateDTO.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return APIResponse.builder()
                .data(toDocumentSigningTemplateDTO(service.update(toDocumentSigningTemplate(template),
                        template.getOrganizationId(), null,null, null, file, compKey,format)))
                .build();
    }

    @GetMapping
    public APIResponse findAll(@RequestParam(required = false) String customerType,
                                     @RequestParam(required = false) String functionality,
                                     @RequestParam(required = false) Boolean status,
                                     @RequestParam(required = false) String startDateStr,
                                     @RequestParam(required = false) String endDateStr) {
        return APIResponse.builder()
                .data(DocumentSigningTemplateMapper.toDocumentSigningTemplateDTOs(service.findAll(customerType,
                        functionality, status, startDateStr, endDateStr)))
                .build();
    }

    @GetMapping("/activateDeactivate/{id}")
    public void activateDeactivate(@PathVariable Long id) {
        service.activateDeactivate(id);
    }
    @GetMapping("/activate/{id}")
    public void activate(@PathVariable Long id) {
        service.activate(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boolean flag = service.delete(id);
    }

    // DocuSign ///////////////////////////////////////////
    @PostMapping("/docuSign/token")
    public AccessTokenResponse getAccessTokenFromRefreshToken() {
        return dataExchangeDocuSign.getAccessTokenViaRefreshToken();
    }

    @PostMapping(value = "/docuSign/templates", consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
    public CreateTemplateResponse createTemplate(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("data") String data,
                                                 @RequestHeader("Docu-Sign") String auth) {
        CreateTemplateResponse createTemplateResponse = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            objectMapper.configOverride(FileDescriptor.class);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            createTemplateResponse = dataExchangeDocuSign.createTemplate(file, objectMapper.readValue(data, TemplateData.class), auth);
//            createTemplateResponse = dataExchangeDocuSign.createTemplate(file, data, auth);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return createTemplateResponse;
    }

    @PostMapping("/docuSign/templates/{templateId}/createDocument")
    public CreateTemplateResponse createDocument(@PathVariable("templateId") String templateId,
                                                 @RequestParam("data") String data,
                                                 @RequestHeader("Docu-Sign") String auth) {
        CreateTemplateResponse createTemplateResponse = null;
        try {
            createTemplateResponse = dataExchangeDocuSign.createDocument(templateId, new ObjectMapper().readValue(data, TemplateData.class), auth);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return createTemplateResponse;
    }

    @GetMapping("/docuSign/document/request/{requestId}")
    public CreateTemplateResponse getDocumentDetails(@PathVariable("requestId") String requestId,
                                   @RequestHeader("Docu-Sign") String auth) {
        return dataExchangeDocuSign.getDocumentDetails(requestId, auth);
    }

    @GetMapping("/docuSign/document/request/{requestId}/pdf")
    public ResponseEntity<byte[]> getDocumentPDF(@PathVariable("requestId") String requestId,
                                   @RequestHeader("Docu-Sign") String auth) {
        byte[] result = new byte[0];
        try {
            result = dataExchangeDocuSign.getDocumentPDF(requestId, auth);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }
    @GetMapping("/getActiveContractsByCustomerTypeFunctionalityOrganizationId")
    public List<DocSigningTemplateTile> getActiveContractsByCustomerTypeFunctionalityOrganizationId(@RequestParam("customerType") String customerType,
                                                                                                     @RequestParam("functionality") String functionality,
                                                                                                     @RequestParam("organizationId") Long organizationId) {
        try{
              List<DocSigningTemplateTile> documentSigningTemplateList = service.getActiveContractsByCustomerTypeFunctionalityOrganizationId(customerType, functionality, organizationId);
            return documentSigningTemplateList;
        }catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

}