package com.solar.api.saas.module.com.solar.batch.configuration.components.writer;

import com.solar.api.tenant.mapper.billingCredits.BillingCreditsTempMapper;
import com.solar.api.tenant.model.billingCredits.BillingCredits;
import com.solar.api.tenant.model.billingCredits.BillingCreditsCsv;
import com.solar.api.tenant.model.billingCredits.BillingCreditsTempStage;
import com.solar.api.tenant.repository.BillingCreditsTempStageRepository;
import com.solar.api.tenant.service.BillingCreditsService;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Component
public class BillingCreditsWriter implements ItemWriter<BillingCreditsCsv> {

    @Autowired
    private BillingCreditsTempStageRepository billingCreditsTempStageRepository;

    @Override
    public void write(List<? extends BillingCreditsCsv> csv) throws Exception {
        List<BillingCreditsTempStage> tempCredits = BillingCreditsTempMapper.toBillingCreditsTempStageFromBillingCreditsCsv((List<BillingCreditsCsv>) csv);
        billingCreditsTempStageRepository.deleteAll();
        billingCreditsTempStageRepository.saveAll(tempCredits);
    }
}
