package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.dataexport.payment.PaymentExportData;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.UserTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long>, PaymentInfoRepositoryCustom {

    List<PaymentInfo> findPaymentInfoByPortalAccount(User userAccount);

    PaymentInfo findByPortalAccountAndPaymentSrcAlias(User portalAccount, String paymentSrcAlias);

    PaymentInfo findByPortalAccountAndPaymentSource(User portalAccount, String paymentSource);

    PaymentInfo findByAccountNumberAndRoutingNumberAndAccountType(String accountNumber, String routingNumber, String accountType);

    @Query("select distinct(customerType) from CustomerDetail WHERE customerType IS NOT NULL ")
    List<String> getCustomerType();
}
