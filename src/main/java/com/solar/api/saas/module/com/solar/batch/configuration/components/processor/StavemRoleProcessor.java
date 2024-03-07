package com.solar.api.saas.module.com.solar.batch.configuration.components.processor;

import com.solar.api.tenant.model.stavem.StavemRoles;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class StavemRoleProcessor implements ItemProcessor<StavemRoles, StavemRoles> {

    @Override
    public StavemRoles process(StavemRoles stavemRoles) throws Exception {
        return stavemRoles;
    }
}
