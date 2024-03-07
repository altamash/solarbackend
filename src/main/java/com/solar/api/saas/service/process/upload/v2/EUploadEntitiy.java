package com.solar.api.saas.service.process.upload.v2;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.billingCredits.BillingCredits;
import com.solar.api.tenant.model.ca.CaUtility;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.extended.assetHead.AssetBlockDetail;
import com.solar.api.tenant.model.extended.project.ProjectInventory;
import com.solar.api.tenant.model.extended.project.ProjectSite;
import com.solar.api.tenant.model.payment.billing.PaymentTransactionDetail;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.User;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum EUploadEntitiy {

    CUSTOMER("CUSTOMER", User.class.getName()),
    ADDRESS("ADDRESS", Address.class.getName()),
    ENTITIY("Entity", Entity.class.getName()),

    CAUTILITY("CAUTILITY", CaUtility.class.getName()),
    PAYMENT_INFO("PAYMENT INFO", PaymentInfo.class.getName()),
    CUSTOMER_SUBSCRIPTION("CUSTOMER SUBSCRIPTION", CustomerSubscription.class.getName()),
    PAYMENT_TRANSACTION_DETAIL("PAYMENT TRANSACTION DETAIL", PaymentTransactionDetail.class.getName()),
    ASSET_BLOCK_DETAIL("ASSET BLOCK DETAIL", AssetBlockDetail.class.getName()),
    PROJECT_INVENTORY_SERIALS("PROJECT INVENTORY", ProjectInventory.class.getName()),
    BILLING_CREDITS("BILLING CREDITS", BillingCredits.class.getName()),
    PROJECT_SITE("Site", ProjectSite.class.getName()),
    MONITOR_READING_DAILY("MONITOR READING DAILY", MonitorReadingDaily.class.getName());
    //ENTITY("Entity", Entity.class.getName());

    String entity;
    String entityPath;

    EUploadEntitiy(String entity, String entityPath) {
        this.entity = entity;
        this.entityPath = entityPath;
    }

    public static EUploadEntitiy get(String entity) {
        return Arrays.stream(values()).filter(value -> entity.equalsIgnoreCase(value.entity)).findFirst().orElseThrow(
                () -> new NotFoundException(EUploadEntitiy.class, "entity", entity));
    }
}
