package com.solar.api.saas.mapper.sendgridDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateDTO {

    private String id;
    private String name;
    private String generation; // "dynamic"
    private String updated_at;
    private List<VersionDTO> versions;

}
