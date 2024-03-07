package com.solar.api.tenant.mapper.controlPanel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ControlPanelDTO {

    private Long id;
    private String variantSize;
    private String noOfSites;
    private String variantOccupancy;
    private String NoOfInverters;
    //    Sunrise & sunset time by date
//    YTD production
//    MTD Production
//    This Week Production
//    Lifetime Production
    private String srcNo;
    private String variantId;
    private String variantName;
    private String variantType;
    private Long orgId;
    private String productCategory;
    private String environmentalContribution;
    private Long premiseNumber;
    private String premiseAllocation;
    private Long premiseEntityId;
    private Long LocId;
    private PhysicalLocationDTO physicalLocationDTO;
}
