package com.solar.api.saas.service.process.upload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadResponse {
    private String message;
    private String entityType;
    private Integer migrated;
    private Integer created;
    private Integer updated;
    private List<Long> createdIds;
    private List<Long> updatedIds;
    private List<String> migratedExternalIds;
    private List<String> updatedExternalIds;
    private List<String> invalidExternalIds;
    private List<Long> unmappedExternalIds;
    private List<Long> invalidInvoiceIds;
    private List<Long> addedDetailIds;
    private List<Long> updatedDetailIds;
    private List<Long> invalidDetailIds;
    private List<Long> markedPaidIds;
    private List<String> invalidCodes;
    private List<String> userNames;
    private List<String> passwords;
    private List<String> uidPass;
    private String responseUrl;
    private Long total;
}
