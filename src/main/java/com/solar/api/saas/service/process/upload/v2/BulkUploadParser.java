package com.solar.api.saas.service.process.upload.v2;

import com.solar.api.saas.service.process.upload.v2.customer.mapper.CustomerV2;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface BulkUploadParser {

    List<CustomerV2> importCustomersFromCSV(InputStream is, List<Long> correctRowIds) throws IOException;
}
