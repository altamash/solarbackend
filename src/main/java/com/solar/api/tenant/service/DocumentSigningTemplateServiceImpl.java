package com.solar.api.tenant.service;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.exception.SolarApiException;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.integration.docuSign.DataExchangeDocuSign;
import com.solar.api.saas.service.integration.docuSign.dto.Action;
import com.solar.api.saas.service.integration.docuSign.dto.Template;
import com.solar.api.saas.service.integration.docuSign.dto.request.TemplateData;
import com.solar.api.saas.service.integration.docuSign.dto.response.CreateTemplateResponse;
import com.solar.api.tenant.mapper.DocumentSigningTemplateMapper;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeTenantDTO;
import com.solar.api.tenant.mapper.tiles.DocSigningTemplateTile;
import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.docuSign.DocumentSigningTemplate;
import com.solar.api.tenant.model.docuSign.ExternalCallBackLog;
import com.solar.api.tenant.model.docuSign.SigningRequestTracker;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.repository.DocuLibraryRepository;
import com.solar.api.tenant.repository.contract.ContractRepository;
import com.solar.api.tenant.repository.contract.EntityRepository;
import com.solar.api.tenant.repository.contract.OrganizationRepository;
import com.solar.api.tenant.repository.docuSign.DocumentSigningTemplateRepository;
import com.solar.api.tenant.repository.docuSign.ExternalCallBackLogRepository;
import com.solar.api.tenant.repository.docuSign.SigningRequestTrackerRepository;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.helper.Utility.*;

