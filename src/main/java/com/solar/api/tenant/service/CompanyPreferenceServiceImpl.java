package com.solar.api.tenant.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.mapper.companyPreference.CompanyPreferenceMapper;
import com.solar.api.tenant.model.companyPreference.Banner;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.repository.BannerRepository;
import com.solar.api.tenant.repository.CompanyPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class CompanyPreferenceServiceImpl implements CompanyPreferenceService {

    @Autowired
    private CompanyPreferenceRepository companyPreferenceRepository;
    @Autowired
    private BannerRepository bannerRepository;
    @Autowired
    private Utility utility;

    @Override
    public CompanyPreference addOrUpdate(CompanyPreference companyPreference) {
        if (companyPreference.getId() != null) {
            CompanyPreference companyPreferenceData = findById(companyPreference.getId());
            if (companyPreferenceData == null) {
                throw new NotFoundException(CompanyPreference.class, companyPreference.getId());
            }
            companyPreferenceData = CompanyPreferenceMapper.toUpdateCompanyPreference(companyPreferenceData,
                    companyPreference);
            return companyPreferenceRepository.save(companyPreferenceData);
        }
        return companyPreferenceRepository.save(companyPreference);
    }

    @Override
    public CompanyPreference findById(Long id) {
        return companyPreferenceRepository.findById(id).orElseThrow(() -> new NotFoundException(CompanyPreference.class, id));
    }

    @Override
    public CompanyPreference getCurrentCompanyPreference() {
        return utility.getCompanyPreference();
    }

    @Override
    public CompanyPreference getCurrentCompanyPreference(Long compKey) {
        return  companyPreferenceRepository.findByCompanyKey(compKey);
    }
    @Override
    public Long getCompanyKey() {
        return utility.getCompKey();
    }

    /**
     * Get FaqUrl
     *
     * @param compKey
     * @return
     */
    public String getCompanyFaqUrl(Long compKey) {
        CompanyPreference companyPreference = findByCompanyKey(compKey);
        if (companyPreference == null) {
            throw new NotFoundException(CompanyPreference.class, compKey);
        }
        return companyPreference.getFaqURL();
    }

    @Override
    public String getCurrentCompanyFaqUrl() {
        return utility.getCompanyPreference().getFaqURL();
    }

    /**
     * Get LogoUrl
     *
     * @param compKey
     * @return
     */
    public String getCompanyLogoUrl(Long compKey) {
        CompanyPreference companyPreference = findByCompanyKey(compKey);
        if (companyPreference == null) {
            throw new NotFoundException(CompanyPreference.class, compKey);
        }
        return companyPreference.getLogo();
    }

    @Override
    public String getCurrentCompanyLogoUrl() {
        return utility.getCompanyPreference().getLogo();
    }

    @Override
    public List<String> getCompanyBannerUrls(Long compKey) {
        CompanyPreference companyPreference = findByCompanyKey(compKey);
        List<Banner> banners = bannerRepository.findByCompanyPreference(companyPreference);
        if (banners == null) {
            throw new NotFoundException(Banner.class, compKey);
        }
        List<String> bannerUrls = new ArrayList<>();
        banners.forEach(data -> {
            bannerUrls.add(data.getUrl());
        });
        return bannerUrls;
    }

    @Override
    public List<String> getCurrentCompanyBannerUrls() {
        List<Banner> banners = bannerRepository.findByCompanyPreference(utility.getCompanyPreference());
        List<String> bannerUrls = new ArrayList<>();
        banners.forEach(data -> {
            bannerUrls.add(data.getUrl());
        });
        return bannerUrls;
    }

    @Override
    public CompanyPreference findByCompanyKey(Long id) {
        return companyPreferenceRepository.findByCompanyKeyFetchBanners(id);
    }

    @Override
    public CompanyPreference findByCompanyCode(String companyCode) {
        CompanyPreference company = companyPreferenceRepository.findByCompanyCode(companyCode);
        if (company == null) {
            throw new NotFoundException("compKey not found with identifier " + companyCode);
        }
        return company;
    }

    @Override
    public List<CompanyPreference> findAll() {
        return companyPreferenceRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        companyPreferenceRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        companyPreferenceRepository.deleteAll();
    }

    @Transactional
    @Override
    public void deleteByCompanyPreferenceId(Long companyPreferenceId) {
        Banner banner = bannerRepository.findById(
                companyPreferenceId).orElseThrow(() -> new NotFoundException(
                CompanyPreference.class, companyPreferenceId));
        bannerRepository.delete(banner);
    }

    @Override
    public void deleteBanner(CompanyPreference companyPreference) {

    }


}
