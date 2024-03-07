package com.solar.api.saas.service.extended;

import com.solar.api.saas.model.permission.component.ComponentLibrary;

import java.util.List;
import java.util.Map;

public interface ComponentLibraryService {

    Map<String, List<String>> addComponents(Long provisionId, String option);

    List<ComponentLibrary>  addComponents(Long provisionId, Long parentId, List<String> selectors);

    ComponentLibrary saveOrUpdate(ComponentLibrary componentLibrary, Long componentTypeProvisionId);

    ComponentLibrary findById(Long id);

    List<ComponentLibrary> findByLevel(Integer level);

    ComponentLibrary findByComponentName(String componentName);

    List<ComponentLibrary> findByParentId(Long parentId);

    List<ComponentLibrary> findByComponentTypeProvision(Long componentTypeProvision);

    List<ComponentLibrary> findSubLevelsByLevel(Integer level);

    List<ComponentLibrary> findAll();

    Long getNextIdentifier(String compReference);

    void delete(Long id);

    void deleteAll();
}
