package com.solar.api.saas.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.solar.api.AppConstants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.tenant.model.EventLog;
import com.solar.api.tenant.service.EventLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("emailService")
public class EmailServiceImpl implements EmailService {

    @Autowired
    MasterTenantRepository masterTenantRepository;

    @Autowired
    EventLogService eventLogService;

    /**
     * SendGrid
     * API Key
     */
//    private static final SendGrid SEND_GRID_API = new SendGrid("");

    /**
     * SendGrid
     * Dynamic Email Template ID
     * Invoice
     * https://mc.sendgrid.com/dynamic-templates/d-546b922f63ab4bd7a577b11c1f1db691/version/9d457854-90e7-47a0-83d8
     * -6f9b52f3fcc9/editor/modules?moduleId=abc86ab7-a23f-4d85-87bd-9ad84d09c6e8.3.1
     */
    private static final String SOLAR_SUBSCRIPTION_INVOICE_TEMPLATE = "d-546b922f63ab4bd7a577b11c1f1db691";


    /**
     * Email Request
     * EndPoint
     */
    private static final String ENDPOINT = "mail/send";

    /**
     * From Email
     * CONSTANT
     */
    private static final Email FROM = new Email("no-reply@solarinformatics.com");

    @Value("${app.profile}")
    private String appProfile;

    @Override
    public Response batchNotification(String jobName, Long jobId, String stackTrace, String subject) throws IOException {

        Email toEmail = new Email(AppConstants.BATCH_EMAIL_NOTIFICATION);
        Content content = new Content("text/html", "Batch job Name: "
                + jobName +
                " \n Batch job Id: "
                + jobId +
                " \n StackTrace: "
                + stackTrace + " on " + appProfile);
        Mail mail = new Mail(FROM, subject, toEmail, content);
        if(appProfile.equals("newprod")) {
            Personalization personalization = new Personalization();
            personalization.addTo(new Email(AppConstants.BATCH_EMAIL_NOTIFICATION_TWO));
            mail.addPersonalization(personalization);
        }

        return catchLogs(emailRequest(mail));
    }

    @Override
    public Response sendEmail(Email toEmail, Content content, String subject) throws IOException {
        Mail mail = new Mail(FROM, subject, toEmail, content);
        return catchLogs(emailRequest(mail));
    }

    @Override
    public Response sendEmailWithDynamicTemplate(Email toEmail, String subject, File file, String fileName,
                                                 Personalization personalization) throws IOException {

        Attachments attachment = addAttachment(file, fileName, "application/pdf");

        Mail mail = new Mail();
        mail.setFrom(FROM);
        mail.setSubject(subject);
        mail.setTemplateId(personalization.getDynamicTemplateData().get("template").toString());
        mail.addPersonalization(personalization);
        mail.addAttachments(attachment);

        return catchLogs(emailRequest(mail));
    }

    @Override
    public Response sendEmailsWithDynamicTemplate(String template, File file, String fileName,
                                                  Personalization personalization) throws IOException {
        Attachments attachment = addAttachment(file, fileName, "application/pdf");
        Mail mail = new Mail();
        mail.setFrom(FROM);
        mail.setSubject(personalization.getSubject());
        mail.setTemplateId(template);
        mail.addPersonalization(personalization);
        mail.addAttachments(attachment);

        return catchLogs(emailRequest(mail));
    }

    @Override
    public Attachments addAttachment(File file, String fileName, String fileType) throws IOException {

        String convertedFile = Utility.convertFileToBase64(file);

        Attachments attachment = new Attachments();
        attachment.setContent(convertedFile);
        attachment.setFilename(fileName);
        attachment.setType(fileType);

        return attachment;
    }

    @Override
    public Response emailDynamicTemplateWithNoFile(String template, Personalization personalization) throws IOException {

        Mail mail = new Mail();
        mail.setFrom(FROM);
        mail.setSubject(personalization.getDynamicTemplateData().get("subject").toString());
        mail.setTemplateId(personalization.getDynamicTemplateData().get("template").toString());
        mail.addPersonalization(personalization);

        return catchLogs(emailRequest(mail));
    }

