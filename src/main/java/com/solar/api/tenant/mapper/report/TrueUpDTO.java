package com.solar.api.tenant.mapper.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrueUpDTO {

    private Long id;
    private Long acctId;
    private Long subscriptionId;
    private Long subscriptionRateMatrixId;
    private Date startDate;
    private Date endDate;
    private String period;
    private String subscriptionType;
    private String reportUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
