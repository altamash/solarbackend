package com.solar.api.saas.service.integration.docuSign.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.saas.service.integration.docuSign.dto.Template;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateData {
  /*{
        "templates": {
            "template_name": "CustAcqTemplate",
            "expiration_days": 1,
            "is_sequential": true,
            "reminder_period": 8,
            "email_reminders": false,
            "actions": [
                    {
                        "action_type": "SIGN",
                        "signing_order": 0,
                        "recipient_name": "",
                        "role": "1",
                        "recipient_email": "",
                        "recipient_phonenumber": "",
                        "recipient_countrycode": "",
                        "private_notes": "Please get back to us for further queries",
                        "verify_recipient": true,
                        "verification_type": "EMAIL",
                        "verification_code": ""
                    }
                ]

            "field_data": {
                "field_text_data": {"Company": "SI", "Text1": "sample", "Full name": "test name"},
                "field_boolean_data": {},
                "field_date_data": {"Date1": "Nov 08 2022"}
            }
        }
    }*/
    private Template templates;

}
