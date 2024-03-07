package com.solar.api.tenant.service.process.pvmonitor.platform.enphase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusDetails {
    private String reason;
    private String statusSeverity;
    private Integer errorCount;
    private Integer totalCount;

    @Override
    public String toString() {
        return "StatusDetails{" +
                "reason='" + reason + '\'' +
                ", statusSeverity='" + statusSeverity + '\'' +
                ", errorCount=" + errorCount +
                ", totalCount=" + totalCount +
                '}';
    }
}
