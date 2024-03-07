package com.solar.api.saas.service.process.aspect.workflow;

import com.solar.api.helper.Utility;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.service.workflow.HookValidator;
import com.solar.api.tenant.model.customerSupport.ConversationHead;
import com.solar.api.tenant.model.customerSupport.ConversationHistory;
import com.solar.api.tenant.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Configuration
public class AfterAopAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HookValidator hookValidator;
    @Autowired
    private UserService userService;

    // executed only when a method executes successfully.
    @AfterReturning(value = "execution(* com.solar.api.tenant.service.lookup.codetyperefmap.ConversationAPIFactory.post(..))", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        if (result instanceof ConversationHead) {
            ConversationHead conversationHead = ((ConversationHead) result);

            if (conversationHead.getCategory().equals("Customer Support")) {
                conversationHead.setCustomerId(userService.getLoggedInUser().getAcctId());
                String ticketSummary = conversationHead.getSummary() != null ? conversationHead.getSummary() : "";
                Map<String, String> placeholderValues = new HashMap<>();
                String raisedBy = userService.findById(conversationHead.getCustomerId()).getFirstName();
                String dateTime = conversationHead.getCreatedAt().format(DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_TIME_FORMAT));
                String ticketId = String.valueOf(conversationHead.getId());
                placeholderValues.put("raised_by", raisedBy);
                placeholderValues.put("cust_first_name", userService.findById(conversationHead.getCustomerId()).getFirstName());
                placeholderValues.put("conv_link", WebUtils.getBaseUrl() + "/conversation/conversationHead/" + conversationHead.getId());
                placeholderValues.put("ticket_id", ticketId);
                placeholderValues.put("ticket_summary", ticketSummary);
                placeholderValues.put("date_time", dateTime);
                String[][] subjectParams = new String[][]{{raisedBy, dateTime}, {ticketId, ticketSummary}};
                hookValidator.hookFinder(null, "ticket_create", conversationHead.getCustomerId(), null, placeholderValues, null, subjectParams);
            }
        } else if (result instanceof ConversationHistory) {
            ConversationHistory conversationHistory = ((ConversationHistory) result);
            ConversationHead conversationHead = conversationHistory.getConversationHead();
            if (conversationHead.getCategory().equals("Customer Support")) {
                Map<String, String> placeholderValues = new HashMap<>();
                String ticketId = String.valueOf(conversationHead.getId());
                placeholderValues.put("conv_link", WebUtils.getBaseUrl() + "/conversation/conversationHead/" + conversationHead.getId());
                placeholderValues.put("ticket_id", ticketId);
                placeholderValues.put("ticket_update", conversationHistory.getMessage());
                String[][] subjectParams = new String[][]{{ticketId, conversationHead.getSummary()}};
                hookValidator.hookFinder(null, "ticket_reply", userService.getLoggedInUser().getAcctId(), userService.getLoggedInUser().getAcctId() != conversationHead.getCustomerId() ? conversationHead.getCustomerId() : conversationHead.getAssignee(), placeholderValues, null, subjectParams);

            }
        }
        logger.info("{} returned with value {}", joinPoint, result);
    }

    // executed in two situations — when a method executes successfully or it throws an exception.
    @After(value = "execution(* com.solar.api.tenant.service.lookup.codetyperefmap.ConversationAPIFactory.post(..))")
    public void after(JoinPoint joinPoint) {
        logger.info("after execution of {}", joinPoint);
    }
}