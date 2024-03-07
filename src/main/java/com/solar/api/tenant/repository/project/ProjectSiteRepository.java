package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.ProjectSite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectSiteRepository extends JpaRepository<ProjectSite, Long> {

    List<ProjectSite> findAllByProjectId(Long projectId);

    ProjectSite deleteByProjectIdAndSiteId(Long projectId, Long siteId);
}
