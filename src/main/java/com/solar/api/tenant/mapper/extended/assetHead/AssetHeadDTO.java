package com.solar.api.tenant.mapper.extended.assetHead;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.extended.register.RegisterHierarchyDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetHeadDTO {

    private Long id;
    private String assetName;
    private Long registerId;
    private String description;
    private Date regDate;
    private Date activeDate;
    private String status;
    private Boolean recordLevelInd;
    private String serialized;//yes/no
    private List<AssetDetailDTO> assetDetails;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String blocks;
    private RegisterHierarchyDTO registerHierarchyDTO;
}
