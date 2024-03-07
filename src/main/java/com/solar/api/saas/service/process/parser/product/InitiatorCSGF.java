package com.solar.api.saas.service.process.parser.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.saas.model.process.rule.RuleHead;
import com.solar.api.saas.repository.RuleHeadRepository;
import com.solar.api.saas.service.integration.mongo.response.subscription.MeasureType;
import com.solar.api.saas.service.integration.mongo.response.subscription.Subscription;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.integration.mongo.response.subscription.Variant;
import com.solar.api.saas.service.process.ExecuteParams;
import com.solar.api.saas.service.process.rule.RulesInitiator;
import com.solar.api.tenant.mapper.billing.billingHead.BillingDetailMapper;
import com.solar.api.tenant.mapper.billingCredits.BillingCreditResult;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.billing.tansStage.TransStageTemp;
import com.solar.api.tenant.model.process.rule.RuleExecutionLog;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.repository.BillingCreditsRepository;
import com.solar.api.tenant.service.*;
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
public final class InitiatorCSGF extends RulesInitiator {

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
    private BillingCreditsRepository billingCreditsRepository;
    @Autowired
    private RuleHeadRepository ruleHeadRepository;
    @Autowired
    private CalculationTrackerService calculationTrackerService;

    @Override
    public Map<String, Object> generateHashMap(CustomerSubscription subscription, Long subscriptionMatrixHeadId,
                                               BillingHead billingHead, Long jobId) {
        Map<String, Object> valuesHashMap = new HashMap<>();
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

    // MONTHLY BILL : (KWDC*YLD*DEPM^(OPYR-1)*(SRTE*DSCM))/12 - It should be a class - (DEPM = 1-DEP)
    @Override
    public void calculate(BillingHead billingHead, Long rateMatrixHeadId, Long ruleHeadId,
                          Map<String, Object> valuesHashMap, Long jobId) {
        if (!(billingHead.getBillStatus().equals(EBillStatus.SCHEDULED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.GENERATED.getStatus()))) {
            return;
        }

        LOGGER.info("\tInitiated CSGF calculations");
        Long billId = billingHead.getId();
        // For RuleExecutionLog:
        // KWDC YLD DEPM OPYR SRTE DSCM
        // Bill head: Discounted Bill Credit Rate
        // Saved in monthly billing heading
        // Estimated Monthly Bill Credit
        // KWDC*YLD*DEPM^(OPYR-1)*SRTE (divice by twelve), if DEPM is not defined then use 1-DEP
        // Last Actual Bill Credit Received (from excel)
        // Savings

        Long subscriptionId = billingHead.getSubscriptionId();
        String subscriptionMatrixRef = subscriptionId + rateMatrixHeadId.toString();
        RuleHead ruleHead = ruleHeadRepository.findById(ruleHeadId).get();
        Double KWDC = getDouble(valuesHashMap.get("KWDC"));
        Double YLD = getDouble(valuesHashMap.get("YLD"));
        // valuesHashMap.get("DEPM"); // not found
        Double DEP = getDouble(valuesHashMap.get("DEP"));
        Double DEPM = 1 - DEP;
        Integer OPYR = getInteger(valuesHashMap.get("OPYR"));
        Double SRTE = (Double) valuesHashMap.get("SRTE");
        Double DSCM = getDouble(valuesHashMap.get("DSCM"));

        Double ebcreValue = (KWDC * YLD * Math.pow(DEPM, (OPYR - 1)) * (SRTE)) / 12;
        Double mbillValue = (KWDC * YLD * Math.pow(DEPM, (OPYR - 1)) * (SRTE * DSCM)) / 12;

        ////////////// Update
        String convertedMonth = rateFunctions.parseMonthYearFormat(billingHead.getBillingMonthYear());
        List<BillingDetail> billingDetailLines = new ArrayList<>();
        BillingCreditResult abcreAndMpa = billingCreditsRepository.getABCREAndMPA((String) valuesHashMap.get("PN"),
                (String) valuesHashMap.get("SCSGN"), convertedMonth);
        if (abcreAndMpa == null) {
            abcreAndMpa = billingCreditsRepository.getABCREAndMPA(billingHead.getSubscriptionId(), convertedMonth);
        }
        BillingDetail mpaDetail = billingDetailService.findByBillingHeadAndBillingCode(billingHead, "MPA");
        BillingDetail abcreDetail = billingDetailService.findByBillingHeadAndBillingCode(billingHead, "ABCRE");
        Double mpa = mpaDetail != null ? mpaDetail.getValue() : 0d;
        Double abcre = abcreDetail != null ? abcreDetail.getValue() : 0d;
        LOGGER.info("\t\tAdding MPA and actual bill credit for bill id " + billingHead.getId());
        if (mpaDetail == null || abcreAndMpa != null) {
            if (abcreAndMpa != null) {
                mpa = abcreAndMpa.getMPa();
            }
            if (mpaDetail == null && abcreAndMpa == null) {
                LOGGER.warn("\t\tMPA not found for bill id " + billingHead.getId());
            }
            billingDetailLines.add(addLineSequence(billingHead,
                    null,
                    mpa,
                    1,
                    "MPA",
                    false));
        }
        if (abcreDetail == null || abcreAndMpa != null) {
            if (abcreAndMpa != null) {
                abcre = abcreAndMpa.getCreditValueSum();
            }
            if (abcreDetail == null && abcreAndMpa == null) {
                LOGGER.warn("\t\tABCRE not found for bill id " + billingHead.getId());
            }
            billingDetailLines.add(addLineSequence(billingHead,
                    null,
                    abcre,
                    2,
                    "ABCRE",
                    false));
        }
        Double absav = abcre - mbillValue;
        billingDetailLines.add(addLineSequence(billingHead,
                null,
                absav,
                3,
                "ABSAV",
                false));
        /////////////////////

        // Line sequence one
        billingDetailLines.add(addLineSequence(billingHead,
                null, // ?
                ebcreValue,
                4,
                "EBCRE",
                false));
        // Line sequence two
        billingDetailLines.add(addLineSequence(billingHead,
                null,
                mbillValue,
                5,
                "MBILL",
                true));
        // Line sequence two
        Double emsav = ebcreValue - mbillValue;
        billingDetailLines.add(addLineSequence(billingHead,
                null,
                emsav,
                6,
                "EMSAV", // estimated monthly savings
                false));
        billingDetailLines.add(addLineSequence(billingHead,
                "SRTE",
                SRTE,
                7,
                "SRTE", // estimated monthly savings
                false));
        billingDetailService.saveAll(billingDetailLines);
        LOGGER.info("\t\tLine sequences added to billing detail records");

        List<RuleExecutionLog> executionLogs = new ArrayList<>();
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "KWDC", KWDC, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "YLD", YLD, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "DEP", DEP, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "DEPM", DEPM, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "OPYR", (double) OPYR, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "SRTE", SRTE, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "DSCM", DSCM, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "MPA", mpa, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "ABCRE", abcre, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "ABSAV", absav, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "EBCRE", ebcreValue, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "MBILL", mbillValue, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "EMSAV", emsav, subscriptionMatrixRef));
        ruleExecutionLogService.save(executionLogs);
        LOGGER.info("\t\tCalculations completed and saved to execution logs");

