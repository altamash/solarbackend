package com.solar.api.tenant.service.solarAmps;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.model.WorkflowHookMaster;
import com.solar.api.saas.model.contact.Contacts;
import com.solar.api.saas.repository.workflow.WorkflowHookMasterRepository;
import com.solar.api.saas.service.ContactsService;
import com.solar.api.saas.service.EmailService;
import com.solar.api.tenant.mapper.stripe.SubmitInterestDTO;
import com.solar.api.tenant.mapper.workflows.IterationDTO;
import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billingInvoicing.InvoiceLog;
import com.solar.api.tenant.model.solarAmps.RequestADemoDTO;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.workflow.MessagePlaceholder;
import com.solar.api.tenant.model.workflow.MessageTemplate;
import com.solar.api.tenant.model.workflow.MessageTemplatePlaceholder;
import com.solar.api.tenant.model.workflow.WorkflowHookMap;
import com.solar.api.tenant.repository.workflow.MessagePlaceholderRepository;
import com.solar.api.tenant.repository.workflow.MessageTemplatePlaceholderRepository;
import com.solar.api.tenant.repository.workflow.WorkflowHookMapRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.DocumentSigningTemplateMapper.toDocumentSigningTemplate;
import static com.solar.api.tenant.mapper.DocumentSigningTemplateMapper.toDocumentSigningTemplateDTO;