@Service
public class DocumentSigningTemplateServiceImpl implements DocumentSigningTemplateService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private DocumentSigningTemplateRepository repository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private EntityRepository entityRepository;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private DocuLibraryRepository docuLibraryRepository;
    @Autowired
    private PortalAttributeOverrideService attributeOverrideService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private DataExchangeDocuSign dataExchangeDocuSign;
    @Autowired
    private MasterTenantService masterTenantService;
    @Autowired
    private ExternalCallBackLogRepository externalCallBackLogRepository;
    @Autowired
    private SigningRequestTrackerRepository signingRequestTrackerRepository;


    @Override
    public DocumentSigningTemplate save(DocumentSigningTemplate documentSigningTemplate) {
        return repository.save(documentSigningTemplate);
    }

    @Override
    public DocumentSigningTemplate update(DocumentSigningTemplate documentSigningTemplate) {
        return repository.save(documentSigningTemplate);
    }

    @Transactional
    @Override
    public DocumentSigningTemplate add(DocumentSigningTemplate template, Long organizationId, Long entityId,
                                       Long contractId, Long docuLibraryId, MultipartFile file, Long compKey, String format) {
        // composite key : functionality + org id + entity id + contract id
        try {
            DocumentSigningTemplate signingTemplateExist = repository.findByTemplateName(template.getTemplateName());
            if (signingTemplateExist != null) {
                throw new SolarApiException("Template Name already exist!");
            }
            Organization organization = null;
            if (organizationId != null) {
                organization = (Organization) findById(organizationId, Organization.class, organizationRepository);
            }
            template.setOrganization(organization);
            Entity entity = null;
            if (entityId != null) {
                entity = (Entity) findById(entityId, Entity.class, entityRepository);
            }
            template.setEntity(entity);
            Contract contract = null;
            if (contractId != null) {
                contract = (Contract) findById(contractId, Contract.class, contractRepository);
            }
            template.setContract(contract);
            template.setEnabled(true);

            try {
                repository.disableTemplateByCustomerTypeAndFunctionality(organizationId, template.getCustomerType(), template.getFunctionality());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            template = repository.save(template);
            DocuLibrary docuLibrary = null;
            if (file != null) {
                docuLibrary = uploadDocument(file, compKey, organization, entity, contract, null, template, format);
            }
            template = repository.save(template);
            template.setDocuLibrary(docuLibrary);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (e.getClass().equals(DataIntegrityViolationException.class)) {
                throw new SolarApiException("Too long Template Name. Max length 50 characters!");
            }
            throw new SolarApiException(e.getMessage());
        }
        return template;
    }
    @Transactional
    @Override
    public DocumentSigningTemplate addWithoutDeactivateFunctionality(DocumentSigningTemplate template, Long organizationId, Long entityId,
                                       Long contractId, Long docuLibraryId, MultipartFile file, Long compKey, String format) {
        // composite key : functionality + org id + entity id + contract id
        try {
            DocumentSigningTemplate signingTemplateExist = repository.findByTemplateName(template.getTemplateName());
            if (signingTemplateExist != null) {
                throw new SolarApiException("Template Name already exist!");
            }
            Organization organization = null;
            if (organizationId != null) {
                organization = (Organization) findById(organizationId, Organization.class, organizationRepository);
            }
            template.setOrganization(organization);
            Entity entity = null;
            if (entityId != null) {
                entity = (Entity) findById(entityId, Entity.class, entityRepository);
            }
            template.setEntity(entity);
            Contract contract = null;
            if (contractId != null) {
                contract = (Contract) findById(contractId, Contract.class, contractRepository);
            }
            template.setContract(contract);
            template.setEnabled(true);

            template = repository.save(template);
            DocuLibrary docuLibrary = null;
            if (file != null) {
                docuLibrary = uploadDocument(file, compKey, organization, entity, contract, null, template, format);
            }
            template = repository.save(template);
            template.setDocuLibrary(docuLibrary);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (e.getClass().equals(DataIntegrityViolationException.class)) {
                throw new SolarApiException("Too long Template Name. Max length 50 characters!");
            }
            throw new SolarApiException(e.getMessage());
        }
        return template;
    }

    private DocuLibrary uploadDocument(MultipartFile file, Long compKey, Organization organization, Entity entity,
                                       Contract contract, DocuLibrary docuLibCurrent, DocumentSigningTemplate template, String format)
            throws URISyntaxException, IOException, StorageException {
        DocuLibrary docuLibrary = uploadToStorage(file, compKey, organization, entity, contract, template.getId(), docuLibCurrent);
        uploadToSigningProvider(file, compKey, organization, entity, contract, template, format);
        return docuLibrary;
    }

    private DocuLibrary uploadToStorage(MultipartFile file, Long compKey, Organization organization, Entity entity,
                                        Contract contract, Long templateId, DocuLibrary docuLibCurrent) throws URISyntaxException, IOException, StorageException {
        String uri = storageService.storeInContainer(file, appProfile, "tenant/" + compKey
                + AppConstants.PATHS.DOC_SIGNING_TEMPLATE, file.getOriginalFilename(), compKey, false);
        DocuLibrary docuLibrary = getDocuLibrary(organization, entity, contract, file.getContentType(),
                "DSGN_TPL", String.valueOf(templateId), file.getOriginalFilename(), true, uri, docuLibCurrent);
        return docuLibraryRepository.save(docuLibrary);
    }

    private void uploadToSigningProvider(MultipartFile file, Long compKey, Organization organization, Entity entity,
                                         Contract contract, DocumentSigningTemplate template, String format) {
        String refreshToken = dataExchangeDocuSign.getAccessTokenViaRefreshToken().getAccessToken();
        /*"{" +
                "  \"templates\": {" +
                "    \"template_name\": \"CustAcqTemplate_new\"," +
                "    \"expiration_days\": 1," +
                "    \"is_sequential\": true," +
                "    \"reminder_period\": 8," +
                "    \"email_reminders\": false," +
                "    \"actions\": [" +
                "      {" +
                "        \"action_type\": \"SIGN\"," +
                "        \"signing_order\": 0," +
                "        \"recipient_name\": \"\"," +
                "        \"role\": \"1\"," +
                "        \"recipient_email\": \"\"," +
                "        \"recipient_phonenumber\": \"\"," +
                "        \"recipient_countrycode\": \"\"," +
                "        \"private_notes\": \"Please get back to us for further queries\"," +
                "        \"verify_recipient\": true," +
                "        \"verification_type\": \"EMAIL\"," +
                "        \"verification_code\": \"\"" +
                "      }" +
                "    ]" +
                "  }" +
                "}"*/
        MasterTenant tenant = masterTenantService.findByDbName(DBContextHolder.getTenantName());
////        composite key : functionality + org id + entity id + contract id
//        StringBuilder templateNameBuilder = new StringBuilder(String.valueOf(tenant.getCompanyKey()));
//        templateNameBuilder.append(organization != null ? "_" + organization.getOrganizationName() : "");
//        templateNameBuilder.append(entity != null ? "_" + entity.getEntityName() : "");
//        templateNameBuilder.append(contract != null ? "_" + contract.getContractName() : "");
//        templateNameBuilder.append("_" + template.getTemplateName() + "_" + file.getOriginalFilename());
        StringBuilder templateNameBuilder = getDocSigningTemplateName(entity, organization, contract, file, template, format);
        TemplateData data = TemplateData.builder()
                .templates(Template.builder()
                        .templateName(templateNameBuilder.toString())
                        .expirationDays(2)
                        .isSequential(true)
                        .reminderPeriod(8)
                        .actions(Arrays.asList(Action.builder()
                                .actionType("SIGN")
                                .signingOrder(0)
//                                        .recipientName("") //
                                .role(template.getCustomerType())
//                                        .recipientEmail("")//
//                                        .recipientPhonenumber("")//
//                                        .recipientCountrycode("")//
                                .privateNotes("")
                                .verifyRecipient(false)//
                                .verificationType("EMAIL")//
//                                        .verificationCode("")//
                                .build()))
                        .build())
                .build();
        CreateTemplateResponse response = dataExchangeDocuSign.createTemplate(file, data, refreshToken);
        template.setExtTemplateId(response.getTemplates().getTemplateId() + "_" +
                response.getTemplates().getDocumentIds().get(0).getDocumentId() + "_" +
                response.getTemplates().getActions().get(0).getActionId());
    }

    private DocuLibrary getDocuLibrary(Organization organization, Entity entity, Contract contract, String docuType,
                                       String codeRefType, String codeRefId, String docuName, Boolean visibilityKey,
                                       String uri, DocuLibrary docuLibCurrent) {
        DocuLibrary docuLibrary;
        if (docuLibCurrent != null) {
            docuLibrary = docuLibCurrent;
        } else {
            docuLibrary = new DocuLibrary();
        }
        docuLibrary.setOrganization(organization);
        docuLibrary.setEntity(entity);
        docuLibrary.setContract(contract);
        docuLibrary.setDocuType(docuType);
        docuLibrary.setCodeRefType(codeRefType);
        docuLibrary.setCodeRefId(codeRefId);
        docuLibrary.setDocuName(docuName);
        docuLibrary.setVisibilityKey(visibilityKey);
        docuLibrary.setUri(uri);
        return docuLibrary;
    }

    @Transactional
    @Override
    public DocumentSigningTemplate update(DocumentSigningTemplate template, Long organizationId, Long entityId,
                                          Long contractId, Long docuLibraryId, MultipartFile file, Long compKey, String format) {
        try {
            Optional<DocumentSigningTemplate> docTemplateOptional = repository.findById(template.getId());
            if (!docTemplateOptional.isPresent()) {
                throw new NotFoundException(DocumentSigningTemplate.class, template.getId());
            }
            template = DocumentSigningTemplateMapper
                    .toUpdatedDocumentSigningTemplate(docTemplateOptional.get(), template);
            Organization organization = null;
            if (organizationId != null) {
                organization = (Organization) findById(organizationId, Organization.class, organizationRepository);
            }
            template.setOrganization(organization);
            Entity entity = null;
            if (entityId != null) {
                entity = (Entity) findById(entityId, Entity.class, entityRepository);
            }
            template.setEntity(entity);
            Contract contract = null;
            if (contractId != null) {
                contract = (Contract) findById(contractId, Contract.class, contractRepository);
            }
            template.setContract(contract);
            DocuLibrary docuLibrary = null;
            if (file != null) {
                List<DocuLibrary> docuLibraries = docuLibraryRepository.findAllByCodeRefIdAndCodeRefType(String.valueOf(template.getId()), "DSGN_TPL");
                docuLibrary = uploadDocument(file, compKey, organization, entity, contract,
                        docuLibraries.isEmpty() ? null : docuLibraries.get(0), template, format);
            }

            try {
                repository.disableTemplateByCustomerTypeAndFunctionality(organizationId, template.getCustomerType(), template.getFunctionality());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            template = repository.save(template);
            template.setDocuLibrary(docuLibrary);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return template;
    }

    @Override
    public List<DocumentSigningTemplate> findAll(String customerType, String functionality, Boolean status,
                                                 String startDateStr, String endDateStr) {
        List<String> customerTypes;
        if (customerType != null) {
            customerTypes = Arrays.asList(customerType);
        } else {
            PortalAttributeTenantDTO typeAttribs = attributeOverrideService.findByNameFetchPortalAttributeValues("Customer Type");
            customerTypes = typeAttribs.getPortalAttributeValuesTenant().stream().map(m -> m.getAttributeValue()).collect(Collectors.toList());
        }
        List<String> functionalities;
        if (functionality != null) {
            functionalities = Arrays.asList(functionality);
        } else {
            PortalAttributeTenantDTO typeAttribs = attributeOverrideService.findByNameFetchPortalAttributeValues("SIGNFN");
            functionalities = typeAttribs.getPortalAttributeValuesTenant().stream().map(m -> m.getAttributeValue()).collect(Collectors.toList());
        }
        List<DocumentSigningTemplate> signingTemplates;
        if (startDateStr != null && endDateStr != null) {
            LocalDateTime startDate = LocalDateTime.ofInstant((getStartOfDate(getDate(startDateStr, SYSTEM_DATE_FORMAT))).toInstant(), ZoneId.systemDefault());
            LocalDateTime endDate = LocalDateTime.ofInstant((getEndOfDate(getDate(endDateStr, SYSTEM_DATE_FORMAT))).toInstant(), ZoneId.systemDefault());
            if (status != null) {
                signingTemplates = repository.findAllByCustomerTypeInAndFunctionalityInAndEnabledAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
                        customerTypes, functionalities, status, startDate, endDate);
            } else {
                signingTemplates = repository.findAllByCustomerTypeInAndFunctionalityInAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
                        customerTypes, functionalities, startDate, endDate);
            }
        } else if (status != null) {
            signingTemplates = repository.findAllByCustomerTypeInAndFunctionalityInAndEnabled(customerTypes, functionalities, status);
        }
        signingTemplates = repository.findAllByCustomerTypeInAndFunctionalityIn(customerTypes, functionalities);
        signingTemplates.sort(Comparator.comparing(DocumentSigningTemplate::getCreatedAt, Comparator.reverseOrder()));
        signingTemplates.sort(Comparator.comparing(DocumentSigningTemplate::getEnabled, Comparator.reverseOrder()));

        signingTemplates.forEach(tmpl -> {
            List<DocuLibrary> docuLibraries = docuLibraryRepository.findAllByCodeRefIdAndCodeRefType(String.valueOf(tmpl.getId()), "DSGN_TPL");
            tmpl.setDocuLibrary(docuLibraries.isEmpty() ? null : docuLibraries.get(0));
        });
        return signingTemplates;
    }

    @Transactional
    @Override
    public void activateDeactivate(Long id) {
        DocumentSigningTemplate signingTemplate = repository.findById(id).orElse(null);
        if (signingTemplate != null) {
            repository.disableTemplateByCustomerTypeAndFunctionality(signingTemplate.getOrganization().getId(),
                    signingTemplate.getCustomerType(), signingTemplate.getFunctionality());

            signingTemplate.setEnabled(true);
            repository.save(signingTemplate);
        }
    }
    @Transactional
    @Override
    public void activate(Long id) {
        DocumentSigningTemplate signingTemplate = repository.findById(id).orElse(null);
        if (signingTemplate != null) {
            signingTemplate.setEnabled(true);
            repository.save(signingTemplate);
        }
    }

    @Override
    public DocumentSigningTemplate findByIdEnabled(Long id) {
        return repository.findByIdEnabled(id);
    }

    @Override
    public Optional<DocumentSigningTemplate> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<DocumentSigningTemplate> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean delete(Long id) {
        boolean flag = false;
        DocumentSigningTemplate template = findByIdEnabled(id);
        if (template != null) {
            List<SigningRequestTracker> signingRequestTrackers = template.getSigningRequestTrackers();
            List<ExternalCallBackLog> externalCallBackLogs = signingRequestTrackers.stream()
                    .flatMap(m -> m.getExternalCallBackLogs().stream())
                    .collect(Collectors.toList());
            externalCallBackLogRepository.deleteAll(externalCallBackLogs);
            signingRequestTrackerRepository.deleteAll(signingRequestTrackers);
            repository.delete(template);
            flag = true;
        }
        return flag;
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public StringBuilder getDocSigningTemplateName(Entity entity, Organization organization, Contract contract, MultipartFile file, DocumentSigningTemplate template, String format) {
        MasterTenant tenant = masterTenantService.findByDbName(DBContextHolder.getTenantName());
        //composite key : functionality + org id + entity id + contract id
        StringBuilder templateNameBuilder = new StringBuilder(String.valueOf(tenant.getCompanyKey()));
        templateNameBuilder.append(organization != null ? "_" + organization.getOrganizationName() : "");
        // TODO: have to convert entity name to entity id
        templateNameBuilder.append(entity != null ? "_" + entity.getEntityName() : "");
        templateNameBuilder.append(contract != null ? "_" + contract.getContractName() : "");
        templateNameBuilder.append("_" + template.getTemplateName() + "." + format);//"_" + file.getOriginalFilename());
        return templateNameBuilder;
    }

    @Override
    public List<DocumentSigningTemplate> findByFunctionalityAndCustomerTypeAndOrganizationAndEnabled(String functionality, String customerType, Organization org, Boolean status) {
        return repository.findByFunctionalityAndCustomerTypeAndOrganizationAndEnabled(functionality, customerType, org, status);
    }

    @Override
    public List<DocSigningTemplateTile> getActiveContractsByCustomerTypeFunctionalityOrganizationId(String customerType, String functionality, Long organizationId) {
        List<DocSigningTemplateTile> documentSigningTemplateList = new ArrayList<>();
        try{
            documentSigningTemplateList = repository.findActiveContractsByCustomerTypeFunctionalityOrganizationId(customerType, functionality, organizationId);
        }catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("No contract present on this organization id " + organizationId, e);
        }
        return documentSigningTemplateList;
    }
    public StringBuilder getDocSigningTemplateNameV2(Entity entity, Organization organization, Contract contract, MultipartFile file, DocumentSigningTemplate template, String format, String timeStamp) {
        MasterTenant tenant = masterTenantService.findByDbName(DBContextHolder.getTenantName());
        //composite key : functionality + org id + entity id + contract id
        StringBuilder templateNameBuilder = new StringBuilder(String.valueOf(tenant.getCompanyKey()));
        templateNameBuilder.append(organization != null ? "_" + organization.getOrganizationName() : "");
        // TODO: have to convert entity name to entity id
        templateNameBuilder.append(entity != null ? "_" + entity.getEntityName() : "");
        templateNameBuilder.append(contract != null ? "_" + contract.getContractName() : "");
        templateNameBuilder.append("_" +  timeStamp + "-"+ template.getTemplateName()  + "." + format);//"_" + file.getOriginalFilename());
        return templateNameBuilder;
    }
}
