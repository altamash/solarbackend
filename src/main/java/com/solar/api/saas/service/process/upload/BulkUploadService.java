package com.solar.api.saas.service.process.upload;

import com.solar.api.tenant.model.process.JobManagerTenant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

public interface BulkUploadService {

    public static String FINAL_DEFAULT_EMAIL = "customerservice@novelenergy.biz";

    UploadResponse upload(String entity, File file, List<Long> correctRowIds, Long rateMatrixId, Boolean isLegacy);

//    MultipartUploadResponse upload(EMigrationParserLocation parser, String entity, File file,
//                                   ObjectNode requestMessage) throws IOException,
//            ClassNotFoundException;

    UploadResponse uploadUsersFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                      JobManagerTenant jobManagerTenant) throws IOException;

    UploadResponse uploadEntityFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                      JobManagerTenant jobManagerTenant) throws IOException;

    UploadResponse uploadLocationsFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                          JobManagerTenant jobManagerTenant) throws IOException;

    UploadResponse uploadUtilitiesFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                          JobManagerTenant jobManagerTenant) throws IOException;

    UploadResponse uploadAddressesFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                          JobManagerTenant jobManagerTenant) throws IOException;

    UploadResponse uploadPaymentInfosFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                             JobManagerTenant jobManagerTenant) throws IOException;

    UploadResponse uploadSubscriptionMappingsFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                                     Long rateMatrixId, JobManagerTenant jobManagerTenant, Boolean isLegacy)
            throws IOException;

    public UploadResponse uploadLeadFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                            JobManagerTenant jobManagerTenant) throws IOException, ParseException;

//    UploadResponse uploadPaymentsFromCSV(EMigrationParserLocation parser, InputStream inputStream,
//                                         JobManagerTenant jobManagerTenant) throws IOException;
}
