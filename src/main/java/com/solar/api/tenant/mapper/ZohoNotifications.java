package com.solar.api.tenant.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZohoNotifications {

    private String performed_by_email; //": "testuser@zoho.com",
    private String performed_at; //: 1555062604837,
    private String reason; //"reason given if any",
    private String activity; // "Document has been signed",
    private String operation_type; //  "RequestSigningSuccess",
    private String action_id; // "1000000000090",
    private String performed_by_name; // "test user",
    private String ip_address; // "192.168.100.100"
}
