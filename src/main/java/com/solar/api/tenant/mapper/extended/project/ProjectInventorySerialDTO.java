package com.solar.api.tenant.mapper.extended.project;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectInventorySerialDTO {

    private Long id;
    private Long assetSerialNumberId;
    private Long locationId;
    private String status;
    private Long projectInventoryId;
    private String serialNumber;
    private String palletNumber;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
