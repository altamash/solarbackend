package com.solar.api.saas.service.integration.mongo.response.subscription;

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
public class MeasureType {

    @JsonProperty("_id")
    private BaseId id;
    @JsonProperty("ref_id")
    private String refId;
    private String actions;
    private String alias;
    @JsonProperty("attribute_id_ref")
    private String attributeIdRef;
    @JsonProperty("attribute_id_ref_id")
    private String attributeIdRefId;
    private String code;
    @JsonProperty("comp_events")
    private String compEvents;
    @JsonProperty("created_at")
    private String createdAt;
    private String format;
    private Boolean locked;

    //Todo
    private Boolean mandatory;
    private String measure;
    private String notes;
    private Boolean pct;
    @JsonProperty("reg_module")
    private String regModule;
    @JsonProperty("reg_module_id")
    private String regModuleId;
    @JsonProperty("related_measure")
    private String relatedMeasure;
    @JsonProperty("system_used")
    private Boolean systemUsed;
    private String type;
    private String uom;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("validation_params")
    private String validationParams;
    @JsonProperty("validation_rule")
    private String validationRule;
    @JsonProperty("visibility_level")
    private String visibilityLevel;
    private Boolean visible;
    private Integer level;
    private Integer seq;
    @JsonProperty("default_value")
    private String defaultValue;
    @JsonProperty("self_register")
    private Boolean selfRegister;
    private String flag;
}
