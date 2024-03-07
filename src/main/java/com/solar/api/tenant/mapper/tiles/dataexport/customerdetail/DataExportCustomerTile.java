package com.solar.api.tenant.mapper.tiles.dataexport.customerdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.jfree.util.Log;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class DataExportCustomerTile {

    private Long id;
    private String customerName;
    private String customerType;
    private String email;
    private String phone;
    private String zipCode;
    private String salesAgent;
    private String region;
    private String states;
    private String repId;

    public DataExportCustomerTile(Long id , String customerName, String customerType, String email, String phone, String zipCode, String region, String states , Long repId) {
        this.id = id;
        this.customerName = customerName;
        this.customerType = customerType;
        this.email = email;
        this.phone = phone;
        this.zipCode = zipCode;
        this.region = region;
        this.states = states;
        this.repId = String.valueOf(repId);

    }



}
