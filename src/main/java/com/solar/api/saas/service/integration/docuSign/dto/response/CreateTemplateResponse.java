package com.solar.api.saas.service.integration.docuSign.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.saas.service.integration.docuSign.dto.Template;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateTemplateResponse {

    /*{
        "code": 9041,
        "message": "Invalid Oauth token",
        "status": "failure"
    }*/

    private Integer code;
    private String message;
    private String status;
    private Template templates;
    private Template requests;
}
