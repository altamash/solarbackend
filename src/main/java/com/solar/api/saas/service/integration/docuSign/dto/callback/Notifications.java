package com.solar.api.saas.service.integration.docuSign.dto.callback;

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
public class Notifications {
  /*"performed_by_email": "noreply1@zohosign.com",
    "performed_at": 1667831890816,
    "reason": "Reason stated if any",
    "activity": "Document has been signed",
    "operation_type": "RequestSigningSuccess",
    "action_id": 1000000102052,
    "performed_by_name": "Recipient Name",
    "ip_address": "127.0.0.1"*/
    @JsonProperty("performed_by_email")
    private String performedByEmail;
    @JsonProperty("performed_at")
    private Double performedAt;
    private String reason;
    private String activity;
    @JsonProperty("operation_type")
    private String operationType;
    @JsonProperty("action_id")
    private Double actionId;
    @JsonProperty("performed_by_name")
    private String performedByName;
    @JsonProperty("ip_address")
    private String ipAddress;
}
