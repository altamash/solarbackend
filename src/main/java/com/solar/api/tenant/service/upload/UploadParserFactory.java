package com.solar.api.tenant.service.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class UploadParserFactory {

    @Autowired
    private ApplicationContext context;

    public Object getParser(String parserLocation) {
        if (EAssociatedParser.get(parserLocation) == null) {
            return null;
        }
        if (EAssociatedParser.get(parserLocation) == EAssociatedParser.MIGRATION) {
            return context.getBean("migrationServiceImpl");
        } else if (EAssociatedParser.get(parserLocation) == EAssociatedParser.UPLOAD) {
            return context.getBean("bulkUploadServiceImpl");
        } else if (EAssociatedParser.get(parserLocation) == EAssociatedParser.PROJECT) {
            return context.getBean("projectUploadServiceImpl");
        } else if (EAssociatedParser.get(parserLocation) == EAssociatedParser.UPLOAD_CUSTOMERS_V2) {
            return context.getBean("bulkUploadCustomersServiceImpl");
        }
        return null;
    }
}
