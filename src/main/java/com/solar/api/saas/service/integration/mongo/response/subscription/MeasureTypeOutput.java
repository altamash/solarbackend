package com.solar.api.saas.service.integration.mongo.response.subscription;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeasureTypeOutput {

    @JsonProperty("id")
    @JsonAlias("_id")
    private BaseIdOutput _id;
    @JsonAlias("ref_id")
    @JsonProperty("refId")
    private String ref_id;
    private String actions;
    private String alias;
    @JsonProperty("attributeIdRef")
    @JsonAlias("attribute_id_ref")
    private String attribute_id_ref;
    @JsonProperty("attributeIdRefId")
    @JsonAlias("attribute_id_ref_id")
    private String attribute_id_ref_id;
    private String code;
    @JsonAlias("comp_events")
    @JsonProperty("compEvents")
    private String comp_events;
    @JsonAlias("created_at")
    @JsonProperty("createdAt")
    private String created_at;
    private String format;
    private Boolean locked;
    //Todo
    private Boolean mandatory;
    private String measure;
    private String notes;
    private Boolean pct;
    @JsonProperty("regModule")
    @JsonAlias("reg_module")
    private String reg_module;
    @JsonProperty("regModuleId")
    @JsonAlias("reg_module_id")
    private String reg_module_id;
    @JsonAlias("related_measure")
    @JsonProperty("relatedMeasure")
    private String related_measure;
    @JsonAlias("system_used")
    @JsonProperty("systemUsed")
    private Boolean system_used;
    private String type;
    private String uom;
    @JsonAlias("updated_at")
    @JsonProperty("updatedAt")
    private String updated_at;
    @JsonProperty("validationParams")
    @JsonAlias("validation_params")
    private String validation_params;
    @JsonProperty("validationRule")
    @JsonAlias("validation_rule")
    private String validation_rule;
    @JsonProperty("visibilityLevel")
    @JsonAlias("visibility_level")
    private String visibility_level;
    private Boolean visible;
    private Integer level;
    private Integer seq;
    @JsonProperty("defaultValue")
    @JsonAlias("default_value")
    private Object default_value;
    @JsonProperty("selfRegister")
    @JsonAlias("self_register")
    private Boolean self_register;
    private String flag;
}