@Service
public
class SolarAmpsServiceImpl implements SolarAmpsService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Value("${app.storage.blobService}")
    private String blobService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private WorkflowHookMasterRepository workFlowHookMasterRepository;
    @Autowired
    private WorkflowHookMapRepository workflowHookMapRepository;

    private static final SendGrid SEND_GRID_API = new SendGrid("SG.REh13aI_S_mQyla8RMhsJg" +
            ".Ze_IEMru-arzoxOiJbjrLyhbBHOH5LwU3iq6DSRQvxk");
    private static final Email NO_REPLY = new Email("no-reply@solarinformatics.com");

    @Autowired
    private MessagePlaceholderRepository messagePlaceholderRepository;
    @Autowired
    private MessageTemplatePlaceholderRepository messageTemplatePlaceholderRepository;

    @Autowired
    private ContactsService contactsService;


    @Override
    public ResponseEntity<Object> requestADemo(RequestADemoDTO requestADemoDTO) {
        Map<String,String> placeholderValues = new HashMap<>();
        try {
            WorkflowHookMaster workFlowHookMaster = workFlowHookMasterRepository.findByHookConstant(Constants.HOOK_CONSTANT.REQUEST_A_DEMO);
            if (workFlowHookMaster != null) {
                if (!StringUtils.hasText(requestADemoDTO.getFirstName()) && !StringUtils.hasText(requestADemoDTO.getLastName())
                        && !StringUtils.hasText(requestADemoDTO.getEmail()) && !StringUtils.hasText(requestADemoDTO.getCompanyName())
                        && !StringUtils.hasText(requestADemoDTO.getPreferredDate().toString()) && !StringUtils.hasText(requestADemoDTO.getDescribeYourBusiness())
                        && !Objects.nonNull(requestADemoDTO.getServicesYourCompanyProvider()) && !StringUtils.hasText(requestADemoDTO.getSizeOfCompOperation())) {
                    return new ResponseEntity<>("Please check all the mandatory fields.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                Long sourceId = Constants.MESSAGE_TEMPLATE.DEMO_SALES_TEAM_EMAIL_SOURCE_ID;
                List<String> CSVs =   contactsService.findBySourceId(sourceId).stream().map(Contacts::getEmail).collect(Collectors.toList());
                String servicesYourCP = String.join(",", requestADemoDTO.getServicesYourCompanyProvider());
                placeholderValues.put("first_name", requestADemoDTO.getFirstName());
                placeholderValues.put("last_name", requestADemoDTO.getLastName());
                placeholderValues.put("user_email", requestADemoDTO.getEmail());
                placeholderValues.put("company_name", requestADemoDTO.getCompanyName());
                placeholderValues.put("preferred_date", Utility.getDateString(requestADemoDTO.getPreferredDate(),Utility.SYSTEM_DATE_FORMAT));
                placeholderValues.put("describe_your_business", requestADemoDTO.getDescribeYourBusiness());
                placeholderValues.put("services_your_company_provider", servicesYourCP);
                placeholderValues.put("size_of_company_operations", requestADemoDTO.getSizeOfCompOperation());
                placeholderValues.put("about", requestADemoDTO.getAbout());
                placeholderValues.put("google_play", blobService + Constants.MESSAGE_TEMPLATE.GOOGLE_PLAY);
                placeholderValues.put("facebook", blobService + Constants.MESSAGE_TEMPLATE.FACEBOOK);
                placeholderValues.put("apple", blobService + Constants.MESSAGE_TEMPLATE.APPLE);
                placeholderValues.put("linkedin", blobService + Constants.MESSAGE_TEMPLATE.LINKEDIN);
                placeholderValues.put("youtube", blobService + Constants.MESSAGE_TEMPLATE.YOUTUBE);
                placeholderValues.put("twitter", blobService + Constants.MESSAGE_TEMPLATE.TWITTER);
                placeholderValues.put("solar_amps", blobService + Constants.MESSAGE_TEMPLATE.SOLAR_AMPS);

                String templateId = "d-0c5d9e95a589485ab72e65e7aff4fc48";
                String htmlCodeUser = getHTMLUserTemplate();
                String htmlCodeSales = getHTMLSalesTemplate();
                String htmlCodeUserMsg = getMessage(htmlCodeUser, placeholderValues );
                if(CSVs.size() > 0) {
                    sendEmail(placeholderValues, requestADemoDTO.getEmail() , null, null, "New Demo Request", null, templateId,htmlCodeUserMsg);
                    String htmlCodeSalesMsg = getMessage(htmlCodeSales, placeholderValues );
                    sendEmail(placeholderValues, CSVs,null , null, "New Demo Request",null , templateId, htmlCodeSalesMsg);
                }else{
                    return new ResponseEntity<>(APIResponse.builder().message("Email sent Failed - No Sales team recipient found")
                            .code(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(APIResponse.builder().message(e.getMessage())
                    .code(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);        }
        return new ResponseEntity<>(APIResponse.builder().message("Email sent successfully.")
                .code(HttpStatus.OK.value()).build(), HttpStatus.OK);    }

    @Override
    public ResponseEntity<Object> submitInterest(SubmitInterestDTO submitInterestDTO) {
        Map<String,String> placeholderValues = new HashMap<>();
        try {
            WorkflowHookMaster workFlowHookMaster = workFlowHookMasterRepository.findByHookConstant(Constants.HOOK_CONSTANT.SUBMIT_INTEREST);
            if (workFlowHookMaster != null) {
                Long sourceId = Constants.MESSAGE_TEMPLATE.INTEREST_SALES_TEAM_EMAIL_SOURCE_ID;
                List<String> CSVs =   contactsService.findBySourceId(sourceId).stream().map(Contacts::getEmail).collect(Collectors.toList());

                placeholderValues.put("company_name", submitInterestDTO.getCompanyName());
                placeholderValues.put("company_type", submitInterestDTO.getCompanyType());
                placeholderValues.put("phone_number", submitInterestDTO.getPhoneNumber());
                placeholderValues.put("GOOGLE_PLAY", blobService + Constants.MESSAGE_TEMPLATE.GOOGLE_PLAY);
                placeholderValues.put("FACEBOOK", blobService + Constants.MESSAGE_TEMPLATE.FACEBOOK);
                placeholderValues.put("APPLE", blobService + Constants.MESSAGE_TEMPLATE.APPLE);
                placeholderValues.put("LINKEDIN", blobService + Constants.MESSAGE_TEMPLATE.LINKEDIN);
                placeholderValues.put("YOUTUBE", blobService + Constants.MESSAGE_TEMPLATE.YOUTUBE);
                placeholderValues.put("TWITTER", blobService + Constants.MESSAGE_TEMPLATE.TWITTER);
                placeholderValues.put("SOLAR_AMPS", blobService + Constants.MESSAGE_TEMPLATE.SOLAR_AMPS);

                String templateId = "d-0c5d9e95a589485ab72e65e7aff4fc48";
                String htmlCodeUser = "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><title>Submit Interest Email Template</title><style type=\"text/css\">*{box-sizing: border-box;}body{margin: 0;background: #f5f5f5;color: #212121;}a,a:hover{text-decoration: none;}.template-wrapper { width: 600px; margin: 0 auto; text-align: center; font-family: sans-serif; background: #f5f5f5; color: #212121; padding-top: 50px;}table{width: 100%;}</style></head><body><div class=\"template-wrapper\"><table style=\"background: #fff;border-collapse: collapse;line-height: 22px;\"><tr><td style=\"padding: 22px 25px;background: #fff;\"><img src= https://devstoragesi.blob.core.windows.net/devpublic/template/email/new-logo.png></td></tr><tr><td style=\"padding: 20px 25px;background: #EEF5FF;\"><table style=\"text-align: left;\"><tr><td style=\"padding-bottom: 15px;\">Hi,</td></tr><!-- <tr><td style=\"font-size: 18px;color: #3F81EC;padding: 10px 0 40px 0;\">Thank you for registering to our platform!</td></tr> --><tr><td style=\"padding: 0 0 15px 0;\">A new request of interest has been received to SolarAMPs, please find below details.</td></tr><!-- <tr><td style=\"padding: 0 0 20px 0;\"><div style=\"max-width: 320px; text-align: left;margin: 0 auto;\"><span style=\"color: #3F81EC;\">Username:</span> cwilliams@emiratessolar.com<br><span style=\"color: #3F81EC;\">Password:</span> c45siw809!@ds</div></td></tr> --><!-- <tr><td style=\"padding: 0 0 50px 0;\"><a href=\"#\" style=\"padding: 8px 40px; font-size: 14px;background: #3F81EC;color: #fff;border-radius: 6px;\">Login</a></td></tr> --><!-- <tr><td style=\"padding: 0 0 15px 0;\">Details:</td></tr> --><tr><td style=\"padding: 0;font-size: 14px;\"><span style=\"color: #3F81EC;\">Company Name:</span> {{company_name}}</td></tr><tr><td style=\"padding: 0;font-size: 14px;\"><span style=\"color: #3F81EC;\">Company Type:</span> {{company_type}}</td></tr><tr><td style=\"padding: 0 0 25px 0;font-size: 14px;\"><span style=\"color: #3F81EC;\">Phone Number:</span> {{phone_number}}</td></tr></table></td></tr><tr><td style=\"padding: 20px 0 10px 0;font-size: 14px;\">Follow us at</td></tr><tr><td style=\"padding: 0 0 20px 0;\"><a href=\"https://www.youtube.com/@solarinformatics2246\" style=\"padding: 0 0 0 5px;\"><img src= https://devstoragesi.blob.core.windows.net/devpublic/template/email/youtube.svg></a><a href=\"https://www.linkedin.com/company/solar-informatics\" style=\"padding: 0 0 0 5px;\"><img src= https://devstoragesi.blob.core.windows.net/devpublic/template/email/linkedin.svg></a><a href=\"https://twitter.com/SolarInformatix\" style=\"padding: 0 0 0 5px;\"><img src= https://devstoragesi.blob.core.windows.net/devpublic/template/email/twitter.svg></a></td></tr><tr><td style=\"padding: 0 0 10px 0;font-size: 12px;color: #515151;\"><a href=\"www.solaramps.com\" target=\"_blank\" style=\"color: #515151;\">www.solaramps.com</a></td></tr><tr><td style=\"padding: 0 0 10px 0;font-size: 12px;color: #515151;\">&#169; 2023, SolarAMPs Inc. All rights reserved.</td></tr><tr><td style=\"padding: 0 0 10px 0;font-size: 12px;color: #515151;\"><a href=\"https://solaramps.com/#/privacy-policy\" target=\"_blank\" style=\"color: #515151;\">Privacy Policy</a> | <a href=\"https://solaramps.com/#/terms-of-use\" target=\"_blank\" style=\"color: #515151;\">Terms of Use</a></td></tr><tr><td style=\"padding: 0 0 30px 0;\"><a href=\"https://apps.apple.com/us/app/solaramps/id1623230082\" target=\"_blank\"><img src= https://devstoragesi.blob.core.windows.net/devpublic/template/email/apple-app-store.png height=\"40\"></a><a href=\"https://play.google.com/store/apps/details?id=com.solarinformatics.solaramps\" target=\"_blank\" style=\"color: #515151;\"><img src= https://devstoragesi.blob.core.windows.net/devpublic/template/email/google-app-store.png height=\"40\"></a></td></tr><tr><td style=\"padding: 0 0 10px 0;font-size: 12px;color: #515151;\">A product of <a href=\"https://solarinformatics.com/\" target=\"_blank\" style=\"color: #3F81EC;\">SolarInformatic</a>, Inc</td></tr></table></div></body></html>";
                String htmlCodeUserMsg = getMessage(htmlCodeUser, placeholderValues );
                if(CSVs.size()>0){
                sendEmail(placeholderValues, CSVs , null, null, "Submit Interest Request", null, templateId,htmlCodeUserMsg);
                }else{
                    return new ResponseEntity<>(APIResponse.builder().message("Email sent Failed - No Sales team recipient found")
                            .code(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(APIResponse.builder().message(e.getMessage())
                    .code(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(APIResponse.builder().message("Email sent successfully.")
                .code(HttpStatus.OK.value()).build(), HttpStatus.OK);
    }

    @Override
    public Response sendEmail(Map<String, String> placeholderValues, String toCSV, String ccCSV, String bccCSV, String subject, String[]
            subjectParams, String templateId, String htmlCode) {
        Mail mail = new Mail();
        mail.setFrom(NO_REPLY);
        mail.setTemplateId(templateId);
        Personalization personalization = new Personalization();
        personalization.addTo(new Email(toCSV));
        personalization.setSubject(subject);
        personalization.addDynamicTemplateData("subject", String.format(subject, subjectParams));
        personalization.addDynamicTemplateData("body", htmlCode);

        mail.addPersonalization(personalization);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        Response response = null;
        try {
            request.setBody(mail.build());
            response = SEND_GRID_API.api(request);
            LOGGER.info(response.getBody());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return response;
    }

    @Override
    public String getMessage(String templateHTMLCode, Map<String, String> placeholderValues) {
        for (Map.Entry<String, String> entry : placeholderValues.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            templateHTMLCode = templateHTMLCode.replaceAll("\\{\\{" + key + "\\}\\}", value);
        }
        return templateHTMLCode;
    }

    public Boolean emailValidation(String emailId) {
        String regex = "^[a-zA-Z0-9_!$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        //Compile regular expression to get the pattern
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(emailId.replaceAll(" ",""));
        if (!matcher.find()) {
            return true;
        }
        return false;
    }

    private String getHTMLSalesTemplate () {
        return "<html><head>\t<meta charset=\"utf-8\">\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\t<title>Request Demo Email Template</title>\t<style type=\"text/css\">\t\t*{\t\t\tbox-sizing: border-box;\t\t}\t\tbody{\t\t\tmargin: 0;\t\t\tbackground: #f5f5f5;\t\t\tcolor: #212121;\t\t}\t\ta,a:hover{\t\t\ttext-decoration: none;\t\t}\t\t.template-wrapper {\t\t    width: 600px;\t\t    margin: 0 auto;\t\t    text-align: center;\t\t    font-family: sans-serif;\t\t    background: #f5f5f5;\t\t    color: #212121;\t\t    padding-top: 50px;\t\t}\t\ttable{\t\t\twidth: 100%;\t\t}\t</style></head><body>\t<div class=\"template-wrapper\">\t\t<table style=\"background: #fff;border-collapse: collapse;line-height: 22px;\">\t\t\t<tr>\t\t\t\t<td style=\"padding: 22px 25px;background: #fff;\">\t\t\t\t\t<img src=\"{{solar_amps}}\">\t\t\t\t</td>\t\t\t</tr>\t\t\t<tr>\t\t\t\t<td style=\"padding: 20px 25px;background: #EEF5FF;\">\t\t\t\t\t<table style=\"text-align: left;\">\t\t\t\t\t\t\t\t\t\t\t<tr>\t\t\t\t\t\t\t<td style=\"padding-bottom: 15px;\">\t\t\t\t\t\t\t\tHi,\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr>\t\t\t\t\t\t<!-- <tr>\t\t\t\t\t\t\t<td style=\"font-size: 18px;color: #3F81EC;padding: 10px 0 40px 0;\">\t\t\t\t\t\t\t\tThank you for registering to our platform!\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr> -->\t\t\t\t\t\t<tr>\t\t\t\t\t\t\t<td style=\"padding: 0 0 15px 0;\">\t\t\t\t\t\t\t\tA new request of demo has been received to SolarAMPs, please find below details.\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr>\t\t\t\t\t\t<!-- <tr>\t\t\t\t\t\t\t<td style=\"padding: 0 0 20px 0;\">\t\t\t\t\t\t\t\t<div style=\"max-width: 320px; text-align: left;margin: 0 auto;\">\t\t\t\t\t\t\t\t\t<span style=\"color: #3F81EC;\"></span> <br>\t\t\t\t\t\t\t\t\t<span style=\"color: #3F81EC;\"></span>\t\t\t\t\t\t\t\t</div>\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr> -->\t\t\t\t\t\t<!-- <tr>\t\t\t\t\t\t\t<td  style=\"padding: 0 0 50px 0;\">\t\t\t\t\t\t\t\t<a href=\"#\" style=\"padding: 8px 40px; font-size: 14px;background: #3F81EC;color: #fff;border-radius: 6px;\">Login</a>\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr> -->\t\t\t\t\t\t<!-- <tr>\t\t\t\t\t\t\t<td style=\"padding: 0 0 15px 0;\">\t\t\t\t\t\t\t\tDetails:\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr> -->\t\t\t\t\t\t<tr>\t\t\t\t\t\t\t<td style=\"padding: 0;font-size: 14px;\">\t\t\t\t\t\t\t\t<span style=\"color: #3F81EC;\">First Name:</span>{{first_name}}\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr>\t\t\t\t\t\t<tr>\t\t\t\t\t\t\t<td style=\"padding: 0;font-size: 14px;\">\t\t\t\t\t\t\t\t<span style=\"color: #3F81EC;\">Last Name:</span>{{last_name}}\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr>\t\t\t\t\t\t<tr>\t\t\t\t\t\t\t<td style=\"padding: 0;font-size: 14px;\">\t\t\t\t\t\t\t\t<span style=\"color: #3F81EC;\">Company Name:</span>{{company_name}}\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr>\t\t\t\t\t\t<tr>\t\t\t\t\t\t\t<td style=\"padding: 0;font-size: 14px;\">\t\t\t\t\t\t\t\t<span style=\"color: #3F81EC;\">Your Email:</span>{{user_email}}\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr>\t\t\t\t\t\t<tr>\t\t\t\t\t\t\t<td style=\"padding: 0;font-size: 14px;\">\t\t\t\t\t\t\t\t<span style=\"color: #3F81EC;\">Services Your Company Provide:</span> {{services_your_company_provider}}\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr>\t\t\t\t\t\t<tr>\t\t\t\t\t\t\t<td style=\"padding: 0;font-size: 14px;\">\t\t\t\t\t\t\t\t<span style=\"color: #3F81EC;\">Describe your business:</span> {{describe_your_business}}\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr>\t\t\t\t\t\t<tr>\t\t\t\t\t\t\t<td style=\"padding: 0;font-size: 14px;\">\t\t\t\t\t\t\t\t<span style=\"color: #3F81EC;\">What is the size of the company's operations:</span> {{size_of_company_operations}}\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr>\t\t\t\t\t\t<tr>\t\t\t\t\t\t\t<td style=\"padding: 0;font-size: 14px;\">\t\t\t\t\t\t\t\t<span style=\"color: #3F81EC;\">Preferred Date:</span> {{preferred_date}}\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr>\t\t\t\t\t\t<tr>\t\t\t\t\t\t\t<td style=\"padding: 0 0 10px 0;font-size: 14px;\">\t\t\t\t\t\t\t\t<span style=\"color: #3F81EC;\">How did you find about the SolarAMPs:</span> {{about}}\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr>\t\t\t\t\t\t<tr>\t\t\t\t\t\t\t<td style=\"padding: 0 0 10px 0;font-size: 14px;\">\t\t\t\t\t\t\t\tThanks!\t\t\t\t\t\t\t</td>\t\t\t\t\t\t</tr>\t\t\t\t\t\t\t\t\t\t\t</table>\t\t\t\t</td>\t\t\t</tr>\t\t\t<tr>\t\t\t\t<td style=\"padding: 20px 0 10px 0;font-size: 14px;\">\t\t\t\t\tFollow us at\t\t\t\t</td>\t\t\t</tr>\t\t\t<tr>\t\t\t\t<td style=\"padding: 0 0 20px 0;\">\t\t\t\t\t<a href=\"https://www.youtube.com/@solarinformatics2246\" style=\"padding: 0 0 0 5px;\">\t\t\t\t\t\t<img src=\"{{youtube}}\">\t\t\t\t\t</a>\t\t\t\t\t<a href=\"https://www.linkedin.com/company/solar-informatics\" style=\"padding: 0 0 0 5px;\">\t\t\t\t\t\t<img src=\"{{linkedin}}\">\t\t\t\t\t</a>\t\t\t\t\t<a href=\"https://twitter.com/SolarInformatix\" style=\"padding: 0 0 0 5px;\">\t\t\t\t\t\t<img src=\"{{twitter}}\">\t\t\t\t\t</a>\t\t\t\t</td>\t\t\t</tr>\t\t\t<tr>\t\t\t\t<td style=\"padding: 0 0 10px 0;font-size: 12px;color: #515151;\">\t\t\t\t\t<a href=\"www.solaramps.com\" target=\"_blank\" style=\"color: #515151;\">www.solaramps.com</a>\t\t\t\t</td>\t\t\t</tr>\t\t\t<tr>\t\t\t\t<td style=\"padding: 0 0 10px 0;font-size: 12px;color: #515151;\">\t\t\t\t\t&#169; 2023, SolarAMPs Inc. All rights reserved.\t\t\t\t</td>\t\t\t</tr>\t\t\t<tr>\t\t\t\t<td style=\"padding: 0 0 10px 0;font-size: 12px;color: #515151;\">\t\t\t\t\t<a href=\"https://solaramps.com/#/privacy-policy\" target=\"_blank\" style=\"color: #515151;\">Privacy Policy</a> | \t\t\t\t\t<a href=\"https://solaramps.com/#/terms-of-use\" target=\"_blank\" style=\"color: #515151;\">Terms of Use</a>\t\t\t\t</td>\t\t\t</tr>\t\t\t<tr>\t\t\t\t<td style=\"padding: 0 0 30px 0;\">\t\t\t\t\t<a href=\"https://apps.apple.com/us/app/solaramps/id1623230082\" target=\"_blank\">\t\t\t\t\t\t<img src=\"{{apple}}\" height=\"40\">\t\t\t\t\t</a>\t\t\t\t\t<a href=\"{{google_play}}\" target=\"_blank\" style=\"color: #515151;\">\t\t\t\t\t\t<img src=\"{{google_play}}\" height=\"40\">\t\t\t\t\t</a>\t\t\t\t</td>\t\t\t</tr>\t\t\t<tr>\t\t\t\t<td style=\"padding: 0 0 10px 0;font-size: 12px;color: #515151;\">\t\t\t\t\tA product of <a href=\"https://solarinformatics.com/\" target=\"_blank\" style=\"color: #3F81EC;\">SolarInformatic</a>, Inc\t\t\t\t</td>\t\t\t</tr>\t\t</table>\t</div></body></html>";
    }

    private String getHTMLUserTemplate() {
        return "<html><head><title>Request for Demo Received</title></head><body><p>Dear <strong><em>{{last_name}}</em></strong>,</p><p>This is to intimate you that your request for demo has been received successfully. We will make sure that one of our representatives will contact you shortly to schedule the demo session at a time that works for you.</p> <p>Thank you for expressing interest in our product, SolarAMP.</p><p>Note: This is a system-generated email.</p></body></html>";
    }

    @Override
    public Response sendEmail(Map<String, String> placeholderValues, List<String> toCSVs, String ccCSV, String bccCSV, String subject, String[]
            subjectParams, String templateId, String htmlCode) {
        Mail mail = new Mail();
        mail.setFrom(NO_REPLY);
        mail.setTemplateId(templateId);

        Personalization personalization = new Personalization();
        for (String toCSV: toCSVs) {
            personalization.addTo(new Email(toCSV));
        }
        personalization.setSubject(subject);
        personalization.addDynamicTemplateData("subject", String.format(subject, subjectParams));
        personalization.addDynamicTemplateData("body", htmlCode);

        mail.addPersonalization(personalization);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        Response response = null;
        try {
            request.setBody(mail.build());
            response = SEND_GRID_API.api(request);
            LOGGER.info(response.getBody());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return response;
    }

}
