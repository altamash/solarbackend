package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.register.RegisterHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegisterHierarchyRepository extends JpaRepository<RegisterHierarchy, Long> {

    List<RegisterHierarchy> findByLevel(Integer level);

    List<RegisterHierarchy> findByName(String name);

    RegisterHierarchy findByCode(String code);

    List<RegisterHierarchy> findByParent(String parent);

    List<RegisterHierarchy> findByParentId(Long parentId);

    @Query("select rh.code from RegisterHierarchy rh")
    List<String> getHierarchyCodes();
}
