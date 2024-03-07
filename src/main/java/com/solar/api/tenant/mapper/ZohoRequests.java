package com.solar.api.tenant.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZohoRequests {

    private String request_name; //"NDA Document",
    private String request_id; //": "1000000000000",
    private String request_type_id; //": "10000000011",
    private ZohoDocumentIds zohoDocumentIds; //
}
