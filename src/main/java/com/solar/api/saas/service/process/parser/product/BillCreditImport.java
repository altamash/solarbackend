package com.solar.api.saas.service.process.parser.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.saas.model.process.rule.RuleHead;
import com.solar.api.saas.repository.RuleHeadRepository;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.Subscription;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.process.rule.RulesInitiator;
import com.solar.api.tenant.model.billing.ImportFileMap;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.billing.tansStage.TransStageHead;
import com.solar.api.tenant.model.billing.tansStage.TransStageTemp;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.repository.BillCreditRepository;
import com.solar.api.tenant.repository.CustomerSubscriptionMappingRepository;
import com.solar.api.tenant.repository.ImportFileMapRepository;
import com.solar.api.tenant.service.BillingDetailService;
import com.solar.api.tenant.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BillCreditImport extends RulesInitiator {

/*
SELECT * FROM solar_db.billingcredits;

SELECT * FROM heroku_33f31a6361c6fcf.billing_credits;

update billing_credits set import_type_ID=1, credit_code_type='PN' WHERE credit_id <100000;

SELECT * FROM heroku_33f31a6361c6fcf.billing_credits;

delete from billing_credits where credit_id<100000;

-- Step 1
select ccm.customer_subscription_id, bc.calendar_month from billing_credits bc, customer_subscription_mapping ccm
	where bc.credit_code_type=ccm.rate_code and bc.credit_code_val=ccm.value and bc.credit_code_type='PN';
-- bill head achieved from above conditions
-- Step 2
select sum(credit_value), calendar_month from billing_credits where credit_code_val='302149247' and
garden_id='SRC038294' group by credit_code_val, garden_id, calendar_month;

SELECT * FROM heroku_33f31a6361c6fcf.billing_credits where credit_code_val='302149247';

update billing_credits set credit_code_type='PN', import_type_id=1 where credit_id<100000;
*/

    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private BillingDetailService billingDetailService;
    @Autowired
    private ImportFileMapRepository mapRepository;
    @Autowired
    private CustomerSubscriptionMappingRepository mappingRepository;
    @Autowired
    private BillCreditRepository creditRepository;
    @Autowired
    private RuleHeadRepository ruleHeadRepository;

    @Autowired
    private DataExchange dataExchange;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

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

        ImportFileMap importFileMap = mapRepository.findByHeaderColumnName("premiseNumber");
        String premiseCode = importFileMap.getRateCode();
        importFileMap = mapRepository.findByHeaderColumnName("gardenID");
        String gardenIDCode = importFileMap.getRateCode();

        String billingMonthYear = billingHead.getBillingMonthYear();

        String[] monthYearParts = billingMonthYear.split("-");
        String bcYearMonth = monthYearParts[1] + "-" + monthYearParts[0];

        Long subscriptionId = billingHead.getSubscriptionId();
        CustomerSubscription subscription = subscriptionService.findCustomerSubscriptionById(subscriptionId);
        CustomerSubscriptionMapping subscriptionMappingPremise =
                mappingRepository.findByRateCodeAndSubscription(premiseCode, subscription);
        CustomerSubscriptionMapping subscriptionMappingGardenIDCode =
                mappingRepository.findByRateCodeAndSubscription(gardenIDCode, subscription);

        Double credits = creditRepository.getBillingCredits(subscriptionMappingPremise.getValue(),
                subscriptionMappingGardenIDCode.getValue(), bcYearMonth);

        // value == null, bill credit cannot be found for subscription id

        billingDetailService.addOrUpdateBillingDetail(BillingDetail.builder()
                .billingCode(ruleHead.getBillingCode())
                .billingHead(billingHead)
                .value(credits)
                .lineSeqNo(billingHead.getBillingDetails().size() + 1)
                .addToBillAmount(false)
                .date(new Date())
                .build());
    }

    @Override
    public void calculate(BillingHead billingHead, Subscription subscription, Long ruleHeadId,
                          Map<String, Object> valuesHashMap, Long jobId) {

        if (!(billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.RECALCULATING.getStatus()))) {
            return;
        }
        RuleHead ruleHead = ruleHeadRepository.findById(ruleHeadId).get();
        if (billingDetailService.findByBillingHeadAndBillingCode(billingHead, ruleHead.getBillingCode()) != null) {
            return;
        }
        Map<String, String> subsMeasures = new HashMap<>();
        String premise = "";
        ImportFileMap importFileMap = mapRepository.findByHeaderColumnName("premiseNumber");
        String premiseCode = importFileMap.getRateCode();
        importFileMap = mapRepository.findByHeaderColumnName("gardenID");
        String gardenIDCode = importFileMap.getRateCode();

        String billingMonthYear = billingHead.getBillingMonthYear();

        String[] monthYearParts = billingMonthYear.split("-");
        String bcYearMonth = monthYearParts[1] + "-" + monthYearParts[0];
// Make Changes for Subscription here
        String subscriptionId = billingHead.getCustProdId();
//        ExtDataStageDefinitionBilling extDataStageDefinitionBilling = extDataStageDefinitionBillingService.findBySubsId(subscriptionId);

        try {
//            subsMeasures = new ObjectMapper().readValue(extDataStageDefinitionBilling.getBillingJson(), Map.class);
            premise = subsMeasures.get("S_PN") != null
                    ? subsMeasures.get("S_PN").toString() : null;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

//        Double credits = creditRepository.getBillingCredits(premise,
//                extDataStageDefinitionBilling.getRefId(), bcYearMonth);

        // value == null, bill credit cannot be found for subscription id

        billingDetailService.addOrUpdateBillingDetail(BillingDetail.builder()
                .billingCode(ruleHead.getBillingCode())
                .billingHead(billingHead)
//                .value(credits)
                .lineSeqNo(billingHead.getBillingDetails().size() + 1)
                .addToBillAmount(false)
                .date(new Date())
                .build());

    }

    @Override
    public void calculate(BillingHead billingHead, CustomerSubscription subscription, List<TransStageTemp> transStageTempList, Long ruleHeadId, Map<String, Object> valuesHashMap, Long jobId) {
        if (!(billingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.RECALCULATING.getStatus()))) {
            return;
        }

        String premiseCodeValue = transStageTempList.stream().filter(m->m.getMeasCode().equalsIgnoreCase("S_PN")).map(TransStageTemp::getValue).findFirst().get();
        String gardenIDCodeValue = transStageTempList.stream().filter(m->m.getMeasCode().equalsIgnoreCase("SCSGN")).map(TransStageTemp::getValue).findFirst().get();

        String billingMonthYear = billingHead.getBillingMonthYear();

        String[] monthYearParts = billingMonthYear.split("-");
        String bcYearMonth = monthYearParts[1] + "-" + monthYearParts[0];

        Double credits = creditRepository.getBillingCredits(premiseCodeValue,
                gardenIDCodeValue, bcYearMonth);

        // value == null, bill credit cannot be found for subscription id

        billingDetailService.addOrUpdateBillingDetail(BillingDetail.builder()
//                .billingCode(ruleHead.getBillingCode())
                .billingHead(billingHead)
                .value(credits)
                .lineSeqNo(billingHead.getBillingDetails().size() + 1)
                .addToBillAmount(false)
                .date(new Date())
                .build());
    }

}
