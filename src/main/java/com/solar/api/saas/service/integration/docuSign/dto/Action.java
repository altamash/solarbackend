package com.solar.api.saas.service.integration.docuSign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Action {
  /*"verify_recipient": false,
    "action_type": "SIGN",
    "action_id": 1000000102052,
    "recipient_email": "noreply1@zohosign.com",
    "is_embedded": false,
    "signing_order": 0,
    "recipient_name": "Recipient Name",
    "action_status": "SIGNED"

    "role": "1",
    "recipient_phonenumber": "",
    "recipient_countrycode": "",
    "private_notes": "Please get back to us for further queries",
    "verification_type": "EMAIL",
    "verification_code": ""

    "action_id": "{{Zoho-action_id}}"*/

    @JsonProperty("action_id")
    private String actionId;
    @JsonProperty("action_type")
    private String actionType;
    @JsonProperty("recipient_email")
    private String recipientEmail;
    @JsonProperty("is_embedded")
    private Boolean isEmbedded;
    @JsonProperty("signing_order")
    private Integer signingOrder;
    @JsonProperty("recipient_name")
    private String recipientName;
    @JsonProperty("action_status")
    private String actionStatus;

    private String role;
    @JsonProperty("recipient_phonenumber")
    private String recipientPhonenumber;
    @JsonProperty("recipient_countrycode")
    private String recipientCountrycode;
    @JsonProperty("private_notes")
    private String privateNotes;
    @JsonProperty("verify_recipient")
    private Boolean verifyRecipient;
    @JsonProperty("verification_type")
    private String verificationType;
    @JsonProperty("verification_code")
    private String verificationCode;
}
