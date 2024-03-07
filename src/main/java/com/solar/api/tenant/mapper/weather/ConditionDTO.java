package com.solar.api.tenant.mapper.weather;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConditionDTO {
    private String text;
    private String icon;
    private int code;

}
