package com.solar.api.tenant.mapper.billing.calculation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalTrackerGraphDTO {

    private String period;
    private String status;
    private List<Double> amount;
    private List<Integer> count;
}
