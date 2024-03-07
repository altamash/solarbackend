package com.solar.api.saas.module.com.solar.batch.configuration.components.processor;


import com.solar.api.tenant.model.stavem.StavemThroughCSG;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class StavemThroughCSGProcessor implements ItemProcessor<StavemThroughCSG, StavemThroughCSG> {

    @Override
    public StavemThroughCSG process(StavemThroughCSG stavemThroughCSG) throws Exception {
        return stavemThroughCSG;
    }
}
