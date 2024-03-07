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
public class Page {

  /*"image_string": "",
    "page": 0,
    "is_thumbnail": true*/
    @JsonProperty("image_string")
    private String imageString;
    private Integer page;
    @JsonProperty("is_thumbnail")
    private Boolean isThumbnail;
}
