package com.solar.api.tenant.service.extended;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.CodeTypeRefMap;
import com.solar.api.tenant.repository.CodeTypeRefMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class CodeTypeRefMapServiceImpl implements CodeTypeRefMapService {

    @Autowired
    private CodeTypeRefMapRepository repository;

    @Override
    public CodeTypeRefMap save(CodeTypeRefMap codeTypeRefMap) {
        return repository.save(codeTypeRefMap);
    }

    @Override
    public CodeTypeRefMap update(CodeTypeRefMap codeTypeRefMap) {
        return repository.save(codeTypeRefMap);
    }

    @Override
    public CodeTypeRefMap findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(CodeTypeRefMap.class, id));
    }

    @Override
    public List<CodeTypeRefMap> findAllByRegModuleId(Long id) {
        return repository.findAllByRegModuleId(id);
    }

    @Override
    public List<CodeTypeRefMap> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public String getRefTable(String refCode) {
        CodeTypeRefMap codeTypeRefMap = repository.findByRefCode(refCode);
        return codeTypeRefMap.getRefTable();
    }
}
