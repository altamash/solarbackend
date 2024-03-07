package com.solar.api.tenant.service.process.billing.invoice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.sendgrid.Response;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.service.SubscriptionRateCodes;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.service.EmailService;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.workflow.HookValidator;
import com.solar.api.tenant.model.billing.BillingInvoice.BillingInvoice;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.process.rule.RuleExecutionLog;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixDetail;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.BillingHeadRepository;
import com.solar.api.tenant.repository.UserRepository;
import com.solar.api.tenant.repository.workflow.MessageTemplatePlaceholderRepository;
import com.solar.api.tenant.service.CompanyPreferenceService;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import com.solar.api.tenant.service.process.billing.EPaymentCode;
import com.solar.api.tenant.service.process.rule.RuleExecutionLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BillingUtilityServiceImpl implements BillingUtilityService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    BillInvoiceService billInvoiceService;
    @Autowired
    BillingHeadRepository billingHeadRepository;
    @Autowired
    JobManagerTenantService jobManagerTenantService;
    @Autowired
    private HookValidator hookValidator;
    @Autowired
    private MessageTemplatePlaceholderRepository messageTemplatePlaceholderRepository;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private PortalAttributeOverrideService attributeOverrideService;
    @Autowired
    RuleExecutionLogService ruleExecutionLogService;
    @Autowired
    MasterTenantService masterTenantService;
    @Autowired
    CompanyPreferenceService companyPreferenceService;
    @Autowired
    EmailService emailService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public String invoicing(String subscriptionCode, String rateMatrixHeadIds, String billingMonthYear, Date invoiceDate,
                          String type, Long compKey) throws Exception {
        List<BillingInvoice> billingInvoices = new ArrayList<>();
        List<Long> subscriptionRateMatrixIdsLong =
                Arrays.stream(rateMatrixHeadIds.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        List<Long> lastMonthNotInvoiced = new ArrayList<>();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();

        //Get billingHeads loop
        for (BillingHead billingHead : billInvoiceService.findForInvoicing(billingMonthYear, subscriptionCode,
                subscriptionRateMatrixIdsLong,
                Arrays.asList(EBillStatus.GENERATED.getStatus(), EBillStatus.INVOICED.getStatus()))) {
            BillingHead lastBillingHead = billingHeadRepository.findLastBillHead(billingHead.getSubscriptionId(),
                    billingHead.getId());
            if (lastBillingHead != null &&
                    (lastBillingHead.getBillStatus().equals(EBillStatus.SCHEDULED.getStatus()) ||
                            lastBillingHead.getBillStatus().equals(EBillStatus.GENERATED.getStatus()))) {
                lastMonthNotInvoiced.add(lastBillingHead.getSubscriptionId());
            }
            BillingInvoice billingInvoice = billInvoiceService.invoice(billingHead, invoiceDate);
            if (billingInvoice != null) {
                try {
                    generateTemplate(billingHead);
                    billingInvoices.add(billingInvoice);
                } catch (Exception e) {
                    ObjectNode messageJson = new ObjectMapper().createObjectNode();
//                    messageJson.put("job_id", jobManagerTenant.getId());
                    messageJson.put("subscriptionCode", subscriptionCode);
                    messageJson.put("garden Ids", Joiner.on(", ").join(Collections.singleton(rateMatrixHeadIds)));
                    messageJson.put("billingMonthYear", billingMonthYear);
                    messageJson.put("type", type);
                    LOGGER.error(messageJson.toPrettyString(), e);
                }
                billInvoiceService.addPaymentTransactionHeadForInvoice(billingHead, billingInvoice.getId(),
                        billInvoiceService.getBillingCode(EPaymentCode.BILLING.getCode()), null);
            }
        }
        return Joiner.on(", ").join(lastMonthNotInvoiced.stream().map(String::valueOf).collect(Collectors.toList()));
//        responseMessage.put("Previous months' invoicing required for subscription ID(s): ", lastMonthNotInvoicedStrings);
//        return responseMessage;
//        jobManagerTenant.setResponseMessage(responseMessage.toPrettyString());
//        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobManagerTenant.getBatchId(), LOGGER);
    }

    @Override
    public void invoicing(Long billingHeadId, Date invoiceDate, Long compKey) throws Exception {
        BillingHead billingHead = billingHeadRepository.findById(billingHeadId).orElseThrow(() -> new NotFoundException(BillingHead.class, billingHeadId));
        generateTemplate(billingHead);
        billInvoiceService.invoice(billingHead, invoiceDate);
    }

    /**
     * 1) SendGrid template is in tenantConfig
     *
     * @param billingHeadId
     * @param compKey
     * @return
     * @throws Exception
     */

    @Override
    public ObjectNode emailInvoicing(Long billingHeadId, Long compKey) throws Exception {
        ObjectNode request = new ObjectMapper().createObjectNode();

        BillingHead billingHead = billingHeadRepository.findById(billingHeadId).orElseThrow(() -> new NotFoundException(BillingHead.class, billingHeadId));

        String invoiceTemplate = getInvoiceTemplate(billingHead);

        Map<String, String> placeholderValues = new HashMap<>();

        placeholderValues.put("invoiceTemplate", invoiceTemplate != null ? invoiceTemplate : " ");
        placeholderValues.put("bill_head_id", billingHead.getId().toString());

        request = hookValidator.hookFinder(null, AppConstants.BILL_INVOICE_EMAIL, 1L, billingHead.getUserAccount().getAcctId(),
                placeholderValues, null);
        String templateId = placeholderValues.get("template");
        String body = placeholderValues.get("body");
        Personalization personalization = new Personalization();
        List<CustomerSubscriptionMapping> customerSubscriptionMappingList =
                subscriptionService.findByIdFetchCustomerSubscriptionMappings(billingHead.getSubscriptionId()).getCustomerSubscriptionMappings()
                        .stream().filter(val ->
                        SubscriptionRateCodes.PREMISE_NUMBER.equalsIgnoreCase(val.getRateCode())).collect(Collectors.toList());
        String subject =
                "Solar Subscription Invoice - " + billingHead.getInvoice().getId() + " "
                        + customerSubscriptionMappingList.get(0).getValue();
        personalization.addDynamicTemplateData("subject", subject);
        personalization.addTo(new Email(AppConstants.SOLAR_TEST_EMAIL));
        personalization.addCc(new Email("m.shariq@solarinformatics.com"));
        personalization.addCc(new Email("shaikhshariq039@gmail.com"));
        personalization.addDynamicTemplateData("body", emailFormatting(body));
        emailService.emailDynamicTemplateWithNoFile(templateId, personalization);

        request.put("userId", billingHead.getUserAccount().getFirstName());
        request.put("billHeadId", billingHead.getId());
        request.put("message", "Email sent");
        return request;

    }

    @Override
    public Response batchNotification(String jobName, Long jobId, String stackTrace, String subject) throws IOException {
        return emailService.batchNotification(jobName, jobId, stackTrace, subject);
    }

    private String emailFormatting(String body) {
        return body.replaceAll("\"", "'");
    }

    private String getInvoiceTemplate(BillingHead billingHead) {
        return subscriptionService.findCustomerSubscriptionMappingByRateCodeAndSubscription(
                AppConstants.SYSTEM_INVOICE_TEMPLATE
                , subscriptionService.findCustomerSubscriptionById(billingHead.getSubscriptionId())).getValue();
    }

    private ObjectNode generateTemplate(BillingHead billingHead) {
        String[][] subjectParams = new String[][]{{
                billingHead.getId().toString(),
                billingHead.getBillingMonthYear()
        }};
        return hookValidator.hookFinder(null, AppConstants.CSG_INVOICING, 1L,
                billingHead.getUserAccountId() != null ? billingHead.getUserAccountId() : billingHead.getUserAccount().getAcctId(),
                getInvoicePlaceholders(billingHead), billingHead, subjectParams);
    }

    private Map<String, String> getInvoicePlaceholders(BillingHead billingHead) {
        String gardenId = null;
        String premiseNo = null;
        Optional<User> user = null;
        if(billingHead.getUserAccount() == null) {
            user = userRepository.findById(billingHead.getUserAccountId());
        } else {
            user = Optional.ofNullable(billingHead.getUserAccount());
        }
        String firstName = user.get().getFirstName();
        String lastName = user.get().getLastName() != null ? user.get().getLastName() : " ";

        String invoiceTemplate = getInvoiceTemplate(billingHead);

        BillingDetail mpa = billingHead.getBillingDetails().stream().filter(s ->
                "MPA".equalsIgnoreCase(s.getBillingCode())).findFirst().get();

        BillingDetail abcre = billingHead.getBillingDetails().stream().filter(s ->
                "ABCRE".equalsIgnoreCase(s.getBillingCode())).findFirst().get();

        CustomerSubscription subscription =
                subscriptionService.findCustomerSubscriptionById(billingHead.getSubscriptionId());

        SubscriptionRateMatrixDetail SCSGNDetail =
                subscriptionService.findSubscriptionRateMatrixHeadById(subscription.getSubscriptionRateMatrixId()).getSubscriptionRateMatrixDetails().stream()
                        .filter(matrix -> "SCSGN".equals(matrix.getRateCode())).findFirst().orElse(null);

        SubscriptionRateMatrixDetail SCSGDetail =
                subscriptionService.findSubscriptionRateMatrixHeadById(subscription.getSubscriptionRateMatrixId()).getSubscriptionRateMatrixDetails().stream()
                        .filter(matrix -> "SCSG".equals(matrix.getRateCode())).findFirst().orElse(null);

        CustomerSubscriptionMapping customerSubscriptionMapping =
                subscriptionService.findCustomerSubscriptionMappingByRateCodeAndSubscription("PN", subscription);

        List<RuleExecutionLog> ruleExecutionLogs =
                ruleExecutionLogService.findAllByBillIdAndRateCodeOrderByIdAsc(billingHead.getId(), "SRTE");

        CustomerSubscriptionMapping discountCode = subscriptionService.getRateCode(subscription,
                Arrays.asList(Constants.DISCOUNT_RATE_CODES.DSC, Constants.DISCOUNT_RATE_CODES.DSCP));

        gardenId = SCSGNDetail != null ? SCSGNDetail.getDefaultValue() : null;

        premiseNo = customerSubscriptionMapping != null ? customerSubscriptionMapping.getValue() : " ";

        CompanyPreference companyPreference = companyPreferenceService.findByCompanyKey(masterTenantService.findByDbName(DBContextHolder.getTenantName()).getCompanyKey());

        Map<String, String> placeholderValues = new HashMap<>();
        placeholderValues.put("tenant_name", companyPreference.getCompanyName());
        placeholderValues.put("first_last_name", firstName + " " + lastName);
        placeholderValues.put("invoice_id", billingHead.getInvoice().getId().toString());
        placeholderValues.put("bill_head_id", billingHead.getId().toString());
        placeholderValues.put("customer_no", user.get().getAcctId() + "-" + subscription.getId() + "-" + premiseNo);
        placeholderValues.put("invoice_date", billingHead.getInvoice().getDateOfInvoice().toString());
        placeholderValues.put("billing_month", billingHead.getBillingMonthYear());
        placeholderValues.put("billing_address", billingHead.getInvoice().getId().toString());
        placeholderValues.put("due_date", billingHead.getDueDate().toString());
        placeholderValues.put("garden_name", SCSGDetail != null ? SCSGDetail.getDefaultValue() : " ");
        placeholderValues.put("garden_src", gardenId != null ? gardenId : " ");
        placeholderValues.put("mpa", mpa.getValue() != null ? mpa.getValue().toString() : " ");
        placeholderValues.put("srte", ruleExecutionLogs.size() == 0 ? "" :
                ruleExecutionLogs.get(0).getReturnedValue() != null ?
                        ruleExecutionLogs.get(0).getReturnedValue().toString().concat("/kWh") : "");
        placeholderValues.put("abcre", abcre.getValue() != null ? abcre.getValue().toString() : " ");
        placeholderValues.put("invoiceTemplate", invoiceTemplate != null ? invoiceTemplate : " ");
        placeholderValues.put("dscp", discountCode != null ? discountCode.getValue() : String.valueOf(0.0));
        return placeholderValues;
    }
}
