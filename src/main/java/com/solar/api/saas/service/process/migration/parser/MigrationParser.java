package com.solar.api.saas.service.process.migration.parser;

import com.solar.api.tenant.model.payment.billing.PaymentTransactionDetail;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface MigrationParser {

    List<User> importCustomersFromCSV(InputStream is) throws IOException;

    List<Address> importAddressesFromCSV(InputStream is) throws IOException;

    List<PaymentInfo> importPaymentInfoFromCSV(InputStream is) throws IOException;

    Map<CustomerSubscription, List<CustomerSubscriptionMapping>> importSubscriptionMappingsFromCSV(InputStream is) throws IOException;

    List<PaymentTransactionDetail> importCustomerPaymentFromCSV(InputStream is) throws IOException;
}
