package com.solar.api.saas.mapper.widget.chart;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChartDetailDTO {

    private Long labelId;
    private Long chartId;
    private Integer seqNo;
    private String labelName;
    private String borderColor;
    private String baseColor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
