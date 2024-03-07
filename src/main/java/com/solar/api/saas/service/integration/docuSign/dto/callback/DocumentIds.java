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
public class DocumentIds {
  /*"document_name": "Sample.pdf",
    "document_size": 5000,
    "document_order": "0",
    "total_pages": 7,
    "document_id": 1000000102053*/
    @JsonProperty("document_name")
    private String documentName;
    @JsonProperty("document_size")
    private Integer documentSize;
    @JsonProperty("document_order")
    private String documentOrder;
    @JsonProperty("total_pages")
    private Integer totalPages;
    @JsonProperty("document_id")
    private Double documentId;
}
