package com.solar.api.tenant.mapper.subscription.customerSubscription.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RateCodesDTO {
    private int ref_id;
    private String measure;
    private String code;
    private String format;
    private String uom;
    private boolean pct;
    private String attributeIdRef;
    private boolean locked;
    private boolean mandatory;
    private String relatedMeasure;
    private String alias;
    private String type;
    private String regModule;
    private String valref_idationRule;
    private String valref_idationParams;
    private String actions;
    private String visibilityLevel;
    private String compEvents;
    private String notes;
    private boolean visible;
    private boolean self_register;
    private String default_value;

}