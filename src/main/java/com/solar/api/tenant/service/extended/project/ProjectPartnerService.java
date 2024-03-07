package com.solar.api.tenant.service.extended.project;

import com.solar.api.tenant.model.extended.project.ProjectPartner;

import java.util.List;

public interface ProjectPartnerService {

    List<ProjectPartner> findAllByProjectId(Long projectId);

    ProjectPartner save(ProjectPartner projectPartner);

    void deleteById(Long id);

    ProjectPartner findById(Long id);

}
