package com.solar.api.saas.service.process.upload.mapper.project;

import com.solar.api.tenant.model.extended.assetHead.AssetSerialNumber;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AssetSerialNumberMapper {

    public static Map<String,Long> toAssetSerialNumbers (List<AssetSerialNumber> assetSerialNumbersSaved){
        Map<String,Long> assetRefIdForSerials = new LinkedHashMap<>();
        assetSerialNumbersSaved.forEach(assetSerials -> {
            assetRefIdForSerials.put(assetSerials.getSerialNumber(),assetSerials.getId());
        });
        return assetRefIdForSerials;
    }
}
