package com.solar.api.saas.model;

import lombok.Getter;

import java.util.Arrays;

public class SaasSchema {

    public static final String SAAS_SCHEMA_NAME = "saas_schema";
    public static final String COMPANY_KEY_HEADER = "Comp-Key";
    public static final int PAGE_SIZE = 20;

    public enum Company {
        EC1001(1001l, "NOVEL", "ec1001", "Novel Energy Solutions");
        private Long compKey;
        private String identifier;
        private String schemaName;
        private String companyName;

        Company(Long compKey, String identifier, String schemaName, String companyName) {
            this.compKey = compKey;
            this.identifier = identifier;
            this.schemaName = schemaName;
            this.companyName = companyName;
        }

        // TODO: check all references
        public Long getCompKey() {
            return compKey;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getSchemaName() {
            return schemaName;
        }

        public String getCompanyName() {
            return companyName;
        }

        public static Company get(String identifier) {
            return Arrays.stream(values()).filter(value -> identifier.equalsIgnoreCase(value.identifier)).findFirst().orElse(null);
        }
    }

    public static class Template {
        @Getter
        public enum Billing {
            CUSTOMER_PAYMENTS("template/billing/payment/csv", "CustomerPayments.csv");
            private String directoryReference;
            private String fileName;

            Billing(String directoryReference, String fileName) {
                this.directoryReference = directoryReference;
                this.fileName = fileName;
            }
        }
    }

}
