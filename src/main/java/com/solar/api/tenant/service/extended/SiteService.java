package com.solar.api.tenant.service.extended;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.service.integration.Content;
import com.solar.api.tenant.mapper.extended.physicalLocation.SiteDTO;
import com.solar.api.tenant.model.extended.physicalLocation.Site;

import java.util.List;

public interface SiteService {

    Site findById(Long id);
    List<Site> findAll();
    ObjectNode saveOrUpdate(SiteDTO siteDTO);
    List<SiteDTO> getSiteAndPhysicalLocationDetail(List<Content> siteDTOList);
    List<SiteDTO> findAllBySiteType(String siteType);
    List<Site> findAllWithPhysicalLocations();
    Site getSiteByRefId(Long refid);
    ObjectNode updateSiteStatus(Long siteId, Boolean isActive);
    ObjectNode deleteById(Long id);

    List<SiteDTO> findAllBySiteByCategoryAndIsDeleted(String category,Boolean isDeleted);

}
