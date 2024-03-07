package com.solar.api.saas.service.process.aspect.workflow;

import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.WorkflowHookMaster;
import com.solar.api.saas.repository.workflow.WorkflowHookMasterRepository;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.MeasureType;
import com.solar.api.saas.service.integration.mongo.response.subscription.Subscription;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.workflow.HookValidator;
import com.solar.api.tenant.mapper.billing.billingHead.BillingDetailDTO;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeTenantDTO;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.calculation.CalculationDetails;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.process.rule.RuleExecutionLog;
import com.solar.api.tenant.repository.BillingInvoiceRepository;
import com.solar.api.tenant.service.*;
import com.solar.api.tenant.service.extended.PhysicalLocationService;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import com.solar.api.tenant.service.process.rule.RuleExecutionLogService;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.*;


@Aspect
@Configuration
@Component
public class AfterAopAspectInvoice {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private HookValidator hookValidator;
    @Autowired
    private AddressService addressService;
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private RuleExecutionLogService ruleExecutionLogService;
    @Autowired
    private PortalAttributeOverrideService attributeOverrideService;
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    private PhysicalLocationService physicalLocationService;
    @Autowired
    private BillingInvoiceRepository billingInvoiceRepository;
    @Autowired
    private CalculationDetailsService calculationDetailsService;
    @Autowired
    private CompanyPreferenceService companyPreferenceService;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private PortalAttributeOverrideService portalAttributeOverrideService;
    @Autowired
    private WorkflowHookMasterRepository workFlowHookMasterRepository;
    /**
     * PUBLISHINVOICE
     *
     * @param joinPoint
     * @param result
     */
/*    @AfterReturning(value = "execution(* com.solar.api.tenant.service.process.billing.publish.PublishInfoServiceImpl.save(..))", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        if (result instanceof PublishInfo) {
            PublishInfo publishInfo = ((PublishInfo) result);
            BillingHead billingHead = billingHeadService.findById(publishInfo.getReferenceId());
            SubscriptionMapping subsAndVariant = dataExchange.getAllMergedMeasuresBySubscription(billingHead.getCustProdId(), DBContextHolder.getTenantName());
            Map<String, String> placeholderValues = new HashMap<>();
            String hookConstant = null;
            try {
                Optional<TenantConfig> tenantConfig = tenantConfigService.findByParameter("WorkFlow");
                if (tenantConfig.isPresent()) {
                    placeholderValues.put("wfId", tenantConfig.get().getText());
                    Optional<WorkflowHookMaster> workflowHookMaster = workFlowHookMasterRepository.findById(Long.valueOf(tenantConfig.get().getText()));
                    hookConstant = workflowHookMaster.isPresent() ? workflowHookMaster.get().getHookConstant() : null;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            *//*CustomerSubscriptionMapping customerSubscriptionMapping = subscriptionService.findCustomerSubscriptionMappingByRateCodeAndSubscription(
                    AppConstants.SYSTEM_INVOICE_TEMPLATE
                    , subscriptionService.findCustomerSubscriptionById(billingHead.getSubscriptionId()));*//*
            placeholderValues.put("month_year", String.valueOf(billingHead.getBillingMonthYear()));
            placeholderValues.put("invoice_number", billingHead.getInvoice().getId().toString());
            placeholderValues.put("invoice_value", billingHead.getAmount().toString());
            String[][] subjectParams = new String[][]{{"CSG ACH", billingHead.getBillingMonthYear()},
                    {"CSG ACH", billingHead.getBillingMonthYear()}};
            //hookValidator.hookFinder(Long.valueOf(customerSubscriptionMapping.getValue()), null, 1L, billingHead.getUserAccount().getAcctId(), placeholderValues, subjectParams);
            if (hookConstant != null)
            hookValidator.hookFinder(null, hookConstant, 1L, billingHead.getUserAccount().getAcctId(), placeholderValues, billingHead, subjectParams);
        }
        logger.info("{} returned with value {}", joinPoint, result);
    }*/

