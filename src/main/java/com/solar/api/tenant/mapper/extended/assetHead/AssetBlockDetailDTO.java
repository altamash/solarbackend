package com.solar.api.tenant.mapper.extended.assetHead;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AssetBlockDetailDTO {

    private Long id;
    private Long assetId;
    private Long refBlockId;//blockId
    private Long measureId;
    private String measureValue;
    private Long recordNumber;//line no generated

    private Boolean measureUnique;
    private Long assetRefId;
    private String palletNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
