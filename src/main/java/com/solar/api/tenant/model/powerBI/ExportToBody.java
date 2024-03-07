package com.solar.api.tenant.model.powerBI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportToBody {
    /*{
        "format": "PDF",
        "PowerBIReportConfiguration" : {
            "reportLevelFilters": [
                {
                    "filter": "billing_head/invoice_id eq 7784"
                }
            ]
        }
    }*/
    private String format;
    @JsonProperty("PowerBIReportConfiguration")
    private ExportToPowerBIReportConfiguration powerBIReportConfiguration;

    /*@Setter
    @Builder
    public static class ReportLevelFilter {
        private String filter;
    }

    @Setter
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PowerBIReportConfiguration {
        private List<ReportLevelFilter> reportLevelFilters;
    }*/
}
