package com.solar.api.saas.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sendgrid.Response;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.solar.api.AppConstants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.EmailService;
import com.solar.api.saas.service.process.calculation.RateCodeParserService;
import com.solar.api.saas.service.reporting.powerBI.ReportingService;
import com.solar.api.tenant.mapper.EmailTemplateDTO;
import com.solar.api.tenant.model.extended.resources.HRHead;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.RateCodesTemp;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.service.RateCodesTempService;
import com.solar.api.tenant.service.extended.resources.HRHeadService;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SAASInstanceController")
@RequestMapping(value = "/saas/instance")
public class SAASInstanceController {

    @Value("${app.profile}")
    private String appProfile;
    @Value("${app.fehosta}")
    private String feHost;
    @Value("${app.storage.blobService}")
    private String blobService;
    @Value("${app.storage.container}")
    private String profile;
    @Value("${app.storage.publicContainer}")
    private String publicUrl;
    @Value("${app.workspaceReportId}")
    private String workspaceReportId;
    @Value("${app.invoiceReportId}")
    private String invoiceReportId;

    @Autowired
    private RateCodeParserService rateCodeParserService;
    @Autowired
    private JobManagerTenantService jobManagerTenantService;
    @Autowired
    private RateCodesTempService rateCodesTempService;
    @Autowired
    ReportingService reportingService;
    @Autowired
    private CustomerSubscriptionRepository subscriptionRepository;
    @Autowired
    private HRHeadService hrHeadService;
    @Autowired
    private EmailService emailService;

    @GetMapping("/allAppProfilesConstants")
    public ObjectNode allAppProfilesConstants() {
        ObjectNode constants = new ObjectMapper().createObjectNode();
        constants.put("Development", AppConstants.PROFILE_DEVELOPMENT);
        constants.put("Staging", AppConstants.PROFILE_STAGING);
        constants.put("Production", AppConstants.PROFILE_PRODUCTION);
        return constants;
    }

    @GetMapping("/currentAppProfile")
    public String appProfile() {
        return appProfile;
    }

    @GetMapping("/powerBiTest")
    public String powerBiTest() {
        return reportingService.test() + " :: " + workspaceReportId + " :: " + invoiceReportId;
    }

    @GetMapping("/getTime")
    public String timeZone() {
        return TimeZone.getDefault().toString();
    }

    @GetMapping("/feHost")
    public String feHost() {
        return feHost;
    }

    @GetMapping("/blobService")
    public String blobService() {
        return blobService;
    }

    @GetMapping("/profile")
    public String profile() {
        return profile;
    }

    @GetMapping("/publicUrl")
    public String publicUrl() {
        return publicUrl;
    }

    @GetMapping("/getRateCodes/{subscriptionCode}")
    public String getRateCodes(@PathVariable String subscriptionCode) {
        List<CustomerSubscription> activeSubscriptions =
                subscriptionRepository.findBySubscriptionStatusAndSubscriptionType("ACTIVE", subscriptionCode);
        activeSubscriptions.forEach(subs -> {
            if (subs.getSubscriptionRateMatrixId() != null) {
                rateCodeParserService.storeRateCodes(subs, subs.getSubscriptionRateMatrixId(), null, null);
            }
        });
        return publicUrl;
    }

    @GetMapping("/exportRateCodes")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=RateCodes.csv";
        response.setHeader(headerKey, headerValue);

        List<RateCodesTemp> rateCodesTemps = rateCodesTempService.getAll();

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Account Id", "Subscription Id", "SN", "CN", "VCN", "CCLAS", "PN", "SADD", "MP", "KWDC"
                , "SSDT", "PSRC", "PCOMP", "DSCP", "TENR",
                "ROLL", "CSGSDT", "DEP", "GLCR", "GLDR", "GNSIZE", "GOWN", "SCSG", "SCSGN", "SPGM", "UTCOMP", "YLD",
                "SRTE", "FDAM", "OPYR", "DEP", "YLD", "TNR1", "DSCM", "ROLL"};
        String[] nameMapping = {"acctId", "id", "rateCode", "value"};

        csvWriter.writeHeader(csvHeader);

        for (RateCodesTemp rateCodesTempList : rateCodesTemps) {
            csvWriter.write(rateCodesTempList, nameMapping);
        }

        csvWriter.close();
    }

    @GetMapping("/exportResourceHashString")
    public void exportResourceHashString() {

        List<HRHead> hrHeads = hrHeadService.findAll();
        for (HRHead hrHead : hrHeads) {
            String encodedId = Utility.toBase64String(String.valueOf(hrHead.getName() + hrHead.getId()).getBytes());
            hrHead.setEncodedId(encodedId);
            hrHeadService.update(hrHead);
        }

    }

    @GetMapping("/test")
    public void test() {

        jobManagerTenantService.findByJobNameOrderByIdDesc(AppConstants.BILLING_BY_TYPE);

        Date newDate = DateUtils.addHours(new Date(), 3);
        LocalDateTime now = LocalDateTime.ofInstant(newDate.toInstant(), ZoneId.systemDefault());
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);


        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date());               // sets calendar time/date
        System.out.println("Before: " + cal.getTime());
        cal.add(Calendar.HOUR_OF_DAY, 1);      // adds one hour
        cal.getTime();                         // returns new date object plus one hour
        System.out.println("After: " + cal.getTime());
    }

    @PostMapping("/testEmail")
    public void testEmail(EmailTemplateDTO emailTemplateDTO) throws IOException {

        Personalization personalization = new Personalization();
        personalization.addTo(new Email(AppConstants.TESTNA_TOEMAIL));
        personalization.addDynamicTemplateData("template", emailTemplateDTO.getHTMLCode());
        Response response = emailService.emailDynamicTemplateWithNoFile(AppConstants.TEST_EMAIL_TEMPLATE, personalization);
        System.out.println(response.getStatusCode());
    }
}
