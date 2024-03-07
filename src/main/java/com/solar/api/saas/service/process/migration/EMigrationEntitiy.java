package com.solar.api.saas.service.process.migration;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.payment.billing.PaymentTransactionDetail;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.User;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum EMigrationEntitiy {

    CUSTOMER("CUSTOMER", User.class.getName()),
    ADDRESS("ADDRESS", Address.class.getName()),
    PAYMENT_INFO("PAYMENT INFO", PaymentInfo.class.getName()),
    CUSTOMER_SUBSCRIPTION("CUSTOMER SUBSCRIPTION", CustomerSubscription.class.getName()),
    PAYMENT_TRANSACTION_DETAIL("PAYMENT TRANSACTION DETAIL", PaymentTransactionDetail.class.getName());

    String entity;
    String entityPath;

    EMigrationEntitiy(String entity, String entityPath) {
        this.entity = entity;
        this.entityPath = entityPath;
    }

    public static EMigrationEntitiy get(String entity) {
        return Arrays.stream(values()).filter(value -> entity.equalsIgnoreCase(value.entity)).findFirst().orElseThrow(
                () -> new NotFoundException(EMigrationEntitiy.class, "entity", entity));
    }
}
