package com.solar.api.tenant.mapper.extended.pallet;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PalletTypeDTO {

    private long id;
    private String palletType;
    private String material;
    private String stackable;
    private String division;
    private String prefix; //4 (CHAR)
    private String desc;
    private String fragileAllowed;
    private Long minTemp; //(F)
    private Long maxTemp; //(F)
    private String disposalDocRef;
    private String packagingRef;
    private String storageRecommendations;
    private Long length;
    private Long width;
    private Long height;
    private Long maxWeight;
    private String lastGeneratedSequence;
    private String palletImage;
    private String thumbnail;
}
