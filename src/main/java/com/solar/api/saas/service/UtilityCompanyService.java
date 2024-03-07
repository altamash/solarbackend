package com.solar.api.saas.service;

import com.solar.api.saas.model.UtilityCompany;

import java.util.List;

public interface UtilityCompanyService {

    UtilityCompany saveOrUpdate(UtilityCompany utilityCompany);

    UtilityCompany findById(Long id);

    UtilityCompany findByCompanyName(String companyName);

    List<UtilityCompany> findByUtilityType(String utilityType);

    UtilityCompany findByEmail(String email);

    List<UtilityCompany> findAll();

    void delete(Long id);

    void deleteAll();
}
