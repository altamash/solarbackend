package com.solar.api.tenant.service.etl;


import com.solar.api.tenant.mapper.ca.CaUserTemplateDTO;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.contract.EEntityType;
import com.solar.api.tenant.model.user.userMapping.UserMapping;

import java.util.Optional;

public interface ETLService {

    /*
        this is just an ETL function
         */
    void createAcquisitionProjectForUsersETL(Long compKey);

    void updateETLTable(UserMapping userMapping);

    Optional<TenantConfig> getTenantConfig(EEntityType entityType) throws Exception;

    String fetchTemplateJson(TenantConfig tenantConfig);
}
