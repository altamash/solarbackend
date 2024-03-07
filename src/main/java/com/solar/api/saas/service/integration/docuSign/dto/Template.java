package com.solar.api.saas.service.integration.docuSign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Template {
  /*{
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

        "owner_email": "itsupport@solarinformatics.com",
        "created_time": 1668092720639,
        "document_ids": []
        "notes": "",
        "owner_id": "286676000000019003",
        "document_fields": []
        "description": "",
        "modified_time": 1668092720639,
        "is_deleted": false,
        "template_id": "286676000000100001",
        "validity": -1,
        "request_type_name": "Others",
        "owner_first_name": "IT",
        "request_type_id": "286676000000000135",
        "owner_last_name": "Support",

        "field_data": {
            "field_text_data": {"Company": "SI", "Text1": "sample", "Full name": "test name"},
            "field_boolean_data": {},
            "field_date_data": {"Date1": "Nov 08 2022"}
        },

    }*/
    @JsonProperty("field_data")
    private FieldData fieldData;
    private List<Action> actions;
    @JsonProperty("template_name")
    private String templateName;
    @JsonProperty("is_sequential")
    private Boolean isSequential;
    @JsonProperty("reminder_period")
    private Integer reminderPeriod;
    @JsonProperty("email_reminders")
    private Boolean emailReminders;

    @JsonProperty("owner_email")
    private String ownerEmail;
    @JsonProperty("created_time")
    private Long createdTime;
    @JsonProperty("document_ids")
    private List<DocumentId> documentIds;
    private String notes;
    @JsonProperty("owner_id")
    private String ownerId;
    @JsonProperty("document_fields")
    private List<DocumentField> documentFields;
    private String description;
    @JsonProperty("modified_time")
    private Long modifiedTime;
    @JsonProperty("is_deleted")
    private Boolean isDeleted;
    @JsonProperty("template_id")
    private String templateId;
    private Integer validity;
    @JsonProperty("request_id")
    private String requestId;
    @JsonProperty("zsdocumentid")
    private String zsdocumentid;
    @JsonProperty("request_type_name")
    private String requestTypeName;
    @JsonProperty("owner_first_name")
    private String ownerFirstName;
    @JsonProperty("request_type_id")
    private String requestTypeId;
    @JsonProperty("owner_last_name")
    private String ownerLastName;

    @JsonProperty("request_name")
    private String requestName;
    @JsonProperty("action_time")
    private Long actionTime;
    @JsonProperty("sign_percentage")
    private Double signPercentage;
    @JsonProperty("sign_submitted_time")
    private Long signSubmittedTime;

    @JsonProperty("expiration_days")
    private Integer expirationDays;
    @JsonProperty("expire_by")
    private Long expireBy;
    @JsonProperty("is_expiring")
    private Boolean isExpiring;

    @JsonProperty("visible_sign_settings")
    private VisibleSignSettings visibleSignSettings;
    @JsonProperty("in_process")
    private Boolean inProcess;
    @JsonProperty("self_sign")
    private Boolean selfSign;
}
