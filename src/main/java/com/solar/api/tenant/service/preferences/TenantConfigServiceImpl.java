package com.solar.api.tenant.service.preferences;

import com.solar.api.AppConstants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.preferences.TenantConfigMapper;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.repository.TenantConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TenantConfigServiceImpl implements TenantConfigService {
    @Autowired
    private TenantConfigRepository tenantConfigRepository;

    @Override
    public TenantConfig add(TenantConfig tenantConfig) throws Exception {
        return tenantConfigRepository.save(tenantConfig);
    }

    @Override
    public TenantConfig update(TenantConfig tenantConfig) throws Exception {
        if (tenantConfig.getId() != null) {
            TenantConfig tenantConfigData = tenantConfigRepository.getOne(tenantConfig.getId());
            if (tenantConfigData == null) {
                throw new NotFoundException(TenantConfig.class, tenantConfig.getId());
            }
            tenantConfigData = TenantConfigMapper.toUpdateTenantConfig(tenantConfigData, tenantConfig);
            return tenantConfigRepository.save(tenantConfigData);
        }
        return tenantConfigRepository.save(tenantConfig);
    }

    @Override
    public TenantConfig findById(Long id) throws Exception {
        return tenantConfigRepository.findById(id).orElseThrow(() -> new NotFoundException(TenantConfig.class, id));
    }

    @Override
    public Optional<TenantConfig> findByParameter(String parameter) throws Exception {
        return tenantConfigRepository.findByParameter(parameter);
    }

    @Override
    public List<TenantConfig> findAllByParameterIn(List<String> parameters) {
        return tenantConfigRepository.findAllByParameterIn(parameters);
    }

    @Override
    public List<TenantConfig> findAll() throws Exception {
        return tenantConfigRepository.findAllByIsVisible(true);
    }

    @Override
    public TenantConfig findByCategory(String category) throws Exception {
        return tenantConfigRepository.findFirstByCategory(category);
    }

    /**
     * Description: Method to return list of email domains in tenant config
     *
     * @return
     * @throws Exception
     * @author: ibtehaj
     */
    @Override
    public Map findAllEmailDomain() {
        Map response = new HashMap();
        List<String> emailDomains = new ArrayList<>();
        try {
            emailDomains = tenantConfigRepository.findAllEmailDomain(AppConstants.EMAIL_DOMAIN);
        } catch (Exception e) {
            response.put("data", null);
            response.put("message", e.getMessage());
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value() + "");
            return response;
        }

        return fillMap(emailDomains, response);
    }

    private Map fillMap(List<String> emailDomains, Map response) {

        if (emailDomains.size() > 0) {
            response.put("code", HttpStatus.OK);
            response.put("message", "Email Domains Found Successfully");
            response.put("data", emailDomains);
        } else {
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("message", "No Email Domains Present");
            response.put("data", null);
        }
        return response;
    }
}
