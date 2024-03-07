package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.CodeTypeRefMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeTypeRefMapRepository extends JpaRepository<CodeTypeRefMap, Long> {
    List<CodeTypeRefMap> findAllByRegModuleId(Long regModuleId);
    CodeTypeRefMap findByRegModuleIdAndType(Long regModuleId, String type);
    List<CodeTypeRefMap> findAllByRegModuleIdAndTypeIn(Long regModuleId, List<String> types);
    CodeTypeRefMap findByRefCode(String refCode);
}
