package com.solar.api.tenant.service.process.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.azure.storage.StorageException;
import com.sendgrid.Response;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.solar.api.AppConstants;
import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.helper.service.SubscriptionRateCodes;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.service.EmailService;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.integration.mongo.response.subscription.Variant;
import com.solar.api.tenant.mapper.report.ReportMapper;
import com.solar.api.tenant.model.billing.BillingInvoice.PublishInfo;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.calculation.CalculationDetails;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.report.ReportIteratorDefinition;
import com.solar.api.tenant.model.report.ReportTemplate;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixDetail;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.repository.ReportIteratorDefinitionRepository;
import com.solar.api.tenant.repository.ReportTemplateRepository;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.CalculationDetailsService;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.contract.UserLevelPrivilegeService;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import com.solar.api.tenant.service.process.billing.publish.PublishInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.solar.api.tenant.model.contract.Entity;

@Service
//@Transactional("masterTransactionManager")
public class ReportServiceImpl implements ReportService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private ReportTemplateRepository reportTemplateRepository;

    @Autowired
    private ReportIteratorDefinitionRepository reportIteratorDefinitionRepository;

    @Autowired
    PublishInfoService publishInfoService;

    @Autowired
    StorageService storageService;

    @Autowired
    UserService userService;

    @Autowired
    @Lazy
    SubscriptionService subscriptionService;

    @Autowired
    BillingHeadService billingHeadService;

    @Autowired
    EmailService emailService;

    @Autowired
    TenantConfigService tenantConfigService;

    @Value("${app.profile}")
    private String appProfile;

    @Autowired
    private Utility utility;

    @Autowired
    private CalculationDetailsService calculationDetailsService;
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    private CustomerSubscriptionRepository customerSubscriptionRepository;

    // ReportTemplate /////////////////////////////////////
    @Override
    public ReportTemplate saveOrUpdate(MultipartFile file, ReportTemplate reportTemplate, String container,
                                       Long compKey) throws URISyntaxException, StorageException, IOException {
        if (reportTemplate.getId() != null) {
            ReportTemplate reportTemplateDb = findById(reportTemplate.getId());
            reportTemplate = ReportMapper.toUpdatedReportTemplate(reportTemplateDb, reportTemplate);
        }
        ReportTemplate reportTemplateDb = findByTemplateNameAndOutputFormat(reportTemplate.getTemplateName(),
                reportTemplate.getOutputFormat());
        if (reportTemplateDb != null &&
                ((reportTemplate.getId() != null && reportTemplate.getId().longValue() != reportTemplateDb.getId().longValue())
                        || (reportTemplateDb.getTemplateName().equals(reportTemplate.getTemplateName()) && reportTemplateDb.getOutputFormat().equals(reportTemplateDb.getOutputFormat())))) {
            throw new AlreadyExistsException(ReportTemplate.class, Arrays.asList(new String[]{"templateName",
                    "outputFormat"}));
        }
        if (file != null) {
            storageService.storeInContainer(file, container, reportTemplate.getTemplateURI(),
                    reportTemplate.getTemplateName(), compKey, true);
        }
        return reportTemplateRepository.save(reportTemplate);
    }

    @Override
    public List<ReportTemplate> save(List<ReportTemplate> reportTemplates) {
        return reportTemplateRepository.saveAll(reportTemplates);
    }

    @Override
    public ReportTemplate findById(Long id) {
        return reportTemplateRepository.findById(id).orElseThrow(() -> new NotFoundException(ReportTemplate.class, id));
    }

    @Override
    public ReportTemplate findByTemplateNameAndOutputFormat(String templateName, String outputFormat) {
        return reportTemplateRepository.findByTemplateNameAndOutputFormat(templateName, outputFormat);
    }

    @Override
    public List<ReportTemplate> findAll() {
        return reportTemplateRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        reportTemplateRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        reportTemplateRepository.deleteAll();
    }

    // ReportIteratorDefinition /////////////////////////////////////
    @Override
    public ReportIteratorDefinition saveOrUpdateReportIteratorDefinition(ReportIteratorDefinition reportIteratorDefinition) {
        if (reportIteratorDefinition.getId() != null) {
            ReportIteratorDefinition reportIteratorDefinitionDb =
                    findReportIteratorDefinitionById(reportIteratorDefinition.getId());
            reportIteratorDefinition = ReportMapper.toUpdatedReportIteratorDefinition(reportIteratorDefinitionDb,
                    reportIteratorDefinition);
        }
        return reportIteratorDefinitionRepository.save(reportIteratorDefinition);
    }

    @Override
    public List<ReportIteratorDefinition> saveReportIteratorDefinition(List<ReportIteratorDefinition> reportIteratorDefinitions) {
        return reportIteratorDefinitionRepository.saveAll(reportIteratorDefinitions);
    }

    @Override
    public ReportIteratorDefinition findReportIteratorDefinitionById(Long id) {
        return reportIteratorDefinitionRepository.findById(id).orElseThrow(() -> new NotFoundException(ReportIteratorDefinition.class, id));
    }

    @Override
    public List<ReportIteratorDefinition> findAllReportIteratorDefinitions() {
        return reportIteratorDefinitionRepository.findAll();
    }

    @Override
    public void deleteReportIteratorDefinition(Long id) {
        ReportIteratorDefinition reportIteratorDefinition = findReportIteratorDefinitionById(id);
        reportIteratorDefinitionRepository.delete(reportIteratorDefinition);
    }

    @Override
    public void deleteAllReportIteratorDefinition() {
        reportIteratorDefinitionRepository.deleteAll();
    }

    @Override
    public ObjectNode publishInvoice(Long billHeadId) {

        ObjectNode response = new ObjectMapper().createObjectNode();

        BillingHead head = billingHeadService.findById(billHeadId);

        if (head.getBillStatus().equals("INVOICED")) {

            try {
                User user = userService.findById(head.getUserAccountId());
                CustomerSubscription customerSubscription =
                        subscriptionService.findCustomerSubscriptionById(head.getSubscriptionId());
                List<CustomerSubscriptionMapping> customerSubscriptionMapping =
                        subscriptionService.findBySubscription(customerSubscription);

                List<CustomerSubscriptionMapping> customerSubscriptionMappingList =
                        customerSubscriptionMapping.stream().filter(val ->
                                SubscriptionRateCodes.PREMISE_NUMBER.equalsIgnoreCase(val.getRateCode())).collect(Collectors.toList());

                SubscriptionRateMatrixHead subscriptionRateMatrixHead =
                        subscriptionService.findSubscriptionRateMatrixHeadById(customerSubscription.getSubscriptionRateMatrixId());

                List<SubscriptionRateMatrixDetail> SCSGN =
                        subscriptionRateMatrixHead.getSubscriptionRateMatrixDetails().stream().filter(val ->
                                SubscriptionRateCodes.SUBSCRIBED_CSG_NAME.equalsIgnoreCase(val.getRateCode())).collect(Collectors.toList());

                List<SubscriptionRateMatrixDetail> SCSG =
                        subscriptionRateMatrixHead.getSubscriptionRateMatrixDetails().stream().filter(val ->
                                SubscriptionRateCodes.SUBSCRIBED_CSG_SRC_NUMBER.equalsIgnoreCase(val.getRateCode())).collect(Collectors.toList());


                File file = null;
                try {
                    file = storageService.getBlob(appProfile, "tenant/" + utility.getCompKey()
                                    + AppConstants.INVOICE_REPORT_PATH,
                            head.getInvoice().getId() + " - " + user.getFirstName().trim()
                                    + " " + user.getLastName().trim()
                                    + ", " + head.getBillingMonthYear() + ".pdf");

                } catch (Exception e) {
                    response.put("warning", "No PDF report found.");
                    throw e;
                }

                if (file == null || file.length() == 0) {
                    int status = 400;
                    publishInfoService.update(head.getId(), status);
                    response.put(head.getId().toString() + " BillHead Id", "File is corrupted Try re-invoicing");
                    return response;
                }
                long fileSizeInKB = file.length() / 1024;

                String readableDate =
                        new DateFormatSymbols().getShortMonths()[Integer.parseInt(head.getBillingMonthYear().split(
                                "-")[0]) - 1] +
                                " " + head.getBillingMonthYear().split("-")[1];

                String statement = null;
                String aname = String.valueOf(tenantConfigService.findByParameter(AppConstants.EmailANameParamTenantConfig).get());
                String astreet = String.valueOf(tenantConfigService.findByParameter(AppConstants.EmailAStreetParamTenantConfig).get());
                String aapartment = String.valueOf(tenantConfigService.findByParameter(AppConstants.EmailAApartmentParamTenantConfig).get());
                String astate = String.valueOf(tenantConfigService.findByParameter(AppConstants.EmailAStateParamTenantConfig).get());
                String subject = String.valueOf(tenantConfigService.findByParameter(AppConstants.EmailSubjectParamTenantConfig).get());
                if (subscriptionRateMatrixHead.getSubscriptionCode().equals("CSGF")) {
                    statement = "Your payment is scheduled to be processed on 15th of " + readableDate + " or the " +
                            "next business day.";
                } else {
                    statement = "Please find your invoice for the month " + readableDate + " attached.";
                }

                String header = "";
                String template = String.valueOf(tenantConfigService.findByParameter(AppConstants.EmailIdParamTenantConfig).get());
                String bcc = String.valueOf(tenantConfigService.findByParameter(AppConstants.EmailBCCParamTenantConfig).get());

                if (template == null) {
                    int status = 400;
                    publishInfoService.update(head.getId(), status);
                    response.put("error", "No template found for: " + DBContextHolder.getTenantName());
                    return response;
                }

                // subject = Solar Invoice for
                // subject + invoiceId + premise Number + gardenName
                subject =
                        subject + " - " + head.getInvoice().getId() + " "
                                + customerSubscriptionMappingList.get(0).getValue() + " "
                                + "(" + SCSGN.get(0).getDefaultValue() + ")";

                List<PublishInfo> publishInfo = publishInfoService.findByReferenceId(billHeadId);

                Personalization personalization = new Personalization();
                personalization.addDynamicTemplateData("template", template);
                personalization.addDynamicTemplateData("subject", subject);
                personalization.addDynamicTemplateData("header", header);
                personalization.addDynamicTemplateData("garden_name", SCSG.get(0).getDefaultValue());
                personalization.addDynamicTemplateData("garden_number", SCSGN.get(0).getDefaultValue());
                personalization.addDynamicTemplateData("premise_number",
                        customerSubscriptionMappingList.get(0).getValue());
                personalization.addDynamicTemplateData("invoice_number", head.getInvoice().getId());
                personalization.addDynamicTemplateData("statement", statement);
                personalization.addDynamicTemplateData("aname", aname);
                personalization.addDynamicTemplateData("astreet", astreet);
                personalization.addDynamicTemplateData("aapartment", aapartment);
                personalization.addDynamicTemplateData("astate", astate);
                personalization.addDynamicTemplateData("firstname", user.getFirstName().trim());
                personalization.addDynamicTemplateData("lastname", user.getLastName().trim());
                personalization.addDynamicTemplateData("name", user.getFirstName().trim());

                if (appProfile.equals(AppConstants.PROFILE_PRODUCTION)) {
                    personalization.addTo(new Email(publishInfo.get(0).getChannelRecipient()));
                    personalization.addBcc(new Email(bcc));
                    personalization.addBcc(new Email(AppConstants.TESTNA_TOEMAIL));
                } else if (appProfile.equals(AppConstants.PROFILE_PRE_PRODUCTION)) {
                    personalization.addTo(new Email(AppConstants.TESTNA_TOEMAIL));
                } else if (appProfile.equals(AppConstants.PROFILE_STAGING)) {
                    personalization.addTo(new Email(AppConstants.TESTNA_TOEMAIL));
                } else {
                    personalization.addTo(new Email(AppConstants.TESTNA_TOEMAIL));
                }

                String fileName = head.getInvoice().getId() + " - " + user.getFirstName().trim()
                        + " " + user.getLastName().trim()
                        + ", " + head.getBillingMonthYear() + ".pdf";

                if (fileSizeInKB != 1) {
                    Response emailResponse =
                            emailService.sendEmailWithDynamicTemplate(new Email(publishInfo.get(0).getChannelRecipient()),
                                    subject, file, fileName, personalization);

                    int status = emailResponse.getStatusCode();
                    publishInfoService.update(head.getId(), status);
                    response.put("message", "Invoice Published Successfully");
                } else {
                    int status = 400;
                    publishInfoService.update(head.getId(), status);
                    response.put("error", "File is corrupted for invoiceId: " + head.getInvoice().getId() + " Try re-invoicing");
                    return response;
                }

            } catch (Exception e) {

                response.put("warning", "Please make sure the invoice is generated before publishing.");

                response.put("invoice_id", head.getInvoice().getId());

                LOGGER.error(e.getMessage(), e);

                return response;
            }
        } else {
            response.put("error", "Please make sure the invoice generated before publishing.");
        }

        return response;
    }

    @Override
    public ObjectNode publishHTMLInvoice(BillingHead billingHead, CalculationDetails calculationDetails) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        if (billingHead.getBillStatus().equals("INVOICED") || billingHead.getBillStatus().equals("PUBLISHED")) {
            try {
                String subject = tenantConfigService.findByParameter(AppConstants.EmailSubjectParamTenantConfig).get().getText();
                String header = "";
                String template = tenantConfigService.findByParameter(AppConstants.EmailIdParamTenantConfig).get().getText();
                String bcc = tenantConfigService.findByParameter(AppConstants.EmailBCCParamTenantConfig).get().getText();

                if (template == null) {
                    int status = 400;
                    publishInfoService.update(billingHead.getId(), status);
                    response.put("error", "No template found for: " + DBContextHolder.getTenantName());
                    return response;
                }
                List<PublishInfo> publishInfo = publishInfoService.findByReferenceId(billingHead.getId());

                Personalization personalization = new Personalization();
                personalization.addDynamicTemplateData("template", template);
                personalization.addDynamicTemplateData("subject", subject);
                personalization.addDynamicTemplateData("header", header);
                personalization.addDynamicTemplateData("templateCode", calculationDetails.getPrevInvHtmlView());

                if (appProfile.equals(AppConstants.PROFILE_PRODUCTION)) {
                    personalization.addTo(new Email(publishInfo.get(0).getChannelRecipient()));
                    personalization.addBcc(new Email(bcc));
                    personalization.addBcc(new Email(AppConstants.TESTNA_TOEMAIL));
                } else if (appProfile.equals(AppConstants.PROFILE_PRE_PRODUCTION)) {
                    personalization.addTo(new Email(AppConstants.TESTNA_TOEMAIL));
                } else if (appProfile.equals(AppConstants.PROFILE_STAGING)) {
                    personalization.addTo(new Email(AppConstants.TESTNA_TOEMAIL));
                } else {
                    personalization.addTo(new Email(AppConstants.TESTNA_TOEMAIL));
                }
                Response emailResponse = emailService.emailDynamicTemplateWithNoFile(subject, personalization);
                int status = emailResponse.getStatusCode();
                publishInfoService.update(billingHead.getId(), status);
                response.put("message", "Invoice Published Successfully");
            } catch (Exception e) {
                response.put("warning", "Please make sure the invoice is generated before publishing.");
                response.put("invoice_id", billingHead.getInvoice().getId());
                LOGGER.error(e.getMessage(), e);
                return response;
            }
        } else {
            response.put("error", "Please make sure the invoice generated before publishing.");
        }
        return response;
    }


}
