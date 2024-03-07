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
public class AssetListsDTO {

    private Long id;
    private Long listId;
    private String assetListAlias;
    private Long assetId;
    private String scanCode;
    private String preferredSourcingLoc;
    private String visibilityCode; // Not used

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
