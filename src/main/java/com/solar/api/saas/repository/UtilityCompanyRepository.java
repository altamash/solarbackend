package com.solar.api.saas.repository;

import com.solar.api.saas.model.UtilityCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UtilityCompanyRepository extends JpaRepository<UtilityCompany, Long> {
    UtilityCompany findByCompanyName(String companyName);

    List<UtilityCompany> findByUtilityType(String utilityType);

    UtilityCompany findByEmail(String email);
}