    /**
     * INVOICING
     *
     * @param joinPoint
     * @param result
     */
/*    @AfterReturning(value = "execution(* com.solar.api.tenant.service.process.billing.invoice.BillInvoiceServiceImpl.individualInvoiceV1(..))", returning = "result")
    public void after(JoinPoint joinPoint, Object result) {
        BillingHead billingHead = null;
        try {
            if (result instanceof Map) {
                Map billResult = (Map) result;
                billingHead = billingHeadService.findByInvoice((BillingInvoice) billResult.get("data"));
                String[][] subjectParams = new String[][]{{billingHead.getInvoice().getId().toString(), billingHead.getBillingMonthYear(), billingHead.getId().toString()}};
                Map<String, String> placeholderValues = getInvoicePlaceholders(billingHead);
                hookValidator.hookFinder(null, placeholderValues.get("hookConstant"), 1L, billingHead.getUserAccount().getAcctId(),
                        placeholderValues, billingHead, subjectParams);
            }
        } catch (Exception ex) {
            CalculationDetails calculationDetailsDB = calculationDetailsService.findBySourceId(billingHead.getId());
            calculationDetailsDB.setErrorInd("Y");
            calculationDetailsDB.setErrorMessage("SI500:" + "Invoice html not generated.");
            calculationDetailsService.addOrUpdate(calculationDetailsDB);
            LOGGER.error(ex.getMessage(), ex);
        }
        //logger.info("after execution of {}", joinPoint);
    }*/


    public void forDraft(BillingHead billingHead) {
        try {
            String[][] subjectParams = new String[][]{{null, billingHead.getBillingMonthYear(), billingHead.getId().toString()}};
            Map<String, String> placeholderValues = getInvoicePlaceholders(billingHead);
            hookValidator.hookFinder(null, placeholderValues.get("hookConstant"), 1L, billingHead.getUserAccount().getAcctId(),
                    placeholderValues, billingHead, subjectParams);

        } catch (Exception ex) {
            CalculationDetails calculationDetailsDB = calculationDetailsService.findBySourceId(billingHead.getId());
            calculationDetailsDB.setErrorInd("Y");
            calculationDetailsDB.setErrorMessage("SI500:" + "Invoice html not generated.");
            calculationDetailsService.addOrUpdate(calculationDetailsDB);
            LOGGER.error(ex.getMessage(), ex);
        }
        //logger.info("after execution of {}", joinPoint);
    }

