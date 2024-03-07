package com.solar.api.saas.service.integration.mongo.response.subscription;

import java.time.LocalDateTime;
import java.util.Date;

public interface SubscriptionDetailTemplate {
    String getPremiseNo();
    String getVariantName();
    Long getUserAcctId();
    Date getActiveSince() ;
    String getSubId();
    String getStatus();
    String getVariantId();

    String getSubName();

    Long getSiteLocationId();

    String getAdd1();
    String getAdd2();
    String getAdd3();
    String getExt1();
    String getExt2();
    String getZipCode();
    Long getCustomerAddress();
    String getActive() ;

}
