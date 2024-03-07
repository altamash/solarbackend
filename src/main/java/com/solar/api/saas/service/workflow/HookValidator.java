package com.solar.api.saas.service.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import com.solar.api.saas.repository.workflow.WorkflowHookMasterRepository;
import com.solar.api.saas.service.EmailService;
import com.solar.api.saas.service.workflow.json.RecipientJson;
import com.solar.api.saas.service.workflow.json.TypeBListJson;
import com.solar.api.tenant.mapper.billing.PowerMonitorPercentileDTO;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.mapper.workflows.IterationDTO;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.billing.EBillingAction;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.calculation.CalculationDetails;
import com.solar.api.tenant.model.billingInvoicing.InvoiceLog;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.user.EUserStatus;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.workflow.*;
import com.solar.api.tenant.repository.*;
import com.solar.api.tenant.repository.workflow.*;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.CalculationDetailsService;
import com.solar.api.tenant.service.InvoiceLogService;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import com.solar.api.tenant.service.preferences.EConfigParameter;
import com.solar.api.tenant.service.process.pvmonitor.ExtDataStageDefinitionService;
import com.solar.api.tenant.service.process.pvmonitor.MonitorWrapperService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Component
public class HookValidator {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
    @Autowired
    private WorkflowHookMasterRepository workFlowHookMasterRepository;
    @Autowired
    private WorkflowHookMapRepository workflowHookMapRepository;
    @Autowired
    private WorkflowRecipientListRepository workflowRecipientListRepository;
    @Autowired
    private WorkflowExecProcessRepository workflowExecProcessRepository;
    @Autowired
    private WorkflowGroupAssignmentRepository workflowGroupAssignmentRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkflowNotificationLogRepository workflowNotificationLogRepository;
    @Autowired
    private MessagePlaceholderRepository messagePlaceholderRepository;
    @Autowired
    private MessageTemplatePlaceholderRepository messageTemplatePlaceholderRepository;
    @Autowired
    private TenantConfigRepository tenantConfigRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private InvoiceLogService invoiceLogService;
    @Autowired
    private CalculationDetailsService calculationDetailsService;
    @Autowired
    MessageTemplateRepository messageTemplateRepository;
    @Autowired
    private BillingDetailRepository billingDetailRepository;
    @Autowired
    private PortalAttributeOverrideService attributeOverrideService;
    @Autowired
    private MonitorReadingDailyRepository monitorReadingDailyRepository;
    @Autowired
    private MonitorWrapperService monitorWrapperService;
    @Autowired
    private ExtDataStageDefinitionService extDataStageDefinitionService;
    private static final SendGrid SEND_GRID_API = new SendGrid("SG.REh13aI_S_mQyla8RMhsJg" +
            ".Ze_IEMru-arzoxOiJbjrLyhbBHOH5LwU3iq6DSRQvxk");
    private static final Email NO_REPLY = new Email("no-reply@solarinformatics.com");
    @Autowired
    private CustomerSubscriptionRepository customerSubscriptionRepository;

    /*Object hookFinder(String hookConstant)     {
        Object =  hookId, TypeA_enabled, TypeB_enabled (wf_hook_master)
        checkBMap(hookId, typeB)
    }*/
    public ObjectNode hookFinder(Long hookMapId, String hookConstant, Long senderId, Long dynamicRecipientId, Map<String, String> placeholderValues, BillingHead billingHead, String[]... subjectParams) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        Optional<WorkflowHookMap> workflowHookMapOptional = null;
        WorkflowHookMaster workFlowHookMaster = null;

        if (hookConstant != null) {
            workFlowHookMaster = workFlowHookMasterRepository.findByHookConstant(hookConstant);
        } else {
            workflowHookMapOptional = Optional.ofNullable(workflowHookMapRepository.findById(hookMapId).orElse(null));
            workFlowHookMaster = workFlowHookMasterRepository.findById(workflowHookMapOptional.get().getHookId()).orElse(null);
        }

