package com.solar.api.tenant.mapper.tiles.signingRequestTracker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.saas.service.integration.mongo.response.subscription.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SigningReqTrackerTile {
    private Long id;
    private Long documentSigningTemplate;
    private String extTemplateId;
    private String extRequestId; // Request Number from Zoho Sign once document is submitted.
    private String templateName;
    private String functionality;
    private String customerType;
    private String expiryDate;
    private String status;
    private String organizationName;
    private String uri;
    private String codeRefType;
    public SigningReqTrackerTile(Long id, Long documentSigningTemplate, String extTemplateId, String extRequestId, String templateName, String functionality, String customerType,String expiryDate, String status, String organizationName,String uri,String codeRefType) {
        this.id = id;
        this.documentSigningTemplate = documentSigningTemplate;
        this.extTemplateId = extTemplateId;
        this.extRequestId = extRequestId;
        this.templateName = templateName;
        this.functionality = functionality;
        this.customerType = customerType;
        this.expiryDate = expiryDate;
        this.status = status;
        this.organizationName = organizationName;
        this.uri = uri;
        this.codeRefType = codeRefType;
    }
}
