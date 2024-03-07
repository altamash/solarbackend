package com.solar.api.tenant.mapper.extended.pallet;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PalletDefinitionDTO {

    private Long id;
    private Long palletRefId;
    private Long palletTypeId;
    private String source;
    private Long sourceRefId;
    private String returnToSourceInd;
    private String status;
    private String returnDatetime;
    private String inspectedBy;
    private String inspectionDatetime;
    private String lockedInd;
    private String lastLocSeqNo;
}
