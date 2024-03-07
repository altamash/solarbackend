package com.solar.api.saas.service.process.parser.product.pwgenbill;

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
public class ProductionImport extends RulesInitiator {

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
            LOGGER.info("Power generation record not found for bill id " + billingHead.getId() + " !!");
            return;
        }
        Double mpa = abcreAndMpa.getMPa();
//        Double actualBillCredit = abcreAndMpa.getCreditValueSum();
        /*if (actualBillCredit == null) {
            LOGGER.info("Power generation record not found for bill id " + billingHead.getId() + " !!");
            return;
        }*/
        if (mpa == null) {
            LOGGER.info("MPA not found for bill id " + billingHead.getId() + " !!");
            return;
        }

        double monthlyBill =
                mpa * ((Double) valuesHashMap.get("SRTE"));
//                - Double.parseDouble((String) valuesHashMap.get("DSC")));
//        double actualSavings = mpa * Double.parseDouble((String) valuesHashMap.get("DSC"));

//        double projectedSavings = Double.parseDouble((String) valuesHashMap.get("KWDC")) *
//                Math.pow(Integer.parseInt((String) valuesHashMap.get("YLD")),
//                        Double.parseDouble((String) valuesHashMap.get("DEP"))) *
//                (Integer) valuesHashMap.get("OPYR");

        int lineSequenceCount = billingDetailService.findByBillingHeadId(billingHead.getId()).size();

//        lineSequenceCount = addOrUpdateDetail(billingHead, "ABCRE", actualBillCredit, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "MPA", mpa, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "MBILL", monthlyBill, lineSequenceCount);
//        lineSequenceCount = addOrUpdateDetail(billingHead, "ABSAV", actualSavings, lineSequenceCount);
//        addOrUpdateDetail(billingHead, "PSAV", projectedSavings, lineSequenceCount);
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
//        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "ABCRE", actualBillCredit,
//                subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "MPA", mpa, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "MBILL", monthlyBill, subscriptionMatrixRef));
//        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "ABSAV", actualSavings, subscriptionMatrixRef));
//        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "PSAV", projectedSavings,
//                subscriptionMatrixRef));
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
            LOGGER.info("Power generation record not found for bill id " + billingHead.getId() + " !!");
            error = "Power generation record not found for bill";
            calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            return;
        }
        Double mpa = abcreAndMpa.getMPa();
//        Double actualBillCredit = abcreAndMpa.getCreditValueSum();
        /*if (actualBillCredit == null) {
            LOGGER.info("Power generation record not found for bill id " + billingHead.getId() + " !!");
            return;
        }*/
        if (mpa == null) {
            LOGGER.info("MPA not found for bill id " + billingHead.getId() + " !!");
            error = "MPA not found for bill";
            calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            return;
        }

        double monthlyBill =
                mpa * ((Double) valuesHashMap.get("SRTE"));
//                - Double.parseDouble((String) valuesHashMap.get("DSC")));
//        double actualSavings = mpa * Double.parseDouble((String) valuesHashMap.get("DSC"));

//        double projectedSavings = Double.parseDouble((String) valuesHashMap.get("KWDC")) *
//                Math.pow(Integer.parseInt((String) valuesHashMap.get("YLD")),
//                        Double.parseDouble((String) valuesHashMap.get("DEP"))) *
//                (Integer) valuesHashMap.get("OPYR");

        int lineSequenceCount = billingDetailService.findByBillingHeadId(billingHead.getId()).size();

