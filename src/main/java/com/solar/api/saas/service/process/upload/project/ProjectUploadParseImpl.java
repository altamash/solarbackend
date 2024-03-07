package com.solar.api.saas.service.process.upload.project;

import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.extended.assetHead.AssetBlockDetail;
import com.solar.api.tenant.model.extended.assetHead.AssetSerialNumber;
import com.solar.api.tenant.repository.AssetBlockDetailRepository;
import com.solar.api.tenant.repository.AssetSerialNumberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class ProjectUploadParseImpl implements ProjectUploadParse {

    @Autowired
    private AssetSerialNumberRepository assetSerialNumberRepository;
    @Autowired
    private AssetBlockDetailRepository assetBlockDetailRepository;

    @Override
    public List<AssetSerialNumber> importAssetSerialMappingsFromCSV(List<Map<String,String>> correctMappings,
                                                                           String uniqueMeasure, String palletNumberField, Long assetId) throws IOException {

        List<AssetSerialNumber> assetSerialNumbersSaving = new ArrayList<>();
        for (int i = 0; i < correctMappings.size(); i++) {
            Map<String, String> mapping = correctMappings.get(i);
            String serialValue = mapping.get(uniqueMeasure);
            String palletNumber = mapping.get(palletNumberField);

            assetSerialNumbersSaving.add(AssetSerialNumber.builder()
                    .assetId(assetId)
                    .serialNumber(serialValue)
                    .palletNo(palletNumber).build());
        }
        return assetSerialNumbersSaving;
    }

    @Override
    public List<AssetBlockDetail> importAssetSerialMappingsToUpdateCSV(List<Map<String,String>> correctMappings,
                                                                 List<MeasureDefinitionTenantDTO> measuresDTOs, String uniqueMeasure, Long assetId) throws IOException {

        List<AssetBlockDetail> assetBlockDetailsUpdating  = new ArrayList<>();
        List<AssetSerialNumber> assetSerialNumbers = assetSerialNumberRepository.findAllByAssetId(assetId);

        for (int i = 0; i < correctMappings.size(); i++) {
            Map<String, String> mapping = correctMappings.get(i);//row
            String serialValue = mapping.get(uniqueMeasure);
            Optional<AssetSerialNumber> assetSerialNumber = assetSerialNumbers.stream().filter(id -> id.getSerialNumber().equals(serialValue)).findFirst();
            List<AssetBlockDetail> assetBlockDetailsDB = assetBlockDetailRepository.findAllByAssetRefId(assetSerialNumber.get().getId());

            Iterator<? extends Map.Entry<?, ?>> iterator = mapping.entrySet().iterator();//cols
            while (iterator.hasNext()) {
                Map.Entry<?, ?> map = iterator.next();
                Optional<MeasureDefinitionTenantDTO> measureOptional =
                        measuresDTOs.stream().filter(m -> m.getMeasure().equals(map.getKey().toString()) &&
                                !m.getMeasure().equals(uniqueMeasure)).findFirst();

                if (measureOptional.isPresent()) {
                    MeasureDefinitionTenantDTO measure = measureOptional.get();
                    Optional<AssetBlockDetail> assetBlockDetailDb = assetBlockDetailsDB.stream().filter(bd -> bd.getMeasureId()==measure.getId().longValue()).findFirst();
                    // measure exist then update value
                    if (assetBlockDetailDb.isPresent()) {
                        if(!map.getValue().toString().isEmpty() && !map.getValue().toString().equals(null)){
                            AssetBlockDetail assetBlockDetailUpd = AssetBlockDetail.builder()
                                    .id(assetBlockDetailDb.get().getId())
                                    .assetId(assetId)
                                    .assetRefId(assetSerialNumber.get().getId())
                                    .recordNumber(assetBlockDetailDb.get().getRecordNumber())
                                    .refBlockId(assetBlockDetailDb.get().getRefBlockId())
                                    .measureId(assetBlockDetailDb.get().getMeasureId())
                                    .measureValue(map.getValue().toString())
                                    .build();
                            assetBlockDetailsUpdating.add(assetBlockDetailUpd);
                            //assetBlockDetailsUpdating.add(AssetBlockDetailMapper.toUpdatedAssetBlockDetail(assetBlockDetailDb.get(),assetBlockDetailUpd));
                        }

                    } else {
                        //adding new row for measure
                        if(!map.getValue().toString().isEmpty() && !map.getValue().toString().equals(null)) {
                            assetBlockDetailsUpdating.add(AssetBlockDetail.builder()
                                    .assetId(assetId)
                                    .assetRefId(assetSerialNumber.get().getId())
                                    .recordNumber(assetBlockDetailsDB.get(0).getRecordNumber())
                                    .refBlockId(assetBlockDetailsDB.get(0).getRefBlockId())
                                    .measureId(measure.getId())
                                    .measureValue(map.getValue().toString())
                                    .build()
                            );
                        }
                    }
                }
            }
        }
        return assetBlockDetailsUpdating;
    }

    @Override
    public List<AssetBlockDetail> importAssetBlockDetailSerialMappingsToUpdateCSV(AssetSerialNumber assetSerialNumber, Map<String, String> rowMapping, List<MeasureDefinitionTenantDTO> measuresDTOs, String uniqueMeasure, Long assetId) throws IOException {
        List<AssetBlockDetail> assetBlockDetailsUpdating  = new ArrayList<>();
        List<AssetBlockDetail> assetBlockDetailsDB = assetBlockDetailRepository.findAllByAssetRefId(assetSerialNumber.getId());

        Iterator<? extends Map.Entry<?, ?>> iterator = rowMapping.entrySet().iterator();//cols
        while (iterator.hasNext()) {
            Map.Entry<?, ?> map = iterator.next();
            Optional<MeasureDefinitionTenantDTO> measureOptional =
                    measuresDTOs.stream().filter(m -> m.getMeasure().equals(map.getKey().toString()) &&
                            !m.getMeasure().equals(uniqueMeasure)).findFirst();

            if (measureOptional.isPresent()) {
                MeasureDefinitionTenantDTO measure = measureOptional.get();
                Optional<AssetBlockDetail> assetBlockDetailDb = assetBlockDetailsDB.stream().filter(bd -> bd.getMeasureId()==measure.getId().longValue()).findFirst();
                // measure exist then update value
                if (assetBlockDetailDb.isPresent()) {
                    if(!map.getValue().toString().isEmpty() && !map.getValue().toString().equals(null)){
                        AssetBlockDetail assetBlockDetailUpd = AssetBlockDetail.builder()
                                .id(assetBlockDetailDb.get().getId())
                                .assetId(assetId)
                                .assetRefId(assetSerialNumber.getId())
                                .recordNumber(assetBlockDetailDb.get().getRecordNumber())
                                .refBlockId(assetBlockDetailDb.get().getRefBlockId())
                                .measureId(assetBlockDetailDb.get().getMeasureId())
                                .measureValue(map.getValue().toString())
                                .build();
                        assetBlockDetailsUpdating.add(assetBlockDetailUpd);
                        //assetBlockDetailsUpdating.add(AssetBlockDetailMapper.toUpdatedAssetBlockDetail(assetBlockDetailDb.get(),assetBlockDetailUpd));
                    }

                } else {
                    //adding new row for measure
                    if(!map.getValue().toString().isEmpty() && !map.getValue().toString().equals(null)) {
                        assetBlockDetailsUpdating.add(AssetBlockDetail.builder()
                                .assetId(assetId)
                                .assetRefId(assetSerialNumber.getId())
                                .recordNumber(assetBlockDetailsDB.get(0).getRecordNumber())
                                .refBlockId(assetBlockDetailsDB.get(0).getRefBlockId())
                                .measureId(measure.getId())
                                .measureValue(map.getValue().toString())
                                .build()
                        );
                    }
                }
            }
        }
        return assetBlockDetailsUpdating;
    }

    @Override
    public List<AssetBlockDetail> importAssetSerialMappingToInsert(List<Map<String,String>> filteredNewSerialRowsToInsert, List<MeasureDefinitionTenantDTO> measuresDTOs,
                                             String uniqueMeasure, Long assetId, Long blockId, Map<String,Long> getAssetRefId, List<Long> createdIds) {

        List<AssetBlockDetail> assetBlockDetailsSaveOrUpd = new ArrayList<>();
        //to get record number
        Long maxRecordId = assetBlockDetailRepository.getMaxRecordNumber(assetId);
        for (int i = 0; i < filteredNewSerialRowsToInsert.size(); i++) {
            Map<?, ?> mapping = filteredNewSerialRowsToInsert.get(i);
            String serialValue = (String) mapping.get(uniqueMeasure);//serial number name
            maxRecordId = getIncrementedRecordNumber(maxRecordId);
            Long assetRefId = getAssetRefId.get(serialValue);
            createdIds.add(assetRefId);

            Iterator<? extends Map.Entry<?, ?>> iterator = mapping.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<?, ?> map = iterator.next();
                System.out.println("key not found:" + map.getKey().toString());
                Optional<MeasureDefinitionTenantDTO> measureOptional =
                        measuresDTOs.stream().filter(m -> m.getMeasure().equals(map.getKey().toString())).findFirst();

                if (measureOptional.isPresent()) {
                    if(!map.getValue().toString().isEmpty() && !map.getValue().toString().equals(null)){
                        assetBlockDetailsSaveOrUpd.add(AssetBlockDetail.builder()
                                .assetId(assetId)
                                .measureId(measureOptional.get().getId())
                                .measureValue(map.getValue().toString())
                                .refBlockId(blockId)
                                .recordNumber(maxRecordId)
                                .assetRefId(assetRefId).build());
                    }
                }
            }
        }
        return assetBlockDetailsSaveOrUpd;
    }

    //TODO:record number incremented
    public Long getIncrementedRecordNumber(Long recordNumber){
        Long maxCounter ;
        if (recordNumber>0) {
            maxCounter = recordNumber+1L;
        } else {
            maxCounter = 1L;
        }
        return maxCounter;
    }
}
