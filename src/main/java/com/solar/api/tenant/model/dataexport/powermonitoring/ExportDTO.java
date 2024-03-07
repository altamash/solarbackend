package com.solar.api.tenant.model.dataexport.powermonitoring;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportDTO {
    private List<DataDTO> projects;
    private List<DataDTO> customers;
    private List<DataDTO> subscriptions;
    private List<String> period;
    private String startDate;
    private String endDate;

}
