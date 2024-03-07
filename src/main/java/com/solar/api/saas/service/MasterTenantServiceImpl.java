package com.solar.api.saas.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.exception.SolarApiException;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.mapper.tenant.MasterTenantDTO;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.user.EUserStatus;
import com.solar.api.tenant.repository.CompanyPreferenceRepository;
import com.solar.api.tenant.repository.TenantConfigRepository;
import com.solar.api.tenant.service.CompanyPreferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
//@Transactional("masterTransactionManager")
public class MasterTenantServiceImpl implements MasterTenantService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Value("${app.storage.publicContainer}")
    private String publicStorageContainer;
    @Autowired
    private MasterTenantRepository masterTenantRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private TenantConfigRepository tenantConfigRepository;
    @Autowired
    private CompanyPreferenceService companyPreferenceService;


    @Override
    public MasterTenant save(MasterTenant masterTenant) {
        return masterTenantRepository.save(masterTenant);
    }

    @Override
    public MasterTenant update(MasterTenant masterTenant) {
        return masterTenantRepository.save(masterTenant);
    }

    public MasterTenant findById(Long id) {
        LOGGER.info("findByClientId() method call...");
        MasterTenant masterTenant = masterTenantRepository.findByIdFetchTenantRoles(id);
        if (null == masterTenant || masterTenant.getStatus().toUpperCase().equals(EUserStatus.INACTIVE)) {
            throw new SolarApiException("Please contact service provider.");
        }
        return masterTenant;
    }

    @Override
    public MasterTenant findByUserName(String userName) {
        return masterTenantRepository.findByUserName(userName);
    }

    @Override
    public MasterTenant findByUserNameFetchTenantRoles(String userName) {
        return masterTenantRepository.findByUserNameFetchTenantPermissions(userName);
    }

    @Override
    public MasterTenant findByCompanyKey(Long companyKey) {
        return masterTenantRepository.findByCompanyKey(companyKey);
    }

    @Override
    public MasterTenant findByCompanyCode(String companyCode) {
        MasterTenant masterTenant = masterTenantRepository.findByCompanyCode(companyCode);
        if (masterTenant == null) {
            throw new NotFoundException("Company Not Found");
        }
        return masterTenant;
    }

    @Override
    public MasterTenant findByDbName(String dbName) {
        return masterTenantRepository.findByDbName(dbName);
    }

    @Override
    public MasterTenant setCurrentDb(Long id) {
        MasterTenant masterTenant = findById(id);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        return masterTenant;
    }

    @Override
    public List<MasterTenant> findAll() {
        return masterTenantRepository.findAll().stream()
                .filter(tenant -> tenant.getEnabled() && tenant.getValid())
                .collect(Collectors.toList());
    }

    @Override
    public List<MasterTenant> findAllFetchTenantRoles() {
        return masterTenantRepository.findAllFetchTenantRoles();
    }

    @Override
    public void delete(Long tenantClientId) {
        MasterTenant masterTenant =
                masterTenantRepository.findById(tenantClientId).orElseThrow(() -> new NotFoundException(MasterTenant.class, tenantClientId));
        masterTenantRepository.delete(masterTenant);
    }

    @Override
    public void deleteAll() {
        masterTenantRepository.deleteAll();
    }

    @Override
    public List<MasterTenantDTO> findAllByCompanyNameLike(String companyName) {
        List<MasterTenantDTO> masterTenantDTOS = new ArrayList<>();
        List<MasterTenant> masterTenant = masterTenantRepository.findAllByCompanyNameContainingIgnoreCase(companyName);
        if (Objects.isNull(masterTenant)) {
            return null;
        } else {
            masterTenant.forEach(m -> {
                try{
                    masterTenantDTOS.add(MasterTenantDTO.builder()
                            .id(m.getId())
                            .dbName(m.getDbName())
                            .loginUrl(m.getLoginUrl())
                            .status(m.getStatus())
                            .companyCode(m.getCompanyCode())
                            .companyKey(m.getCompanyKey())
                            .companyName(m.getCompanyName())
                            .companyLogo(m.getCompanyLogo())
                            .build());
                } catch(Exception e) {
                    e.getMessage();
                }
            });
        }
        return masterTenantDTOS;
    }

    @Override
    public MasterTenantDTO findByLoginUrlLike(String keyword, boolean isMobileLanding) {
        MasterTenantDTO masterTenantDTO = null ;
        MasterTenant masterTenant = masterTenantRepository.findByLoginUrlContaining(keyword);
        if (Objects.isNull(masterTenant)) {
            return null;
        } else {
            try{
                List<String> landingImagesUrl = new ArrayList<>();
                DBContextHolder.setTenantName(masterTenant.getDbName());
                DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
                CompanyPreference companyPreference = companyPreferenceService.findByCompanyKey(Long.valueOf(masterTenant.getId()));
                String landingImagesDirPath = null;
                if(!isMobileLanding){
                    landingImagesDirPath =  companyPreference.getLandingImagesUrl();
                }else{
                    landingImagesDirPath =  companyPreference.getMobileLandingImagesUrl();
                }
               if(landingImagesDirPath != null && !landingImagesDirPath.isEmpty()) {
                   landingImagesUrl.addAll(storageService.getBlobUrl(landingImagesDirPath,publicStorageContainer));
               }
                masterTenantDTO = MasterTenantDTO.builder()
                        .id(masterTenant.getId())
                        .dbName(masterTenant.getDbName())
                        .loginUrl(masterTenant.getLoginUrl())
                        .status(masterTenant.getStatus())
                        .companyCode(masterTenant.getCompanyCode())
                        .companyKey(masterTenant.getCompanyKey())
                        .companyName(masterTenant.getCompanyName())
                        .companyLogo(masterTenant.getCompanyLogo())
                        .landingText(companyPreference.getLandingText()!=null ? companyPreference.getLandingText() : "")
                        .landingDescription(companyPreference.getLandingDescription()!=null ? companyPreference.getLandingDescription() : "")
                        .landingImagesUrl(landingImagesUrl)
                        .build();
            } catch(Exception e) {
                LOGGER.error(e.getMessage(),e);
            }
        }
        return masterTenantDTO;
    }
}
