package com.solar.api.tenant.service.process.pvmonitor.platform.goodwe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorAPIBodyDTO {

    private String sn;              //inverterNo for get by sn api
    private String date;
    private int type;
    private String id;             //inverterNo for yeild ratio api
    private String InverterSn;     //inverterNo for inverter pac by day


    @Override
    public String toString(){
        return  "{\"InverterSn\":\""+InverterSn+"\","+ "\"date\":\""+date+"\","+
                "\"type\":"+type+ ",\"id\":\""+id+"\""+ ",\"sn\":\""+sn+"\""+
                "}";
    }

}
