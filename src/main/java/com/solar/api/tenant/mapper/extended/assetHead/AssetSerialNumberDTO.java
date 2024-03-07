package com.solar.api.tenant.mapper.extended.assetHead;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetSerialNumberDTO {

    private Long id;
    private String serialNumber;
    private Long assetId;
    private Long suppId;
    private Long manuId;
    private String notes;
    private String palletNo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
