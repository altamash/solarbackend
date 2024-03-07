package com.solar.api.tenant.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailTemplateDTO {

    private String HTMLCode;
    private String template;
}
