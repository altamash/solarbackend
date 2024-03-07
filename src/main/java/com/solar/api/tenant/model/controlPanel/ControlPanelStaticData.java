package com.solar.api.tenant.model.controlPanel;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "cp_static_data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ControlPanelStaticData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
