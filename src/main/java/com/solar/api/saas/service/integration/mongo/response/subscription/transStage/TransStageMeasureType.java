package com.solar.api.saas.service.integration.mongo.response.subscription.transStage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.solar.api.saas.service.integration.mongo.response.subscription.BaseId;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransStageMeasureType {

    @JsonProperty("measure_id")
    private String id;
    @JsonProperty("code")
    private String code;
    @JsonProperty("format")
    private String format;
    @JsonProperty("level")
    private Integer level;
    @JsonProperty("seq")
    private Integer seq;
    @JsonProperty("default_value")
    private String defaultValue;
}
