package com.solar.api.tenant.model.dataexport.powermonitoring;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataDTO {
    private Long acctId;
    private String id;
    private String name;
    private String image;
    private String subsIds;
    private String variantId;
    private String refId;
    private Long subCount;


    public DataDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public DataDTO(Long acctId, String name, String image,String variantId) {
        this.acctId = acctId;
        this.name = name;
        this.image = image;
        this.variantId=variantId;
    }

    public DataDTO(String refId, Long subCount) {
        this.refId = refId;
        this.subCount = subCount;
    }
}
