package com.solar.api.tenant.mapper.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractByEntityDTO  {

    private Long docuId;
    private String uri;
    private String docuName;
    private String docuType;
    private String docuSize;
//    public ContractByEntityDTO(String uri, String docuName, String docuType, String docuSize) {
//        this.uri = uri;
//        this.docuName = docuName;
//        this.docuType = docuType;
//        this.docuSize = docuSize;
//    }
}