//        lineSequenceCount = addOrUpdateDetail(billingHead, "ABCRE", actualBillCredit, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "MPA", mpa, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "MBILL", monthlyBill, lineSequenceCount);
//        lineSequenceCount = addOrUpdateDetail(billingHead, "ABSAV", actualSavings, lineSequenceCount);
//        addOrUpdateDetail(billingHead, "PSAV", projectedSavings, lineSequenceCount);
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
        String subscriptionMatrixRef = String.valueOf(subscriptionId); //Mongo Subscription Changes
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
//        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "ABCRE", actualBillCredit,
//                subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "MPA", mpa, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "MBILL", monthlyBill, subscriptionMatrixRef));
//        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "ABSAV", actualSavings, subscriptionMatrixRef));
//        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "PSAV", projectedSavings,
//                subscriptionMatrixRef));
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

        // month, garden, premise
        String convertedMonth = rateFunctions.parseMonthYearFormat(billingHead.getBillingMonthYear());
        BillingCreditResult abcreAndMpa = billingCreditsRepository.getABCREAndMPA((String) valuesHashMap.get("S_PN"),
                (String) valuesHashMap.get("SCSGN"), convertedMonth);
        if (abcreAndMpa == null) {
            abcreAndMpa = billingCreditsRepository.getABCREAndMPA(billingHead.getSubscriptionId(), convertedMonth);
        }
        if (abcreAndMpa == null) {
            LOGGER.info("Power generation record not found for bill id " + billingHead.getId() + " !!");
            error = "Power generation record not found for bill";
            calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            return;
        }
        Double mpa = abcreAndMpa.getMPa();
        if (mpa == null) {
            LOGGER.info("MPA not found for bill id " + billingHead.getId() + " !!");
            error = "MPA not found for bill";
            calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            return;
        }
        Double SRTE = (Double) valuesHashMap.get("S_SRTE");
        if (SRTE == null) {
            error = "SRTE not found for bill";
            calculationTrackerService.updateBillingLogError(billingHead.getId(), error);
            return;
        }
        double monthlyBill =
                mpa * (SRTE);


        int lineSequenceCount = billingDetailService.findByBillingHeadId(billingHead.getId()).size();
        if ( valuesHashMap.get("S_VADCOM") != null && !valuesHashMap.get("S_VADCOM").toString().trim().isEmpty()) {
            monthlyBill = monthlyBill * (Double) valuesHashMap.get("S_VADCOM");
            }
//        lineSequenceCount = addOrUpdateDetail(billingHead, "ABCRE", actualBillCredit, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "MPA", mpa, lineSequenceCount);
        lineSequenceCount = addOrUpdateDetail(billingHead, "MBILL", monthlyBill, lineSequenceCount);
        if (valuesHashMap.get("S_TAXP") != null && !valuesHashMap.get("S_TAXP").toString().trim().isEmpty()) {
            Double taxAmount = (Double.parseDouble((String) valuesHashMap.get("S_TAXP")) / 100) * monthlyBill;
            lineSequenceCount = addOrUpdateDetail(billingHead, "TAX", taxAmount, lineSequenceCount);
        }
//        lineSequenceCount = addOrUpdateDetail(billingHead, "ABSAV", actualSavings, lineSequenceCount);
//        addOrUpdateDetail(billingHead, "PSAV", projectedSavings, lineSequenceCount);
        billingDetailService.addOrUpdateBillingDetail(BillingDetail.builder()
                .billingCode("SRTE")
                .billingHead(billingHead)
                .value((Double) valuesHashMap.get("S_SRTE"))
                .lineSeqNo(++lineSequenceCount)
                .addToBillAmount(false)
                .date(new Date())
                .build());

        Long billId = billingHead.getId();
        Long subscriptionId = billingHead.getSubscriptionId();
        String subscriptionMatrixRef = String.valueOf(subscriptionId); //Mongo Subscription Changes
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
//        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "ABCRE", actualBillCredit,
//                subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "MPA", mpa, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, null, "MBILL", monthlyBill, subscriptionMatrixRef));
//        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "ABSAV", actualSavings, subscriptionMatrixRef));
//        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "PSAV", projectedSavings,
//                subscriptionMatrixRef));
        ruleExecutionLogService.save(executionLogs);

        // set amount
        Double amount = billingDetailService.findByBillingHeadId(billingHead.getId()).stream()
                .filter(value -> value.getAddToBillAmount())
                .mapToDouble(detail -> detail.getValue())
                .sum();
        billingHead.setAmount(amount);
        // set status to CALCULATED
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
                    .addToBillAmount("MBILL".equals(billingCode) || "TAX".equals(billingCode))
                    .date(new Date())
                    .build();
        } else {
            detail.setValue(value);
        }
        billingDetailService.addOrUpdateBillingDetail(detail);
        return lineSequenceCount;
    }

}
