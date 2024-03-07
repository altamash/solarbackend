package com.solar.api.tenant.mapper.extended.project;

import com.solar.api.tenant.mapper.extended.partner.PartnerHeadDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectPartnerDTO {

    private Long id;
    private Long projectId;
    private Long partnerId;
    private String associationType;
    private String revenueCap;
    private String actualRevenue;
    private String actualRevenueUsed;
    private String status;
    private Date estimatedStartDate;
    private Date estimatedEndDate;
    private PartnerHeadDTO partnerHead;
}
