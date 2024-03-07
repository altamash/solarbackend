package com.solar.api.saas.service.integration.docuSign.dto.callback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.solar.api.saas.service.integration.docuSign.dto.Action;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZRequests {
  /*"request_status": "completed",
    "owner_email": "noreply@zohosign.com",
    "document_ids": [
        {
            "document_name": "Sample.pdf",
            "document_size": 5000,
            "document_order": "0",
            "total_pages": 7,
            "document_id": 1000000102053
        }
    ],
    "self_sign": false,
    "owner_id": 1000000102050,
    "request_name": "Sample",
    "modified_time": 1667831890816,
    "action_time": 1667831890816,
    "is_sequential": true,
    "owner_first_name": "First Name",
    "request_type_name": "Sample Documents",
    "request_id": 1000000102049,
    "owner_last_name": "Last Name",
    "request_type_id": 1000000102051,
    "zsdocumentid": "NLJHO1QURJAKYXMTCIYY_2BUCENGF2PE5HAHFTOFQ1K",
    "actions": [
        {
            "verify_recipient": false,
            "action_type": "SIGN",
            "action_id": 1000000102052,
            "recipient_email": "noreply1@zohosign.com",
            "is_embedded": false,
            "signing_order": 0,
            "recipient_name": "Recipient Name",
            "action_status": "SIGNED"
        }
    ]*/
    @JsonProperty("request_status")
    private String requestStatus;
    @JsonProperty("owner_email")
    private String ownerEmail;
    @JsonProperty("document_ids")
    private List<DocumentIds> documentIds;
    @JsonProperty("self_sign")
    private Boolean selfSign;
    @JsonProperty("owner_id")
    private Double ownerId;
    @JsonProperty("request_name")
    private String requestName;
    @JsonProperty("modified_time")
    private Long modifiedTime;
    @JsonProperty("action_time")
    private Long actionTime;
    @JsonProperty("is_sequential")
    private Boolean isSequential;
    @JsonProperty("owner_first_name")
    private String ownerFirstName;
    @JsonProperty("request_type_name")
    private String requestTypeName;
    @JsonProperty("request_id")
    private String requestId;
    @JsonProperty("owner_last_name")
    private String ownerLastName;
    @JsonProperty("request_type_id")
    private Double requestTypeId;
    private String zsdocumentid;
    private List<Action> actions;
}
