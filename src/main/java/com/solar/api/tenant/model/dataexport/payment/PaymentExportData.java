package com.solar.api.tenant.model.dataexport.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.dataexport.employee.EmployeeDataDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentExportData {

    private List<String> customerType;
    private List<PaymentDataDTO> customers;
    private List<PaymentDataDTO> period;
    private List<Long> billId;
    private List<String> status;
    private List<PaymentDataDTO> source;
    private List<String> errors;

}
