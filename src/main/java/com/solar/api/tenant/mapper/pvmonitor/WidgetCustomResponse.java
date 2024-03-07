package com.solar.api.tenant.mapper.pvmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WidgetCustomResponse {
    private Long userId;
    private String customerName;
    private Double sytemSize;
    private Double currentValue;
    private Double peakValue;
    private Double dailyYield;
    private Double monthlyYield;
    private Double annualYield;
    private Double grossYield;
    private String subscriptionIds;
    private Double treesPlanted;
    private Double co2Reduction;
    private String subscriptionName;
    private String subsId;
    private String image;
    private String invertedBrand;
    private String monitoringBrand;
    private String systemSize;
    private String siteLocation;
    private String variantId;
    private String productId;
    private Long entityId;

}

