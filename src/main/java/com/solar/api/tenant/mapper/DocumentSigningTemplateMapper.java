package com.solar.api.tenant.mapper;

import com.solar.api.tenant.model.docuSign.DocumentSigningTemplate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DocumentSigningTemplateMapper {

    public static DocumentSigningTemplate toDocumentSigningTemplate(DocumentSigningTemplateDTO docSigningTemplateDTO) {
        if (docSigningTemplateDTO == null) {
            return null;
        }
        return DocumentSigningTemplate.builder()
                .id(docSigningTemplateDTO.getId())
//                .signTempId(docSigningTemplateDTO.getSignTempId())
                .templateName(docSigningTemplateDTO.getTemplateName())
                .enabled(docSigningTemplateDTO.getEnabled())
                .functionality(docSigningTemplateDTO.getFunctionality())
//                .contractName(docSigningTemplateDTO.getContractName())
//                .organization(docSigningTemplateDTO.getOrganization())
                .customerType(docSigningTemplateDTO.getCustomerType())
//                .docUrl(docSigningTemplateDTO.getUri())
                .extTemplateId(docSigningTemplateDTO.getExtTemplateId())
                .build();
    }

    public static DocumentSigningTemplateDTO toDocumentSigningTemplateDTO(DocumentSigningTemplate docSigningTemplate) {
        if (docSigningTemplate == null) {
            return null;
        }
        return DocumentSigningTemplateDTO.builder()
                .id(docSigningTemplate.getId())
//                .signTempId(docSigningTemplate.getSignTempId())
                .templateName(docSigningTemplate.getTemplateName())
                .enabled(docSigningTemplate.getEnabled())
                .functionality(docSigningTemplate.getFunctionality())
                .organizationId(docSigningTemplate.getOrganization() != null ? docSigningTemplate.getOrganization().getId() : null)
                .customerType(docSigningTemplate.getCustomerType())
                .docUrl(docSigningTemplate.getDocuLibrary() != null ? docSigningTemplate.getDocuLibrary().getUri() : null)
                .extTemplateId(docSigningTemplate.getExtTemplateId())
                .createdAt(docSigningTemplate.getCreatedAt())
                .build();
    }

    public static DocumentSigningTemplate toUpdatedDocumentSigningTemplate(DocumentSigningTemplate docSigningTemplate, DocumentSigningTemplate docSigningTemplateUpdate) {
//        docSigningTemplate.setSignTempId(docSigningTemplateUpdate.getSignTempId() == null ? docSigningTemplate.getSignTempId() : docSigningTemplateUpdate.getSignTempId());
        docSigningTemplate.setTemplateName(docSigningTemplateUpdate.getTemplateName() == null ? docSigningTemplate.getTemplateName() : docSigningTemplateUpdate.getTemplateName());
        docSigningTemplate.setEnabled(docSigningTemplateUpdate.getEnabled() == null ? docSigningTemplate.getEnabled() : docSigningTemplateUpdate.getEnabled());
        docSigningTemplate.setFunctionality(docSigningTemplateUpdate.getFunctionality() == null ? docSigningTemplate.getFunctionality() : docSigningTemplateUpdate.getFunctionality());
        docSigningTemplate.setCustomerType(docSigningTemplateUpdate.getCustomerType() == null ? docSigningTemplate.getCustomerType() : docSigningTemplateUpdate.getCustomerType());
        docSigningTemplate.setExtTemplateId(docSigningTemplateUpdate.getExtTemplateId() == null ? docSigningTemplate.getExtTemplateId() : docSigningTemplateUpdate.getExtTemplateId());
        return docSigningTemplate;
    }

    public static List<DocumentSigningTemplateDTO> toDocumentSigningTemplateDTOs(List<DocumentSigningTemplate> docSigningTemplate) {
        return docSigningTemplate.stream().map(DocumentSigningTemplateMapper::toDocumentSigningTemplateDTO).collect(Collectors.toList());
    }

    public static List<DocumentSigningTemplate> toDocumentSigningTemplates(Set<DocumentSigningTemplateDTO> docSigningTemplateDTOs) {
        return docSigningTemplateDTOs.stream().map(DocumentSigningTemplateMapper::toDocumentSigningTemplate).collect(Collectors.toList());
    }
}
