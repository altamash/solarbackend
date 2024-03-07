package com.solar.api.tenant.mapper.workOrder;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MongoWorkOrderMasterDTO<T> implements Serializable {

    private List<MongoWorkOrderDTO> mongoWorkOrderDTO;
    private HashMap<String, TreeMap<String, Integer>> custTypeMap;
}
