package com.solar.api.tenant.service.process.billing.postBillilng;

import com.solar.api.saas.service.process.parser.product.EParserLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class PostBillingParserFactory {

    @Autowired
    private ApplicationContext context;

    public PostBillingCalculation getParser(String location) {
        if (EParserLocation.get(location) == null) {
            return null;
        }
        if (EParserLocation.get(location) == EParserLocation.POST_BILLING_CALCULATION) {
            return (PostBillingCalculation) context.getBean("postBillingCalculationCSGF");
        }
        return null;
    }
}
