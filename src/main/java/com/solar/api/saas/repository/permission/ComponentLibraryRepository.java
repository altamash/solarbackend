package com.solar.api.saas.repository.permission;

import com.solar.api.saas.model.permission.component.ComponentLibrary;
import com.solar.api.saas.model.permission.component.ComponentTypeProvision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ComponentLibraryRepository extends JpaRepository<ComponentLibrary, Long> {

    List<ComponentLibrary> findByLevel(Integer level);

    ComponentLibrary findByComponentName(String componentName);

    List<ComponentLibrary> findByParentId(Long parentId);

    List<ComponentLibrary> findByComponentTypeProvision(ComponentTypeProvision componentTypeProvision);

    @Query("SELECT MAX(c.id) FROM ComponentLibrary c WHERE c.id < :nextIdGroup")
    Long getLastIdentifier(Long nextIdGroup);
}
