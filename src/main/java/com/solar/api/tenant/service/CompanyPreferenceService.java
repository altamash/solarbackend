package com.solar.api.tenant.service;

import com.solar.api.tenant.model.companyPreference.CompanyPreference;

import java.util.List;

public interface CompanyPreferenceService {

    CompanyPreference addOrUpdate(CompanyPreference companyPreference);

    CompanyPreference findById(Long id);

    CompanyPreference getCurrentCompanyPreference();

    Long getCompanyKey();

    String getCompanyFaqUrl(Long compKey);

    String getCurrentCompanyFaqUrl();

    String getCompanyLogoUrl(Long compKey);

    String getCurrentCompanyLogoUrl();

    List<String> getCompanyBannerUrls(Long compKey);

    List<String> getCurrentCompanyBannerUrls();

    CompanyPreference findByCompanyKey(Long id);

    CompanyPreference findByCompanyCode(String companyCode);

    List<CompanyPreference> findAll();

    void delete(Long id);

    void deleteAll();

    void deleteByCompanyPreferenceId(Long companyPreferenceId);

    void deleteBanner(CompanyPreference companyPreference);

    CompanyPreference getCurrentCompanyPreference(Long compKey);



    }
