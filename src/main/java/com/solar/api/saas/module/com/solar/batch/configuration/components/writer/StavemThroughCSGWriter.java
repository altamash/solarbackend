package com.solar.api.saas.module.com.solar.batch.configuration.components.writer;

import com.solar.api.tenant.model.stavem.StavemThroughCSG;
import com.solar.api.tenant.service.StavemThroughCSGService;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Component
public class StavemThroughCSGWriter implements ItemWriter<StavemThroughCSG> {

    @Autowired
    private StavemThroughCSGService stavemThroughCSGService;

    @Override
    public void write(List<? extends StavemThroughCSG> csv) throws Exception {
        stavemThroughCSGService.saveAll((List<StavemThroughCSG>) csv);
    }
}
