package com.solar.api.tenant.mapper.extended.pallet;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RackSpaceMapDTO {

    private Long id;
    private String location;
    private String areaCode;
    private String block;
    private String lane;
    private String side;
    private Long height;
    private Long depth;
    private Long externalRefId;
    private String status;
    private String reservedFor;
}
