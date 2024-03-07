package com.solar.api.tenant.mapper.extended.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveDocumentDTO {
    private String name;
    private String type;
    private String fileBase64;
}
