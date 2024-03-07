package com.solar.api.saas.module.com.solar.batch.configuration.components.writer;

import com.solar.api.tenant.model.stavem.StavemRoles;
import com.solar.api.tenant.repository.StavemRolesRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Component
public class StavemRoleWriter implements ItemWriter<StavemRoles> {

    @Autowired
    private StavemRolesRepository stavemRolesRepository;

    @Override
    public void write(List<? extends StavemRoles> csv) throws Exception {
        stavemRolesRepository.saveAll((List<StavemRoles>) csv);
    }
}
