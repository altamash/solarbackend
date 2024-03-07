package com.solar.api.saas.service.process.parser.product;

import com.solar.api.saas.model.process.rule.RuleHead;
import com.solar.api.saas.repository.RuleHeadRepository;
import com.solar.api.saas.service.integration.mongo.response.subscription.Subscription;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.process.calculation.RateFunctions;
import com.solar.api.saas.service.process.rule.RulesInitiator;
import com.solar.api.tenant.mapper.billingCredits.BillingCreditResult;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.billing.tansStage.TransStageHead;
import com.solar.api.tenant.model.billing.tansStage.TransStageTemp;
import com.solar.api.tenant.model.process.rule.RuleExecutionLog;
import com.solar.api.tenant.model.stage.billing.ExtDataStageDefinitionBilling;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.repository.BillingCreditsRepository;
import com.solar.api.tenant.service.BillingDetailService;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.CalculationTrackerService;
import com.solar.api.tenant.service.process.rule.RuleExecutionLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class BillCreditImportVOS extends RulesInitiator {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private BillingDetailService billingDetailService;
    @Autowired
    private RuleExecutionLogService ruleExecutionLogService;
    @Autowired
    private RuleHeadRepository ruleHeadRepository;
    @Autowired
    private BillingCreditsRepository billingCreditsRepository;
    @Autowired
    private RateFunctions rateFunctions;
    @Autowired
    private CalculationTrackerService calculationTrackerService;

    @Override
    public Map<String, Object> generateHashMap(CustomerSubscription subscription, Long subscriptionMatrixHeadId,
                                               BillingHead billingHead, Long jobId) {
        return null;
    }

    @Override
    public Map<String, Object> generateHashMap(SubscriptionMapping subscriptionMapping, BillingHead billingHead, Long jobId) {
        return null;
    }

    @Override
    public Map<String, Object> generateHashMap(List<TransStageTemp> transStageTempList, BillingHead billingHead, String subscriptionId, Long jobId) {
        return null;
    }

    @Override
    public void calculate(BillingHead billingHead, Long rateMatrixHeadId, Long ruleHeadId,
                          Map<String, Object> valuesHashMap, Long jobId) {

        if (!(billingHead.getBillStatus().equals(EBillStatus.SCHEDULED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.GENERATED.getStatus()))) {
            return;
        }
        RuleHead ruleHead = ruleHeadRepository.findById(ruleHeadId).get();
        if (billingDetailService.findByBillingHeadAndBillingCode(billingHead, ruleHead.getBillingCode()) != null) {
            return;
        }

        // month, garden, premise
        String convertedMonth = rateFunctions.parseMonthYearFormat(billingHead.getBillingMonthYear());
        BillingCreditResult abcreAndMpa = billingCreditsRepository.getABCREAndMPA((String) valuesHashMap.get("PN"),
                (String) valuesHashMap.get("SCSGN"), convertedMonth);
        if (abcreAndMpa == null) {
            abcreAndMpa = billingCreditsRepository.getABCREAndMPA(billingHead.getSubscriptionId(), convertedMonth);
        }
        if (abcreAndMpa == null) {
            LOGGER.info("Actual bill credit and MPA not found for bill id " + billingHead.getId() + " !!");
            return;
        }
        Double mpa = (Double) abcreAndMpa.getMPa();
        Double actualBillCredit = abcreAndMpa.getCreditValueSum();
        if (actualBillCredit == null) {
            LOGGER.info("Actual bill credit not found for bill id " + billingHead.getId() + " !!");
            return;
        }
        if (mpa == null) {
            LOGGER.info("MPA not found for bill id " + billingHead.getId() + " !!");
            return;
        }

        Double DSC = getDouble(valuesHashMap.get("DSC"));
        Double KWDC = getDouble(valuesHashMap.get("KWDC"));
        Integer YLD = getInteger(valuesHashMap.get("YLD"));
        Double DEP = getDouble(valuesHashMap.get("DEP"));
        Integer OPYR = getInteger(valuesHashMap.get("OPYR"));

        double monthlyBill = mpa * ((Double) valuesHashMap.get("SRTE") - DSC);
        double actualSavings = mpa * DSC;
        double projectedSavings = KWDC * Math.pow(YLD, DEP) * OPYR;

        int lineSequenceCount = billingDetailService.findByBillingHeadId(billingHead.getId()).size();

        // TODO: Save in bulk
        lineSequenceCount = addOrUpdateDetail(billingHead, "ABCRE", actualBillCredit, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "MPA", mpa, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "MBILL", monthlyBill, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "ABSAV", actualSavings, lineSequenceCount);
        addOrUpdateDetail(billingHead, "PSAV", projectedSavings, lineSequenceCount);
        billingDetailService.addOrUpdateBillingDetail(BillingDetail.builder()
                .billingCode("SRTE")
                .billingHead(billingHead)
                .value((Double) valuesHashMap.get("SRTE"))
                .lineSeqNo(++lineSequenceCount)
                .addToBillAmount(false)
                .date(new Date())
                .build());

        Long billId = billingHead.getId();
        Long subscriptionId = billingHead.getSubscriptionId();
        String subscriptionMatrixRef = subscriptionId + rateMatrixHeadId.toString();
        List<RuleExecutionLog> executionLogs = new ArrayList<>();
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "SRTE", (Double) valuesHashMap.get("SRTE"),
                subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "DSC",
                Double.parseDouble((String) valuesHashMap.get("DSC")), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "KWDC",
                Double.parseDouble((String) valuesHashMap.get("KWDC")), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "YLD",
                Double.parseDouble((String) valuesHashMap.get("YLD")), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "DEP",
                Double.parseDouble((String) valuesHashMap.get("DEP")), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "OPYR", (double) (Integer) valuesHashMap.get(
                "OPYR"), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "ABCRE", actualBillCredit,
                subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "MPA", mpa, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "MBILL", monthlyBill, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "ABSAV", actualSavings, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "PSAV", projectedSavings,
                subscriptionMatrixRef));
        ruleExecutionLogService.save(executionLogs);

        // set amount
        Double amount = billingDetailService.findByBillingHeadId(billingHead.getId()).stream()
                .filter(value -> value.getAddToBillAmount())
                .mapToDouble(detail -> detail.getValue())
                .sum();
        billingHead.setAmount(amount);
        // set status to GENERATED
        billingHead.setBillStatus(EBillStatus.CALCULATED.toString());
        billingHeadService.addOrUpdateBillingHead(billingHead);
        LOGGER.info("\t\tAggregated bill amount added to billing head");
    }

    @Override
    public void calculate(BillingHead billingHead, Subscription subscription, Long ruleHeadId,
                          Map<String, Object> valuesHashMap, Long jobId) {
        String error;
        if (!(billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.RECALCULATING.getStatus()))) {
            return;
        }
        RuleHead ruleHead = ruleHeadRepository.findById(ruleHeadId).get();
        if (billingDetailService.findByBillingHeadAndBillingCode(billingHead, ruleHead.getBillingCode()) != null) {
            return;
        }

        // month, garden, premise
        String convertedMonth = rateFunctions.parseMonthYearFormat(billingHead.getBillingMonthYear());
        BillingCreditResult abcreAndMpa = billingCreditsRepository.getABCREAndMPA((String) valuesHashMap.get("PN"),
                (String) valuesHashMap.get("SCSGN"), convertedMonth);
        if (abcreAndMpa == null) {
            abcreAndMpa = billingCreditsRepository.getABCREAndMPA(billingHead.getSubscriptionId(), convertedMonth);
        }
        if (abcreAndMpa == null) {
            LOGGER.info("Actual bill credit and MPA not found for bill id " + billingHead.getId() + " !!");
            error = "Actual bill credit and MPA not found for bill";
            calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            return;
        }
        Double mpa = (Double) abcreAndMpa.getMPa();
        Double actualBillCredit = abcreAndMpa.getCreditValueSum();
        if (actualBillCredit == null) {
            LOGGER.info("Actual bill credit not found for bill id " + billingHead.getId() + " !!");
            error = "Actual bill credit not found for bill";
            calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            return;
        }
        if (mpa == null) {
            LOGGER.info("MPA not found for bill id " + billingHead.getId() + " !!");
            error = "MPA not found for bill";
            calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            return;
        }

        Double DSC = getDouble(valuesHashMap.get("DSC"));
        Double KWDC = getDouble(valuesHashMap.get("KWDC"));
        Integer YLD = getInteger(valuesHashMap.get("YLD"));
        Double DEP = getDouble(valuesHashMap.get("DEP"));
        Integer OPYR = getInteger(valuesHashMap.get("OPYR"));

        double monthlyBill = mpa * ((Double) valuesHashMap.get("SRTE") - DSC);
        double actualSavings = mpa * DSC;
        double projectedSavings = KWDC * Math.pow(YLD, DEP) * OPYR;

        int lineSequenceCount = billingDetailService.findByBillingHeadId(billingHead.getId()).size();

        // TODO: Save in bulk
        lineSequenceCount = addOrUpdateDetail(billingHead, "ABCRE", actualBillCredit, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "MPA", mpa, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "MBILL", monthlyBill, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "ABSAV", actualSavings, lineSequenceCount);
        addOrUpdateDetail(billingHead, "PSAV", projectedSavings, lineSequenceCount);
        billingDetailService.addOrUpdateBillingDetail(BillingDetail.builder()
                .billingCode("SRTE")
                .billingHead(billingHead)
                .value((Double) valuesHashMap.get("SRTE"))
                .lineSeqNo(++lineSequenceCount)
                .addToBillAmount(false)
                .date(new Date())
                .build());

        Long billId = billingHead.getId();
        String subscriptionId = billingHead.getCustProdId();
        String subscriptionMatrixRef = subscriptionId + subscription.getVariantGroup().getId();
        List<RuleExecutionLog> executionLogs = new ArrayList<>();
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "SRTE", (Double) valuesHashMap.get("SRTE"),
                subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "DSC",
                Double.parseDouble((String) valuesHashMap.get("DSC")), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "KWDC",
                Double.parseDouble((String) valuesHashMap.get("KWDC")), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "YLD",
                Double.parseDouble((String) valuesHashMap.get("YLD")), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "DEP",
                Double.parseDouble((String) valuesHashMap.get("DEP")), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "OPYR", (double) (Integer) valuesHashMap.get(
                "OPYR"), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "ABCRE", actualBillCredit,
                subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "MPA", mpa, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "MBILL", monthlyBill, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "ABSAV", actualSavings, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "PSAV", projectedSavings,
                subscriptionMatrixRef));
        ruleExecutionLogService.save(executionLogs);

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

    @Override
    public void calculate(BillingHead billingHead, CustomerSubscription subscription, List<TransStageTemp> transStageTempList, Long ruleHeadId, Map<String, Object> valuesHashMap, Long jobId) {
        String error;
        if (!(billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.RECALCULATING.getStatus()))) {
            return;
        }
        //TODO: Update billing code fetch process

        // month, garden, premise
        String convertedMonth = rateFunctions.parseMonthYearFormat(billingHead.getBillingMonthYear());
        BillingCreditResult abcreAndMpa = billingCreditsRepository.getABCREAndMPA((String) valuesHashMap.get("S_PN"),
                (String) valuesHashMap.get("SCSGN"), convertedMonth);
        if (abcreAndMpa == null) {
            abcreAndMpa = billingCreditsRepository.getABCREAndMPA(billingHead.getSubscriptionId(), convertedMonth);
        }
        if (abcreAndMpa == null) {
            LOGGER.info("Actual bill credit and MPA not found for bill id " + billingHead.getId() + " !!");
            error = "Actual bill credit and MPA not found for bill";
            calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            return;
        }
        Double mpa = (Double) abcreAndMpa.getMPa();
        Double actualBillCredit = abcreAndMpa.getCreditValueSum();
        if (actualBillCredit == null) {
            LOGGER.info("Actual bill credit not found for bill id " + billingHead.getId() + " !!");
            error = "Actual bill credit not found for bill";
            calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            return;
        }
        if (mpa == null) {
            LOGGER.info("MPA not found for bill id " + billingHead.getId() + " !!");
            error = "MPA not found for bill";
            calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            return;
        }

        Double DSC = getDouble(valuesHashMap.get("S_DSC"));
        Double KWDC = getDouble(valuesHashMap.get("S_KWDC"));
        Integer YLD = getInteger(valuesHashMap.get("S_YLD"));
        Double DEP = getDouble(valuesHashMap.get("S_DEP"));
        Integer OPYR = getInteger(valuesHashMap.get("S_OPYR"));

        double monthlyBill = mpa * ((Double) valuesHashMap.get("S_SRTE") - DSC);
        double actualSavings = mpa * DSC;
        double projectedSavings = KWDC * Math.pow(YLD, DEP) * OPYR;

        int lineSequenceCount = billingDetailService.findByBillingHeadId(billingHead.getId()).size();

        // TODO: Save in bulk
        lineSequenceCount = addOrUpdateDetail(billingHead, "ABCRE", actualBillCredit, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "MPA", mpa, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "MBILL", monthlyBill, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "ABSAV", actualSavings, lineSequenceCount);
        addOrUpdateDetail(billingHead, "PSAV", projectedSavings, lineSequenceCount);
        billingDetailService.addOrUpdateBillingDetail(BillingDetail.builder()
                .billingCode("SRTE")
                .billingHead(billingHead)
                .value((Double) valuesHashMap.get("S_SRTE"))
                .lineSeqNo(++lineSequenceCount)
                .addToBillAmount(false)
                .date(new Date())
                .build());

        Long billId = billingHead.getId();
        String subscriptionMatrixRef = subscription.getExtSubsId() + subscription.getSubscriptionTemplate();
        List<RuleExecutionLog> executionLogs = new ArrayList<>();
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "SRTE", (Double) valuesHashMap.get("S_SRTE"),
                subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "DSC",
                Double.parseDouble((String) valuesHashMap.get("S_DSC")), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "KWDC",
                Double.parseDouble((String) valuesHashMap.get("S_KWDC")), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "YLD",
                Double.parseDouble((String) valuesHashMap.get("S_YLD")), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "DEP",
                Double.parseDouble((String) valuesHashMap.get("S_DEP")), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "OPYR", (double) (Integer) valuesHashMap.get(
                "S_OPYR"), subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "ABCRE", actualBillCredit,
                subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "MPA", mpa, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "MBILL", monthlyBill, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "ABSAV", actualSavings, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "PSAV", projectedSavings,
                subscriptionMatrixRef));
        ruleExecutionLogService.save(executionLogs);

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

    private int addOrUpdateDetail(BillingHead billingHead, String billingCode, Double value, int lineSequenceCount) {
        BillingDetail detail = billingDetailService.findByBillingHeadAndBillingCode(billingHead, billingCode);
        if (detail == null) {
            detail = BillingDetail.builder()
                    .billingCode(billingCode)
                    .billingHead(billingHead)
                    .value(value)
                    .lineSeqNo(++lineSequenceCount)
                    .addToBillAmount("MBILL".equals(billingCode))
                    .date(new Date())
                    .build();
        } else {
            detail.setValue(value);
        }
        billingDetailService.addOrUpdateBillingDetail(detail);
        return lineSequenceCount;
    }

}
