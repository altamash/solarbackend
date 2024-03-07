package com.solar.api.tenant.model.dataexport.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerExportSalesAgent {

    private String name;
    private Long id;
    private String repId;

    public CustomerExportSalesAgent(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public CustomerExportSalesAgent(String name, Long id, Long repId) {
        this.name = name;
        this.id = id;
        this.repId = String.valueOf(repId);
    }
}
