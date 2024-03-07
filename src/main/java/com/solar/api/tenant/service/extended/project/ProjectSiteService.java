package com.solar.api.tenant.service.extended.project;

import com.solar.api.tenant.model.extended.project.ProjectSite;

import java.util.List;

public interface ProjectSiteService {

    ProjectSite save(ProjectSite projectSite);

    List<ProjectSite> findAllByProjectId(Long projectId);

    List<ProjectSite> findAll();

    ProjectSite update(ProjectSite projectSite);

    void deleteByProjectIdAndSiteId(Long projectId, Long siteId);

}
