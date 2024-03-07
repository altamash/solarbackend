package com.solar.api.saas.service.process.parser.product.pwgenbill;

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
import com.solar.api.tenant.model.stage.billing.ExtDataStageDefinitionBilling;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.repository.ImportFileMapRepository;
import com.solar.api.tenant.service.BillingDetailService;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.process.rule.RuleExecutionLogService;
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
public final class InitiatorPWGenBill extends RulesInitiator {

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
    private ProductionImport productionImport;

    @Override
    public Map<String, Object> generateHashMap(CustomerSubscription subscription, Long subscriptionMatrixHeadId,
                                               BillingHead billingHead, Long jobId) {
//        valuesHashMap.clear();
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
    public Map<String, Object> generateHashMap(List<TransStageTemp> transStageTempList, BillingHead billingHead,String subscriptionId, Long jobId) {
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

    // MONTHLY BILL : (KWDC*YLD*DEPM^(OPYR-1)*(SRTE*DSCM))/12 - It should be a class - (DEPM = 1-DEP)
    public void calculate(BillingHead billingHead, Long rateMatrixHeadId, Long ruleHeadId,
                          Map<String, Object> valuesHashMap, Long jobId) {
        if (!(billingHead.getBillStatus().equals(EBillStatus.SCHEDULED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.GENERATED.getStatus()))) {
            return;
        }
        try {
            RuleHead ruleHead = ruleHeadRepository.findById(ruleHeadId).get();
            productionImport.calculate(billingHead, rateMatrixHeadId, ruleHead.getId(), valuesHashMap, jobId);
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
    public void calculate(BillingHead billingHead, Subscription subscription, Long ruleHeadId,
                          Map<String, Object> valuesHashMap, Long jobId) {
        if (!(billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.RECALCULATING.getStatus()))) {
            return;
        }
        try {
            RuleHead ruleHead = ruleHeadRepository.findById(ruleHeadId).get();
            productionImport.calculate(billingHead, subscription, ruleHead.getId(), valuesHashMap, jobId);
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
        if (!(billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.RECALCULATING.getStatus()))) {
            return;
        }
        try {
            productionImport.calculate(billingHead, subscription,transStageTempList, null, valuesHashMap, jobId);
            LOGGER.info("\tInitiated CSGR calculations");
            LOGGER.info("\tInitiatorCSGF bill generation completed");
        } catch (Exception e) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("jobId", jobId);
            messageJson.put("billingHeadId", billingHead.getId());
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void addLineSequence(BillingHead billingHead, String rateCode, double value, int lineSequence,
                                 String billingCode, boolean addToBillAmount) {
        BillingDetail billingDetailLine = BillingDetail.builder()
                .billingHead(billingHead)
                .rateCode(rateCode)
                .value(value)
                .lineSeqNo(lineSequence)
                .billingCode(billingCode) // from PortalAttribute
                .date(new Date())
                .addToBillAmount(addToBillAmount)
                .build();
        BillingDetail billingDetail = billingDetailService.findByBillingHeadAndBillingCode(billingHead, billingCode);
        if (billingDetail != null) {
            billingDetail = BillingDetailMapper.toUpdatedBillingDetail(billingDetail, billingDetailLine);
            billingDetailService.addOrUpdateBillingDetail(billingDetail);
        } else {
            billingDetailService.addOrUpdateBillingDetail(billingDetailLine);
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