        // set amount
        billingHead.setAmount(billingDetailService.findByBillingHeadId(billingHead.getId()).stream()
                .filter(billingDetail -> billingDetail.getAddToBillAmount())
                .mapToDouble(billingDetail -> billingDetail.getValue())
                .sum());
        // set status to GENERATED
        billingHead.setBillStatus(EBillStatus.CALCULATED.toString());
        billingHeadService.addOrUpdateBillingHead(billingHead);
        LOGGER.info("\t\tAggregated bill amount added to billing head");
        LOGGER.info("\tInitiatorCSGF bill generation completed");
    }

    @Override
    public void calculate(BillingHead billingHead, Subscription subscription, Long ruleHeadId,
                          Map<String, Object> valuesHashMap, Long jobId) {
        String error;
        if (!(billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.RECALCULATING.getStatus()))) {
            return;
        }

        LOGGER.info("\tInitiated CSGF calculations");
        Long billId = billingHead.getId();
        // For RuleExecutionLog:
        // KWDC YLD DEPM OPYR SRTE DSCM
        // Bill head: Discounted Bill Credit Rate
        // Saved in monthly billing heading
        // Estimated Monthly Bill Credit
        // KWDC*YLD*DEPM^(OPYR-1)*SRTE (divice by twelve), if DEPM is not defined then use 1-DEP
        // Last Actual Bill Credit Received (from excel)
        // Savings

        String subscriptionId = billingHead.getCustProdId();
        String subscriptionMatrixRef = subscriptionId + subscription.getVariantGroup().getId(); //Mongo Change
        RuleHead ruleHead = ruleHeadRepository.findById(ruleHeadId).get();
        Double KWDC = getDouble(valuesHashMap.get("KWDC"));
        Double YLD = getDouble(valuesHashMap.get("YLD"));
        // valuesHashMap.get("DEPM"); // not found
        Double DEP = getDouble(valuesHashMap.get("DEP"));
        Double DEPM = 1 - DEP;
        Integer OPYR = getInteger(valuesHashMap.get("OPYR"));
        Double SRTE = (Double) valuesHashMap.get("SRTE");
        Double DSCM = getDouble(valuesHashMap.get("DSCM"));

        Double ebcreValue = (KWDC * YLD * Math.pow(DEPM, (OPYR - 1)) * (SRTE)) / 12;
        Double mbillValue = (KWDC * YLD * Math.pow(DEPM, (OPYR - 1)) * (SRTE * DSCM)) / 12;

        ////////////// Update
        String convertedMonth = rateFunctions.parseMonthYearFormat(billingHead.getBillingMonthYear());
        List<BillingDetail> billingDetailLines = new ArrayList<>();
        BillingCreditResult abcreAndMpa = billingCreditsRepository.getABCREAndMPA((String) valuesHashMap.get("PN"),
                (String) valuesHashMap.get("SCSGN"), convertedMonth);
        if (abcreAndMpa == null) {
            abcreAndMpa = billingCreditsRepository.getABCREAndMPA(billingHead.getSubscriptionId(), convertedMonth);
        }
        BillingDetail mpaDetail = billingDetailService.findByBillingHeadAndBillingCode(billingHead, "MPA");
        BillingDetail abcreDetail = billingDetailService.findByBillingHeadAndBillingCode(billingHead, "ABCRE");
        Double mpa = mpaDetail != null ? mpaDetail.getValue() : 0d;
        Double abcre = abcreDetail != null ? abcreDetail.getValue() : 0d;
        LOGGER.info("\t\tAdding MPA and actual bill credit for bill id " + billingHead.getId());
        if (mpaDetail == null || abcreAndMpa != null) {
            if (abcreAndMpa != null) {
                mpa = abcreAndMpa.getMPa();
            }
            if (mpaDetail == null && abcreAndMpa == null) {
                LOGGER.warn("\t\tMPA not found for bill id " + billingHead.getId());
                error = "MPA not found for bill";
                calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            }
            billingDetailLines.add(addLineSequence(billingHead,
                    null,
                    mpa,
                    1,
                    "MPA",
                    false));
        }
        if (abcreDetail == null || abcreAndMpa != null) {
            if (abcreAndMpa != null) {
                abcre = abcreAndMpa.getCreditValueSum();
            }
            if (abcreDetail == null && abcreAndMpa == null) {
                LOGGER.warn("\t\tABCRE not found for bill id " + billingHead.getId());
                error = "Actual bill credit and MPA not found for bill";
                calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            }
            billingDetailLines.add(addLineSequence(billingHead,
                    null,
                    abcre,
                    2,
                    "ABCRE",
                    false));
        }
        Double absav = abcre - mbillValue;
        billingDetailLines.add(addLineSequence(billingHead,
                null,
                absav,
                3,
                "ABSAV",
                false));
        /////////////////////

        // Line sequence one
        billingDetailLines.add(addLineSequence(billingHead,
                null, // ?
                ebcreValue,
                4,
                "EBCRE",
                false));
        // Line sequence two
        billingDetailLines.add(addLineSequence(billingHead,
                null,
                mbillValue,
                5,
                "MBILL",
                true));
        // Line sequence two
        Double emsav = ebcreValue - mbillValue;
        billingDetailLines.add(addLineSequence(billingHead,
                null,
                emsav,
                6,
                "EMSAV", // estimated monthly savings
                false));
        billingDetailLines.add(addLineSequence(billingHead,
                "SRTE",
                SRTE,
                7,
                "SRTE", // estimated monthly savings
                false));
        billingDetailService.saveAll(billingDetailLines);
        LOGGER.info("\t\tLine sequences added to billing detail records");

        List<RuleExecutionLog> executionLogs = new ArrayList<>();
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "KWDC", KWDC, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "YLD", YLD, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "DEP", DEP, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "DEPM", DEPM, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "OPYR", (double) OPYR, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "SRTE", SRTE, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "DSCM", DSCM, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "MPA", mpa, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "ABCRE", abcre, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "ABSAV", absav, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "EBCRE", ebcreValue, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "MBILL", mbillValue, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "EMSAV", emsav, subscriptionMatrixRef));
        ruleExecutionLogService.save(executionLogs);
        LOGGER.info("\t\tCalculations completed and saved to execution logs");

        // set amount
        billingHead.setAmount(billingDetailService.findByBillingHeadId(billingHead.getId()).stream()
                .filter(billingDetail -> billingDetail.getAddToBillAmount())
                .mapToDouble(billingDetail -> billingDetail.getValue())
                .sum());
        // set status to GENERATED
        billingHead.setBillStatus(EBillStatus.CALCULATED.toString());
        billingHeadService.addOrUpdateBillingHead(billingHead);
        //Updating Status Of Calculation Detail Here
        calculationTrackerService.updateBillingLogCalculation(billingHead.getId(), billingHead.getBillStatus());
        LOGGER.info("\t\tAggregated bill amount added to billing head");
        LOGGER.info("\tInitiatorCSGF bill generation completed");
    }

    @Override
    public void calculate(BillingHead billingHead, CustomerSubscription subscription, List<TransStageTemp> transStageTempList, Long ruleHeadId, Map<String, Object> valuesHashMap, Long jobId) {
        String error;
        if (!(billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.RECALCULATING.getStatus()))) {
            return;
        }
        LOGGER.info("\tInitiated CSGF calculations");
        Long billId = billingHead.getId();
        // For RuleExecutionLog:
        // KWDC YLD DEPM OPYR SRTE DSCM
        // Bill head: Discounted Bill Credit Rate
        // Saved in monthly billing heading
        // Estimated Monthly Bill Credit
        // KWDC*YLD*DEPM^(OPYR-1)*SRTE (divice by twelve), if DEPM is not defined then use 1-DEP
        // Last Actual Bill Credit Received (from excel)
        // Savings

        String subscriptionMatrixRef = subscription.getExtSubsId() + subscription.getSubscriptionTemplate(); //Mongo Change
        //TODO: Update RuleHead
        Double KWDC = getDouble(valuesHashMap.get("S_KWDC"));
        Double YLD = getDouble(valuesHashMap.get("S_YLD"));
        // valuesHashMap.get("DEPM"); // not found
        Double DEP = getDouble(valuesHashMap.get("S_DEP"));
        Double DEPM = 1 - DEP;
        Integer OPYR = getInteger(valuesHashMap.get("S_OPYR"));
        Double SRTE = (Double) valuesHashMap.get("S_SRTE");
        Double DSCM = getDouble(valuesHashMap.get("S_DSCM"));

        if (SRTE == null) {
            error = "SRTE not found for bill";
            calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
        }
        Double ebcreValue = (KWDC * YLD * Math.pow(DEPM, (OPYR - 1)) * (SRTE)) / 12;
        Double mbillValue = (KWDC * YLD * Math.pow(DEPM, (OPYR - 1)) * (SRTE * DSCM)) / 12;

        ////////////// Update
        String convertedMonth = rateFunctions.parseMonthYearFormat(billingHead.getBillingMonthYear());
        List<BillingDetail> billingDetailLines = new ArrayList<>();
        BillingCreditResult abcreAndMpa = billingCreditsRepository.getABCREAndMPA((String) valuesHashMap.get("S_PN"),
                (String) valuesHashMap.get("SCSGN"), convertedMonth);
        if (abcreAndMpa == null) {
            abcreAndMpa = billingCreditsRepository.getABCREAndMPA(billingHead.getSubscriptionId(), convertedMonth);
        }
        BillingDetail mpaDetail = billingDetailService.findByBillingHeadAndBillingCode(billingHead, "MPA");
        BillingDetail abcreDetail = billingDetailService.findByBillingHeadAndBillingCode(billingHead, "ABCRE");
        Double mpa = mpaDetail != null ? mpaDetail.getValue() : 0d;
        Double abcre = abcreDetail != null ? abcreDetail.getValue() : 0d;
        LOGGER.info("\t\tAdding MPA and actual bill credit for bill id " + billingHead.getId());
        if (mpaDetail == null || abcreAndMpa != null) {
            if (abcreAndMpa != null) {
                mpa = abcreAndMpa.getMPa();
            }
            if (mpaDetail == null && abcreAndMpa == null) {
                LOGGER.warn("\t\tMPA not found for bill id " + billingHead.getId());
                error = "MPA not found for bill";
                calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            }
            billingDetailLines.add(addLineSequence(billingHead,
                    null,
                    mpa,
                    1,
                    "MPA",
                    false));
        }
        if (abcreDetail == null || abcreAndMpa != null) {
            if (abcreAndMpa != null) {
                abcre = abcreAndMpa.getCreditValueSum();
            }
            if (abcreDetail == null && abcreAndMpa == null) {
                LOGGER.warn("\t\tABCRE not found for bill id " + billingHead.getId());
                error = "Actual bill credit and MPA not found for bill";
                calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            }
            billingDetailLines.add(addLineSequence(billingHead,
                    null,
                    abcre,
                    2,
                    "ABCRE",
                    false));
        }
        Double absav = abcre - mbillValue;
        billingDetailLines.add(addLineSequence(billingHead,
                null,
                absav,
                3,
                "ABSAV",
                false));
        /////////////////////

        // Line sequence one
        billingDetailLines.add(addLineSequence(billingHead,
                null, // ?
                ebcreValue,
                4,
                "EBCRE",
                false));
        // Line sequence two
        billingDetailLines.add(addLineSequence(billingHead,
                null,
                mbillValue,
                5,
                "MBILL",
                true));
        // Line sequence two
        Double emsav = ebcreValue - mbillValue;
        billingDetailLines.add(addLineSequence(billingHead,
                null,
                emsav,
                6,
                "EMSAV", // estimated monthly savings
                false));
        billingDetailLines.add(addLineSequence(billingHead,
                "SRTE",
                SRTE,
                7,
                "SRTE", // estimated monthly savings
                false));
        billingDetailService.saveAll(billingDetailLines);
        LOGGER.info("\t\tLine sequences added to billing detail records");

        List<RuleExecutionLog> executionLogs = new ArrayList<>();
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "KWDC", KWDC, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "YLD", YLD, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "DEP", DEP, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "DEPM", DEPM, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "OPYR", (double) OPYR, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "SRTE", SRTE, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "DSCM", DSCM, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "MPA", mpa, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "ABCRE", abcre, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "ABSAV", absav, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "EBCRE", ebcreValue, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "MBILL", mbillValue, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "EMSAV", emsav, subscriptionMatrixRef));
        ruleExecutionLogService.save(executionLogs);
        LOGGER.info("\t\tCalculations completed and saved to execution logs");

        // set amount
        billingHead.setAmount(billingDetailService.findByBillingHeadId(billingHead.getId()).stream()
                .filter(billingDetail -> billingDetail.getAddToBillAmount())
                .mapToDouble(billingDetail -> billingDetail.getValue())
                .sum());
        // set status to GENERATED
        billingHead.setBillStatus(EBillStatus.CALCULATED.toString());
        billingHeadService.addOrUpdateBillingHead(billingHead);
        //Updating Status Of Calculation Detail Here
        calculationTrackerService.updateBillingLogCalculation(billingHead.getId(), billingHead.getBillStatus());
        LOGGER.info("\t\tAggregated bill amount added to billing head");
        LOGGER.info("\tInitiatorCSGF bill generation completed");
    }

    private BillingDetail addLineSequence(BillingHead billingHead, String rateCode, double value, int lineSequence,
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
            return billingDetail;
        } else {
            return billingDetailLine;
        }
    }

    @Override
    public void execute(ExecuteParams params, CustomerSubscription subscription, BillingHead billingHead, Long jobId) {
        Map<String, Object> valuesHashMap = generateHashMap(subscription, params.getRateMatrixHeadId(), billingHead,
                jobId);
        calculate(billingHead, params.getRateMatrixHeadId(), params.getRuleHeadId(), valuesHashMap, jobId);
    }

    @Override
    public void execute(ExecuteParams params, SubscriptionMapping subscriptionMapping, BillingHead billingHead, Long jobId) {
        Map<String, Object> valuesHashMap = generateHashMap(subscriptionMapping, billingHead, jobId); // generate only static, dynamic in loop with billing head
        calculate(billingHead, subscriptionMapping.getSubscription(), params.getRuleHeadId(), valuesHashMap, jobId);
    }

}
