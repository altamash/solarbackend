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
public class TextProperty {

    /*"is_italic": false,
    "max_field_length": 2048,
    "is_underline": false,
    "font_color": "000000",
    "is_fixed_width": false,
    "font_size": 10,
    "is_fixed_height": true,
    "is_read_only": false,
    "is_bold": false,
    "font": "Arial"*/

    @JsonProperty("is_italic")
    private Boolean isItalic;
    @JsonProperty("max_field_length")
    private Integer maxFieldLength;
    @JsonProperty("is_underline")
    private Boolean isUnderline;
    @JsonProperty("font_color")
    private String fontColor;
    @JsonProperty("is_fixed_width")
    private Boolean isFixedWidth;
    @JsonProperty("font_size")
    private Integer fontSize;
    @JsonProperty("is_fixed_height")
    private Boolean isFixedHeight;
    @JsonProperty("is_read_only")
    private Boolean isReadOnly;
    @JsonProperty("is_bold")
    private Boolean isBold;
    private String font;

}
