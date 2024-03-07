package com.solar.api.saas.service.process.upload.v2.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadPayload {

    private String emails;
    private String correctRowIds;
    private String correctStagedIds;
    private String customerType; //individual, commercial
}
