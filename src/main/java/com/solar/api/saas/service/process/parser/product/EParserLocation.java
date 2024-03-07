package com.solar.api.saas.service.process.parser.product;

import com.solar.api.saas.service.process.parser.product.csg.ppa.postpaid.InitiatorCSGPPAPost;

import java.util.Arrays;

public enum EParserLocation {

    INITIATOR_CSGF("com.solar.api.tenant.service.process.billing.csg.csgf"),
    BILL_CREDIT_IMPORT("com.solar.api.tenant.service.process.billing.csg.csgf.bc"),
    INITIATOR_CSGR("com.solar.api.tenant.service.process.billing.csg.csgr"),
    BILL_CREDIT_IMPORT_CSGR("com.solar.api.tenant.service.process.billing.csg.csgr"),
    BILL_CREDIT_IMPORT_JSON("com.solar.api.saas.service.process"),
    POST_BILLING_CALCULATION("com.solar.api.tenant.service.process.billing.postBillilng.csg.csgf"),
    BILL_CREDIT_IMPORT_ETL("com.solar.api.saas.module.com.solar.batch.configuration"),
    STAVEM_THROUGH_CSG("com.solar.api.saas.module.com.solar.batch.configuration.stavem"),
    STAVEM_ROLES("com.solar.api.saas.module.com.solar.batch.configuration.stavem"),
    DG_PWG_BILL("com.solar.api.saas.service.process.parser.product.pwgenbill"),
    CSG_PPA_POST(InitiatorCSGPPAPost.class.getPackage().getName()),
    PROJECTION_IMPORT_ETL_YEARLY("com.solar.api.saas.module.com.solar.batch.configuration.MonitorReadingDaily"),
    PROJECTION_IMPORT_ETL_MONTHLY("com.solar.api.saas.module.com.solar.batch.configuration.MonitorReadingDaily"),
    PROJECTION_IMPORT_ETL_DAILY("com.solar.api.saas.module.com.solar.batch.configuration.MonitorReadingDaily"),
    PROJECTION_IMPORT_ETL_QUARTERLY("com.solar.api.saas.module.com.solar.batch.configuration.MonitorReadingDaily");


    String location;

    EParserLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public static EParserLocation get(String location) {
        return Arrays.stream(values()).filter(value -> location.equalsIgnoreCase(value.location)).findFirst().orElse(null);
    }

}
