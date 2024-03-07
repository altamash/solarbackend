package com.solar.api.tenant.model.stage.monitoring;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class chart {

    private String showBorder;
    private String bgcolor;
    private String bgAlpha;
    private String caption;
    private String manageResize;
    private String numberscalevalue;
    private String numberscaleunit;
    private String paletteColors;
    private String useRoundEdges;
    private String labelDisplay;
    private String showValues;
}
