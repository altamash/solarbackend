package com.solar.api.saas.service.process.parser.product.csg.ppa.postpaid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.model.process.rule.RuleHead;
import com.solar.api.saas.repository.RuleHeadRepository;
import com.solar.api.saas.service.integration.mongo.response.subscription.MeasureType;
import com.solar.api.saas.service.integration.mongo.response.subscription.Subscription;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.integration.mongo.response.subscription.Variant;
import com.solar.api.saas.service.process.ExecuteParams;
import com.solar.api.saas.service.process.rule.RulesInitiator;
import com.solar.api.tenant.mapper.billing.billingHead.BillingDetailMapper;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.billing.tansStage.TransStageTemp;
import com.solar.api.tenant.model.billingCredits.BillingCredits;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.repository.BillingCreditsRepository;
import com.solar.api.tenant.service.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
@Component
public class InitiatorCSGPPAPost extends RulesInitiator {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private RuleHeadRepository ruleHeadRepository;
    @Autowired
    private BillingDetailService billingDetailService;
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private BillingCreditsRepository billingCreditsRepository;
    @Autowired
    private CalculationTrackerService calculationTrackerService;

    @Override
    public Map<String, Object> generateHashMap(CustomerSubscription subscription, Long subscriptionMatrixHeadId, BillingHead billingHead, Long jobId) {
        Map<String, Object> valuesHashMap = new HashMap<>();
        valuesHashMap.put("MNYR", billingHead.getBillingMonthYear());
        List<CustomerSubscriptionMapping> staticMappings =
                subscriptionService.getMappingsWithStaticValues(subscription, subscriptionMatrixHeadId);
        List<CustomerSubscriptionMapping> calculationMappings =
                subscriptionService.getMappingsForCalculationOrderedBySequence(subscription, subscriptionMatrixHeadId);
        LOGGER.info("\tStarted storing values in HashMap");
        LOGGER.info("\t\tStatic values:");
        staticMappings.stream()
                .forEach(mapping -> {
                    valuesHashMap.put(mapping.getRateCode(), mapping.getValue());
                    LOGGER.info("\t\t\t" + mapping.getRateCode() + " = " + mapping.getValue());
                });
        // Add parsed dynamic values in map
        parseAndUpdateValues(calculationMappings, valuesHashMap, subscription.getId(), jobId);
        LOGGER.info("\tCompleted  storing values in HashMap");
        return valuesHashMap;
    }

    @Override
    public Map<String, Object> generateHashMap(SubscriptionMapping subscriptionMapping, BillingHead billingHead,
                                               Long jobId) {
        Map<String, Object> valuesHashMap = new HashMap<>();
        valuesHashMap.put("MNYR", billingHead.getBillingMonthYear());
        LOGGER.info("\tStarted storing values in HashMap");
        LOGGER.info("\t\tStatic values:");
        Subscription subscription = subscriptionMapping.getSubscription();
        Variant variant = subscriptionMapping.getVariant();
        List<MeasureType> measureTypes = new ArrayList<>();
        measureTypes.addAll(variant.getMeasures().getByProduct().stream().filter(m -> m.getLevel() == 0).collect(Collectors.toList()));
        measureTypes.addAll(subscription.getMeasures().getByCustomer().stream().filter(m -> m.getLevel() == 0).collect(Collectors.toList()));
        measureTypes.stream()
                .forEach(type -> {
                    valuesHashMap.put(type.getCode(), type.getDefaultValue());
                    LOGGER.info("\t\t\t" + type.getCode() + " = " + type.getDefaultValue());
                });
        // Add parsed dynamic values in map
        parseAndUpdateValues(subscriptionMapping, valuesHashMap, subscription.getId().getOid(), jobId);
        LOGGER.info("\tCompleted  storing values in HashMap");
        return valuesHashMap;
    }

    @Override
    public Map<String, Object> generateHashMap(List<TransStageTemp> transStageTempList, BillingHead billingHead, String subscriptionId, Long jobId) {
        Map<String, Object> valuesHashMap = new HashMap<>();
        valuesHashMap.put("MNYR", billingHead.getBillingMonthYear());
        LOGGER.info("\tStarted storing values in HashMap");
        LOGGER.info("\t\tStatic values:");
        List<TransStageTemp> transStageTempListLvl0 = transStageTempList.stream().filter(m -> m.getLevel() == 0).collect(Collectors.toList());
        transStageTempListLvl0.stream()
                .forEach(type -> {
                    valuesHashMap.put(type.getMeasCode(), type.getValue());
                    LOGGER.info("\t\t\t" + type.getMeasCode() + " = " + type.getValue());
                });
        // Add parsed dynamic values in map
        parseAndUpdateValues(transStageTempList, valuesHashMap, subscriptionId, jobId);
        LOGGER.info("\tCompleted  storing values in HashMap");
        return valuesHashMap;
    }

