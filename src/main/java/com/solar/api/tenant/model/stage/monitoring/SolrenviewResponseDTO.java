package com.solar.api.tenant.model.stage.monitoring;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolrenviewResponseDTO {

    private chart chart;
    private List<data> data;

}
