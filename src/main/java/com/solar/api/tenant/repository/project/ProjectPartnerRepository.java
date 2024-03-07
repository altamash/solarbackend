package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.ProjectPartner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectPartnerRepository extends JpaRepository<ProjectPartner, Long> {

    List<ProjectPartner> findAllByProjectId(Long projectId);

}
