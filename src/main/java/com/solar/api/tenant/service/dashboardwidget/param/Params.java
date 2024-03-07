package com.solar.api.tenant.service.dashboardwidget.param;

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
public class Params {

    @JsonProperty("dropdown_selections")
    private List<Dropdown> dropdownSelections;
    @JsonProperty("param")
    private List<Param> params;
    private String year;
}
/*
{
    "dropdown_selections": [
        {
            "name": "Site",
            "value": [
                "649d09dd54293276eadeb282"
            ]
        }
    ]
}
*/
