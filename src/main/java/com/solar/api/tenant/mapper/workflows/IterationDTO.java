package com.solar.api.tenant.mapper.workflows;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IterationDTO {

    String iterationTemplate;
    Long iterationId;
    String billing_code_ph;
    String billing_code_value_ph;
    String percentile_ph;
    String yield_ph;
    String prod_det_date_ph;
    String history_billing_month_ph;
    String history_billing_value_ph;
    String history_billing_value;
    String history_billing_month;
    String percentile;
    String prod_det_date;
    String yield;
    String billing_code;
    String billing_code_value;
    String efficiency_ph;
    String amount_1_ph;
    String amount_2_ph;
    String amount_3_ph;
    String efficiency_value;
    String amount_1_value;
    String amount_2_value;
    String amount_3_value;

}
