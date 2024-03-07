package com.solar.api.tenant.service.extended.register;

import com.solar.api.tenant.model.extended.register.InMemoryRegisterHierarchy;
import com.solar.api.tenant.model.extended.register.RegisterHierarchy;

import java.util.List;

public interface RegisterHierarchyService {

    RegisterHierarchy save(RegisterHierarchy hierarchy);

    void saveAll(List<RegisterHierarchy> hierarchies);

    RegisterHierarchy update(RegisterHierarchy hierarchy);

    RegisterHierarchy findById(Long id);

    List<RegisterHierarchy> findByLevel(Integer level);

    List<RegisterHierarchy> findByName(String name);

    RegisterHierarchy findByCode(String code);

    List<RegisterHierarchy> findByParent(String parent);

    List<RegisterHierarchy> findByParentId(Long parentId);

    List<RegisterHierarchy> findSubLevelsByLevel(Integer level);

    List<RegisterHierarchy> findAll();

    InMemoryRegisterHierarchy addInMemoryRegisterHierarchies();

    List<RegisterHierarchy> getInMemoryRegisterHierarchies();

    RegisterHierarchy getInMemoryRegisterHierarchyById(Long id);

    RegisterHierarchy getTopLevelHierarchy(Long id);

    void delete(Long id);

    void deleteAll();
}
