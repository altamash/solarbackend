package com.solar.api.saas.service.process.upload;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.solar.api.Application;
import com.solar.api.saas.service.process.upload.mapper.*;
import com.solar.api.saas.service.process.upload.mapper.csgr.CustomerSubscriptionMappingMapper;
import com.solar.api.tenant.mapper.ca.CaUtilityMapper;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper;
import com.solar.api.tenant.model.ca.CaUtility;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//import com.solar.api.saas.service.process.migration.parser.vista.mapper.*;
//import com.solar.api.saas.service.process.migration.parser.vista.mapper.csgf.csgr.CustomerSubscriptionMappingMapper;
//import com.solar.api.saas.service.process.migration.parser.vista.mapper.csgf.csgr.SubscriptionMapping;

@Component
public class BulkUploadParserImpl implements BulkUploadParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    private SubscriptionService subscriptionService;

    @Override
    public List<User> importCustomersFromCSV(InputStream is, List<Long> correctRowIds) throws IOException {
        CsvSchema customerLineSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Customer> customerLines = csvMapper
                .readerFor(Customer.class)
                .with(customerLineSchema)
                .readValues(is);
        List<Customer> customers = customerLines.readAll();
        List<Customer> correctCustomers = new ArrayList<>();
        for (long correctRowId : correctRowIds) {
            correctCustomers.add(customers.get((int) correctRowId));
        }
//        return CustomerMapper.toUsers(customers);
        List<User> usersMapped = CustomerMapper.toUsers(correctCustomers);
        List<User> finalUsersMapped = usersMapped;
        return IntStream.range(0, correctCustomers.size()).filter(i -> correctRowIds.stream().map(j -> j.intValue()).collect(Collectors.toList()).contains(i)).mapToObj(i -> finalUsersMapped.get(i)).collect(Collectors.toList());
    }

    @Override
    public List<Address> importAddressesFromCSV(InputStream is, List<Long> correctRowIds) throws IOException {
        CsvSchema addressLineSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<CustomerAddress> addressLines = csvMapper
                .readerFor(CustomerAddress.class)
                .with(addressLineSchema)
                .readValues(is);
        List<CustomerAddress> addresses = addressLines.readAll();
        List<CustomerAddress> correctAddresses = new ArrayList<>();
        for (long correctRowId : correctRowIds) {
            correctAddresses.add(addresses.get((int) correctRowId));
        }
//        return AddressMapper.toAddresses(addresses);
        List<Address> addressesMapped = AddressMapper.toAddresses(correctAddresses);
        List<Address> finalAddressesMapped = addressesMapped;
        return IntStream.range(0, correctAddresses.size()).filter(i -> correctRowIds.stream().map(j -> j.intValue()).collect(Collectors.toList()).contains(i)).mapToObj(i -> finalAddressesMapped.get(i)).collect(Collectors.toList());
    }

    @Override
    public List<PhysicalLocation> importLocationsFromCSV(InputStream is, List<Long> correctRowIds) throws IOException {
        CsvSchema locationLineSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<CustomerAddress> addressLines = csvMapper
                .readerFor(CustomerAddress.class)
                .with(locationLineSchema)
                .readValues(is);
        List<CustomerAddress> locations = addressLines.readAll();
        List<CustomerAddress> correctLocations = new ArrayList<>();
        for (long correctRowId : correctRowIds) {
            correctLocations.add(locations.get((int) correctRowId));
        }
        List<PhysicalLocation> locationsMapped = PhysicalLocationMapper.addressesToPhysicalLocations(correctLocations);
        List<PhysicalLocation> finalLocationsMapped = locationsMapped;
        return IntStream.range(0, correctLocations.size()).filter(i -> correctRowIds.stream().
                map(j -> j.intValue()).collect(Collectors.toList()).contains(i)).mapToObj(i -> finalLocationsMapped.get(i)).collect(Collectors.toList());
    }

    @Override
    public List<CaUtility> importUtilitiesFromCSV(InputStream is, List<Long> correctRowIds) throws IOException {
        CsvSchema utilitiesLineSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<CustomerUtility> addressLines = csvMapper
                .readerFor(CustomerUtility.class)
                .with(utilitiesLineSchema)
                .readValues(is);
        List<CustomerUtility> utilities = addressLines.readAll();
        List<CustomerUtility> correctUtilities = new ArrayList<>();
        for (long correctRowId : correctRowIds) {
            correctUtilities.add(utilities.get((int) correctRowId));
        }
        List<CaUtility> utilitiesMapped = CaUtilityMapper.toCaUtilitys(correctUtilities);
        List<CaUtility> finalUtilitiesMapped = utilitiesMapped;
        return IntStream.range(0, utilitiesMapped.size()).filter(i -> correctRowIds.stream().
                map(j -> j.intValue()).collect(Collectors.toList()).contains(i)).mapToObj(i -> finalUtilitiesMapped.get(i)).collect(Collectors.toList());
    }

    @Override
    public List<PaymentInfo> importPaymentInfoFromCSV(InputStream is, List<Long> correctRowIds) throws IOException {
        CsvSchema pInfoLineSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<CustomerPaymentInfo> paymentInfoLines = csvMapper
                .readerFor(CustomerPaymentInfo.class)
                .with(pInfoLineSchema)
                .readValues(is);
        List<CustomerPaymentInfo> paymentInfos = paymentInfoLines.readAll();
        List<CustomerPaymentInfo> correctPaymentInfos = new ArrayList<>();
        for (long correctRowId : correctRowIds) {
            correctPaymentInfos.add(paymentInfos.get((int) correctRowId));
        }
//        IntStream.range(0, paymentInfos.size()).filter(i -> correctRowIds.contains(i)).mapToObj(i -> paymentInfos.get(i)).collect(Collectors.toList())
        List<PaymentInfo> paymentInfosMapped = CustomerPaymentInfoMapper.toPaymentInfos(correctPaymentInfos);
        List<PaymentInfo> finalPaymentInfosMapped = paymentInfosMapped;
        return IntStream.range(0, correctPaymentInfos.size()).filter(i -> correctRowIds.stream().map(j -> j.intValue()).collect(Collectors.toList()).contains(i)).mapToObj(i -> finalPaymentInfosMapped.get(i)).collect(Collectors.toList());
//        return CustomerPaymentInfoMapper.toPaymentInfos(paymentInfos);
    }

    @Override
    public Map<CustomerSubscription, List<CustomerSubscriptionMapping>> importSubscriptionMappingsFromCSV(InputStream is, List<Long> correctRowIds, Long rateMatrixId) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Map<String, String>> subscriptionMappingLines = csvMapper
                .readerFor(Map.class)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(is);
        List<Map<String, String>> mappings = subscriptionMappingLines.readAll();

