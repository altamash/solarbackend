package com.solar.api.tenant.mapper.pvmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorReadingDailyDTO {

    private Double yieldValue;
    private List<String> inverterNumbers;
    private List<InverterDetailDTO> dataDTO;
}
