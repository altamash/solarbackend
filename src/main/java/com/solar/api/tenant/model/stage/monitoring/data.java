package com.solar.api.tenant.model.stage.monitoring;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class data {

    private String value;
    private String displayValue;
    private String label;
}
