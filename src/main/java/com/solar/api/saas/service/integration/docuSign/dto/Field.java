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
public class Field {

  /*"field_id": "286676000000100054",
    "x_coord": 380,
    "field_type_id": "286676000000000147",
    "abs_height": 18,
    "text_property":,
    "field_category": "textfield",
    "field_label": "Text",
    "is_mandatory": true,
    "default_value": "",
    "page_no": 2,
    "document_id": "286676000000100002",
    "field_name": "Text",
    "y_value": 73.229088,
    "abs_width": 121,
    "action_id": "286676000000100044",
    "width": 19.745251,
    "y_coord": 580,
    "field_type_name": "Textfield",
    "description_tooltip": "",
    "x_value": 62.018745,
    "height": 2.256041*/


    /*
    "field_category": "textfield",
    "field_label": "Text",
    "is_mandatory": true,
    "default_value": "",
    "page_no": 2,
    "document_id": "286676000000100002",
    "field_name": "Text",
    "y_value": 73.229088,
    "abs_width": 121,
    "action_id": "286676000000100044",
    "width": 19.745251,
    "y_coord": 580,
    "field_type_name": "Textfield",
    "description_tooltip": "",
    "x_value": 62.018745,
    "height": 2.256041*/

    @JsonProperty("field_id")
    private String fieldId;
    @JsonProperty("x_coord")
    private Integer xCoord;
    @JsonProperty("field_type_id")
    private String fieldTypeId;
    @JsonProperty("abs_height")
    private Integer absHeight;
    @JsonProperty("text_property")
    private TextProperty textProperty;
    @JsonProperty("field_category")
    private String fieldCategory;
    @JsonProperty("field_label")
    private String fieldLabel;
    @JsonProperty("is_mandatory")
    private Boolean isMandatory;
    @JsonProperty("default_value")
    private String defaultValue;
    @JsonProperty("page_no")
    private Integer pageNo;
    @JsonProperty("document_id")
    private String documentId;
    @JsonProperty("field_name")
    private String fieldName;
    @JsonProperty("y_value")
    private Double yValue;
    @JsonProperty("abs_width")
    private Integer absWidth;
    @JsonProperty("action_id")
    private String actionId;
    @JsonProperty("width")
    private Double width;
    @JsonProperty("y_coord")
    private Integer yCoord;
    @JsonProperty("field_type_name")
    private String fieldTypeName;
    @JsonProperty("description_tooltip")
    private String descriptionTooltip;
    @JsonProperty("x_value")
    private Double xValue;
    private Double height;
}
