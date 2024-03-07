package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompanyPreferenceRepository extends JpaRepository<CompanyPreference, Long> {

    CompanyPreference findByCompanyKey(Long companyKey);

    @Query("select c from CompanyPreference c LEFT JOIN FETCH c.banners where c.companyKey = :companyKey")
    CompanyPreference findByCompanyKeyFetchBanners(Long companyKey);

    CompanyPreference findByCompanyCode(String companyCode);

}
