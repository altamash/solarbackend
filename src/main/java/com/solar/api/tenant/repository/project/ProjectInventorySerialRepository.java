package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.ProjectInventory;
import com.solar.api.tenant.model.extended.project.ProjectInventorySerial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectInventorySerialRepository extends JpaRepository<ProjectInventorySerial, Long> {

    ProjectInventorySerial findByAssetSerialNumberId(Long assetSerialNumberId);

    List<ProjectInventorySerial> findAllByAssetSerialNumberIdIn(List<Long> assetSerialNumbers);

    Page<ProjectInventorySerial> findAllByProjectInventory(Pageable pageable , ProjectInventory projectInventory);
}