    @Override
    public Response monitoringApiNotification(String appProfile, Long csId, String inverterNumber, String loginName, String monitoringPlatform, String stackTrace)
            throws IOException {
        Personalization personalization = new Personalization();

        Email toEmail = new Email(AppConstants.MONITOR_API_TO_EMAIL_NOTIFICATION);
        Email CcEmail = new Email();
        CcEmail.setEmail(AppConstants.MONITOR_API_CC_EMAIL_NOTIFICATION);
        Email CcEmail2 = new Email();
        CcEmail2.setEmail(AppConstants.MONITOR_API_CC2_EMAIL_NOTIFICATION);
        Content content = new Content("text/html", "Environment:"
                + appProfile +
                "\nCustomerSubscription Id: "
                + csId +
                " \nInverterNumber: "
                + inverterNumber +
                "\nloginName:"
                + loginName +
                "\nmonitoringPlatform:"
                + monitoringPlatform +
                " \nStackTrace: "
                + stackTrace);

        personalization.addTo(toEmail);
        personalization.addCc(CcEmail);
        personalization.addCc(CcEmail2);

        Mail mail = new Mail();
        mail.setSubject("MONITORING API NOTIFICATION");
        mail.setFrom(FROM);
        mail.addContent(content);
        mail.addPersonalization(personalization);

        return catchLogs(emailRequest(mail));
    }

    @Override
    public Response monitoringApiNotification(String appProfile, String csId, String inverterNumber, String loginName, String monitoringPlatform, String stackTrace)
            throws IOException {
        Personalization personalization = new Personalization();

        Email toEmail = new Email(AppConstants.MONITOR_API_TO_EMAIL_NOTIFICATION);
        Email CcEmail = new Email();
        CcEmail.setEmail(AppConstants.MONITOR_API_CC_EMAIL_NOTIFICATION);
        Email CcEmail2 = new Email();
        CcEmail2.setEmail(AppConstants.MONITOR_API_CC2_EMAIL_NOTIFICATION);
        Content content = new Content("text/html", "Environment:"
                + appProfile +
                "\nCustomerSubscription Id: "
                + csId +
                " \nInverterNumber: "
                + inverterNumber +
                "\nloginName:"
                + loginName +
                "\nmonitoringPlatform:"
                + monitoringPlatform +
                " \nStackTrace: "
                + stackTrace);

        personalization.addTo(toEmail);
        personalization.addCc(CcEmail);
        personalization.addCc(CcEmail2);

        Mail mail = new Mail();
        mail.setSubject("MONITORING API NOTIFICATION");
        mail.setFrom(FROM);
        mail.addContent(content);
        mail.addPersonalization(personalization);

        return catchLogs(emailRequest(mail));
    }

    @Override
    public void addTo(String csv, Personalization personalization) {
        if (csv != null) {
            List<String> addresses =
                    Arrays.stream(csv.split(",")).map(String::trim).collect(Collectors.toList());
            for (String address : addresses) {
                personalization.addTo(new Email(address));
            }
        }
    }

    @Override
    public void addCC(String csv, Personalization personalization) {
        if (csv != null) {
            List<String> addresses =
                    Arrays.stream(csv.split(",")).map(String::trim).collect(Collectors.toList());
            for (String address : addresses) {
                personalization.addCc(new Email(address));
            }
        }
    }

    @Override
    public void addBCC(String csv, Personalization personalization) {
        if (csv != null) {
            List<String> addresses =
                    Arrays.stream(csv.split(",")).map(String::trim).collect(Collectors.toList());
            for (String address : addresses) {
                personalization.addBcc(new Email(address));
            }
        }
    }

    private Request emailRequest(Mail mail) throws IOException {
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint(ENDPOINT);
        request.setBody(mail.build());

        return request;
    }

    @Override
    public String getSendGridApiKey() {
        return masterTenantRepository.findByDbName(DBContextHolder.getTenantName()).getEmailApiKey();
    }

    private Response emailResponse(Request request) throws IOException {
        return new SendGrid(getSendGridApiKey()).api(request);
    }

    private Response catchLogs(Request request) throws IOException {

//        Response response = emailResponse(request);
        Response response = new Response();
        ObjectNode requestJson = new ObjectMapper().createObjectNode();
        requestJson.put("requestBody", request.getBody());
        requestJson.put("requestMethod", String.valueOf(request.getMethod()));
        requestJson.put("requestEndpoint", request.getEndpoint());

        ObjectNode responseJson = new ObjectMapper().createObjectNode();
        responseJson.put("responseBody", response.getBody().trim());
        responseJson.put("responseStatusCode", response.getStatusCode());
        responseJson.put("responseHeaders", String.valueOf(response.getHeaders()));

        EventLog eventLog = EventLog.builder()
                .dateTime(new Date())
                .eventType(AppConstants.EMAIL_EVENT)
                .request(requestJson.toPrettyString())
                .log(responseJson.toPrettyString())
                .status(String.valueOf(response.getStatusCode()))
                .error(String.valueOf(response.getStatusCode() != 202 ? response.getStatusCode() : null))
                .build();
        eventLogService.addOrUpdate(eventLog);
        return response;
    }
}