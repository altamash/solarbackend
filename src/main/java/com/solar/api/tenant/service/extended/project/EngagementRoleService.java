package com.solar.api.tenant.service.extended.project;


import com.solar.api.tenant.model.extended.project.EngagementRole;

import java.util.List;
public interface EngagementRoleService {

    EngagementRole saveOrUpdate(EngagementRole engagementRole);

    List<EngagementRole> findByGlReferenceCode(Long glReferenceCode);

    EngagementRole findById(Long id);

    List<EngagementRole> findByGlReferenceCodeAndRoleName(Long glReferenceCode, String roleName);

    List<EngagementRole> findByExternalRoleIdAndRoleName(String externalRoleId, String phaseName);

    EngagementRole findByExternalRoleId(String externalRoleId);

    List<EngagementRole> findAll();

}
