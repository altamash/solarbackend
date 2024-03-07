package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.ProjectInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectInventoryRepository extends JpaRepository<ProjectInventory, Long> {

    List<ProjectInventory> findAllByProjectId(Long projectId);

    ProjectInventory findByAssetIdAndProjectId(Long assetId, Long projectId);
}
