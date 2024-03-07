package com.solar.api.saas.service.process.parser.product.csgrpre;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.process.rule.RuleHead;
import com.solar.api.saas.repository.RuleHeadRepository;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.MeasureType;
import com.solar.api.saas.service.integration.mongo.response.subscription.Subscription;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.integration.mongo.response.subscription.Variant;
import com.solar.api.saas.service.process.ExecuteParams;
import com.solar.api.saas.service.process.parser.product.BillCreditImportVOS;
import com.solar.api.saas.service.process.rule.RulesInitiator;
import com.solar.api.tenant.mapper.billing.billingHead.BillingDetailMapper;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.billing.tansStage.TransStageTemp;
import com.solar.api.tenant.model.process.rule.RuleExecutionLog;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.repository.ImportFileMapRepository;
import com.solar.api.tenant.service.*;
import com.solar.api.tenant.service.process.rule.RuleExecutionLogService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
@Component
public class InitiatorCSGRPre extends RulesInitiator {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private BillingDetailService billingDetailService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private RuleExecutionLogService ruleExecutionLogService;
    @Autowired
    private ImportFileMapRepository mapRepository;
    @Autowired
    private RuleHeadRepository ruleHeadRepository;
    @Autowired
    private BillCreditImportVOS billCreditImportVOS;
    @Autowired
    private CalculationDetailsService calculationDetailsService;
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    private CalculationTrackerService calculationTrackerService;

    @Override
    public Map<String, Object> generateHashMap(CustomerSubscription subscription, Long subscriptionMatrixHeadId,
                                               BillingHead billingHead, Long jobId) {
        return null;
    }

