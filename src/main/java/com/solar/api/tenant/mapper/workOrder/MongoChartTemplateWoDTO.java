package com.solar.api.tenant.mapper.workOrder;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MongoChartTemplateWoDTO<T> implements Serializable {
    private String label;
    private Integer value;
    private List<String> labels;
    private List<Integer> values;
}