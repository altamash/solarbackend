package com.solar.api.tenant.service;

import com.solar.api.tenant.mapper.tiles.DocSigningTemplateTile;
import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.docuSign.DocumentSigningTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentSigningTemplateService extends BaseService<DocumentSigningTemplate, Object> {

    DocumentSigningTemplate add(DocumentSigningTemplate template, Long organizationId,
                                Long entityId, Long contractId, Long docuLibraryId, MultipartFile file, Long compKey,  String format);
    DocumentSigningTemplate addWithoutDeactivateFunctionality(DocumentSigningTemplate template, Long organizationId,
                                Long entityId, Long contractId, Long docuLibraryId, MultipartFile file, Long compKey,  String format);

    DocumentSigningTemplate update(DocumentSigningTemplate template, Long organizationId, Long entityId,
                                   Long contractId, Long docuLibraryId, MultipartFile file, Long compKey, String format);

    List<DocumentSigningTemplate> findAll(String customerType, String functionality, Boolean status,
                                          String startDateStr, String endDateStr);

    void activateDeactivate(Long id);
    void activate(Long id);

    DocumentSigningTemplate findByIdEnabled(Long id);

    StringBuilder getDocSigningTemplateName(Entity entity, Organization organization, Contract contract, MultipartFile file, DocumentSigningTemplate template, String format);
    List<DocumentSigningTemplate> findByFunctionalityAndCustomerTypeAndOrganizationAndEnabled(String functionality,String customerType, Organization org, Boolean status);

    List<DocSigningTemplateTile> getActiveContractsByCustomerTypeFunctionalityOrganizationId(String customerType, String functionality, Long organizationId);
    StringBuilder getDocSigningTemplateNameV2(Entity entity, Organization organization, Contract contract, MultipartFile file, DocumentSigningTemplate template, String format, String timestamp);
}