    @Override
    public void calculate(BillingHead billingHead, Long rateMatrixHeadId, Long ruleHeadId,
                          Map<String, Object> valuesHashMap, Long jobId) {
        if (!(billingHead.getBillStatus().equals(EBillStatus.SCHEDULED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.GENERATED.getStatus()))) {
            return;
        }
        try {
            if (!(billingHead.getBillStatus().equals(EBillStatus.SCHEDULED.getStatus()) ||
                    billingHead.getBillStatus().equals(EBillStatus.GENERATED.getStatus()))) {
                return;
            }
            RuleHead ruleHead = ruleHeadRepository.findById(ruleHeadId).get();
            if (billingDetailService.findByBillingHeadAndBillingCode(billingHead, ruleHead.getBillingCode()) != null) {
                return;
            }
            // garden, month, subscription id
            String convertedMonth = rateFunctions.parseMonthYearFormat(billingHead.getBillingMonthYear());
            Optional<BillingCredits> billingCreditsOptional =
                    billingCreditsRepository.findByGardenIdAndCalendarMonthAndCreditCodeVal(
                            (String) valuesHashMap.get("SCSGN"), convertedMonth, (String) valuesHashMap.get("PN"));
            if (!billingCreditsOptional.isPresent()) {
                LOGGER.info("Power generation record not found for bill id " + billingHead.getId() + " !!");
                return;
            }
            doCalculate(valuesHashMap, billingCreditsOptional.get(), billingHead);
            LOGGER.info("\tInitiated CSGR calculations");
            LOGGER.info("\tInitiatorCSGF bill generation completed");
        } catch (Exception e) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("jobId", jobId);
            messageJson.put("billingHeadId", billingHead.getId());
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void doCalculate(Map<String, Object> valuesHashMap, BillingCredits billingCredit, BillingHead billingHead) {
        List<BillingDetail> billingDetailLines = new ArrayList<>();
        // Step 1. Hash Map Values Compared with Excel Records (bill credit upload) using Garden SRC and Month
        // and Subscription ID (from Premise Number Column). Save the values in Billing Detail
        double UPWC = billingCredit.getCreditValue();
        double MPA = billingCredit.getMpa();
        double MRATE = billingCredit.getTariffRate();
        double STAX = ((Double) valuesHashMap.get("STAX"));
        double CTAX = ((Double) valuesHashMap.get("CTAX"));

        // Step 2 Check Current Value in Reserve Excess Amount Measure (REXCS)
        // and save in Billing Detail As Previous Excess Amount (Billing Code PEXAMT). 0 if null.
        Double REXCS = ((Double) valuesHashMap.get("REXCS"));
        REXCS = REXCS == null ? 0 : REXCS;
        double PEXAMT = REXCS;

        // Step 3
        // If REXCS = 0 or Null which means no excess units available following Scenarios Apply
            /* MPA <= UPWC then GBILL = MPA * MRATE  in Billing Detail. This record gbill shows bill without any tax.
            MEXCS = 0, RPCR=REXCS. NO NEED TO UPDATE REXCS AGAIN */
        double GBILL = 0;
        double MEXCS = 0;
        double RPCR = 0;
        if (MPA <= UPWC) {
            GBILL = MPA * MRATE;
            MEXCS = 0;
            RPCR = REXCS;
        } else if (MPA > UPWC) {
            GBILL = UPWC * MRATE;
            RPCR = MPA - UPWC;
            MEXCS = 0;
            REXCS = REXCS + RPCR;
        }
        // if REXCS>0 and if you produced less than the energy billed from utility company then you can use excess from REXCS value
            /* MPA<UPWC then MEXCS = UPWC - MPA upto the value of REXCS (Rexcs can be equal or less than the required
            excess credit from UPWC-MPA) > RPCR = REXCS-MEXCS, GBILL = (MPA+MEXCS) * MRATE and REXCS = RPCR */
        if (REXCS > 0 && MPA < UPWC) {
            MEXCS = UPWC - MPA;
            RPCR = REXCS - MEXCS;
            GBILL = (MPA + MEXCS) * MRATE;
            REXCS = RPCR;
        }

        // Step 4
        double MBILL = GBILL * (1 + STAX - CTAX);  // this bill goes to Bill Head for Invoice

        billingDetailLines.add(addLineSequence(billingHead, PEXAMT, 1, "PEXAMT", false));
        billingDetailLines.add(addLineSequence(billingHead, REXCS, 2, "REXCS", false));
        billingDetailLines.add(addLineSequence(billingHead, MEXCS, 3, "MEXCS", false));
        billingDetailLines.add(addLineSequence(billingHead, RPCR, 4, "RPCR", false));
        billingDetailLines.add(addLineSequence(billingHead, UPWC, 5, "UPWC", false));
        billingDetailLines.add(addLineSequence(billingHead, MPA, 6, "MPA", false));
        billingDetailLines.add(addLineSequence(billingHead, MRATE, 7, "MRATE", false));
        billingDetailLines.add(addLineSequence(billingHead, GBILL, 8, "GBILL", false));
        billingDetailLines.add(addLineSequence(billingHead, STAX, 9, "STAX", false));
        billingDetailLines.add(addLineSequence(billingHead, CTAX, 10, "CTAX", false));
        billingDetailLines.add(addLineSequence(billingHead, MBILL, 11, "MBILL", true));
        billingDetailService.saveAll(billingDetailLines);
        LOGGER.info("\t\tLine sequences added to billing detail records");

        // set amount
        Double amount = billingDetailService.findByBillingHeadId(billingHead.getId()).stream()
                .filter(value -> value.getAddToBillAmount())
                .mapToDouble(detail -> detail.getValue())
                .sum();
        billingHead.setAmount(amount);
        // set status to GENERATED
        billingHead.setBillStatus(EBillStatus.CALCULATED.toString());
        billingHeadService.addOrUpdateBillingHead(billingHead);
        //Updating Status Of Calculation Detail Here
        calculationTrackerService.updateBillingLogCalculation(billingHead.getId(), billingHead.getBillStatus());
        LOGGER.info("\t\tAggregated bill amount added to billing head");
    }

    private BillingDetail addLineSequence(BillingHead billingHead, double value, int lineSequence,
                                          String billingCode, boolean addToBillAmount) {
        BillingDetail billingDetailLine = BillingDetail.builder()
                .billingHead(billingHead)
                .value(value)
                .lineSeqNo(lineSequence)
                .billingCode(billingCode)
                .date(new Date())
                .addToBillAmount(addToBillAmount)
                .build();
        BillingDetail billingDetail = billingDetailService.findByBillingHeadAndBillingCode(billingHead, billingCode);
        if (billingDetail != null) {
            billingDetail = BillingDetailMapper.toUpdatedBillingDetail(billingDetail, billingDetailLine);
            return billingDetail;
        } else {
            return billingDetailLine;
        }
    }

    @Override
    public void calculate(BillingHead billingHead, Subscription subscription, Long ruleHeadId, Map<String, Object> valuesHashMap, Long jobId) {
        String error;
        if (!(billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.RECALCULATING.getStatus()))) {
            return;
        }
        try {
            RuleHead ruleHead = ruleHeadRepository.findById(ruleHeadId).get();
            if (billingDetailService.findByBillingHeadAndBillingCode(billingHead, ruleHead.getBillingCode()) != null) {
                return;
            }
            // garden, month, subscription id
            String convertedMonth = rateFunctions.parseMonthYearFormat(billingHead.getBillingMonthYear());
            Optional<BillingCredits> billingCreditsOptional =
                    billingCreditsRepository.findByGardenIdAndCalendarMonthAndCreditCodeVal(
                            (String) valuesHashMap.get("SCSGN"), convertedMonth, (String) valuesHashMap.get("PN"));
            if (!billingCreditsOptional.isPresent()) {
                LOGGER.info("Power generation record not found for bill id " + billingHead.getId() + " !!");
                error = "Power generation record not found for bill";
                calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
                return;
            }
            doCalculate(valuesHashMap, billingCreditsOptional.get(), billingHead);
            LOGGER.info("\tInitiated CSGR calculations");
            LOGGER.info("\tInitiatorCSGF bill generation completed");
        } catch (Exception e) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("jobId", jobId);
            messageJson.put("billingHeadId", billingHead.getId());
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void calculate(BillingHead billingHead, CustomerSubscription subscription, List<TransStageTemp> transStageTempList, Long ruleHeadId, Map<String, Object> valuesHashMap, Long jobId) {
        String error;
        if (!(billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.RECALCULATING.getStatus()))) {
            return;
        }
        try {
            // garden, month, subscription id
            String convertedMonth = rateFunctions.parseMonthYearFormat(billingHead.getBillingMonthYear());
            Optional<BillingCredits> billingCreditsOptional =
                    billingCreditsRepository.findByGardenIdAndCalendarMonthAndCreditCodeVal(
                            (String) valuesHashMap.get("SCSGN"), convertedMonth, (String) valuesHashMap.get("S_PN"));
            if (!billingCreditsOptional.isPresent()) {
                LOGGER.info("Power generation record not found for bill id " + billingHead.getId() + " !!");
                error = "Power generation record not found for bill";
                calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
                return;
            }
            doCalculate(valuesHashMap, billingCreditsOptional.get(), billingHead);
            LOGGER.info("\tInitiated CSGR calculations");
            LOGGER.info("\tInitiatorCSGF bill generation completed");
        } catch (Exception e) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("jobId", jobId);
            messageJson.put("billingHeadId", billingHead.getId());
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void execute(ExecuteParams params, CustomerSubscription subscription, BillingHead billingHead, Long jobId) {
        Map<String, Object> valuesHashMap = generateHashMap(subscription, params.getRateMatrixHeadId(), billingHead,
                jobId); // generate only static, dynamic in loop with billing head
        calculate(billingHead, params.getRateMatrixHeadId(), params.getRuleHeadId(), valuesHashMap, jobId);
    }

    @Override
    public void execute(ExecuteParams params, SubscriptionMapping subscriptionMapping, BillingHead billingHead, Long jobId) {
        Map<String, Object> valuesHashMap = generateHashMap(subscriptionMapping, billingHead, jobId); // generate only static, dynamic in loop with billing head
        calculate(billingHead, subscriptionMapping.getSubscription(), params.getRuleHeadId(), valuesHashMap, jobId);
    }
}
