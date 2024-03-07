package com.solar.api.saas.service.process.upload;

import com.solar.api.saas.service.process.upload.mapper.Customer;
import com.solar.api.tenant.model.ca.CaUtility;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface BulkUploadParser {

    List<User> importCustomersFromCSV(InputStream is, List<Long> correctRowIds) throws IOException;

    List<Address> importAddressesFromCSV(InputStream is, List<Long> correctRowIds) throws IOException;

    List<PhysicalLocation> importLocationsFromCSV(InputStream is, List<Long> correctRowIds) throws IOException;

    List<CaUtility> importUtilitiesFromCSV(InputStream is, List<Long> correctRowIds) throws IOException;

    List<PaymentInfo> importPaymentInfoFromCSV(InputStream is, List<Long> correctRowIds) throws IOException;

    Map<CustomerSubscription, List<CustomerSubscriptionMapping>> importSubscriptionMappingsFromCSV(InputStream is,
                                                                                                   List<Long> correctRowIds, Long rateMatrixId) throws IOException;

//    List<PaymentTransactionDetail> importCustomerPaymentFromCSV(InputStream is) throws IOException;
    List<Customer> importLeadsFromCSV(InputStream is, List<Long> correctRowIds) throws IOException;

}
