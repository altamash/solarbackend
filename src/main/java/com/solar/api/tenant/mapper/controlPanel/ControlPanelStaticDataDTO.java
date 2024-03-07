package com.solar.api.tenant.mapper.controlPanel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
//@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ControlPanelStaticDataDTO {

    private Long id;
    // @JsonProperty("variant_Size")
    private String variantSize;
    private String noOfSites;
    // @JsonProperty("variant_Occupancy")
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
    private String requestedCapacity;
    private String availableCapacity;
    private Long physicalLocId;
    private PhysicalLocation physicalLocation;

    public ControlPanelStaticDataDTO(Long id, String variantId, String variantName, String variantSize, String variantOccupancy, String availableCapacity, PhysicalLocation physicalLocation) {
        this.id = id;
        this.variantId = variantId;
        this.variantSize = variantSize;
        this.variantOccupancy = variantOccupancy;
        this.availableCapacity = availableCapacity;
        this.physicalLocation = physicalLocation;
        this.variantName = variantName;
    }

    public ControlPanelStaticDataDTO(Long id, String variantSize, String noOfSites, String variantOccupancy, String noOfInverters, String srcNo, String variantId, String variantName, String variantType, Long orgId, String productCategory, String environmentalContribution, Long premiseNumber, String premiseAllocation, Long premiseEntityId, Long locId, String requestedCapacity, String availableCapacity, Long physicalLocId, PhysicalLocation physicalLocation) {
        this.id = id;
        this.variantSize = variantSize;
        this.noOfSites = noOfSites;
        this.variantOccupancy = variantOccupancy;
        NoOfInverters = noOfInverters;
        this.srcNo = srcNo;
        this.variantId = variantId;
        this.variantName = variantName;
        this.variantType = variantType;
        this.orgId = orgId;
        this.productCategory = productCategory;
        this.environmentalContribution = environmentalContribution;
        this.premiseNumber = premiseNumber;
        this.premiseAllocation = premiseAllocation;
        this.premiseEntityId = premiseEntityId;
        LocId = locId;
        this.requestedCapacity = requestedCapacity;
        this.availableCapacity = availableCapacity;
        this.physicalLocId = physicalLocId;
        this.physicalLocation = physicalLocation;
    }
}