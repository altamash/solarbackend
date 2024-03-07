package com.solar.api.saas.service.integration.docuSign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentId {

  /*"image_string": "",
    "document_name": "withzohoSigntags_Mutual NDA Template_Spring2022",
    "pages": [],
    "document_size": 172942,
    "document_order": "0",
    "is_editable": false,
    "total_pages": 3,
    "document_id": "286676000000100002"*/
    @JsonProperty("image_string")
    private String imageString;
    @JsonProperty("document_name")
    private String documentName;
    private List<Page> pages;
    @JsonProperty("document_size")
    private Long documentSize;
    @JsonProperty("document_order")
    private String documentOrder;
    @JsonProperty("is_editable")
    private Boolean isEditable;
    @JsonProperty("total_pages")
    private Integer totalPages;
    @JsonProperty("document_id")
    private String documentId;
}
