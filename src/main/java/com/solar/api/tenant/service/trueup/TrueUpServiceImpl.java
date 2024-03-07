package com.solar.api.tenant.service.trueup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.azure.storage.StorageException;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.solar.api.AppConstants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.EmailService;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.job.EJobName;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.tenant.model.extended.CsgBillcreRecon;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.report.TrueUp;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.trueup.CsgBillcreReconTemplate;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.BillcredReconDetailGardenViewRepository;
import com.solar.api.tenant.repository.CsgBillcreReconRepository;
import com.solar.api.tenant.repository.CsgBillcreReconSpecification;
import com.solar.api.tenant.repository.TrueUpRepository;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class TrueUpServiceImpl implements TrueUpService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private BillcredReconDetailGardenViewRepository viewRepository;
    @Autowired
    private CsgBillcreReconRepository reconRepository;
    @Autowired
    private Utility utility;
    @Autowired
    StorageService storageService;
    @Autowired
    JobManagerTenantService jobManagerTenantService;
    @Lazy
    @Autowired
    SubscriptionService subscriptionService;
    @Autowired
    EmailService emailService;
    @Autowired
    UserService userService;
    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    TrueUpRepository trueUpRepository;

    //TODO: Make period and subscription_type dynamic
    @Override
    public TrueUp saveOrUpdate(MultipartFile multipartFile, Long subscription_id, Long subscriptionRateMatrixId,
                               String type) throws IOException, URISyntaxException, StorageException {
        TrueUp trueUpData = getBySubscriptionId(subscription_id);
        CustomerSubscription customerSubscription = subscriptionService.findCustomerSubscriptionById(subscription_id);
        if (trueUpData == null) {
            ByteArrayInputStream dataStream = new ByteArrayInputStream(multipartFile.getBytes());
            String reportUrl = uploadTrueUp(type, subscription_id, customerSubscription.getUserAccount().getAcctId(),
                    dataStream, multipartFile);
            TrueUp trueUp = new TrueUp();
            trueUp.setSubscriptionRateMatrixId(subscriptionRateMatrixId);
            trueUp.setAcctId(customerSubscription.getUserAccount().getAcctId());
            trueUp.setSubscriptionId(subscription_id);
            trueUp.setSubscriptionType(type);
            trueUp.setStartDate(customerSubscription.getStartDate());
            trueUp.setEndDate(customerSubscription.getEndDate());
            trueUp.setReportUrl(reportUrl);
            return trueUpRepository.save(trueUp);
        } else {
            ByteArrayInputStream dataStream = new ByteArrayInputStream(multipartFile.getBytes());
            String reportUrl = uploadTrueUp(type, subscription_id, customerSubscription.getUserAccount().getAcctId(),
                    dataStream, multipartFile);
            trueUpData.setReportUrl(reportUrl);
            trueUpRepository.save(trueUpData);
        }
        return trueUpData;
    }

    @Override
    public List<TrueUp> getAllByGardenId(Long subscriptionRateMatrixId) {
        return trueUpRepository.findBySubscriptionRateMatrixId(subscriptionRateMatrixId);
    }

    @Override
    public void deleteByGarden(Long subscriptionRateMatrixId) {
        List<TrueUp> trueUps = trueUpRepository.findBySubscriptionRateMatrixId(subscriptionRateMatrixId);
        trueUps.forEach(t -> {
            trueUpRepository.delete(t);
        });
    }

    @Override
    public TrueUp getBySubscriptionId(Long subscriptionId) {
        return trueUpRepository.findBySubscriptionId(subscriptionId);
    }

    @Async
    @Override
    public void emailBySubscriptionRateMatrixId(String type, Long subscriptionRateMatrixId) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        String reportType = null;
        String reportTemplate = null;
        if (type.equals(AppConstants.CSGF)) {
            reportType = AppConstants.CSGF;
            reportTemplate = AppConstants.TRUE_UP_ARR_EMAIL_TEMPLATE;
        } else {
            reportType = AppConstants.CSGR;
            reportTemplate = AppConstants.TRUE_UP_VOS_EMAIL_TEMPLATE;
        }
        response.put("startTime", System.currentTimeMillis());
        List<TrueUp> trueUps = getAllByGardenId(subscriptionRateMatrixId);
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(EJobName.EMAIL_TRUE_UPS.toString(), response,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        String finalReportTemplate = reportTemplate;
        String finalReportType = reportType;
        trueUps.forEach(trueUp -> {
            User user = userService.findById(trueUp.getAcctId());
            Personalization personalization = new Personalization();
            personalization.addDynamicTemplateData("subject", AppConstants.TRUE_UP_SUBJECT);
            File file = null;
            try {
                file = storageService.getBlob(appProfile,
                        "tenant/" + 1001L + AppConstants.TRUE_UP_PATH,
                        AppConstants.TRUEUP + "_" + finalReportType + "_" + trueUp.getSubscriptionId() + "_" + trueUp.getAcctId() + AppConstants.MIME_TYPE_PDF);

                String fileName = AppConstants.TRUEUP + "_" + finalReportType + "_" + trueUp.getSubscriptionId() + "_"
                        + trueUp.getAcctId() + AppConstants.MIME_TYPE_PDF;
                if (appProfile.equals(AppConstants.PROFILE_PRODUCTION)) {
                    personalization.addTo(new Email(user.getEmailAddress()));
                    personalization.addBcc(new Email(AppConstants.BCC_NOVEL));
                } else if (appProfile.equals(AppConstants.PROFILE_STAGING)) {
                    personalization.addBcc(new Email(AppConstants.BCC_NOVEL));
                    personalization.addTo(new Email(AppConstants.SOLAR_TEST_EMAIL));
                } else {
                    personalization.addTo(new Email(AppConstants.SOLAR_TEST_EMAIL));
                }

                emailService.sendEmailsWithDynamicTemplate(finalReportTemplate, file, fileName, personalization);

                jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);

            } catch (IOException ioException) {
                response.put("warning", "No PDF report found.");
                LOGGER.error(ioException.getMessage(), ioException);
            }

        });
    }

    @Override
    public TrueUp emailBySubscriptionId(String type, Long subscriptionId) throws IOException {
        String reportType = null;
        String reportTemplate = null;
        if (type.equals(AppConstants.CSGF)) {
            reportType = AppConstants.CSGF;
            reportTemplate = AppConstants.TRUE_UP_ARR_EMAIL_TEMPLATE;
        } else {
            reportType = AppConstants.CSGR;
            reportTemplate = AppConstants.TRUE_UP_VOS_EMAIL_TEMPLATE;
        }
        TrueUp trueUp = getBySubscriptionId(subscriptionId);
        User user = userService.findById(trueUp.getAcctId());

        Personalization personalization = new Personalization();
        personalization.addDynamicTemplateData("subject", AppConstants.TRUE_UP_SUBJECT);
        File file = null;
        try {
            file = storageService.getBlob(appProfile,
                    "tenant/" + 1001L + AppConstants.TRUE_UP_PATH,
                    AppConstants.TRUEUP + "_" + reportType + "_" + trueUp.getSubscriptionId() + "_" + trueUp.getAcctId() + AppConstants.MIME_TYPE_PDF);

            String fileName =
                    AppConstants.TRUEUP + "_" + reportType + "_" + trueUp.getSubscriptionId() + "_" + trueUp.getAcctId() + AppConstants.MIME_TYPE_PDF;
            if (appProfile.equals(AppConstants.PROFILE_PRODUCTION)) {
                personalization.addTo(new Email(user.getEmailAddress()));
                personalization.addBcc(new Email(AppConstants.BCC_NOVEL));
            } else {
                personalization.addTo(new Email(AppConstants.SOLAR_TEST_EMAIL));
            }

            emailService.sendEmailsWithDynamicTemplate(reportTemplate, file, fileName, personalization);

        } catch (Exception e) {
            throw e;
        }
        return trueUp;
    }

    private String uploadTrueUp(String type, Long subscription_id, Long userAccountId,
                                ByteArrayInputStream dataStream, MultipartFile multipartFile) throws IOException,
            StorageException, URISyntaxException {
        return storageService.storeInContainer(
                multipartFile,
                appProfile,
                "tenant/" + 1001L + AppConstants.TRUE_UP_PATH,
                AppConstants.TRUEUP + "_" + type + "_" + subscription_id + "_" + userAccountId + AppConstants.MIME_TYPE_PDF,
                1001L,
                false);
    }

    @Override
    public List<CsgBillcreRecon> generate(String gardenId, List<Long> subscriptionIds, String startMonthYear,
                                          String endMonthYear) throws ParseException {
        String[] monthYear = startMonthYear.split("-");
        startMonthYear = monthYear[1] + "-" + monthYear[0] + "-01";
        Date startDate = Utility.getStartOfMonth(startMonthYear, Utility.SYSTEM_DATE_FORMAT);
        monthYear = endMonthYear.split("-");
        endMonthYear = monthYear[1] + "-" + monthYear[0] + "-01";
        Date endDate = Utility.getEndOfMonth(endMonthYear, Utility.SYSTEM_DATE_FORMAT);
        List<CsgBillcreReconTemplate> csgBillcreReconTemplates = new ArrayList<>();
        if (!"-1".equals(gardenId)) {
            csgBillcreReconTemplates = viewRepository.generateByGarden(gardenId, startDate, endDate);
        } else if (subscriptionIds != null) {
            csgBillcreReconTemplates = viewRepository.generateBySubscriptionId(subscriptionIds, startDate, endDate);
        }
        return save(csgBillcreReconTemplates, startDate, endDate);
    }

    private List<CsgBillcreRecon> save(List<CsgBillcreReconTemplate> templates, Date startDate, Date endDate) {
        String startMonthYear = new SimpleDateFormat("MM-yyyy").format(startDate);
        String endMonthYear = new SimpleDateFormat("MM-yyyy").format(endDate);
        List<CsgBillcreRecon> csgBillcreReconsToSave = new ArrayList<>();
        List<CsgBillcreRecon> csgBillcreReconsToUpdate = new ArrayList<>();
        List<CsgBillcreRecon> csgBillcreRecons = new ArrayList<>();
        templates.forEach(t -> {
            CsgBillcreRecon csgBillcreRecon =
                    reconRepository.findBySubscriptionIdAndGardenIdAndPremiseNoAndPeriodStartDateAndPeriodEndDate(
                            t.getSubscriptionId(), t.getGardenId(), t.getPremiseNo(), startMonthYear, endMonthYear);
            CsgBillcreRecon recon = buildCsgBillcreRecon(t, startMonthYear, endMonthYear);
            if (csgBillcreRecon != null) {
                recon.setReconId(csgBillcreRecon.getReconId());
                csgBillcreReconsToUpdate.add(recon);
            } else {
                csgBillcreReconsToSave.add(recon);
            }
            csgBillcreRecons.add(recon);
        });
        reconRepository.saveAll(csgBillcreReconsToSave);
        reconRepository.saveAll(csgBillcreReconsToUpdate);
        return csgBillcreRecons;
    }

    private CsgBillcreRecon buildCsgBillcreRecon(CsgBillcreReconTemplate template, String startMonthYear,
                                                 String endMonthYear) {
        int rounding = utility.getCompanyPreference().getRounding();
        return CsgBillcreRecon.builder()
                .subscriptionId(template.getSubscriptionId())
                .periodStartDate(startMonthYear)
                .periodEndDate(endMonthYear)
                .totalCredits(utility.round(template.getTotalCredits(), rounding))
                .totalPayment(utility.round(template.getTotalPayment(), rounding))
                .totalBilled(utility.round(template.getTotalBilled(), rounding))
                .subscriptionCost(utility.round(template.getSubscriptionCost(), rounding))
                .balance(utility.round(template.getBalance(), rounding))
                .premiseNo(template.getPremiseNo())
                .gardenId(template.getGardenId())
                .gardenName(template.getGardenName())
                .status("NEW")
                .reportURI("")
                .build();
    }

    @Override
    public List<CsgBillcreRecon> view(Long subscriptionId, String gardenId, String premiseNo, String periodStartDate,
                                      String periodEndDate) {
        if (subscriptionId == null && gardenId == null && premiseNo == null &&
                periodStartDate == null && periodEndDate == null) {
            return reconRepository.findAll();
        }
        Specification<CsgBillcreRecon> specs = null;
        if (subscriptionId != null) {
            specs = getSpecification(specs, "subscriptionId", subscriptionId);
        }
        if (gardenId != null) {
            specs = getSpecification(specs, "gardenId", gardenId);
        }
        if (premiseNo != null) {
            specs = getSpecification(specs, "premiseNo", premiseNo);
        }
        if (periodStartDate != null) {
            specs = getSpecification(specs, "periodStartDate", periodStartDate);
        }
        if (periodEndDate != null) {
            specs = getSpecification(specs, "periodEndDate", periodEndDate);
        }
        return reconRepository.findAll(specs);
    }

    private Specification<CsgBillcreRecon> getSpecification(Specification<CsgBillcreRecon> specs, String field,
                                                            Object value) {
        if (specs != null) {
            return specs.and(Specification.where(CsgBillcreReconSpecification.withFieldValue(field, value)));
        }
        return Specification.where(CsgBillcreReconSpecification.withFieldValue(field, value));
    }
}
