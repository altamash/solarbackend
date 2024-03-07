package com.solar.api.tenant.model.dataexport.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.dataexport.powermonitoring.DataDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)


public class CustomerExportDTO {
    private List<String> customerType;
    private List<String> status;

}


