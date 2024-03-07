package com.solar.api.tenant.mapper.tiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UtilityInformationTile {

    private Long caUtility;
    private Long entityId;
    private String accountHolderName;
    private String utilityProvider;
    private String premiseNumber;
    private Long averageMonthlyBill;
    private String utilityAccountAddress;
}