    @Override
    public Map<String, Object> generateHashMap(SubscriptionMapping subscriptionMapping, BillingHead billingHead, Long jobId) {
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
        List<TransStageTemp> transStageTempListLvl0 = transStageTempList.stream().filter(m->m.getLevel()==0).collect(Collectors.toList());
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

    @Transactional
    @Override
    public void calculate(BillingHead billingHead, Subscription subscription, Long ruleHeadId,
                          Map<String, Object> valuesHashMap, Long jobId) {
        if (!(billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus())
                || billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()))) {
            return;
        }
        try {
            LOGGER.info("\tInitiated CSG_R_PRE calculations");
            RuleHead ruleHead = ruleHeadRepository.findById(ruleHeadId).get();
            /* Multiply POWG with STRE (Derivative with condition of customer type),
            (POWG * [SRTE] * (1 - NVL (S_DSCP, 0)) * -1 as MBILL (Adjusted monthly bill) & add to billing detail */
//            BillingDetail powgDetail = billingHead.getBillingDetails().stream().filter(d -> d.getRateCode().equals("POWG")).findFirst().orElse(null);
//            double POWG = powgDetail.getValue(); // = MPA
            double POWG = 10;
            double SRTE = (Double) valuesHashMap.get("S_SRTE");
            Double S_DSCP = Double.parseDouble((String) valuesHashMap.get("S_DSCP"));
            double MBILL = (POWG * SRTE * (1 - (S_DSCP != null ? S_DSCP : 0))) * -1;
            // TODO: Also check if last total prepaid amount is different from the current prepaid amount in measure, then add the same difference to S_REMBAL
            // Add total prepaid amount (S_TPRE) in rule_execution_log
            // Add running balance to billing detail (billing code = RBAL) from the current value of S_REMBAL from subscription, replace if exists.
            double S_TPRE = Double.parseDouble(String.valueOf(valuesHashMap.get("S_TPRE")));
            Double S_REMBAL = Double.parseDouble(String.valueOf(valuesHashMap.get("S_REMBAL")));
            if (S_REMBAL == null) {
                S_REMBAL = S_TPRE;
            }
            double RBAL = S_REMBAL;
            // if the bill is net off to 0, mark the bill to PAID and update S_REMBAL measure code in subscription to (RBAL - MBILL)
            S_REMBAL = RBAL - MBILL;
            if (S_REMBAL >= 0) {
                billingHead.setBillStatus(EBillStatus.PAID.getStatus());
            }
            // If the bill is not net to 0 or amount is pending the bill is marked to INVOICED and S_REMBAL is updated to RBAL - MBILL, where the value will stand less than 0
            if (S_REMBAL < 0) {
                billingHead.setBillStatus(EBillStatus.INVOICED.getStatus());
            }
            // Update S_REMBAL measure code in subscription
            MeasureType srembalMeasure =
                    subscription.getMeasures().getByCustomer().stream().filter(m -> m.getCode().equals("S_REMBAL")).findFirst().orElse(null);
            if (srembalMeasure != null) {
                srembalMeasure.setDefaultValue(String.valueOf(S_REMBAL));
                dataExchange.updateSubscription(subscription, DBContextHolder.getTenantName());
            }

            saveBillingDetails(billingHead, MBILL, RBAL, POWG, SRTE);
            LOGGER.info("\t\tLine sequences added to billing detail records");
            saveRuleExecutionLogs(billingHead, jobId, ruleHead, POWG, SRTE, S_DSCP, MBILL, S_TPRE, RBAL, S_REMBAL);

            // set amount
            billingHead.setAmount(billingDetailService.findByBillingHeadId(billingHead.getId()).stream()
                    .filter(billingDetail -> billingDetail.getAddToBillAmount())
                    .mapToDouble(billingDetail -> billingDetail.getValue())
                    .sum());
            billingHead.setBillStatus(EBillStatus.CALCULATED.toString()); //Why was this commented?

            //Updating Status Of Calculation Detail Here
            calculationTrackerService.updateBillingLogCalculation(billingHead.getId(), billingHead.getBillStatus());

            LOGGER.info("\t\tAggregated bill amount added to billing head");
            LOGGER.info("\tCSG_R_PRE bill generation completed");
        } catch (Exception e) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("jobId", jobId);
            messageJson.put("billingHeadId", billingHead.getId());
            LOGGER.error(e.getMessage(), e);
        }
        /* P.S
             S_REMBAL is in subscription
             S_TPRE in subscription and
             RBAL in billing_detail */
    }

    @Override
    public void calculate(BillingHead billingHead, CustomerSubscription subscription, List<TransStageTemp> transStageTempList, Long ruleHeadId, Map<String, Object> valuesHashMap, Long jobId) {
        if (!(billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus())
                || billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()))) {
            return;
        }
        try {
            LOGGER.info("\tInitiated CSG_R_PRE calculations");
            //TODO: Update ruleHead
            /* Multiply POWG with STRE (Derivative with condition of customer type),
            (POWG * [SRTE] * (1 - NVL (S_DSCP, 0)) * -1 as MBILL (Adjusted monthly bill) & add to billing detail */
//            BillingDetail powgDetail = billingHead.getBillingDetails().stream().filter(d -> d.getRateCode().equals("POWG")).findFirst().orElse(null);
//            double POWG = powgDetail.getValue(); // = MPA
            double POWG = 10;
            double SRTE = (Double) valuesHashMap.get("S_SRTE");
            Double S_DSCP = Double.parseDouble((String) valuesHashMap.get("S_DSCP"));
            double MBILL = (POWG * SRTE * (1 - (S_DSCP != null ? S_DSCP : 0))) * -1;
            // TODO: Also check if last total prepaid amount is different from the current prepaid amount in measure, then add the same difference to S_REMBAL
            // Add total prepaid amount (S_TPRE) in rule_execution_log
            // Add running balance to billing detail (billing code = RBAL) from the current value of S_REMBAL from subscription, replace if exists.
            double S_TPRE = Double.parseDouble(String.valueOf(valuesHashMap.get("S_TPRE")));
            Double S_REMBAL = Double.parseDouble(String.valueOf(valuesHashMap.get("S_REMBAL")));
            if (S_REMBAL == null) {
                S_REMBAL = S_TPRE;
            }
            double RBAL = S_REMBAL;
            // if the bill is net off to 0, mark the bill to PAID and update S_REMBAL measure code in subscription to (RBAL - MBILL)
            S_REMBAL = RBAL - MBILL;
            if (S_REMBAL >= 0) {
                billingHead.setBillStatus(EBillStatus.PAID.getStatus());
            }
            // If the bill is not net to 0 or amount is pending the bill is marked to INVOICED and S_REMBAL is updated to RBAL - MBILL, where the value will stand less than 0
            if (S_REMBAL < 0) {
                billingHead.setBillStatus(EBillStatus.INVOICED.getStatus());
            }
            // TODO: Update S_REMBAL measure code in subscription
//            MeasureType srembalMeasure =
//                    subscription.getMeasures().getByCustomer().stream().filter(m -> m.getCode().equals("S_REMBAL")).findFirst().orElse(null);
//            if (srembalMeasure != null) {
//                srembalMeasure.setDefaultValue(String.valueOf(S_REMBAL));
//                dataExchange.updateSubscription(subscription, DBContextHolder.getTenantName());
//            }

            saveBillingDetails(billingHead, MBILL, RBAL, POWG, SRTE);
            LOGGER.info("\t\tLine sequences added to billing detail records");
            saveRuleExecutionLogs(billingHead, jobId, null, POWG, SRTE, S_DSCP, MBILL, S_TPRE, RBAL, S_REMBAL);

            // set amount
            billingHead.setAmount(billingDetailService.findByBillingHeadId(billingHead.getId()).stream()
                    .filter(billingDetail -> billingDetail.getAddToBillAmount())
                    .mapToDouble(billingDetail -> billingDetail.getValue())
                    .sum());
            billingHead.setBillStatus(EBillStatus.CALCULATED.toString()); //Why was this commented?

            //Updating Status Of Calculation Detail Here
            calculationTrackerService.updateBillingLogCalculation(billingHead.getId(), billingHead.getBillStatus());

            LOGGER.info("\t\tAggregated bill amount added to billing head");
            LOGGER.info("\tCSG_R_PRE bill generation completed");
        } catch (Exception e) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("jobId", jobId);
            messageJson.put("billingHeadId", billingHead.getId());
            LOGGER.error(e.getMessage(), e);
        }
        /* P.S
             S_REMBAL is in subscription
             S_TPRE in subscription and
             RBAL in billing_detail */
    }

    private void saveBillingDetails(BillingHead billingHead, double MBILL, double RBAL, double POWG, double SRTE) {
        List<BillingDetail> billingDetailLines = new ArrayList<>();
        billingDetailLines.add(addLineSequence(billingHead, MBILL, 1, "MBILL", true));
        billingDetailLines.add(addLineSequence(billingHead, RBAL, 2, "RBAL", false));
        billingDetailLines.add(addLineSequence(billingHead, POWG, 3, "POWG", false));
        billingDetailLines.add(addLineSequence(billingHead, SRTE, 4, "SRTE", false));
        billingDetailService.saveAll(billingDetailLines);
    }

    private void saveRuleExecutionLogs(BillingHead billingHead, Long jobId, RuleHead ruleHead, double POWG, double SRTE,
                                       double S_DSCP, double MBILL, double S_TPRE, double RBAL, double S_REMBAL) {
        List<RuleExecutionLog> executionLogs = new ArrayList<>();
        Long billId = billingHead.getId();
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "POWG", POWG));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "SRTE", SRTE));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "S_DSCP", S_DSCP));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "MBILL", MBILL));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "S_TPRE", S_TPRE));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "RBAL", RBAL));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "S_REMBAL", S_REMBAL));
        ruleExecutionLogService.save(executionLogs);
    }

    public void calculate(BillingHead billingHead, Long rateMatrixHeadId, Long ruleHeadId,
                          Map<String, Object> valuesHashMap, Long jobId) {
    }

    private BillingDetail addLineSequence(BillingHead billingHead, double value, int lineSequence,
                                          String billingCode, boolean addToBillAmount) {
        BillingDetail billingDetailLine = BillingDetail.builder()
                .billingHead(billingHead)
                .value(value)
                .lineSeqNo(lineSequence)
                .billingCode(billingCode) // from PortalAttribute
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
    public void execute(ExecuteParams params, SubscriptionMapping subscriptionMapping, BillingHead billingHead, Long jobId) {
        Map<String, Object> valuesHashMap = generateHashMap(subscriptionMapping, billingHead, jobId); // generate only static, dynamic in loop with billing head
        calculate(billingHead, subscriptionMapping.getSubscription(), params.getRuleHeadId(), valuesHashMap, jobId);
    }

}
