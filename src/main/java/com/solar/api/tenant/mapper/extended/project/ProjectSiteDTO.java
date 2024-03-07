package com.solar.api.tenant.mapper.extended.project;

import com.solar.api.tenant.mapper.extended.physicalLocation.SiteDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectSiteDTO {

    private Long id;
    private Long projectId;
    private Long siteId;
    private String readiness;
    private Long siteManagerId;
    private String manpowerValue;
    private String inventoryValue;
    private SiteDTO site;
}
