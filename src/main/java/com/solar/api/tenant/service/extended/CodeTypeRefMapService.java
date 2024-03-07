package com.solar.api.tenant.service.extended;

import com.solar.api.tenant.model.extended.CodeTypeRefMap;

import java.util.List;

public interface CodeTypeRefMapService {

    CodeTypeRefMap save(CodeTypeRefMap codeTypeRefMap);

    CodeTypeRefMap update(CodeTypeRefMap codeTypeRefMap);

    CodeTypeRefMap findById(Long id);

    List<CodeTypeRefMap> findAllByRegModuleId(Long id);

    List<CodeTypeRefMap> findAll();

    void delete(Long id);

    void deleteAll();

    String getRefTable(String refCode);
}
