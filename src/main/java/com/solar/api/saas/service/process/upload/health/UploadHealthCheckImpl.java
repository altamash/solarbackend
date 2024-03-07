package com.solar.api.saas.service.process.upload.health;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.solar.api.saas.service.process.upload.EUploadEntitiy;
import com.solar.api.tenant.model.billingCredits.BillingCredits;
import com.solar.api.tenant.model.ca.CaUtility;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.extended.assetHead.AssetBlockDetail;
import com.solar.api.tenant.model.extended.project.ProjectInventory;
import com.solar.api.tenant.model.externalFile.ExternalFile;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

@Service
public class UploadHealthCheckImpl implements UploadHealthCheck {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final UserUploadHealthCheck userUploadHealthCheck;
    private final AddressUploadHealthCheck addressUploadHealthCheck;
    private final PaymentInfoUploadHealthCheck paymentInfoUploadHealthCheck;
    private final SubscriptionUploadHealthCheck subscriptionUploadHealthCheck;
    private final ProjectUploadHealthCheck projectUploadHealthCheck;
    private final UtilityUploadHealthCheck utilityUploadHealthCheck;
    private final BillingCreditsUploadHealthCheck billingCreditsUploadHealthCheck;
    private final MonitorReadingDailyUploadHealthCheck monitorReadingDailyUploadHealthCheck;

    public UploadHealthCheckImpl(UserUploadHealthCheck userUploadHealthCheck,
                                 AddressUploadHealthCheck addressUploadHealthCheck,
                                 PaymentInfoUploadHealthCheck paymentInfoUploadHealthCheck,
                                 SubscriptionUploadHealthCheck subscriptionUploadHealthCheck,
                                 ProjectUploadHealthCheck projectUploadHealthCheck,
                                 UtilityUploadHealthCheck utilityUploadHealthCheck,
                                 BillingCreditsUploadHealthCheck billingCreditsUploadHealthCheck,
                                 MonitorReadingDailyUploadHealthCheck monitorReadingDailyUploadHealthCheck) {
        this.userUploadHealthCheck = userUploadHealthCheck;
        this.addressUploadHealthCheck = addressUploadHealthCheck;
        this.paymentInfoUploadHealthCheck = paymentInfoUploadHealthCheck;
        this.subscriptionUploadHealthCheck = subscriptionUploadHealthCheck;
        this.projectUploadHealthCheck = projectUploadHealthCheck;
        this.utilityUploadHealthCheck = utilityUploadHealthCheck;
        this.billingCreditsUploadHealthCheck = billingCreditsUploadHealthCheck;
        this.monitorReadingDailyUploadHealthCheck = monitorReadingDailyUploadHealthCheck;
    }

    @Override
    public HealthCheckResult validate(String entity, MultipartFile file, Long rateMatrixId, String action, Long projectId, ExternalFile externalFile) {
        if (file == null) {
            return null;
        }
        try {
            Class clazz = Class.forName(EUploadEntitiy.get(entity).getEntityPath());
            List<Map<?, ?>> mappings;
            try (FileInputStream is = (FileInputStream) file.getInputStream()) {
                CsvMapper csvMapper = new CsvMapper();
                MappingIterator<Map<?, ?>> mappingLines = csvMapper
                        .readerFor(Map.class)
                        .with(CsvSchema.emptySchema().withHeader())
                        .readValues(is);
                mappings = mappingLines.readAll();
            }
            if (clazz == User.class) {
                return userUploadHealthCheck.validate(mappings);
            } else if (clazz == Address.class) {
                return addressUploadHealthCheck.validate(mappings, rateMatrixId);
            } else if (clazz == CaUtility.class) {
                return utilityUploadHealthCheck.validate(mappings, rateMatrixId);
            } else if (clazz == PaymentInfo.class) {
                return paymentInfoUploadHealthCheck.validate(mappings, rateMatrixId);
            } else if (clazz == CustomerSubscription.class) {
                return subscriptionUploadHealthCheck.validate(mappings, rateMatrixId);
            } else if (clazz == AssetBlockDetail.class) {
                return projectUploadHealthCheck.validate(mappings, rateMatrixId, action, null, null);
            } else if (clazz == ProjectInventory.class) {
                return projectUploadHealthCheck.validate(mappings, null, null, rateMatrixId, projectId);
            } else if (clazz == Entity.class) {
                return userUploadHealthCheck.validateLead(mappings);
            } else if (clazz == BillingCredits.class) {
                return billingCreditsUploadHealthCheck.validate(mappings);
            } else if (clazz == MonitorReadingDaily.class) {
                return monitorReadingDailyUploadHealthCheck.validate(mappings, externalFile);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return null;
    }
}
