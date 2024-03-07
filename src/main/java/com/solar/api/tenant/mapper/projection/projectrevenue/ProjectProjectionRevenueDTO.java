package com.solar.api.tenant.mapper.projection.projectrevenue;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectProjectionRevenueDTO {
    private String efficiency;
    private Double amount1;
    private Double amount2;
    private Double amount3;
}
