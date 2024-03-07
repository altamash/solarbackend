package com.solar.api.tenant.service.stripe;

import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.solar.api.AppConstants;
import com.solar.api.saas.service.EmailService;
import com.solar.api.saas.service.workflow.HookValidator;
import com.solar.api.tenant.mapper.stripe.SubmitInterestDTO;
import com.solar.api.tenant.model.workflow.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SubmitInterestServiceImpl implements SubmitInterestService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private EmailService emailService;
    @Autowired
    private HookValidator hookValidator;
    @Override
    public ResponseEntity<Object> saveSubmitInterest(SubmitInterestDTO body) {
        try {
            MessageTemplate messageTemplate = hookValidator.getTemplateByName(AppConstants.SUBMIT_INTEREST);
            Personalization personalization = new Personalization();
            personalization.addTo(new Email(AppConstants.SOLAR_TEST_EMAIL));
            personalization.addCc(new Email("kashaf.arshad@solarinformatics.com"));
            personalization.setSubject(messageTemplate.getSubject());
            personalization.addDynamicTemplateData("subject", messageTemplate.getSubject());
            personalization.addDynamicTemplateData("company_name", body.getCompanyName());
            personalization.addDynamicTemplateData("company_type", body.getCompanyType());
            personalization.addDynamicTemplateData("phone_number", body.getPhoneNumber());

            return new ResponseEntity<>(emailService.emailDynamicTemplateWithNoFile(messageTemplate.getParentTmplId(), personalization), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
