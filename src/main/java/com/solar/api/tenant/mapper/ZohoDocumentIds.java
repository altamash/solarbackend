package com.solar.api.tenant.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZohoDocumentIds {

    private String document_name; //": "CommonNDA.pdf",
    private String document_id; //": "100000000000050"
}
