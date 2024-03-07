package com.solar.api.saas.service.process.migration.parser.vista;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.solar.api.Application;
import com.solar.api.exception.SolarApiException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.process.migration.parser.MigrationParser;
import com.solar.api.saas.service.process.migration.parser.vista.mapper.*;
import com.solar.api.saas.service.process.migration.parser.vista.mapper.csgf.csgr.CustomerSubscriptionMappingMapper;
import com.solar.api.saas.service.process.migration.parser.vista.mapper.csgf.csgr.SubscriptionMapping;
import com.solar.api.tenant.model.payment.billing.PaymentTransactionDetail;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("VistaCustomersMigrationParser")
public class VistaCustomersMigrationParser implements MigrationParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    private SubscriptionService subscriptionService;

    @Override
    public List<User> importCustomersFromCSV(InputStream is) throws IOException {
        CsvSchema customerLineSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Customer> customerLines = csvMapper
                .readerFor(Customer.class)
                .with(customerLineSchema)
                .readValues(is);
        List<Customer> customers = customerLines.readAll();
        return CustomerMapper.toUsers(customers);
    }

    @Override
    public List<Address> importAddressesFromCSV(InputStream is) throws IOException {
        CsvSchema addressLineSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<CustomerAddress> addressLines = csvMapper
                .readerFor(CustomerAddress.class)
                .with(addressLineSchema)
                .readValues(is);
        List<CustomerAddress> addresses = addressLines.readAll();
        return AddressMapper.toAddresses(addresses);
    }

    @Override
    public List<PaymentInfo> importPaymentInfoFromCSV(InputStream is) throws IOException {
        CsvSchema pInfoLineSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<CustomerPaymentInfo> paymentInfoLines = csvMapper
                .readerFor(CustomerPaymentInfo.class)
                .with(pInfoLineSchema)
                .readValues(is);
        List<CustomerPaymentInfo> paymentInfos = paymentInfoLines.readAll();
        return CustomerPaymentInfoMapper.toPaymentInfos(paymentInfos);
    }

    @Override
    public Map<CustomerSubscription, List<CustomerSubscriptionMapping>> importSubscriptionMappingsFromCSV(InputStream is) throws IOException {
        CsvSchema subsLineSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<SubscriptionMapping> subscriptionMappingLines = csvMapper
                .readerFor(SubscriptionMapping.class)
                .with(subsLineSchema)
                .readValues(is);
        List<SubscriptionMapping> mappings = subscriptionMappingLines.readAll();
        List<Integer> ssdtExceptionLines = new ArrayList<>();
        for (int idx = 0; idx < mappings.size(); idx++) {
            try {
                new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT).parse(mappings.get(idx).getSsdt());
            } catch (ParseException e) {
                LOGGER.error("Incorrect ssdt format on line number {}. Must be {}", idx + 2, Utility.SYSTEM_DATE_FORMAT, e);
                ssdtExceptionLines.add(idx + 2);
            }
        }
        if (!ssdtExceptionLines.isEmpty()) {
            throw new SolarApiException("Incorrect ssdt format on line number(s) " + ssdtExceptionLines + ". Must be " + Utility.SYSTEM_DATE_FORMAT);
        }
        List<String> matrixHeads =
                mappings.stream().map(m -> m.getSubscriptionRateMatrixHeadId()).distinct().collect(Collectors.toList());
        List<Long> matrixHeadIds = matrixHeads.stream().map((h -> Long.parseLong(h))).collect(Collectors.toList());
        List<SubscriptionRateMatrixHead> rateMatrixHeads =
                subscriptionService.findSubscriptionRateMatrixHeadsByIdsIn(matrixHeadIds);
        mappings.forEach(mapping -> mapping.setSubscriptionRateMatrixHead(
                rateMatrixHeads.stream().filter(head -> head.getId() == Long.parseLong(mapping.getSubscriptionRateMatrixHeadId())).findFirst().orElse(null)));
        Map<String, List<String>> headRateCodesMap = new HashMap<>();
        matrixHeads.forEach(head -> {
            headRateCodesMap.put(head,
                    subscriptionService.findRateCodesBySubscriptionRateMatrixIdAndVaryByCustomer(Long.parseLong(head)
                            , true));
        });
        return CustomerSubscriptionMappingMapper.toCustomerSubscriptionMappings(mappings, headRateCodesMap);
    }

    @Override
    public List<PaymentTransactionDetail> importCustomerPaymentFromCSV(InputStream is) throws IOException {
        CsvSchema paymentLineSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<CustomerPayment> paymentLines = csvMapper
                .readerFor(CustomerPayment.class)
                .with(paymentLineSchema)
                .readValues(is);
        List<CustomerPayment> customerPayments = paymentLines.readAll();
        return PaymentMapper.toPaymentTransactionDetails(customerPayments);
    }
}
