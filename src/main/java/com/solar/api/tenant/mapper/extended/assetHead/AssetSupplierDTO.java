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
public class AssetSupplierDTO {

    private Long id;
    private Long assetId;
    private Long supplierId;
    //    private Long serialNumber;
    private Boolean primarySupplier;
    private Long scanId;
    private String ext1;
    private String ext2;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
