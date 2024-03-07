package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.EngagementRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EngagementRoleRepository extends JpaRepository<EngagementRole, Long> {

    List<EngagementRole> findByGlReferenceCode(Long glReferenceCode);
    List<EngagementRole> findByGlReferenceCodeAndRoleName(Long glReferenceCode, String roleName);
    List<EngagementRole> findByExternalRoleIdAndRoleName(String glReferenceCode, String roleName);
    EngagementRole findByExternalRoleId(String externalRoleId);
}
