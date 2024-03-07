package com.solar.api.tenant.model.powerBI;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportToPowerBIReportConfiguration {
    /*"reportLevelFilters": [
        {
            "filter": "billing_head/invoice_id eq 7784"
        }
    ]*/
    private List<ExportToReportLevelFilter> reportLevelFilters;
}