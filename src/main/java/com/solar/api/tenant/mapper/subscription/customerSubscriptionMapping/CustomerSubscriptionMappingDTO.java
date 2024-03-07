package com.solar.api.tenant.mapper.subscription.customerSubscriptionMapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHeadDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerSubscriptionMappingDTO {

    private Long id;
    private Long subscriptionId;
    private Long subscriptionRateMatrixId;
    private String rateCode;
    private String value;
    private String defaultValue;
    private Integer level;
    private String status;
    private Date effectiveDate;
    private Date endDate;
    private Integer hourOfDay;
    private SubscriptionRateMatrixHeadDTO subscriptionRateMatrixHead;
    private MeasureDefinitionTenantDTO measureDefinition;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
