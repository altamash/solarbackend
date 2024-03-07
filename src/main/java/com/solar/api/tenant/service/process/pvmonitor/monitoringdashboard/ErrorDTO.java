package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDTO {
    private int status;
    private String message;
    private boolean isValidationFailed;

    public ErrorDTO(int status, String message) {
        this.status=status;
        this.message=message;
    }


}
