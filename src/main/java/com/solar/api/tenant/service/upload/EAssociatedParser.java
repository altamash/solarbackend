package com.solar.api.tenant.service.upload;

import com.solar.api.saas.service.process.upload.v2.customer.BulkUploadCustomersServiceImpl;

import java.util.Arrays;

public enum EAssociatedParser {

    MIGRATION("com.solar.api.saas.service.process.migration"),
    UPLOAD("com.solar.api.saas.service.process.upload"),
    PROJECT("com.solar.api.saas.service.process.upload.project"),
    UPLOAD_CUSTOMERS_V2(BulkUploadCustomersServiceImpl.class.getName());

    String parser;

    EAssociatedParser(String parser) {
        this.parser = parser;
    }

    public String getParser() {
        return parser;
    }

    public static EAssociatedParser get(String parser) {
        return Arrays.stream(values()).filter(value -> parser.equalsIgnoreCase(value.parser)).findFirst().orElse(null);
    }
}
