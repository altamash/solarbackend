package com.solar.api.saas.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.model.UtilityCompany;
import com.solar.api.saas.repository.UtilityCompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("masterTransactionManager")
public class UtilityCompanyServiceImpl implements UtilityCompanyService {

    @Autowired
    private UtilityCompanyRepository repository;

    @Override
    public UtilityCompany saveOrUpdate(UtilityCompany utilityCompany) {
        return repository.save(utilityCompany);
    }

    @Override
    public UtilityCompany findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(UtilityCompany.class, id));
    }

    @Override
    public UtilityCompany findByCompanyName(String companyName) {
        return repository.findByCompanyName(companyName);
    }

    @Override
    public List<UtilityCompany> findByUtilityType(String utilityType) {
        return repository.findByUtilityType(utilityType);
    }

    @Override
    public UtilityCompany findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public List<UtilityCompany> findAll() {
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
}
