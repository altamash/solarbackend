package com.solar.api.saas.service.process.upload.v2;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.solar.api.Application;
import com.solar.api.saas.service.process.upload.v2.customer.mapper.CustomerV2;
import com.solar.api.tenant.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component("bulkUploadParserImplV2")
public class BulkUploadParserImpl implements BulkUploadParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    private SubscriptionService subscriptionService;

    @Override
    public List<CustomerV2> importCustomersFromCSV(InputStream is, List<Long> correctRowIds) throws IOException {
        CsvSchema customerLineSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<CustomerV2> customerLines = csvMapper
                .readerFor(CustomerV2.class)
                .with(customerLineSchema)
                .readValues(is);
//        List<CustomerV2> customers = customerLines.readAll();
        return customerLines.readAll();
        /*if (correctRowIds != null) {
            List<CustomerV2> correctCustomers = new ArrayList<>();
            for (long correctRowId : correctRowIds) {
                correctCustomers.add(customers.get((int) correctRowId));
            }
//        return CustomerMapper.toUsers(customers);
            List<User> usersMapped = CustomerMapper.toUsersV2(correctCustomers);
            List<User> finalUsersMapped = usersMapped;
            return IntStream.range(0, correctCustomers.size()).filter(i -> correctRowIds.stream()
                    .map(j -> j.intValue()).collect(Collectors.toList()).contains(i)).mapToObj(i -> finalUsersMapped.get(i)).collect(Collectors.toList());
        }
        return CustomerMapper.toUsersV2(customers);*/
    }
}
