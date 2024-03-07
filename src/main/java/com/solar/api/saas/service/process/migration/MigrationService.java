package com.solar.api.saas.service.process.migration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.service.process.upload.UploadResponse;
import com.solar.api.tenant.model.process.JobManagerTenant;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface MigrationService {
    UploadResponse migrate(EMigrationParserLocation parser, String entity, MultipartFile file,
                           JobManagerTenant jobManagerTenant, Boolean isLegacy) throws IOException,
            ClassNotFoundException;

    UploadResponse migrate(EMigrationParserLocation parser, String entity, File file,
                           ObjectNode requestMessage, Boolean isLegacy) throws IOException,
            ClassNotFoundException;

    UploadResponse importUsersFromCSV(EMigrationParserLocation parser, InputStream inputStream,
                                      JobManagerTenant jobManagerTenant) throws IOException;

    UploadResponse importAddressesFromCSV(EMigrationParserLocation parser, InputStream inputStream,
                                          JobManagerTenant jobManagerTenant) throws IOException;

    UploadResponse importPaymentInfosFromCSV(EMigrationParserLocation parser, InputStream inputStream,
                                             JobManagerTenant jobManagerTenant) throws IOException;

    UploadResponse importSubscriptionMappingsFromCSV(EMigrationParserLocation parser,
                                                     InputStream inputStream,
                                                     JobManagerTenant jobManagerTenant,
                                                     Boolean isLegacy) throws IOException;

    UploadResponse importPaymentsFromCSV(EMigrationParserLocation parser, InputStream inputStream,
                                         JobManagerTenant jobManagerTenant) throws IOException;
}
