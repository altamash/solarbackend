package com.solar.api.tenant.mapper.tiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocSigningTemplateTile {

    private Long id;
    private String templateName;
    private Boolean enabled;
    private String functionality; // portal attribute, SIGNFN
    private Long organization;
    private String customerType; // portal attribute, DSGN_TPL
    private String extTemplateId; // Zoho Signs template ID once the document is uploaded.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String organizationName;

    public DocSigningTemplateTile(Long id, String templateName, Boolean enabled, String functionality, Long organization, String customerType, String extTemplateId, LocalDateTime createdAt, LocalDateTime updatedAt, String organizationName) {
        this.id = id;
        this.templateName = templateName;
        this.enabled = enabled;
        this.functionality = functionality;
        this.organization = organization;
        this.customerType = customerType;
        this.extTemplateId = extTemplateId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.organizationName = organizationName;
    }
}
