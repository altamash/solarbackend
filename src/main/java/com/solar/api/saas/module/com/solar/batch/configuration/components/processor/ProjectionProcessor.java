package com.solar.api.saas.module.com.solar.batch.configuration.components.processor;

import com.solar.api.saas.service.process.upload.mapper.MonitorReadingDailyCSV;
import com.solar.api.tenant.model.billingCredits.BillingCreditsCsv;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ProjectionProcessor implements ItemProcessor<MonitorReadingDailyCSV, MonitorReadingDailyCSV> {

    @Override
    public MonitorReadingDailyCSV process(MonitorReadingDailyCSV monitorReadingDailyCSV) throws Exception {

        return monitorReadingDailyCSV;
//        if (billingCreditsCsv.getPaymentType().equals("S")) {
//            if (billingCreditsCsv.getMonthlyProductionAllocationinkWh() == null
//                    || billingCreditsCsv.getMonthlyProductionAllocationinkWh().equals("")) {
//                return null;
//            }
//            if (billingCreditsCsv.getTariffRate() == null
//                    || billingCreditsCsv.getTariffRate().equals("")
//                    || billingCreditsCsv.getTariffRate().equals("0")) {
//                return null;
//            }
//            if (billingCreditsCsv.getBillCredit() == null
//                    || billingCreditsCsv.getBillCredit().equals("")
//                    || billingCreditsCsv.getBillCredit().equals("0")) {
//                return null;
//            } else {
//                return billingCreditsCsv;
//            }
//        } else {
//            return null;
//        }
    }
}
