package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessDTO<T> {
    private int status;
    private String message;
    private T data;
}
