package com.solar.api.saas.service.process.upload.project;

import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.extended.assetHead.AssetBlockDetail;
import com.solar.api.tenant.model.extended.assetHead.AssetSerialNumber;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProjectUploadParse {

    List<AssetSerialNumber> importAssetSerialMappingsFromCSV (List<Map<String,String>> correctMappings,
                                                             String uniqueMeasure, String palletNumberField, Long assetId) throws IOException;

    List<AssetBlockDetail> importAssetSerialMappingsToUpdateCSV (List<Map<String,String>> correctMappings,
                                                                 List<MeasureDefinitionTenantDTO> measuresDTOs, String uniqueMeasure, Long assetId) throws IOException;

    List<AssetBlockDetail> importAssetBlockDetailSerialMappingsToUpdateCSV(AssetSerialNumber assetSerialNumber, Map<String,String> rowMapping,
                                                                       List<MeasureDefinitionTenantDTO> measuresDTOs, String uniqueMeasure, Long assetId) throws IOException;

    List<AssetBlockDetail> importAssetSerialMappingToInsert(List<Map<String,String>> filteredNewSerialRowsToInsert, List<MeasureDefinitionTenantDTO> measuresDTOs,
                                                            String uniqueMeasure, Long assetId, Long blockId, Map<String,Long> getAssetRefId, List<Long> createdIds) throws IOException;
    }
