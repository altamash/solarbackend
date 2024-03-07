package com.solar.api.tenant.mapper.workOrder;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgnazationTemplateMasterWoDTO {
    private Long orgId;
    private String unitName;
    private String unitManager;
    private String unitType;
    private String address;
    private String state;
    private String country;
    private String geoLat;
    private String geoLong;
}

