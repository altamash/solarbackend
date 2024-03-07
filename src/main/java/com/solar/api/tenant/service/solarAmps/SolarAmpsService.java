package com.solar.api.tenant.service.solarAmps;

import com.sendgrid.Response;
import com.solar.api.tenant.mapper.stripe.SubmitInterestDTO;
import com.solar.api.tenant.model.solarAmps.RequestADemoDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface SolarAmpsService {
    ResponseEntity<Object> requestADemo(RequestADemoDTO requestADemoDTO);

    ResponseEntity<Object> submitInterest(SubmitInterestDTO submitInterestDTO);
    String getMessage(String templateHTMLCode, Map<String, String> placeholderValues);

    Response sendEmail(Map<String, String> placeholderValues, String toCSV, String ccCSV, String bccCSV, String subject, String[]
            subjectParams, String templateId, String htmlCode);

    Response sendEmail(Map<String, String> placeholderValues, List<String> toCSVs, String ccCSV, String bccCSV, String subject, String[]
            subjectParams, String templateId, String htmlCode);

}
