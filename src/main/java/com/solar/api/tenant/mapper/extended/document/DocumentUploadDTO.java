package com.solar.api.tenant.mapper.extended.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentUploadDTO {

    private Long docuType;
    private MultipartFile multipartFile;
}
