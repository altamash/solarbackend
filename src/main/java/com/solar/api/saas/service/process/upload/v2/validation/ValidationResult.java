package com.solar.api.saas.service.process.upload.v2.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationResult {

    private Integer totalRows;
    private List<Integer> correctRowIdsList;
    private String correctRowIds;
    private List<ValidationDTO> validationDTOs;
    private Integer totalCorrectRows;
    private String uploadId;
    private String fileStorageUri;

    public ValidationResult setUploadId(String uploadId) {
        this.uploadId = uploadId;
        return this;
    }

    public ValidationResult setFileStorageUri(String fileStorageUri) {
        this.fileStorageUri = fileStorageUri;
        return this;
    }
}
