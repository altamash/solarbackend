package com.solar.api.tenant.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyticalCalculationArchiveDTO {

    private Long id;
    private Long accountId;
    private Long subscriptionId;
    private String scope;
    private String analysis;
    private Double oldValue;
    private Double currentValue;
    private Long movingAverage;
    private Date lastUpdatedDatetime;
}
