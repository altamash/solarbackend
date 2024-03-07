package com.solar.api.tenant.mapper.extended.pallet;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PalletContainerAssetListDTO {

    private Long id;
    private Long palletId;
    private Long assetId;
    private Long serialNo;
    private Long inventoryBarcodeUsed;
    private String status;
    private Long boxInd;
    private Long quantity;
    private Long boxHeight;
    private Long boxLength;
    private Long boxDepth;
    private Long boxRefId;
    private Long unitNetWeight; //gms
}
