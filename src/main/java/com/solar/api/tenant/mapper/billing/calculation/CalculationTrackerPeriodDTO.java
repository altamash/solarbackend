package com.solar.api.tenant.mapper.billing.calculation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.saas.model.permission.component.ComponentLibrary;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculationTrackerPeriodDTO  {


    String periodNumeric;

    String period;


}
