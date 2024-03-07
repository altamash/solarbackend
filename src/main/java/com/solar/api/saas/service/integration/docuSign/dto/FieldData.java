package com.solar.api.saas.service.integration.docuSign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldData {

  /*"field_text_data": {"Company": "SI", "Text1": "sample", "Full name": "test name"},
    "field_boolean_data": {},
    "field_date_data": {"Date1": "Nov 08 2022"}*/
    @JsonProperty("field_text_data")
    private JsonNode fieldTextData;
    @JsonProperty("field_boolean_data")
    private JsonNode fieldBooleanData;
    @JsonProperty("field_date_data")
    private JsonNode fieldDateData;
}
