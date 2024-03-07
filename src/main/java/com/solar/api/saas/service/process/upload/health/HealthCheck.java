package com.solar.api.saas.service.process.upload.health;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"Line", "action", "entity_id", "location_id", "payment_info_id", "subscription_id","Premise No","Monthly Production","Garden ID","Calendar Month", "issue"})
public class HealthCheck {

    @JsonProperty("Line")
    private Integer line;
    private String action;
    @JsonProperty("entity_id")
    private String entityId;
    @JsonProperty("location_id")
    private String locationId;
    @JsonProperty("utility_id")
    private String utilityId;
    @JsonProperty("payment_info_id")
    private String paymentInfoId;
    @JsonProperty("subscription_id")
    private String subscriptionId;
    @JsonProperty("Premise No")
    private String premiseNo;
    @JsonProperty("Monthly Production")
    private Double mpa;
    @JsonProperty("Garden ID")
    private String gardenID;
    @JsonProperty("Calendar Month")
    private String calendarMonth;
    private String issue;
    @JsonProperty("Years")
    private String years;
    @JsonProperty("Months")
    private String months;
    @JsonProperty("Days")
    private String days;
    @JsonProperty("Projected")
    private String projected;
}
