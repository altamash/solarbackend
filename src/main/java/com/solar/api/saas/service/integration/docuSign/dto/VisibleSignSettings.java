package com.solar.api.saas.service.integration.docuSign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisibleSignSettings {

  /*"visible_sign": false,
    "allow_reason_visible_sign": false*/
    @JsonProperty("visible_sign")
    private Boolean visible_sign;
    @JsonProperty("allow_reason_visible_sign")
    private Boolean allowReasonVisibleSign;
}
