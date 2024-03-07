package com.solar.api.tenant.mapper.extended.assetHead;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PagedAssetBlockDetailDTO {
    long totalItems;
   // List<AssetSerialNumberDTO> assetSerialNumberDTOS;
    String blockValues ;
}
