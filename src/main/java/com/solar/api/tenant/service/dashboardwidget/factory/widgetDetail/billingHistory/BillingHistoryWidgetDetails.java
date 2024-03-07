package com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.billingHistory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.Details;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingHistoryWidgetDetails implements Details {

    private String heading;
    private Boolean isDropdown;
    private Boolean isMultiSelect;
    private String multiSelectName;
    private List<SelectOption> multiSelectOptions;
    private List<String> multiSelectedValue;
    private String height;
    private BasicData basicData;
    private BasicOptions basicOptions;
/*

    "details": {
        "heading": "Billing History",
                "isDropdown": false,
                "isMultiSelect": true,
                "multiSelectName": "Site",
                "multiSelectOptions": [
        {
            "label": "Project Site 1",
                "value": "Project Site 1"
        },
        {
            "label": "Project Site 2",
                "value": "Project Site 2"
        },
        {
            "label": "Project Site 3",
                "value": "Project Site 3"
        }
		],
        "multiSelectedValue": [
        "Project Site 1",
                "Project Site 2"
		],
        "height": "380px",
                "basicData": {
            "labels": [
            "2015",
                    "2016",
                    "2017",
                    "2018",
                    "2019",
                    "2020",
                    "2021",
                    "2022"
			],
            "datasets": [
            {
                "label": "Project Site 1",
                    "data": [
                540,
                        325,
                        702,
                        620,
                        493,
                        391,
                        422,
                        943
					],
                "backgroundColor": "#A45FB8",
                    "borderWidth": 0,
                    "barThickness": 12,
                    "borderRadius": 10,
                    "borderSkipped": false
            },
            {
                "label": "Project Site 2",
                    "data": [
                311,
                        421,
                        453,
                        563,
                        492,
                        320,
                        432,
                        432
					],
                "backgroundColor": "#068CFF",
                    "borderWidth": 0,
                    "barThickness": 12,
                    "borderRadius": 10,
                    "borderSkipped": false
            },
            {
                "label": "Project Site 3",
                    "data": [
                230,
                        432,
                        875,
                        243,
                        532,
                        391,
                        765,
                        943
					],
                "backgroundColor": "#E5AD14",
                    "borderWidth": 0,
                    "barThickness": 12,
                    "borderRadius": 10,
                    "borderSkipped": false
            }
			]
        },
        "basicOptions": {
            "legend": {
                "labels": {
                    "color": "#212121"
                },
                "position": "bottom"
            },
            "scales": {
                "y": {
                    "beginAtZero": true,
                            "ticks": {
                        "color": "#212121"
                    },
                    "grid": {
                        "color": "#919191",
                                "drawBorder": false
                    }
                },
                "x": {
                    "ticks": {
                        "color": "#000"
                    },
                    "grid": {
                        "color": "#919191",
                                "drawBorder": false
                    }
                }
            }
        }
    }
*/

}
