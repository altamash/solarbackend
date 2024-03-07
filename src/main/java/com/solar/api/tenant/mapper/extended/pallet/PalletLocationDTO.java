package com.solar.api.tenant.mapper.extended.pallet;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PalletLocationDTO {

    private Long id;
    private Long palletId;
    private String seqNo;
    private Long locationId;
    private Long rackId;
    private String opened;
    private String openedBy;
    private String openDatetime;
    private String vehicleType;
    private String vehicleRefNum;
    private String mobileRackRef;
    private Long lane;
    private Long depth;
    private Long height;
    private String transferId;
    private String notes;
}
