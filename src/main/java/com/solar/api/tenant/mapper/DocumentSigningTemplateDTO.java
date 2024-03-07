package com.solar.api.tenant.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentSigningTemplateDTO {

    private Long id;
    private String signTempId;
    private String templateName;
    private Boolean enabled;
    private String functionality;
    private String contractName;
    private String organization;
    private Long organizationId;
    private String customerType;
    private String docUrl;
    private String extTemplateId;
    private LocalDateTime createdAt;
}