//        CsvSchema subsLineSchema = CsvSchema.emptySchema().withHeader();
//        CsvMapper csvMapper = new CsvMapper();
//        MappingIterator<SubscriptionMapping> subscriptionMappingLines = csvMapper
//                .readerFor(SubscriptionMapping.class)
//                .with(subsLineSchema)
//                .readValues(is);
//        List<SubscriptionMapping> mappings = subscriptionMappingLines.readAll();
//        List<String> matrixHeads =
//                mappings.stream().map(m -> m.getSubscriptionRateMatrixHeadId()).distinct().collect(Collectors.toList());
//        List<Long> matrixHeadIds = matrixHeads.stream().map((h -> Long.parseLong(h))).collect(Collectors.toList());
        /*List<SubscriptionRateMatrixHead> rateMatrixHeads =
                subscriptionService.findSubscriptionRateMatrixHeadsByIdsIn(matrixHeadIds);
        mappings.forEach(mapping -> mapping.setSubscriptionRateMatrixHead(
                rateMatrixHeads.stream().filter(head -> head.getId() == Long.parseLong(mapping.getSubscriptionRateMatrixHeadId())).findFirst().orElse(null)));*/
        /*Map<String, List<String>> headRateCodesMap = new HashMap<>();
        matrixHeads.forEach(head -> {
            headRateCodesMap.put(head,
                    subscriptionService.findRateCodesBySubscriptionRateMatrixIdAndVaryByCustomer(Long.parseLong(head)
                            , true));
        });*/
        List<Map<String, String>> correctMappings = new ArrayList<>();
        for (long correctRowId : correctRowIds) {
            correctMappings.add(mappings.get((int) correctRowId));
        }
        SubscriptionRateMatrixHead rateMatrixHead =
                subscriptionService.findSubscriptionRateMatrixHeadById(rateMatrixId);
        List<String> codesForSubscriptionMapping = subscriptionService.getRequiredCodesForSubscriptionMapping(rateMatrixId);
        return CustomerSubscriptionMappingMapper.toCustomerSubscriptionMappings(correctMappings, rateMatrixHead, codesForSubscriptionMapping);
    }

    @Override
    public List<Customer> importLeadsFromCSV(InputStream is, List<Long> correctRowIds) throws IOException {
        CsvSchema customerLineSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MappingIterator<Customer> customerLines = csvMapper
                .readerFor(Customer.class)
                .with(customerLineSchema)
                .readValues(is);
        List<Customer> customers = customerLines.readAll();
        List<Customer> correctCustomers = new ArrayList<>();
        for (long correctRowId : correctRowIds) {
            correctCustomers.add(customers.get((int) correctRowId));
        }
        return IntStream.range(0, correctCustomers.size()).filter(i -> correctRowIds.stream().map(j -> j.intValue()).collect(Collectors.toList()).contains(i)).mapToObj(i -> correctCustomers.get(i)).collect(Collectors.toList());
    }

   /* @Override
    public List<PaymentTransactionDetail> importCustomerPaymentFromCSV(InputStream is) throws IOException {
        CsvSchema paymentLineSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<CustomerPayment> paymentLines = csvMapper
                .readerFor(CustomerPayment.class)
                .with(paymentLineSchema)
                .readValues(is);
        List<CustomerPayment> customerPayments = paymentLines.readAll();
        return PaymentMapper.toPaymentTransactionDetails(customerPayments);
    }*/

}
