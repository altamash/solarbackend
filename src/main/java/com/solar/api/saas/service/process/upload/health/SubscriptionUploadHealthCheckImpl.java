package com.solar.api.saas.service.process.upload.health;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.solar.api.helper.Utility;
import com.solar.api.helper.ValidationUtils;
import com.solar.api.helper.service.SubscriptionRateCodes;
import com.solar.api.saas.service.process.upload.EUploadAction;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.ESubscriptionStatus;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixDetail;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SubscriptionUploadHealthCheckImpl extends AbstractUploadHealthCheck implements SubscriptionUploadHealthCheck {

    @Autowired
    private UserService userService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private Utility utility;

    @Override
    void addParseError(List<HealthCheck> checks, Integer line, Map<?, ?> mapping, String issue) {
        checks.add(HealthCheck.builder()
                .line(line + 2)
                .entityId(mapping.get("entity_id") == null ? "" : (String) mapping.get("entity_id"))
                .subscriptionId(mapping.get("subscription_id") == null ? "" : (String) mapping.get("subscription_id"))
                .action(mapping.get("action") == null ? "" : (String) mapping.get("action"))
                .issue(issue)
                .build());
    }

    @Override
    int checkRequiredFieldsInHeader(List<HealthCheck> checks, Set<?> fileHeader, String... fields) {
        AtomicInteger count = new AtomicInteger();
        Arrays.asList(fields).forEach(field -> {
            if (!fileHeader.contains(field)) {
                checks.add(HealthCheck.builder()
                        .line(1)
                        .entityId("")
                        .subscriptionId("")
                        .action("")
                        .issue("Missing required header field " + field)
                        .build());
                count.getAndIncrement();
            }
        });
        return count.get();
    }

    @Override
    public HealthCheckResult validate(List<Map<?, ?>> mappings, Long subscriptionRateMatrixId) {
        List<SubscriptionRateMatrixDetail> detailsRequired =
                subscriptionService.getRequiredForUpload(subscriptionRateMatrixId);
        boolean isMonitoring = Arrays.asList("solax", "solis", "goodwe").contains(detailsRequired.stream()
                        .filter(d -> "MP".equals(d.getRateCode())).findFirst().get().getDefaultValue().toLowerCase());
        List<HealthCheck> checks = new ArrayList<>();
        checkRequiredFieldsInHeader(checks, mappings.get(0).keySet(), "action", "acct_id", "subscription_id");
        checkRequiredCodes(checks, subscriptionRateMatrixId, detailsRequired, mappings.get(0).keySet());
        List<MeasureDefinitionTenantDTO> mandatoryMeasures =
                measureDefinitionOverrideService.findByIds(detailsRequired.stream()
                        .filter(m -> m.getMandatory())
                        .map(m -> m.getMeasureDefinitionId()).collect(Collectors.toList()));
//        checkMandatoryFields(checks, mappings, mandatoryMeasures);
        checkMandatoryFieldsNotEmpty(checks, mappings, mandatoryMeasures.stream()
                .map(m -> m.getCode().toLowerCase()).collect(Collectors.toList()).toArray(new String[0]));
        checkFormatOfFields(checks, mappings, mandatoryMeasures);
        checkActions(checks, mappings, "subscription_id");
        checkSubscriptionStatus(checks, mappings);
        if (!isMonitoring) {
            checkSubscriptionStartDate(checks, subscriptionRateMatrixId, mappings);
            checkPaymentInfoAndAddressAliases(checks, mappings);
            checkGardenCapacity(checks, subscriptionRateMatrixId, mappings);
            insertWithExistingPremiseNumberNotAllowed(checks, subscriptionRateMatrixId, mappings);
            checkMultipleOccurrences(checks, mappings, "pn");
        }
        checks.sort(Comparator.comparing(HealthCheck::getLine));
        return healthCheckResult(mappings, checks);
    }

    // Check 1 - Required codes for subscription type must be present
    private int checkRequiredCodes(List<HealthCheck> checks,
                                   Long subscriptionRateMatrixId,
                                   List<SubscriptionRateMatrixDetail> detailsRequired,
                                   Set<?> fileHeader) {
        SubscriptionRateMatrixHead matrixHead =
                subscriptionService.findSubscriptionRateMatrixHeadById(subscriptionRateMatrixId);
        Set<String> codesRequiredForMatrix =
                detailsRequired.stream().map(m -> m.getRateCode().toLowerCase()).collect(Collectors.toSet());
        Sets.SetView<String> difference = Sets.difference(codesRequiredForMatrix, fileHeader);
        if (!difference.isEmpty()) {
            List<String> message = new ArrayList<>();
            message.add(Joiner.on(", ").join(difference) + " are required for '" + matrixHead.getSubscriptionTemplate() + "'");
            checks.add(HealthCheck.builder()
                    .line(1)
                    .entityId("")
                    .subscriptionId("")
                    .action("")
                    .issue(Joiner.on(", ").join(difference) + " are required for '" + matrixHead.getSubscriptionTemplate() + "'")
                    .build());
        }
        return checks.size();
    }

    // Check 2 - Mandatory fields must not be empty
    /*private int checkMandatoryFields(List<HealthCheck> checks, List<Map<?, ?>> mappings,
                                     List<MeasureDefinitionTenantDTO> mandatoryMeasures) {
        List<String> mandatoryMeasureCodes = mandatoryMeasures.stream()
                .map(m -> m.getCode().toLowerCase()).collect(Collectors.toList());
        List<String> emptyFieldsList = new ArrayList<>();
        for (int i = 0; i < mappings.size(); i++) {
            List<String> emptyFields = mappings.get(i).entrySet().stream()
                    .filter(m -> ((String) m.getValue()).isEmpty() && mandatoryMeasureCodes.contains(((String) m
                    .getKey()).toLowerCase()))
                    .map(m -> (String) m.getKey()).collect(Collectors.toList());
            if (!emptyFields.isEmpty()) {
                addParseError(checks, i, mappings.get(i), emptyFields + " are mandatory");
                emptyFieldsList.addAll(emptyFields);
            }
        }
        return emptyFieldsList.size();
    }*/

    // Check 3 - Format according to measure_definition
    private int checkFormatOfFields(List<HealthCheck> checks, List<Map<?, ?>> mappings,
                                    List<MeasureDefinitionTenantDTO> mandatoryMeasures) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            Iterator<? extends Map.Entry<?, ?>> iterator = mapping.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<?, ?> map = iterator.next();
                Optional<MeasureDefinitionTenantDTO> measureOptional =
                        mandatoryMeasures.stream().filter(m -> m.getCode().toLowerCase().equals(map.getKey().toString())).findFirst();
                if (measureOptional.isPresent() && measureOptional.get().getFormat() != null) {
                    MeasureDefinitionTenantDTO measure = measureOptional.get();
                    String fieldValue = (String) map.getValue();
                    switch (measureOptional.get().getFormat()) {
                        case "NUM":
                        case "NUMBER":
                            if (fieldValue != null && !fieldValue.isEmpty() && !ValidationUtils.isNumeric(fieldValue)) {
                                addParseError(checks, i, mappings.get(i), map.getKey() + " must be numeric");
                                count++;
                            }
                            break;
                        case "DATE":
                            if (fieldValue != null && !fieldValue.isEmpty() && !ValidationUtils.isValidDate(fieldValue, Utility.SYSTEM_DATE_FORMAT)) {
                                addParseError(checks, i, mappings.get(i),
                                        map.getKey() + " must be a date in format " + Utility.SYSTEM_DATE_FORMAT);
                                count++;
                            }
                    }
                }
            }
        }
        return count;
    }

    // Check 4 - Active and terminated subscriptions cannot be edited
    private int checkSubscriptionStatus(List<HealthCheck> checks, List<Map<?, ?>> mappings) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String subscriptionId = (String) mapping.get("subscription_id");
            String action = (String) mapping.get("action");
            if (subscriptionId != null && action != null && action.equals(EUploadAction.UPDATE.getAction()) && !subscriptionId.isEmpty()) {
                if (!ValidationUtils.isLong(subscriptionId)) {
                    addParseError(checks, i, mappings.get(i), "subscription_id must be an integer number");
                    count++;
                    continue;
                }
                CustomerSubscription subscription =
                        subscriptionService.findCustomerSubscriptionByIdNoThrow(Long.parseLong(subscriptionId));
                if (subscription == null) {
                    addParseError(checks, i, mappings.get(i), "Subscription not found with id " + subscriptionId);
                } else if (subscription.getSubscriptionStatus().equals(ESubscriptionStatus.ACTIVE.getStatus())) {
                    addParseError(checks, i, mappings.get(i), "Active subscriptions are not allowed to be updated");
                } else if (subscription.getSubscriptionStatus().equals(ESubscriptionStatus.TERMINATED.getStatus())) {
                    addParseError(checks, i, mappings.get(i), "Terminated subscriptions are not allowed to be updated");
                }
                count++;
            }
        }
        return count;
    }

    // Check 5 - Check subscription start date which can only be after minimum one month of garden start date (CSGSDT)
    private int checkSubscriptionStartDate(List<HealthCheck> checks, Long subscriptionRateMatrixId,
                                           List<Map<?, ?>> mappings) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String ssdtStr = (String) mapping.get("ssdt");
            if (ssdtStr != null && ValidationUtils.isValidDate(ssdtStr, Utility.SYSTEM_DATE_FORMAT)) {
                Date ssdt = Utility.getDate(ssdtStr, Utility.SYSTEM_DATE_FORMAT);
                String gardenStartDateStr =
                        subscriptionService.findBySubscriptionRateMatrixId(subscriptionRateMatrixId).stream()
                                .filter(d -> d.getRateCode().equals("CSGSDT")).findFirst().get().getDefaultValue();
                if (gardenStartDateStr != null) {
                    Date gardenStartDate = Utility.getDate(gardenStartDateStr, Utility.SYSTEM_DATE_FORMAT);
                    long monthsBetween = Utility.chronoUnitBetween(ChronoUnit.MONTHS, gardenStartDate, ssdt);
                    if (monthsBetween < 1) {
                        addParseError(checks, i, mappings.get(i), "ssdt can be minimum one month after garden start date ("
                                + gardenStartDateStr + "), i.e " + Utility.getDateString(Utility.addMonths(gardenStartDate, 1),
                                Utility.SYSTEM_DATE_FORMAT));
                        count++;
                    }
                }
            }
        }
        return count;
    }

    // Check 6 - Compare PSRC with payment info alias of respective account and SADD with address alias of respective
    // account
    private int checkPaymentInfoAndAddressAliases(List<HealthCheck> checks, List<Map<?, ?>> mappings) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String acctId = (String) mapping.get("acct_id");
            if (acctId == null) {
                addParseError(checks, i, mappings.get(i), "acct_id is required");
                count++;
            } else if (!ValidationUtils.isLong(acctId)) {
                addParseError(checks, i, mappings.get(i), "acct_id must be an integer number");
                count++;
            } else {
                User user = userService.findByIdNoThrow(Long.parseLong(acctId));
                if (user == null) {
                    addParseError(checks, i, mappings.get(i), "Customer account not found with acct_id " + acctId);
                    count++;
                } else {
                    /*String action = (String) mapping.get("action");
                    if (action != null && action.equals(EUploadAction.INSERT.getAction())) {*/
                    if (!user.getPaymentInfos().stream().filter(pi -> pi.getPaymentSrcAlias().equals(mapping.get(
                            "psrc"))).findAny().isPresent()) {
                        addParseError(checks, i, mappings.get(i),
                                "Payment alias not found with psrc " + mapping.get("psrc"));
                        count++;
                    }
                    if (!user.getAddresses().stream().filter(a -> a.getAddressType().equalsIgnoreCase("site")
                            && a.getAlias().trim().equals(mapping.get("sadd"))).findAny().isPresent()) {
                        addParseError(checks, i, mappings.get(i),
                                "Address alias not found with sadd " + mapping.get("sadd"));
                        count++;
                    }
                    /*} else if (action != null && action.equals(EUploadAction.UPDATE.getAction())) {
                        if (ValidationUtils.isLong((String) mapping.get("subscription_id"))) {
                            CustomerSubscription subscription =
                                    subscriptionService.findCustomerSubscriptionByIdNoThrow(Long.parseLong((String) mapping.get("subscription_id")));
                            if (subscription != null) {
                                Optional<CustomerSubscriptionMapping> psrcOptional =
                                        subscription.getCustomerSubscriptionMappings().stream().filter(m -> m.getRateCode().equalsIgnoreCase("psrc")).findFirst();
                                if (psrcOptional.isPresent() && !(mapping.get("psrc")).equals(psrcOptional.get().getValue())) {
                                    addParseError(checks, i, mappings.get(i),
                                            "Payment alias not found with psrc " + mapping.get("psrc"));
                                    count++;
                                }
                                Optional<CustomerSubscriptionMapping> saddOptional =
                                        subscription.getCustomerSubscriptionMappings().stream().filter(m -> m.getRateCode().equalsIgnoreCase("sadd")).findFirst();
                                if (saddOptional.isPresent() && !(mapping.get("sadd")).equals(saddOptional.get().getValue())) {
                                    addParseError(checks, i, mappings.get(i),
                                            "Address alias not found with sadd '" + mapping.get("sadd") + "'");
                                    count++;
                                }
                            }
                        }
                    }*/
                }
            }
        }
        return count;
    }

    // Check 7 - Cannot exceed garden size (GNSIZE) available capacity with active/inactive subscriptions
    private int checkGardenCapacity(List<HealthCheck> checks, Long subscriptionRateMatrixId, List<Map<?, ?>> mappings) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String kwdcStr = (String) mapping.get("kwdc");
            if (kwdcStr != null && ValidationUtils.isNumeric(kwdcStr)) {
                String gardenCapacityString = subscriptionService.findBySubscriptionRateMatrixIdAndRateCode(subscriptionRateMatrixId,
                        SubscriptionRateCodes.GARDEN_SIZE).getDefaultValue();
                double gardenCapacity =
                        Double.parseDouble(gardenCapacityString == null ? "0" : gardenCapacityString);
                Double gardenCapacityConsumed = subscriptionService.gardenCapacityConsumed(subscriptionRateMatrixId);
                gardenCapacityConsumed = gardenCapacityConsumed == null ? 0 : gardenCapacityConsumed;
                double kwdc = Double.parseDouble(kwdcStr);
                if (kwdc > gardenCapacity - gardenCapacityConsumed) {
                    addParseError(checks, i, mappings.get(i), "kwdc " + kwdc + " is more than the unutilized garden capacity "
                            + utility.round((gardenCapacity - gardenCapacityConsumed), utility.getCompanyPreference().getRounding()));
                    count++;
                }
            }
        }
        return count;
    }

    // Premise number is unique in garden.
    private int insertWithExistingPremiseNumberNotAllowed(List<HealthCheck> checks, Long subscriptionRateMatrixId,
                                                          List<Map<?, ?>> mappings) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String action = (String) mapping.get("action");
            String pn = (String) mapping.get("pn");
            String subscriptionIdStr = (String) mapping.get("subscription_id");
            if (subscriptionIdStr != null && !subscriptionIdStr.isEmpty() && ValidationUtils.isLong(subscriptionIdStr)) {
                Long subscriptionId = Long.parseLong(subscriptionIdStr);
//                CustomerSubscription subscription = subscriptionService.findCustomerSubscriptionByIdNoThrow
//                (subscriptionId);
                SubscriptionRateMatrixHead matrixHead =
                        subscriptionService.findSubscriptionRateMatrixHeadById(subscriptionRateMatrixId);
                List<CustomerSubscriptionMapping> existingPN =
                        subscriptionService.findCustomerSubscriptionMappingByfindByRateCodeValueMatrixHead(SubscriptionRateCodes.PREMISE_NUMBER, pn, matrixHead);
                if (action != null && !existingPN.isEmpty()) {
                    if (action.equals(EUploadAction.INSERT.getAction()) || (action.equals(EUploadAction.UPDATE.getAction())
                            && existingPN.stream().filter(m -> m.getSubscription().getId() != subscriptionId.longValue()).findFirst().isPresent())) {
                        addParseError(checks, i, mappings.get(i),
                                "Premise '" + pn + "' in garden '" + matrixHead.getSubscriptionTemplate() + "' " +
                                        "already exists ");
                    }
                }
            }
        }
        return count;
    }
}
