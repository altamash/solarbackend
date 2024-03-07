package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.companyPreference.Banner;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long>, BannerRepositoryCustom {

    @Query(value = "select idx from Banner b order by createdAt desc", nativeQuery = true)
    List<Integer> getLastIndex();

    Banner findByIdxAndCompanyPreference(Integer indexId, CompanyPreference companyPreference);

    List<Banner> findByCompanyPreference(CompanyPreference companyPreference);
}