    private Map<String, String> getInvoicePlaceholders(BillingHead billingHead) {
        String firstName = billingHead.getUserAccount().getFirstName();
        String lastName = billingHead.getUserAccount().getLastName() != null ? billingHead.getUserAccount().getLastName() : " ";
        SubscriptionMapping subsAndVariant = dataExchange.getAllMergedMeasuresBySubscription(billingHead.getCustProdId(), DBContextHolder.getTenantName());
        String gardenId = subsAndVariant.getVariant().getId().getOid();
        String gardenName = subsAndVariant.getVariant().getVariantAlias();
        Subscription subscription = subsAndVariant.getSubscription();
        MeasureType invoiceTemplate =
                subscription.getMeasures().getAllMeasures().stream().filter(rt -> rt.getCode().equals(AppConstants.SYSTEM_INVOICE_HTML_TEMPLATE)).findFirst().orElse(null);
        MeasureType SCSGNDetail =
                subscription.getMeasures().getAllMeasures().stream().filter(rt -> rt.getCode().equals("SCSGN")).findFirst().orElse(null);
        MeasureType SCSGDetail =
                subscription.getMeasures().getAllMeasures().stream().filter(rt -> rt.getCode().equals("SCSG")).findFirst().orElse(null);
        MeasureType premiseNo =
                subscription.getMeasures().getAllMeasures().stream().filter(rt -> rt.getCode().equals("S_PN")).findFirst().orElse(null);
        MeasureType discountCode =
                subscription.getMeasures().getAllMeasures().stream()
                        .filter(rt -> Arrays.asList(Constants.DISCOUNT_RATE_CODES.S_DSC, Constants.DISCOUNT_RATE_CODES.DSCP).contains(rt.getCode())).findFirst().orElse(null);
        PhysicalLocation location = physicalLocationService.findById(Long.valueOf(subsAndVariant.getVariant().getSitePhysicalLocId()));
        MeasureType billingAdd =
                subscription.getMeasures().getAllMeasures().stream().filter(rt -> rt.getCode().equals("SADD")).findFirst().orElse(null);
       CompanyPreference companyPreference = companyPreferenceService.findByCompanyKey(Long.valueOf(DBContextHolder.getTenantName()));
       List<TenantConfig> tenantConfig = tenantConfigService.findAllByParameterIn(Arrays.asList("remitName","remitAddress"));
       PortalAttributeTenantDTO portalAttributeTenant = portalAttributeOverrideService.findByIdFetchPortalAttributeValues(Long.valueOf(invoiceTemplate.getAttributeIdRefId()));
       Optional<WorkflowHookMaster> workflowHookMaster = workFlowHookMasterRepository.findById(portalAttributeTenant.getWfId());
       String hookConstant = workflowHookMaster.isPresent() ? workflowHookMaster.get().getHookConstant() : null;


       //}
        /*CustomerSubscriptionMapping invoicetemplate = subscriptionService.findCustomerSubscriptionMappingByRateCodeAndSubscription(
                        AppConstants.SYSTEM_INVOICE_TEMPLATE
                        , subscriptionService.findCustomerSubscriptionById(billingHead.getSubscriptionId()));*/
        //        List<Address> shippingAddresses = addressService.findSiteAddressWithAlias("SADD", billingHead.getSubscriptionId(),
        //                billingHead.getUserAccountId());
        //        Address shippingAddress = shippingAddresses.size() == 0 ? null : shippingAddresses.get(0);

        BillingDetail mpa = billingHead.getBillingDetails().stream().filter(s ->
                "MPA".equalsIgnoreCase(s.getBillingCode())).findFirst().get();
        BillingDetail abcre = billingHead.getBillingDetails().stream().filter(s ->
                "ABCRE".equalsIgnoreCase(s.getBillingCode())).findFirst().get();
        /*CustomerSubscription subscription =
                subscriptionService.findCustomerSubscriptionById(billingHead.getSubscriptionId());
        SubscriptionRateMatrixDetail SCSGNDetail =
                subscriptionService.findSubscriptionRateMatrixHeadById(subscription.getSubscriptionRateMatrixId()).getSubscriptionRateMatrixDetails().stream().filter(matrix -> "SCSGN".equals(matrix.getRateCode())).findFirst().orElse(null);
        SubscriptionRateMatrixDetail SCSGDetail =
                subscriptionService.findSubscriptionRateMatrixHeadById(subscription.getSubscriptionRateMatrixId()).getSubscriptionRateMatrixDetails().stream().filter(matrix -> "SCSG".equals(matrix.getRateCode())).findFirst().orElse(null);
        */
        //CustomerSubscriptionMapping customerSubscriptionMapping =
        //     subscriptionService.findCustomerSubscriptionMappingByRateCodeAndSubscription("PN", subscription);
        List<RuleExecutionLog> ruleExecutionLogs =
                ruleExecutionLogService.findAllByBillIdAndRateCodeOrderByIdAsc(billingHead.getId(), "SRTE");
        //CustomerSubscriptionMapping discountCode = subscriptionService.getRateCode(subscription,
        //      Arrays.asList(Constants.DISCOUNT_RATE_CODES.DSC, Constants.DISCOUNT_RATE_CODES.DSCP));
        //gardenId = SCSGNDetail != null ? SCSGNDetail.getDefaultValue() : null;
        //premiseNo = customerSubscriptionMapping != null ? customerSubscriptionMapping.getValue() : " ";

        //*********************************ITERATIONS*********************************//
        List<BillingDetailDTO> billingDetailDS = new ArrayList<>();
        Map<String, String> placeholderValues = new HashMap<>();
        int lineNo = 0;
        for (BillingDetail bd : billingHead.getBillingDetails()) {
            Double lineAmount = bd.getValue();
            String description = null;
            PortalAttributeValueTenantDTO billCodeValue = attributeOverrideService.findByAttributeValue(bd.getBillingCode());
            if (billCodeValue != null) {
                description = billCodeValue.getDescription();
            }
            String billCode = null;
            if (description != null) {
                billCode = description;
            } else {
                continue;
            }
            if (bd.getAddToBillAmount() || (bd.getBillingCode().equals(Constants.BILLING_CODES.ABCRE) && bd.getValue() != 0)) {
                billingDetailDS.add(BillingDetailDTO.builder()
                        .lineSeqNo(++lineNo)
                        .billingCode(billCode)
                        //.rateCode(rateCode)
                        //.kwhsValue(val)
                        .lineAmount("$".concat(String.format("%.2f", lineAmount)))
                        .build());
                ++lineNo;
                placeholderValues.put(billCode, "$".concat(String.format("%.2f", lineAmount)));
            }
        }
        placeholderValues.put("lineNo", String.valueOf(lineNo));
        //*********************************ITERATIONS*********************************//
        placeholderValues.put("tenant_name", companyPreference.getCompanyName() != null ? companyPreference.getCompanyName() : " ");
        placeholderValues.put("first_last_name", firstName + " " + lastName);
        placeholderValues.put("invoice_id", billingHead.getInvoice() != null ? billingHead.getInvoice().getId().toString() : " ");
        placeholderValues.put("bill_head_id", billingHead.getBillingDetails().toString());
        placeholderValues.put("customer_no", billingHead.getUserAccount().getAcctId() + "-" + billingHead.getCustProdId() + "-" + premiseNo);
        placeholderValues.put("invoice_date", billingHead.getInvoice() != null ? billingHead.getInvoice().getDateOfInvoice().toString() : " ");
        placeholderValues.put("billing_month", billingHead.getBillingMonthYear().toString());
        placeholderValues.put("billing_address", billingAdd != null ? billingAdd.getDefaultValue() : " ");
        placeholderValues.put("due_date", billingHead.getDueDate() != null ? billingHead.getDueDate().toString(): " ");
        //placeholderValues.put("garden_name", SCSGDetail != null ? SCSGDetail.getDefaultValue() : " ");
        placeholderValues.put("garden_name", gardenName != null ? gardenName : " ");
        placeholderValues.put("garden_src", SCSGNDetail.getDefaultValue() != null ? SCSGNDetail.getDefaultValue() : " ");
        placeholderValues.put("mpa", mpa.getValue() != null ? mpa.getValue().toString() : " ");
        placeholderValues.put("srte", ruleExecutionLogs.size() == 0 ? "" :
                ruleExecutionLogs.get(0).getReturnedValue() != null ?
                        ruleExecutionLogs.get(0).getReturnedValue().toString().concat("/kWh") : "");
        placeholderValues.put("abcre", abcre.getValue() != null ? abcre.getValue().toString() : " ");
        placeholderValues.put("invoice_template", invoiceTemplate != null ? invoiceTemplate.getDefaultValue() : " ");
        placeholderValues.put("dscp", discountCode != null ? discountCode.getDefaultValue() : String.valueOf(0.0));
        placeholderValues.put("garden_address", location.getAdd1() + " , " + location.getAdd2() + " , " + location.getZipCode() + " , " + location.getAdd3());
        placeholderValues.put("total", billingHead.getAmount() != null ? billingHead.getAmount().toString() : String.valueOf(0.0));
        placeholderValues.put("remit_name", tenantConfig.size() != 0 ? tenantConfig.stream().filter(rtName -> rtName.equals("remitName")).findFirst().orElse(null).getText() : " ");
        placeholderValues.put("remit_address", tenantConfig.size() != 0 ? tenantConfig.stream().filter(rtName -> rtName.equals("remitAddress")).findFirst().orElse(null).getText() : " ");
        placeholderValues.put("wfId", portalAttributeTenant.getWfId().toString());
        placeholderValues.put("hookConstant", hookConstant);
        return placeholderValues;
    }

}
