package com.solar.api.saas.service.process.upload.mapper.csgr;

import com.solar.api.saas.service.process.upload.EUploadAction;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomerSubscriptionMappingMapper {

    public static Map<CustomerSubscription, List<CustomerSubscriptionMapping>> toCustomerSubscriptionMappings(
//            List<SubscriptionMapping> subscriptionMappingDTOs,
                                                                                                              List<Map<String, String>> mappings,
                                                                                                               SubscriptionRateMatrixHead rateMatrixHead,
                                                                                                               List<String> codesForSubscriptionMapping
//                                                                                                              Map<String, List<String>> headRateCodesMap,
) {
        Map<CustomerSubscription, List<CustomerSubscriptionMapping>> subscriptionsMap = new LinkedHashMap<>();

        ////////////////////
        for (int i = 0; i < mappings.size(); i++) {
            Map<String, String> mapping = mappings.get(i);
            Long subscriptionId = mapping.get("action").equals(EUploadAction.UPDATE.getAction())? Long.parseLong(mapping.get("subscription_id")) : null;
            CustomerSubscription subscription = CustomerSubscription.builder()
                    .userAccountId(Long.parseLong(mapping.get("acct_id")))
                    .subscriptionType(rateMatrixHead.getSubscriptionCode())
                    .subscriptionRateMatrixId(rateMatrixHead.getId())
                    .id(subscriptionId)
                    .build();


            subscription.setAction(mapping.get("action"));
            List<CustomerSubscriptionMapping> subscriptionMappings = new ArrayList<>();
            codesForSubscriptionMapping.forEach(code -> {
                CustomerSubscriptionMapping.CustomerSubscriptionMappingBuilder builder =
                        CustomerSubscriptionMapping.builder().rateCode(code).value(mapping.get(code.toLowerCase()));
                if (code.equals("ROLLDT")) {
                    builder.level(-1);
                } else {
                    builder.level(0);
                }
                subscriptionMappings.add(builder.build());
            });
            subscriptionsMap.put(subscription, subscriptionMappings);
        }
        ////////////////////

        /*subscriptionMappingDTOs.forEach(mapping -> {
            List<String> allowedCodes = headRateCodesMap.get(mapping.getSubscriptionRateMatrixHeadId());
            List<CustomerSubscriptionMapping> mappings = new ArrayList<>();
            if (allowedCodes.contains("SN")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("SN").value(mapping.getSn()).build());
            }
            if (allowedCodes.contains("CN")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("CN").value(mapping.getCn()).build());
            }
            if (allowedCodes.contains("VCN")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("VCN").value(mapping.getVcn()).build());
            }
            if (allowedCodes.contains("CCLAS")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("CCLAS").value(mapping.getCclas()).build());
            }
            if (allowedCodes.contains("PN")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("PN").value(mapping.getPn()).build());
            }
            if (allowedCodes.contains("SADD")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("SADD").value(mapping.getSadd()).build());
            }
            if (allowedCodes.contains("MP")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("MP").value(mapping.getMp()).build());
            }
            if (allowedCodes.contains("KWDC")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("KWDC").value(mapping.getKwdc()).build());
            }
            if (allowedCodes.contains("SSDT")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("SSDT").value(mapping.getSsdt()).build());
            }
            if (allowedCodes.contains("PSRC")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("PSRC").value(mapping.getPsrc()).build());
            }
            if (allowedCodes.contains("PCOMP")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("PCOMP").value(mapping.getPcomp()).build());
            }
            if (allowedCodes.contains("DSCP")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("DSCP").value(mapping.getDscp()).build());
            }
            if (allowedCodes.contains("TENR")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("TENR").value(mapping.getTenr()).build());
            }
            if (allowedCodes.contains("ROLL")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("ROLL").value(mapping.getRoll()).build());
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("ROLLDT").build());
            }
            if (allowedCodes.contains("PRJN")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("PRJN").value(mapping.getPrjn()).build());
            }
            if (allowedCodes.contains("DEP")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("DEP").value(mapping.getDep()).build());
            }
            if (allowedCodes.contains("DSC")) {
                mappings.add(CustomerSubscriptionMapping.builder().rateCode("DSC").value(mapping.getDsc()).build());
            }
            subscriptionsMap.put(CustomerSubscription.builder()
                    .externalId(mapping.getExternalId())
                    .subscriptionType(mapping.getSubscriptionRateMatrixHead().getSubscriptionCode())
                    .subscriptionRateMatrixId(Long.parseLong(mapping.getSubscriptionRateMatrixHeadId()))
                    .build(), mappings);
        });*/
        return subscriptionsMap;
    }
}
