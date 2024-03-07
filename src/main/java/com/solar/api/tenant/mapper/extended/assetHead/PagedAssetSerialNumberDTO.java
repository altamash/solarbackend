package com.solar.api.tenant.mapper.extended.assetHead;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PagedAssetSerialNumberDTO {
    long totalItems;
    String serialized;
    List<AssetSerialNumberDTO> assetSerialNumberDTOS;
}
