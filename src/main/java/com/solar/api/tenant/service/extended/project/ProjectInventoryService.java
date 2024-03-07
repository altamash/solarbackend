package com.solar.api.tenant.service.extended.project;

import com.solar.api.tenant.mapper.extended.project.PagedProjectInventorySerialDTO;
import com.solar.api.tenant.model.extended.project.ProjectInventory;
import com.solar.api.tenant.model.extended.project.ProjectInventorySerial;

import java.util.List;

public interface ProjectInventoryService {

    ProjectInventory save(ProjectInventory projectInventory);

    ProjectInventory findById(Long projectInventoryId);

    ProjectInventory update(ProjectInventory projectInventory);

    List<ProjectInventory> findAllByProjectId(Long projectId);

    void deleteById(Long id);

    /////Project Inventory Serial
    List<ProjectInventorySerial> updateInventorySerials(List<ProjectInventorySerial> projectInventorySerials);

    PagedProjectInventorySerialDTO findAllByProjectInventory(Long projectInventoryId, int pageNumber, Integer pageSize, String sort);

    void deleteInventorySerials(List<ProjectInventorySerial> projectInventorySerials);

    void deleteInventorySerialsById(Long id);
}