        if (workFlowHookMaster != null) {

            List<WorkflowHookMap> workflowHookMapList = workflowHookMapRepository.findListByHookId(workFlowHookMaster.getId());

            if (hookConstant.equals(AppConstants.CSG_INVOICING)) {

                csgInvoicing(hookConstant, workflowHookMapList, placeholderValues, response, dynamicRecipientId, billingHead, subjectParams);
                return response;

            } else if (hookConstant.equals(Constants.PROJECTION_REVENUE.PROJECTION_REVENUE_HOOK)) {

                projectProjectionRevenue(hookConstant, workflowHookMapList, placeholderValues, response, dynamicRecipientId, billingHead, subjectParams);
                return response;

            } else if (hookConstant.equals(AppConstants.BILL_INVOICE_EMAIL)) {

//                String templateId = getMessageTemplate(workflowHookMapList, placeholderValues).getParentTmplId();
                String messageBody = invoiceLogService.findByBillId(billingHead.getId()).getMessage();
                placeholderValues.put("body", messageBody);
//                placeholderValues.put("template", templateId);
                //return response;

            }
            checkBMap(workflowHookMapOptional, workFlowHookMaster, senderId, dynamicRecipientId, placeholderValues, billingHead, subjectParams);
        }
        return null;
    }

    public String getParentIdByTemplateName(String hookConstant) {
        return messageTemplateRepository.findByMsgTmplName(hookConstant).map(MessageTemplate::getParentTmplId).orElse(null);
    }

    public MessageTemplate getTemplateByName(String hookConstant) {
        return messageTemplateRepository.findByMsgTmplName(hookConstant).orElse(null);
    }

    private ObjectNode csgInvoicing(String hookConstant, List<WorkflowHookMap> workflowHookMapList, Map<String, String> placeholderValues, ObjectNode response, Long dynamicRecipientId, BillingHead billingHead, String[]... subjectParams) {

//        MessageTemplate messageTemplate = getMessageTemplate(workflowHookMapList, placeholderValues);
        MessageTemplate messageTemplate = getMessageTemplateByMessageTempalateName(workflowHookMapList, placeholderValues);

        String templateHTMLCode = getIterations(placeholderValues, hookConstant, messageTemplate, billingHead);

        String emailMessage = getMessage(templateHTMLCode, placeholderValues, userRepository.findById(dynamicRecipientId).orElse(null));

        CalculationDetails calculationDetailsDB = calculationDetailsService.findBySourceId(billingHead.getId());
        calculationDetailsDB.setPrevInvHtmlView(emailMessage);
        calculationDetailsService.addOrUpdate(calculationDetailsDB);

//        invoiceLogService.save(InvoiceLog.builder()
//                .billId(billingHead.getId())
//                .invoiceId(billingHead.getInvoice() != null ? billingHead.getInvoice().getId() : null)
//                .invoiceStatus(billingHead.getInvoice() != null ? "INVOICED" : "DRAFT")
//                .message(emailMessage).build());
        response.put("message", "Invoice template has been generated Successfully");
        return response;
    }

    private MessageTemplate getMessageTemplate(List<WorkflowHookMap> workflowHookMapList, Map<String, String> placeholderValues) {
        return workflowHookMapList.stream().filter(wfhm ->
                placeholderValues.get("invoice_template").equalsIgnoreCase(wfhm.getEmailTemplate().getMsgTmplName())).findFirst().get().getEmailTemplate();
    }

    private MessageTemplate getMessageTemplateByMessageTempalateName(List<WorkflowHookMap> workflowHookMapList, Map<String, String> placeholderValues) {
        return workflowHookMapList.stream().filter(wfhm ->
                placeholderValues.get("invoice_template").equalsIgnoreCase(wfhm.getEmailTemplate().getMsgTmplName().replace("_", " "))).findFirst().get().getEmailTemplate();
    }

    private String getIterations(Map<String, String> placeholderValues, String hookConstant, MessageTemplate messageTemplate, BillingHead billingHead) {

        //TODO: Template Translator Microservice
        //******************************Iterations******************************
        //BillingHead billingHead = billingHeadService.findById(Long.valueOf(placeholderValues.get("bill_head_id")));
        List<MessageTemplatePlaceholder> messageTemplatePlaceholders = messageTemplatePlaceholderRepository.findByMsgTmpId(messageTemplate.getId());
        String templateHTMLCode = messageTemplate.getTemplateHTMLCode();
        return iterations(hookConstant, templateHTMLCode, billingHead, messageTemplatePlaceholders, messageTemplate);
        //******************************Iterations******************************
    }

    /*private checkBMap(hookId, typeB) {
          if typeB is enabled then {
              check in wf_hook_map wkf id if you have type B hook attached then initiatorWfProcess(wkf_id, hookId,
              list_id)
          }
    }*/
    private void checkBMap(Optional<WorkflowHookMap> workflowHookMapOptional, WorkflowHookMaster
            workFlowHookMaster, Long senderId, Long dynamicRecipientId,
                           Map<String, String> placeholderValues, BillingHead billingHead, String[]... subjectParams) {
        if (workFlowHookMaster.getTypeBEnabled()) {
            if (workflowHookMapOptional == null || !workflowHookMapOptional.isPresent()) {
                workflowHookMapOptional = workflowHookMapRepository.findByHookId(workFlowHookMaster.getId());
            }
            WorkflowHookMap workflowHookMap = workflowHookMapOptional.isPresent() ? workflowHookMapOptional.get() : null;
            WorkflowHead workflowHead = null;
            // check in wf_hook_map wf_id if you have type B hook attached then call initiatorWfProcess(wkf_id,
            // hookId, list_id)
            if (workflowHookMap != null) {
                workflowHead = workflowHookMap.getWorkflowHead() != null ? workflowHookMap.getWorkflowHead() : null;
                if (workflowHead.getHookType() != null && EHookType.B.name().equals(workflowHead.getHookType())) {
                    if (billingHead != null) {
                        InvoiceLog invoiceLog = getByBillId(billingHead);
                        if (invoiceLog != null) {
                            initializeWfProcess(workflowHead, workFlowHookMaster.getId(), workflowHookMap, workFlowHookMaster.getDynamicRecipient(), senderId, dynamicRecipientId,
                                    placeholderValues, invoiceLog, subjectParams);
                        }
                    } else {
                        initializeWfProcess(workflowHead, workFlowHookMaster.getId(), workflowHookMap, workFlowHookMaster.getDynamicRecipient(), senderId, dynamicRecipientId,
                                placeholderValues, null, subjectParams);
                    }
                }
            }
        }
    }

    private InvoiceLog getByBillId(BillingHead billingHead) {
        return invoiceLogService.findByBillId(billingHead.getId());
    }

    /*private void initializeWfProcess(workflowId, hookId, listId) {
        get typeB_list for emails (e)
            if either e or n records exist
               add entry in Wf_Exec_Process as status 'NEW'
        expandWorkflow(wf_exec_id)
    }*/
    private void initializeWfProcess(WorkflowHead workflowHead, Long hookId, WorkflowHookMap
            workflowHookMap, Boolean dynamicRecipient, Long senderId,
                                     Long dynamicRecipientId, Map<String, String> placeholderValues, InvoiceLog invoiceLog, String[]... subjectParams) {
        WorkflowRecipientList workflowRecipientList = workflowHookMap.getWorkflowRecipientList() != null ? workflowHookMap.getWorkflowRecipientList() : null;
        List<User> recipientList = getRecipientList(workflowRecipientList, dynamicRecipient);
        List<WorkflowNotificationLog> workflowNotificationLogs = new ArrayList<>();
        Optional<User> requester = userRepository.findById(senderId); //.orElseThrow(() -> new NotFoundException(User.class, senderId));
        WorkflowExecProcess workflowExecProcess = workflowExecProcessRepository.save(
                WorkflowExecProcess.builder()
                        .processId(hookId)
                        .workflowHead(workflowHead != null ? workflowHead : null)
                        .workflowRecipientList(workflowRecipientList != null ? workflowRecipientList : null)
                        .user(requester.isPresent() ? requester.get() : null) // submitter
                        .workflowType(EHookType.B.name())
                        .status(EWorkflowExecProcessStatus.NEW.name())
                        .build());
        String template = null;
        String recipientSubject = null;
        String recipientTemplateId = null;
        if (invoiceLog != null) {
            template = getFinalTemplateFromInvoiceLog(invoiceLog, true);
        } else {
            template = getFinalTemplate(workflowHookMap, true);
        }
        if (workflowHookMap.getEmailTemplate() != null) {
            //for publish parentId would be grid id
            recipientTemplateId = workflowHookMap.getEmailTemplate().getParentTmplId() != null ? workflowHookMap.getEmailTemplate().getParentTmplId() : null;
            recipientSubject = workflowHookMap.getEmailTemplate().getSubject() != null ? workflowHookMap.getEmailTemplate().getSubject() : null;
        }
        if (dynamicRecipient != null && dynamicRecipient) {
            User recipient = userRepository.findById(dynamicRecipientId).orElse(null);
            if (recipient != null) {
                String cc = null;
                String bcc = null;
                //not getting recipient list in-case of individual
                String recipientListCSV = recipientList != null ? StringUtils.stripToNull(String.join(", ", recipientList.stream().filter(r -> r.getEmailAddress() != null)
                        .map(r -> String.valueOf(r.getEmailAddress())).collect(Collectors.toList()))) : null;
                if (workflowHookMap.getListTarget() != null) {
                    cc = EListTarget.CC.name().equalsIgnoreCase(workflowHookMap.getListTarget()) ? recipientListCSV : null;
                    bcc = EListTarget.BCC.name().equalsIgnoreCase(workflowHookMap.getListTarget()) ? recipientListCSV : null;
                }
                addLogEntry(workflowNotificationLogs, workflowExecProcess, template, recipient, placeholderValues, null, cc, bcc, recipientSubject, invoiceLog, subjectParams[0], recipientTemplateId);
            }
        } else if (EListTarget.TO.name().equalsIgnoreCase(workflowHookMap.getListTarget())) {
            String recipientListCSV = StringUtils.stripToNull(String.join(", ", recipientList.stream().filter(r -> r.getEmailAddress() != null).map(r -> String.valueOf(r.getEmailAddress())).collect(Collectors.toList())));
            addLogEntry(workflowNotificationLogs, workflowExecProcess, template, null, placeholderValues, recipientListCSV, null, null, recipientSubject, null, subjectParams[0], recipientTemplateId);
        } else if (EListTarget.IND.name().equalsIgnoreCase(workflowHookMap.getListTarget()) || StringUtils.isEmpty(workflowHookMap.getListTarget())) {
            for (User recipient : recipientList) {
                addLogEntry(workflowNotificationLogs, workflowExecProcess, template, recipient, placeholderValues, null, null, null, recipientSubject, null, subjectParams[0], recipientTemplateId);
            }
        }
        //MessageTemplate requesterEmailTemplate = workflowHookMap.getRequesterEmailTemplate();
        if (workflowHookMap.getRequesterEmailTemplate() != null) {
            String subject = workflowHookMap.getRequesterEmailTemplate().getSubject() != null ? workflowHookMap.getRequesterEmailTemplate().getSubject() : null;
            String parentTmpId = workflowHookMap.getRequesterEmailTemplate().getParentTmplId() != null ? workflowHookMap.getRequesterEmailTemplate().getParentTmplId() : null;
            addLogEntry(workflowNotificationLogs, workflowExecProcess, getFinalTemplate(workflowHookMap, false), requester.isPresent() ? requester.get() : null, placeholderValues, null, null, null, subject, null, subjectParams[1], parentTmpId);
        }
        workflowNotificationLogRepository.saveAll(workflowNotificationLogs);
    }

    private List<User> getRecipientList(WorkflowRecipientList workflowRecipientList, Boolean dynamicRecipient) {
        List<User> recipientList = new ArrayList<>();
        if (workflowRecipientList != null) {
            String typeBList = workflowRecipientList.getTypeBList();
            if (typeBList == null && dynamicRecipient != null && !dynamicRecipient) {
                LOGGER.error("typeBList not found for list id {}", workflowRecipientList.getId());
            }
            RecipientJson recipientJson = getRecipientJson(typeBList);
            // if either e or n records exist add entry in Wf_Exec_Process as status 'NEW'
            if (!ListUtils.emptyIfNull(recipientJson.getE()).isEmpty()) {
                // Expand workflow and add in workflow_notification_log
                recipientList.addAll(expandWorkflow(workflowRecipientList));
            }
        }
        return recipientList;
    }

    private String getFinalTemplate(WorkflowHookMap workflowHookMap, boolean recipient) {
        if (workflowHookMap.getEmailTemplate() == null) {
            return null;
        }
        if (workflowHookMap.getRequesterEmailTemplate() == null) {
            return null;
        }
        StringBuilder finalTemplate = new StringBuilder();
        MessageTemplate messageTemplate;
        if (recipient) {
            messageTemplate = workflowHookMap.getEmailTemplate();
        } else {
            messageTemplate = workflowHookMap.getRequesterEmailTemplate();
        }
        if (messageTemplate.getHeader() != null) {
            finalTemplate.append(messageTemplate.getHeader());
        } else {
            Optional<TenantConfig> header = tenantConfigRepository.findByParameter(EConfigParameter.EMAIL_HEADER.getName());
            header.ifPresent(h -> {
                if (h.getText() != null) {
                    finalTemplate.append(h.getText());
                }
            });
        }
        finalTemplate.append(messageTemplate.getTemplateHTMLCode());
        if (messageTemplate.getFooter() != null) {
            finalTemplate.append(messageTemplate.getFooter());
        } else {
            Optional<TenantConfig> footer = tenantConfigRepository.findByParameter(EConfigParameter.EMAIL_FOOTER.getName());
            footer.ifPresent(f -> {
                if (f.getText() != null) {
                    finalTemplate.append(f.getText());
                }
            });
        }
        return finalTemplate.toString();
    }

    private String getFinalTemplateFromInvoiceLog(InvoiceLog invoiceLog, boolean recipient) {
        StringBuilder finalTemplate = new StringBuilder();
        if (recipient) {
            finalTemplate.append(invoiceLog.getMessage());
        }
        Optional<TenantConfig> header = tenantConfigRepository.findByParameter(EConfigParameter.EMAIL_HEADER.getName());
        header.ifPresent(h -> {
            if (h.getText() != null) {
                finalTemplate.append(h.getText());
            }
        });

        Optional<TenantConfig> footer = tenantConfigRepository.findByParameter(EConfigParameter.EMAIL_FOOTER.getName());
        footer.ifPresent(f -> {
            if (f.getText() != null) {
                finalTemplate.append(f.getText());
            }
        });
        return finalTemplate.toString();
    }

    private void addLogEntry(List<WorkflowNotificationLog> workflowNotificationLogs,
                             WorkflowExecProcess workflowExecProcess, String templateHTMLCode, User recipient,
                             Map<String, String> placeholderValues, String toCSV, String ccCSV, String bccCSV, String
                                     subject, InvoiceLog invoiceLog, String[] subjectParams,
                             String templateId) {
        //String message = getMessage(templateHTMLCode, placeholderValues, recipient);
        workflowNotificationLogs.add(
                WorkflowNotificationLog.builder()
                        .workflowExecProcess(workflowExecProcess)
                        .recipient(recipient)
                        .destInfo(recipient != null ? recipient.getEmailAddress() : null)
                        .destType(EWorkflowDestType.EMAIL.name())
                        .commType("e")
//                        .message(getMessage(templateHTMLCode, placeholderValues, recipient))
                        .toCSV(toCSV)
                        .ccCSV(ccCSV)
                        .bccCSV(bccCSV)
                        .status(EWorkflowNotificationLog.PENDING.name())
                        .build());
        sendEmail(recipient, toCSV, ccCSV, bccCSV, subject, subjectParams, templateId, getPlaceholderValues(templateHTMLCode, placeholderValues, recipient), invoiceLog);
    }

    private void sendEmail(User recipient, String toCSV, String ccCSV, String bccCSV, String subject, String[]
            subjectParams, String templateId, Map<String, String> placeholderValues, InvoiceLog invoiceLog) {
        Mail mail = new Mail();
        mail.setFrom(NO_REPLY);
//        mail.setSubject(String.format(subject, subjectParams));
        mail.setTemplateId(templateId);
        Personalization personalization = new Personalization();
        // Dynamic template fix https://github.com/sendgrid/sendgrid-nodejs/issues/713#issuecomment-443206995
        personalization.addDynamicTemplateData("subject", String.format(subject, subjectParams));
        personalization.addDynamicTemplateData("body", invoiceLog.getMessage());
        if (recipient != null) {
            personalization.addTo(new Email(recipient.getEmailAddress()));
        } else {
            emailService.addTo(toCSV, personalization);
        }
        emailService.addCC(ccCSV, personalization);
        emailService.addBCC(bccCSV, personalization);
        placeholderValues.entrySet().forEach(entry -> personalization.addDynamicTemplateData(entry.getKey(), entry.getValue()));
        mail.addPersonalization(personalization);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        try {
            request.setBody(mail.build());
            Response response = SEND_GRID_API.api(request);
            CalculationDetails calculationDetails = calculationDetailsService.findBySourceId(invoiceLog.getBillId());
            if (calculationDetails != null) {
                calculationDetails.setState(EBillingAction.PUBLISH.getAction());
                calculationDetails.setPublishState("Completed");
                calculationDetailsService.addOrUpdate(calculationDetails);
            }
            LOGGER.info(response.getBody());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private String getMessage(String templateHTMLCode, Map<String, String> placeholderValues, User recipient) {
        if (recipient != null) {
            placeholderValues.put("first_name", recipient.getFirstName());
            placeholderValues.put("last_name", recipient.getLastName());
        }
        List<String> placeholders = Utility.findBetween(templateHTMLCode, "\\{\\{", "\\}\\}");
        List<MessagePlaceholder> messagePlaceholders = messagePlaceholderRepository.findByPlaceholderNameIn(placeholders);
        // Find mandatory
//        messagePlaceholders.stream()
//                .filter(mp -> !placeholderValues.keySet().contains(mp.getPlaceholderName()) && mp.getOptional() != null && !mp.getOptional())
//                .collect(Collectors.toSet())
//                .forEach(mp -> LOGGER.error("Mandatory placeholder " + mp.getPlaceholderName() + " not provided"));
//        // Set defaults when not provided
//        messagePlaceholders.stream()
//                .filter(mp -> {
//                    MessageTemplatePlaceholder messageTemplatePlaceholder = messageTemplatePlaceholderRepository.findByMessagePlaceholder(mp);
//                    return !placeholderValues.keySet().contains(mp.getPlaceholderName()) && messageTemplatePlaceholder != null;
//                })
//                .collect(Collectors.toSet())
//                .forEach(mp -> {
//                    MessageTemplatePlaceholder messageTemplatePlaceholder = messageTemplatePlaceholderRepository.findByMessagePlaceholder(mp);
//                    placeholderValues.put(mp.getPlaceholderName(), messageTemplatePlaceholder.getDefaultMsgText());
//                });
        // Set placeholder values
        for (Map.Entry<String, String> entry : placeholderValues.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            templateHTMLCode = templateHTMLCode.replaceAll("\\{\\{" + key + "\\}\\}", value);
        }
        return templateHTMLCode;
    }

    private String iterations(String type, String templateHTMLCode, BillingHead
            billingHead, List<MessageTemplatePlaceholder> messageTemplatePlaceholders, MessageTemplate messageTemplate) {

        /**
         * Getting iteration Number
         * Getting number of iterationTemplates
         * Binding data on basis of workFlow type
         */
        Map<String, List<String>> iterationTemplatesLists = new HashMap<>();
        for (int i = 0; i < messageTemplatePlaceholders.size(); i++) {
            //Getting all iteration templates from messageTemplate.getTemplateHTMLCode()
            if (iterationTemplatesLists.get(String.valueOf(messageTemplatePlaceholders.get(i).getIterationNum())) == null) {
                iterationTemplatesLists.put(String.valueOf(messageTemplatePlaceholders.get(i).getIterationNum()),
                        Utility.findBetween(templateHTMLCode, "\\[\\[" + messageTemplatePlaceholders.get(i).getIterationNum() + ":", "\\]\\]"));
            }
        }
        /**
         * Condition for respective workflows
         */
        List<IterationDTO> iterationDTOList = new ArrayList<>();
        final Map<String, List<IterationDTO>> iterationsMapping = new HashMap<>();
        iterationTemplatesLists.forEach((key, value) -> {
            if (key.equals("1")) {
                if (type.equals("csg_invoicing")) {
                    iterationsMapping.put("1", mapBillingDetails(key, value, iterationDTOList, billingHead, messageTemplatePlaceholders));
                } else if (type.equals("projection_revenue")) {
                    iterationsMapping.put("1", projectProjectionRevenueDetails(key, value, iterationDTOList, billingHead, messageTemplatePlaceholders));
                }
            } else if (key.equals("2")) {
                iterationsMapping.put("2", mapMonthlyProduction(key, value, iterationDTOList, billingHead, messageTemplatePlaceholders));
            } else if (key.equals("3")) {
                iterationsMapping.put("3", mapBillingHistory(key, value, iterationDTOList, billingHead, messageTemplatePlaceholders,
                        7, 12));
            } else if (key.equals("4")) {
                iterationsMapping.put("4", mapBillingHistory(key, value, iterationDTOList, billingHead, messageTemplatePlaceholders,
                        1, 6));
            }
        });
        if (type.equals("csg_invoicing")) {
            final String[] temp = new String[9];
            final String[] HTMLTemplate = {""};
            iterationsMapping.forEach((key, value) -> {
                temp[1] = "";
                if (key.equals("1")) {
                    value.forEach(iterationDTO -> {
                        if (iterationDTO.getIterationId() == 1) {
                            temp[4] = iterationDTO.getBilling_code_ph();
                            temp[5] = iterationDTO.getBilling_code_value_ph();
                            if (AppConstants.MessageTemplate.INVOICE_TEMPLATE_MPA.equalsIgnoreCase(messageTemplate.getMsgTmplName())) {
                                temp[3] = AppConstants.ITE_BILLING_DETAILS_1;
                                iterationDTO.setIterationTemplate(AppConstants.ITE_BILLING_DETAILS_1
                                        .replaceAll("\\{\\{" + iterationDTO.getBilling_code_ph() + "\\}\\}",
                                                Matcher.quoteReplacement(iterationDTO.getBilling_code())));
                            } else {
                                temp[3] = iterationDTO.getIterationTemplate();
                                iterationDTO.setIterationTemplate(iterationDTO.getIterationTemplate().replaceAll("\\{\\{" + iterationDTO.getBilling_code_ph() + "\\}\\}",
                                        Matcher.quoteReplacement(iterationDTO.getBilling_code())));
                            }

                            temp[0] = iterationDTO.getIterationTemplate().replaceAll("\\{\\{" + iterationDTO.getBilling_code_value_ph() + "\\}\\}",
                                    "\\$" + iterationDTO.getBilling_code_value());
                            temp[1] = temp[0] + temp[1];
                            temp[2] = String.valueOf(iterationDTO.getIterationId());
                        }
                    });
//                    HTMLTemplate[0].replaceAll("\\[\\[" + temp[2] + ":\\]\\]", temp[1].replaceAll("S","\\$\\"));
                    HTMLTemplate[0] = replaceTemplate(temp[1], temp[2], temp[3], temp[4], temp[5], templateHTMLCode);
                } else if (key.equals("2")) {
                    value.forEach(iterationDTO -> {
                        if (iterationDTO.getIterationId() == 2) {
                            temp[3] = iterationDTO.getIterationTemplate();
                            temp[4] = iterationDTO.getYield_ph();
                            temp[5] = iterationDTO.getPercentile_ph();
                            temp[8] = iterationDTO.getProd_det_date_ph();
                            iterationDTO.setIterationTemplate(AppConstants.ITE_MONTHLY_PRODUCTION_2
                                    .replaceAll("\\{\\{" + iterationDTO.getPercentile_ph() + "\\}\\}",
                                            Matcher.quoteReplacement(iterationDTO.getPercentile())));
                            temp[7] = iterationDTO.getIterationTemplate().replaceAll("\\{\\{" + iterationDTO.getYield_ph() + "\\}\\}",
                                    iterationDTO.getYield());
                            temp[0] = temp[7].replaceAll("\\{\\{" + iterationDTO.getProd_det_date_ph() + "\\}\\}",
                                    iterationDTO.getProd_det_date());
                            temp[1] = temp[0] + temp[1];
                            temp[2] = String.valueOf(iterationDTO.getIterationId());
                        }
                    });
                    HTMLTemplate[0] = HTMLTemplate[0].replaceAll("\\[\\[" + temp[2] + ":\\]\\]", temp[1]);
//                    temp[3] = temp[3].replaceAll("\\{\\{" + temp[8] + "\\}\\}", "");
//                    HTMLTemplate[0] = replaceTemplate(temp[1], temp[2], temp[3], temp[4], temp[5], HTMLTemplate[0]);
                } else if (key.equals("3")) {
                    value.forEach(iterationDTO -> {
                        if (iterationDTO.getIterationId() == 3) {
                            temp[3] = iterationDTO.getIterationTemplate();
                            temp[4] = iterationDTO.getHistory_billing_month_ph();
                            temp[5] = iterationDTO.getHistory_billing_value_ph();
                            iterationDTO.setIterationTemplate(AppConstants.ITE_BILLING_HISTORY_3
                                    .replaceAll("\\{\\{" + iterationDTO.getHistory_billing_month_ph() + "\\}\\}",
                                            Matcher.quoteReplacement(iterationDTO.getHistory_billing_month())));
                            temp[0] = iterationDTO.getIterationTemplate().replaceAll("\\{\\{" + iterationDTO.getHistory_billing_value_ph() + "\\}\\}",
                                    "\\$" + iterationDTO.getHistory_billing_value());
                            temp[1] = temp[0] + temp[1];
                            temp[2] = String.valueOf(iterationDTO.getIterationId());
                        }
                    });
                    String escapedTemp1 = temp[1].replace("$", "$"); // replace dollar sign with itself
                    HTMLTemplate[0] = HTMLTemplate[0].replace("[[" + temp[2] + ":]]", escapedTemp1);

//                    HTMLTemplate[0] = replaceTemplate(temp[1], temp[2], temp[3], temp[4], temp[5], HTMLTemplate[0]);
                } else if (key.equals("4")) {
                    value.forEach(iterationDTO -> {
                        if (iterationDTO.getIterationId() == 4) {
                            temp[3] = iterationDTO.getIterationTemplate();
                            temp[4] = iterationDTO.getHistory_billing_month_ph();
                            temp[5] = iterationDTO.getHistory_billing_value_ph();
                            iterationDTO.setIterationTemplate(AppConstants.ITE_BILLING_HISTORY_4
                                    .replaceAll("\\{\\{" + iterationDTO.getHistory_billing_month_ph() + "\\}\\}",
                                            Matcher.quoteReplacement(iterationDTO.getHistory_billing_month())));
                            temp[0] = iterationDTO.getIterationTemplate().replaceAll("\\{\\{" + iterationDTO.getHistory_billing_value_ph() + "\\}\\}",
                                    "\\$" + iterationDTO.getHistory_billing_value());
                            temp[1] = temp[0] + temp[1];
                            temp[2] = String.valueOf(iterationDTO.getIterationId());
                        }
                    });
                    String escapedTemp1 = temp[1].replace("$", "$"); // replace dollar sign with itself
                    HTMLTemplate[0] = HTMLTemplate[0].replace("[[" + temp[2] + ":]]", escapedTemp1);

//                    HTMLTemplate[0] = replaceTemplate(temp[1], temp[2], temp[3], temp[4], temp[5], HTMLTemplate[0]);
                }
            });
            for (String s : iterationsMapping.keySet()) {
                HTMLTemplate[0] = HTMLTemplate[0].replaceAll("\\[\\[" + s + ":\\]\\]", "");
            }
            return HTMLTemplate[0];
        }
        if (type.equals("projection_revenue")) {
            final String[] temp = new String[9];
            final String[] HTMLTemplate = {""};
            iterationsMapping.forEach((key, value) -> {
                temp[1] = "";
                if (key.equals("1")) {
                    value.forEach(iterationDTO -> {
                        if (iterationDTO.getIterationId() == 1) {
                            temp[4] = iterationDTO.getEfficiency_ph();
                            temp[5] = iterationDTO.getAmount_1_ph();
                            temp[6] = iterationDTO.getAmount_2_ph();
                            temp[7] = iterationDTO.getAmount_3_ph();
                            temp[3] = AppConstants.ITE_PROJECT_PROJECTION_DETAILS_1;
                            iterationDTO.setIterationTemplate(AppConstants.ITE_PROJECT_PROJECTION_DETAILS_1
                                    .replaceAll("\\{\\{" + iterationDTO.getEfficiency_ph() + "\\}\\}",
                                            Matcher.quoteReplacement(iterationDTO.getEfficiency_value())));


                            temp[0] = iterationDTO.getIterationTemplate().replaceAll("\\{\\{" + iterationDTO.getAmount_1_ph() + "\\}\\}",
                                    "\\$" + iterationDTO.getAmount_1_value());
                            temp[0] = temp[0].replaceAll("\\{\\{" + iterationDTO.getAmount_2_ph() + "\\}\\}",
                                    "\\$" + iterationDTO.getAmount_2_value());
                            temp[0] = temp[0].replaceAll("\\{\\{" + iterationDTO.getAmount_3_ph() + "\\}\\}",
                                    "\\$" + iterationDTO.getAmount_3_value());
                            temp[1] = temp[0] + temp[1];
                            temp[2] = String.valueOf(iterationDTO.getIterationId());
                        }
                    });
//                    HTMLTemplate[0].replaceAll("\\[\\[" + temp[2] + ":\\]\\]", temp[1].replaceAll("S","\\$\\"));
                    HTMLTemplate[0] = replaceTemplate(temp[1], temp[2], temp[3], temp[4], temp[5], temp[6], temp[7], templateHTMLCode);
                }
            });
            for (String s : iterationsMapping.keySet()) {
                HTMLTemplate[0] = HTMLTemplate[0].replaceAll("\\[\\[" + s + ":\\]\\]", "");
            }
            return HTMLTemplate[0];
        }
        return null;
    }

    private String replaceTemplate(String s1, String s2, String s3, String s4, String s5, String templateHTMLCode) {
        s3 = s3.replaceAll("\\{\\{" + s4 + "\\}\\}", "").replaceAll("\\{\\{" + s5 + "\\}\\}", "");
        return templateHTMLCode.replaceAll("\\{\\{" + s4 + "\\}\\}", "")
                .replaceAll("\\{\\{" + s5 + "\\}\\}", "")
                .replaceAll("\\[\\[" + s2 + ":" + s3 + "\\]\\]", Matcher.quoteReplacement(s1));
    }

    private String replaceTemplate(String s1, String s2, String s3, String s4, String s5, String s6, String s7, String templateHTMLCode) {
        s3 = s3.replaceAll("\\{\\{" + s4 + "\\}\\}", "").replaceAll("\\{\\{" + s5 + "\\}\\}", "").replaceAll("\\{\\{" + s6 + "\\}\\}", "").replaceAll("\\{\\{" + s7 + "\\}\\}", "");
        return templateHTMLCode.replaceAll("\\{\\{" + s4 + "\\}\\}", "")
                .replaceAll("\\{\\{" + s5 + "\\}\\}", "").replaceAll("\\{\\{" + s6 + "\\}\\}", "").replaceAll("\\{\\{" + s7 + "\\}\\}", "")
                .replaceAll("\\[\\[" + s2 + ":" + s3 + "\\]\\]", Matcher.quoteReplacement(s1));
    }

    private List<IterationDTO> mapBillingHistory(String key, List<String> value, List<IterationDTO> iterationDTOList,
                                                 BillingHead billingHead, List<MessageTemplatePlaceholder> messageTemplatePlaceholders,
                                                 int iterationStart, int iterationEnd) {
//        Map<String, List<IterationDTO>> iterationsMapping = new HashMap<>();

        String historyBillingMonth = messageTemplatePlaceholders.stream().filter(messageTemplatePlaceholder ->
                        messageTemplatePlaceholder.getMessagePlaceholder().getPlaceholderName().equals(AppConstants.HISTORY_BILLING_MONTH))
                .findFirst().get().getMessagePlaceholder().getPlaceholderName();

        String historyBillingValue = messageTemplatePlaceholders.stream().filter(messageTemplatePlaceholder ->
                        messageTemplatePlaceholder.getMessagePlaceholder().getPlaceholderName().equals(AppConstants.HISTORY_BILLING_VALUE))
                .findFirst().get().getMessagePlaceholder().getPlaceholderName();
        List<String> monthYears = getMonthYears(billingHead.getBillingMonthYear(), iterationStart, iterationEnd);
        List<BillingHead> billingHeads = billingHeadService.findLastTwelveMonths(monthYears, billingHead.getSubscriptionId());
        for (BillingHead bh : billingHeads) {
            iterationDTOList.add(IterationDTO.builder()
                    .iterationId(Long.valueOf(key))
                    .iterationTemplate(value.get(0))
                    .history_billing_month_ph(historyBillingMonth)
                    .history_billing_value_ph(historyBillingValue)
                    .history_billing_month(Utility.convertToShortMonthYearFormat(bh.getBillingMonthYear()))
                    .history_billing_value(bh.getAmount() != null ? decimalFormat.format(bh.getAmount()) : "0").build());
        }
//                }
//        iterationsMapping.put(String.valueOf(key), iterationDTOList);
        return iterationDTOList;
    }

    private static List<String> getMonthYears(String billingMonthYear, int iterationStart, int iterationEnd) {
        SimpleDateFormat format = new SimpleDateFormat(Utility.MONTH_YEAR_FORMAT);

        Date date;
        try {
            date = format.parse(billingMonthYear);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected MM-yyyy");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Subtract the starting iteration from the month before the loop starts
        calendar.add(Calendar.MONTH, -iterationStart);

        List<String> monthYears = new ArrayList<>();
        for (int i = iterationStart; i <= iterationEnd; i++) {
            monthYears.add(format.format(calendar.getTime()));
            calendar.add(Calendar.MONTH, -1);
        }

        return monthYears;
    }

    private List<IterationDTO> mapMonthlyProduction(String key, List<String> value, List<IterationDTO> iterationDTOList, BillingHead billingHead, List<MessageTemplatePlaceholder> messageTemplatePlaceholders) {
        String percentile = messageTemplatePlaceholders.stream().filter(messageTemplatePlaceholder ->
                        messageTemplatePlaceholder.getMessagePlaceholder().getPlaceholderName().equals(AppConstants.PERCENTILE))
                .findFirst().get().getMessagePlaceholder().getPlaceholderName();

        String yield = messageTemplatePlaceholders.stream().filter(messageTemplatePlaceholder ->
                        messageTemplatePlaceholder.getMessagePlaceholder().getPlaceholderName().equals(AppConstants.YIELD))
                .findFirst().get().getMessagePlaceholder().getPlaceholderName();


        String prodDetDate = messageTemplatePlaceholders.stream().filter(messageTemplatePlaceholder ->
                        messageTemplatePlaceholder.getMessagePlaceholder().getPlaceholderName().equals(AppConstants.PROD_DET_DATE))
                .findFirst().get().getMessagePlaceholder().getPlaceholderName();
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM-yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(billingHead.getBillingMonthYear(), inputFormatter);
        String month = outputFormatter.format(yearMonth);
        CustomerSubscription customerSubscription = billingHeadService.findCustomerSubscriptionBySubscriptionId(billingHead.getSubscriptionId());
        List<PowerMonitorPercentileDTO> powerMonitorPercentileDTOS = monitorWrapperService.generatePercentileByMonthAndSub(month, customerSubscription.getExtSubsId());
        for (PowerMonitorPercentileDTO pmpDTO : powerMonitorPercentileDTOS) {
            iterationDTOList.add(IterationDTO.builder()
                    .iterationId(Long.valueOf(key))
                    .iterationTemplate(value.get(0))
                    .percentile_ph(percentile)
                    .yield_ph(yield)
                    .prod_det_date_ph(prodDetDate)
                    .percentile(String.valueOf(pmpDTO.getPercentile()))
                    .yield(decimalFormat.format(pmpDTO.getYield()))
                    .prod_det_date(String.valueOf(pmpDTO.getDay().getDate()))
                    .build());
        }
        return iterationDTOList;
    }

    private List<Date> getDaysInMonths(String days) {
        Date date = Utility.getDate(days, Utility.MONTH_YEAR_FORMAT);
        List<Date> monthYears = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        monthYears.add(Utility.getStartOfDate(date));
        Date daysInMonths = Utility.getEndOfMonth(date, Utility.SYSTEM_DATE_FORMAT);
        for (int i = 1; i < daysInMonths.getDate(); i++) {
            date = Utility.addDays(date, 1);
            monthYears.add(date);
        }
        return monthYears;
    }

    private List<IterationDTO> mapBillingDetails(String key, List<String> value, List<IterationDTO> iterationDTOList, BillingHead billingHead, List<MessageTemplatePlaceholder> messageTemplatePlaceholders) {
//        Map<String, List<IterationDTO>> iterationsMapping = new HashMap<>();

        String billingCode = messageTemplatePlaceholders.stream().filter(messageTemplatePlaceholder ->
                        messageTemplatePlaceholder.getMessagePlaceholder().getPlaceholderName().equals(AppConstants.BILLING_CODE))
                .findFirst().get().getMessagePlaceholder().getPlaceholderName();

        String billingCodeValue = messageTemplatePlaceholders.stream().filter(messageTemplatePlaceholder ->
                        messageTemplatePlaceholder.getMessagePlaceholder().getPlaceholderName().equals(AppConstants.BILLING_CODE_VALUE))
                .findFirst().get().getMessagePlaceholder().getPlaceholderName();
        for (BillingDetail bd : billingDetailRepository.findByBillingHeadIdAndAddToBillAmountOrderById(billingHead.getId(), true)) {
            iterationDTOList.add(IterationDTO.builder()
                    .iterationId(Long.valueOf(key))
                    .iterationTemplate(value.get(0))
                    .billing_code_ph(billingCode)
                    .billing_code_value_ph(billingCodeValue)
                    .billing_code(getBillingCodeDescription(bd.getBillingCode()))
                    .billing_code_value(decimalFormat.format(bd.getValue())).build());
        }
//                }
//        iterationsMapping.put(String.valueOf(key), iterationDTOList);
        return iterationDTOList;
    }

    private List<IterationDTO> projectProjectionRevenueDetails(String key, List<String> value, List<IterationDTO> iterationDTOList, BillingHead billingHead, List<MessageTemplatePlaceholder> messageTemplatePlaceholders) {
//        Map<String, List<IterationDTO>> iterationsMapping = new HashMap<>();
        String efficiency = messageTemplatePlaceholders.stream().filter(messageTemplatePlaceholder ->
                        messageTemplatePlaceholder.getMessagePlaceholder().getPlaceholderName().equals(Constants.PROJECTION_REVENUE.EFFICIENCY))
                .findFirst().get().getMessagePlaceholder().getPlaceholderName();
        String amount1 = messageTemplatePlaceholders.stream().filter(messageTemplatePlaceholder ->
                        messageTemplatePlaceholder.getMessagePlaceholder().getPlaceholderName().equals(Constants.PROJECTION_REVENUE.AMOUNT_1))
                .findFirst().get().getMessagePlaceholder().getPlaceholderName();
        String amount2 = messageTemplatePlaceholders.stream().filter(messageTemplatePlaceholder ->
                        messageTemplatePlaceholder.getMessagePlaceholder().getPlaceholderName().equals(Constants.PROJECTION_REVENUE.AMOUNT_2))
                .findFirst().get().getMessagePlaceholder().getPlaceholderName();
        String amount3 = messageTemplatePlaceholders.stream().filter(messageTemplatePlaceholder ->
                        messageTemplatePlaceholder.getMessagePlaceholder().getPlaceholderName().equals(Constants.PROJECTION_REVENUE.AMOUNT_3))
                .findFirst().get().getMessagePlaceholder().getPlaceholderName();
        CustomerSubscription customerSubscription = customerSubscriptionRepository.findById(billingHead.getSubscriptionId()).get();
        extDataStageDefinitionService.getProjectProjectionRevenueDetails(customerSubscription.getSubscriptionTemplate()).stream().forEach(p -> {
            iterationDTOList.add(IterationDTO.builder()
                    .iterationId(Long.valueOf(key))
                    .iterationTemplate(value.get(0))
                    .efficiency_ph(efficiency)
                    .amount_1_ph(amount1)
                    .amount_2_ph(amount2)
                    .amount_3_ph(amount3)
                    .efficiency_value(p.getEfficiency() + (p.getEfficiency().contains("%") ? " Efficiency" : "% Efficiency"))
                    .amount_1_value(p.getAmount1() != null ? decimalFormat.format(p.getAmount1()) : "0")
                    .amount_2_value(p.getAmount2() != null ? decimalFormat.format(p.getAmount2()) : "0")
                    .amount_3_value(p.getAmount3() != null ? decimalFormat.format(p.getAmount3()) : "0").build());
        });

//                }
//        iterationsMapping.put(String.valueOf(key), iterationDTOList);
        return iterationDTOList;
    }

    private Map<String, String> getPlaceholderValues(String
                                                             templateHTMLCode, Map<String, String> placeholderValues, User recipient) {
        if (recipient != null) {
            placeholderValues.put("first_name", recipient.getFirstName());
            placeholderValues.put("last_name", recipient.getLastName());
        }
        List<String> placeholders = Utility.findBetween(templateHTMLCode, "\\{\\{", "\\}\\}");
        List<MessagePlaceholder> messagePlaceholders = messagePlaceholderRepository.findByPlaceholderNameIn(placeholders);
        // Find mandatory
        messagePlaceholders.stream()
                .filter(mp -> !placeholderValues.keySet().contains(mp.getPlaceholderName()) && mp.getOptional() != null && !mp.getOptional())
                .collect(Collectors.toSet())
                .forEach(mp -> LOGGER.error("Mandatory placeholder " + mp.getPlaceholderName() + " not provided"));
        // Set defaults when not provided
        messagePlaceholders.stream()
                .filter(mp -> {
                    MessageTemplatePlaceholder messageTemplatePlaceholder = messageTemplatePlaceholderRepository.findByMessagePlaceholder(mp);
                    return !placeholderValues.keySet().contains(mp.getPlaceholderName()) && messageTemplatePlaceholder != null;
                })
                .collect(Collectors.toSet())
                .forEach(mp -> {
                    MessageTemplatePlaceholder messageTemplatePlaceholder = messageTemplatePlaceholderRepository.findByMessagePlaceholder(mp);
                    placeholderValues.put(mp.getPlaceholderName(), messageTemplatePlaceholder.getDefaultMsgText());
                });
        return placeholderValues;
    }

    /*expandWorkflow(wfExecId) {
        if e has values
                get CSV from e and email_template_id from the correspoding list
        if any value is e is starting with G then
                fetch the active ids from the user group and add them to the master list
        get first_name, last_name, email_addresses from corresponding active users
        fill notification log alongwith every email address
    }*/

    Set<User> expandWorkflow(WorkflowRecipientList recipientList) {
        RecipientJson recipientJson = getRecipientJson(recipientList.getTypeBList());
        // if e has values then
        //      get CSV from e and email_template_id from the correspoding list
        List<String> emailRecipients = ListUtils.emptyIfNull(recipientJson.getE());
        Set<User> recipientUsers = new HashSet<>();
        if (!emailRecipients.isEmpty()) {
            List<Long> userList =
                    emailRecipients.stream().filter(value -> value.startsWith("U")).map(value -> Long.valueOf(value.substring(1))).collect(Collectors.toList());
            List<Long> groupList =
                    emailRecipients.stream().filter(value -> value.startsWith("G")).map(value -> Long.valueOf(value.substring(1))).collect(Collectors.toList());
            List<Long> roleList =
                    emailRecipients.stream().filter(value -> value.startsWith("R")).map(value -> Long.valueOf(value.substring(1))).collect(Collectors.toList());
            recipientUsers.addAll(userRepository.findAllById(userList));
            recipientUsers.addAll(groupExpander(groupList));
            recipientUsers.addAll(roleExpander(roleList));
            return recipientUsers.stream().filter(u -> u.getStatus().equals(EUserStatus.ACTIVE.getStatus())).collect(Collectors.toSet());
        }
        return recipientUsers;
        /*// if any value is e is starting with G then
        //      fetch the active ids from the user group and add them to the master list

        get first_name, last_name, email_addresses from corresponding active users

        fill notification log alongwith every email address*/

    }

    private RecipientJson getRecipientJson(String typeBList) {
        TypeBListJson typeBListJson = null;
        try {
            typeBListJson = new ObjectMapper().readValue(typeBList, TypeBListJson.class);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return typeBListJson.getWlist().getRecipients();
    }

    /*set of users groupExpander() {
        add active user / active group
    }*/

    List<User> groupExpander(List<Long> groupListIds) {
        // add active user / active group
        List<WorkflowGroupAssignment> workflowGroupAssignments =
                workflowGroupAssignmentRepository.findAllByIdFetchAssignees(groupListIds);
        return workflowGroupAssignments.stream().flatMap(assignment -> assignment.getAssignees().stream()).collect(Collectors.toList());
    }

    List<User> roleExpander(List<Long> roleList) {
        // add active user
        return userRepository.findByRolesContaining(roleRepository.findAllById(roleList));
    }

    public Long verifyHookMap(String hookConstant) {
        WorkflowHookMaster workFlowHookMaster = workFlowHookMasterRepository.findByHookConstant(hookConstant);
        Optional<WorkflowHookMap> workflowHookMapOptional = workflowHookMapRepository.findByHookId(workFlowHookMaster.getId());
        if (workflowHookMapOptional.isPresent()) {
            return workflowHookMapOptional.get().getId();
        }
        return 0l;
    }

    private String getBillingCodeDescription(String code) {

        PortalAttributeValueTenantDTO billCodeValue = attributeOverrideService.findByAttributeValue(code);
        String description = billCodeValue != null ? billCodeValue.getDescription() != null ? billCodeValue.getDescription() : code : code;
        return description;
    }

    private ObjectNode projectProjectionRevenue(String hookConstant, List<WorkflowHookMap> workflowHookMapList, Map<String, String> placeholderValues, ObjectNode response, Long dynamicRecipientId, BillingHead billingHead, String[]... subjectParams) {

        MessageTemplate messageTemplate = getMessageTemplate(workflowHookMapList, placeholderValues);

        String templateHTMLCode = getIterations(placeholderValues, hookConstant, messageTemplate, billingHead);

        String emailMessage = getMessage(templateHTMLCode, placeholderValues, null);

        CalculationDetails calculationDetailsDB = calculationDetailsService.findBySourceId(billingHead.getId());
        calculationDetailsDB.setPrevInvHtmlView(emailMessage);
        calculationDetailsService.addOrUpdate(calculationDetailsDB);

//        invoiceLogService.save(InvoiceLog.builder()
//                .billId(billingHead.getId())
//                .invoiceId(billingHead.getInvoice() != null ? billingHead.getInvoice().getId() : null)
//                .invoiceStatus(billingHead.getInvoice() != null ? "INVOICED" : "DRAFT")
//                .message(emailMessage).build());
        response.put("message", "Invoice template has been generated Successfully");
        return response;
    }
}
//users, email_template_id for e entries (explode group(G))