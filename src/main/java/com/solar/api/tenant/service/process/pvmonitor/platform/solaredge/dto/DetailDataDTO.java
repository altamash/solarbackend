package com.solar.api.tenant.service.process.pvmonitor.platform.solaredge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailDataDTO {
    private String sn;
    private Double eday;
    private Double emonth;
    private Double etotal;
    private Double capacity;
    private Double pac;
    private String date;
    private List<DetailDataDTO> detailDataList;
    private List<DataPVDTO> pv;

}
