package com.solar.api.saas.service.process.upload.project;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.solar.api.saas.service.process.upload.EUploadAction;
import com.solar.api.saas.service.process.upload.EUploadEntitiy;
import com.solar.api.saas.service.process.upload.UploadResponse;
import com.solar.api.saas.service.process.upload.mapper.project.AssetSerialNumberMapper;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.extended.assetHead.AssetBlockDetail;
import com.solar.api.tenant.model.extended.assetHead.AssetHead;
import com.solar.api.tenant.model.extended.assetHead.AssetSerialNumber;
import com.solar.api.tenant.model.extended.project.ProjectInventory;
import com.solar.api.tenant.model.extended.project.ProjectInventorySerial;
import com.solar.api.tenant.model.extended.register.RegisterDetail;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.repository.AssetBlockDetailRepository;
import com.solar.api.tenant.repository.AssetHeadRepository;
import com.solar.api.tenant.repository.AssetSerialNumberRepository;
import com.solar.api.tenant.repository.RegisterHeadRepository;
import com.solar.api.tenant.repository.project.ProjectInventoryRepository;
import com.solar.api.tenant.repository.project.ProjectInventorySerialRepository;
import com.solar.api.tenant.service.extended.register.RegisterDetailService;
import com.solar.api.tenant.service.extended.register.RegisterHierarchyService;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectUploadServiceImpl implements ProjectUploadService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegisterHeadRepository registerHeadRepository;
    @Autowired
    private AssetHeadRepository assetHeadRepository;
    @Autowired
    @Lazy
    private RegisterDetailService registerDetailService;
    @Autowired
    private RegisterHierarchyService registerHierarchyService;
    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    private ProjectUploadParse projectUploadParse;
    @Autowired
    private AssetSerialNumberRepository assetSerialNumberRepository;
    @Autowired
    private AssetBlockDetailRepository assetBlockDetailRepository;
    @Autowired
    private ProjectInventoryRepository projectInventoryRepository;
    @Autowired
    private ProjectInventorySerialRepository projectInventorySerialRepository;


    @Override
    public UploadResponse upload(String entity, File file, List<Long> correctRowIds, Long assetId, String action, Long projectId) {
        if (file == null) {
            return null;
        }
        try {
            Class clazz = Class.forName(EUploadEntitiy.get(entity).getEntityPath());
            try (FileInputStream in = new FileInputStream(file)) {
                if (clazz == AssetBlockDetail.class) {
                    return uploadAssetBlockFromCSV(in, correctRowIds, assetId, null, action);
                }else if (clazz == ProjectInventory.class) {
                    return uploadProjectInventoryFromCSV(in, correctRowIds, assetId, null, projectId);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @Override
    public UploadResponse uploadAssetBlockFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                                  Long assetId, JobManagerTenant jobManagerTenant, String action) throws IOException, Exception {

        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Map<String, String>> assetSerialMappingLines = csvMapper
                .readerFor(Map.class)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(inputStream);
        List<Map<String, String>> mappings = assetSerialMappingLines.readAll();

        List<Map<String, String>> correctMappings = new ArrayList<>();
        for (long correctRowId : correctRowIds) {
            correctMappings.add(mappings.get((int) correctRowId));
        }

        AssetHead assetHead = assetHeadRepository.findById(assetId).get();
        List<RegisterDetail> registerDetails = registerDetailService.findByRegisterIdAndBlockIdNotNull(assetHead.getRegisterId());

        List<MeasureDefinitionTenantDTO> measuresDTOs =
            measureDefinitionOverrideService.findByIds(registerDetails.stream()
                    .filter(m -> m.getMeasureCodeId() != null)
                    .map(m -> m.getMeasureCodeId()).collect(Collectors.toList()));

        //unique field should be in measure definition
        String uniqueMeasure = null;
        Long blockId = null;

        for(RegisterDetail rd: registerDetails) {
            if(rd.getMeasureUnique()) {
                uniqueMeasure = measureDefinitionOverrideService.findById(rd.getMeasureCodeId()).getMeasure();
                blockId = rd.getMeasureBlockId();
            }
        };
/*
            Map<String,Long> measureNameId= new HashMap<>();
            registerDetails.forEach(rd-> {
                MeasureDefinitionTenantDTO measureDefinitionTenantDTO = measureDefinitionOverrideService.findById(rd.getMeasureCodeId());
                measureNameId.put(measureDefinitionTenantDTO.getMeasure(),measureDefinitionTenantDTO.getId());
            });

            Map<String,Long>  sortedMap =  measureNameId.entrySet().
                    stream().
                    sorted(Map.Entry.comparingByValue()).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            System.out.println(sortedMap);*/
        //}

        Map<String,Long> getAssetRefId = null;
        List<AssetBlockDetail> assetBlockDetailsSaveOrUpd = null;
        List<Long> createdIds = null ; Integer updated = 0;

        if (action.equals(EUploadAction.INSERT.getAction())) {

            List<AssetSerialNumber> assetSerialNumbersSaving = projectUploadParse.importAssetSerialMappingsFromCSV (correctMappings,uniqueMeasure,"Pallet Number", assetId);
            getAssetRefId = AssetSerialNumberMapper.toAssetSerialNumbers(assetSerialNumberRepository.saveAll(assetSerialNumbersSaving));

            //assetBlockDetail
            assetBlockDetailsSaveOrUpd = new ArrayList<>();
            createdIds = new ArrayList<>();
            //to get record number
            Long maxRecordId = assetBlockDetailRepository.getMaxRecordNumber(assetId);
            for (int i = 0; i < correctMappings.size(); i++) {
                Map<?, ?> mapping = correctMappings.get(i);
                String serialValue = (String) mapping.get(uniqueMeasure);//serial number name
                maxRecordId = getIncrementedRecordNumber(maxRecordId);
                Long assetRefId = getAssetRefId.get(serialValue);
                createdIds.add(assetRefId);

                Iterator<? extends Map.Entry<?, ?>> iterator = mapping.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<?, ?> map = iterator.next();
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

        } else {
            //update
            assetBlockDetailsSaveOrUpd = projectUploadParse.importAssetSerialMappingsToUpdateCSV (correctMappings,measuresDTOs, uniqueMeasure ,assetId);
            updated = assetBlockDetailsSaveOrUpd.size();
            //updatedIds.add((long) assetBlockDetailsSaveOrUpd.size());
        }

        LOGGER.info("csv serial block upload: " + assetBlockDetailsSaveOrUpd);
        assetBlockDetailRepository.saveAll(assetBlockDetailsSaveOrUpd);

        if(action.equals(EUploadAction.INSERT.getAction())){
            return UploadResponse.builder()
                    .entityType(EUploadEntitiy.ASSET_BLOCK_DETAIL.toString())
                    .created(assetBlockDetailsSaveOrUpd.size() !=0 ? assetBlockDetailsSaveOrUpd.size() : 0)
                    .createdIds(createdIds)
                    .total(Long.valueOf(correctMappings.size() !=0 ? correctMappings.size(): 0))
                    .build();
        }

        return UploadResponse.builder()
                .entityType(EUploadEntitiy.ASSET_BLOCK_DETAIL.toString())
                .updated(updated)
                .total(Long.valueOf(correctMappings.size() !=0 ? correctMappings.size(): 0))
                .build();
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

    @Override
    public UploadResponse uploadProjectInventoryFromCSV(InputStream inputStream, List<Long> correctRowIds, Long assetId, JobManagerTenant jobManagerTenant, Long projectId) throws IOException {

        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Map<String, String>> assetSerialMappingLines = csvMapper
                .readerFor(Map.class)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(inputStream);
        List<Map<String, String>> mappings = assetSerialMappingLines.readAll();

        AssetHead assetHead = assetHeadRepository.findById(assetId).get();
        List<RegisterDetail> registerDetails = registerDetailService.findByRegisterIdAndBlockIdNotNull(assetHead.getRegisterId());

        List<MeasureDefinitionTenantDTO> measuresDTOs =
                measureDefinitionOverrideService.findByIds(registerDetails.stream()
                        .filter(m -> m.getMeasureCodeId() != null)
                        .map(m -> m.getMeasureCodeId()).collect(Collectors.toList()));

        //unique field should be in measure definition
        String uniqueMeasure = null;
        Long blockId = null;

        for(RegisterDetail rd: registerDetails) {
            if(rd.getMeasureUnique()) {
                uniqueMeasure = measureDefinitionOverrideService.findById(rd.getMeasureCodeId()).getMeasure();
                blockId = rd.getMeasureBlockId();
            }
        };

        List<Map<String, String>> correctMappings = new ArrayList<>();
        List<String> correctSerialNumbers= new LinkedList<>();
        for (long correctRowId : correctRowIds) {
            Map<String, String> row = mappings.get((int) correctRowId);
            correctMappings.add(row);
            correctSerialNumbers.add(row.get(uniqueMeasure));
        }

        //filtered existing serial numbers
        List<AssetSerialNumber> assetSerialNumbersExists = assetSerialNumberRepository.findAllBySerialNumberIn(correctSerialNumbers.stream()
                               .map(m->m).collect(Collectors.toList()));
        //update
        List<AssetBlockDetail> assetBlockDetailsUpdate = new LinkedList<>();
        List<AssetSerialNumber> assetSerialNumbersUpdate = new ArrayList<>();
        List<Map<String, String>> filteredNewSerialRowsToInsert = new ArrayList<>();

        Long updatedAssetBlockCounter =0l;
        for (int i = 0; i < correctMappings.size(); i++) {
            Map<?, ?> mapping = correctMappings.get(i);
            String serialNumber = (String) mapping.get(uniqueMeasure);
            String palletNo = (String) mapping.get("Pallet Number");
            AssetSerialNumber assetSerialNumber = null;

            if (serialNumber!=null && palletNo!=null) {
                if (assetSerialNumbersExists != null) {
                    assetSerialNumber = assetSerialNumbersExists.stream().filter(m -> m.getSerialNumber().equals(serialNumber)).findFirst().orElse(null);
                }

                if (assetSerialNumber !=null) {
                    if (assetSerialNumber.getPalletNo() == null || !assetSerialNumber.getPalletNo().equals(palletNo)) {
                        // pallet no. update
                        assetSerialNumbersUpdate.add(AssetSerialNumber.builder()
                                .id(assetSerialNumber.getId())
                                .assetId(assetSerialNumber.getAssetId())
                                .serialNumber(assetSerialNumber.getSerialNumber())
                                .palletNo(palletNo)
                                .build());
                    }
                    //asset block detail update
                    List<AssetBlockDetail> assetBlockDetails = projectUploadParse.importAssetBlockDetailSerialMappingsToUpdateCSV(assetSerialNumber,
                            mappings.get(i),measuresDTOs,uniqueMeasure,assetId);
                    assetBlockDetailsUpdate.addAll(assetBlockDetails);
                    updatedAssetBlockCounter++;
                } else {
                    //new serial numbers to be inserted
                    filteredNewSerialRowsToInsert.add(correctMappings.get(i));
                }
            }
        }
        //for new project get null
        ProjectInventory projectInventory = projectInventoryRepository.findByAssetIdAndProjectId(assetId,projectId);
        if (assetSerialNumbersExists.size() != 0) {
            //updated pallets in asset serial
            Long updatedPalletsCount = 0l;
            List<ProjectInventorySerial> projectInventorySerialUpdates = new ArrayList<>();

            if (assetBlockDetailsUpdate.size()!=0) {
                assetBlockDetailRepository.saveAll(assetBlockDetailsUpdate);
            }

            if (assetSerialNumbersUpdate.size() != 0) {
                List<AssetSerialNumber> updatedPallets = assetSerialNumberRepository.saveAll(assetSerialNumbersUpdate);
                LOGGER.info("updated pallets in asset serial number:" + updatedPallets.size());
            }

            if (projectInventory != null) {
               List<ProjectInventorySerial> projectInventorySerialExists = projectInventorySerialRepository.findAllByAssetSerialNumberIdIn(assetSerialNumbersExists.stream()
                .map(m-> m.getId()).collect(Collectors.toList()));

               //not exists in project serial
                List<AssetSerialNumber> assetSerialNumbersFiltered = new ArrayList<>();
                List<Long> serialToExclude = projectInventorySerialExists.stream().map(m->m.getAssetSerialNumberId()).collect(Collectors.toList());
               if (projectInventorySerialExists.size()!=0) {

                   assetSerialNumbersFiltered = assetSerialNumbersExists.stream().filter(m-> !serialToExclude.contains(m.getId())).collect(Collectors.toList());

                   projectInventorySerialExists.forEach(m -> {
                       //replace project inventory id if serial exists
                       if (m.getProjectInventory().getId()!=projectInventory.getId()) {
                           LOGGER.info("different project inventory id :" + m.getProjectInventory());
                           projectInventorySerialUpdates.add(ProjectInventorySerial.builder()
                                   .projectInventory(projectInventory)
                                   .assetSerialNumberId(m.getAssetSerialNumberId())
                                   .id(m.getId()).build());
                       }
                   });

                   if (assetSerialNumbersFiltered.size() != 0) {
                       assetSerialNumbersFiltered.forEach( s -> {
                           projectInventorySerialUpdates.add(ProjectInventorySerial.builder()
                                   .projectInventory(projectInventory)
                                   .assetSerialNumberId(s.getId())
                                   .build());
                       });
                   }

               } else {
                   //new entries in project inventories serial
                   assetSerialNumbersExists.forEach( s -> {
                       projectInventorySerialUpdates.add(ProjectInventorySerial.builder()
                               .projectInventory(projectInventory)
                               .assetSerialNumberId(s.getId())
                               .build());
                   });
               }

            } else {

                //new entry in project inventory 2
                ProjectInventory projectInventorySave = projectInventoryRepository.save(ProjectInventory.builder().projectId(projectId)
                        .assetId(assetId).build());

                List<ProjectInventorySerial> projectInventorySerialExists = projectInventorySerialRepository.findAllByAssetSerialNumberIdIn(assetSerialNumbersExists.stream()
                        .map(m-> m.getId()).collect(Collectors.toList()));

                //not exists in project serial
                List<AssetSerialNumber> assetSerialNumbersFiltered = new ArrayList<>();
                List<Long> serialToExclude = projectInventorySerialExists.stream().map(m->m.getAssetSerialNumberId()).collect(Collectors.toList());
                if (projectInventorySerialExists.size()!=0) {

                    assetSerialNumbersFiltered = assetSerialNumbersExists.stream().filter(m -> !serialToExclude.contains(m.getId())).collect(Collectors.toList());

                    projectInventorySerialExists.forEach(m -> {
                        //replace project inventory id if serial exists
                        if (m.getProjectInventory().getId() != projectInventorySave.getId()) {
                            LOGGER.info("different project inventory id :" + m.getProjectInventory());
                            projectInventorySerialUpdates.add(ProjectInventorySerial.builder()
                                    .projectInventory(projectInventorySave)
                                    .assetSerialNumberId(m.getAssetSerialNumberId())
                                    .id(m.getId()).build());
                        }
                    });

                    if (assetSerialNumbersFiltered.size() != 0) {
                        assetSerialNumbersFiltered.forEach(s -> {
                            projectInventorySerialUpdates.add(ProjectInventorySerial.builder()
                                    .projectInventory(projectInventorySave)
                                    .assetSerialNumberId(s.getId())
                                    .build());
                        });
                    }
                }
                   /* assetSerialNumbersExists.forEach( s -> {
                    projectInventorySerialUpdates.add(ProjectInventorySerial.builder()
                    .projectInventory(projectInventorySave)
                    .assetSerialNumberId(s.getId())
                    .build());
                });*/
            }

            //project inventory serial saveOrUpdate
            if (projectInventorySerialUpdates.size()!=0) {
                 projectInventorySerialRepository.saveAll(projectInventorySerialUpdates);
            }
        }

        //inserted new serial numbers
        Map<String,Long> getAssetRefId = new LinkedHashMap<>();
        List<AssetBlockDetail> assetBlockDetailsSaveOrUpd = null;
        List<Long> createdIds = new ArrayList<>();
        if (filteredNewSerialRowsToInsert.size() != 0) {
            LOGGER.info("inserted records count :" + filteredNewSerialRowsToInsert.size());
            List<AssetSerialNumber> assetSerialNumbersSaving = projectUploadParse.importAssetSerialMappingsFromCSV (filteredNewSerialRowsToInsert,uniqueMeasure,"Pallet Number",assetId);
            getAssetRefId = AssetSerialNumberMapper.toAssetSerialNumbers(assetSerialNumberRepository.saveAll(assetSerialNumbersSaving));
            LOGGER.info("created asset ref id: " + getAssetRefId.size() +assetSerialNumbersSaving.size());

            //assetBlockDetail
            assetBlockDetailsSaveOrUpd = new ArrayList<>();
            assetBlockDetailsSaveOrUpd.addAll(projectUploadParse.importAssetSerialMappingToInsert(filteredNewSerialRowsToInsert,measuresDTOs,
                                            uniqueMeasure,assetId,blockId,getAssetRefId,createdIds));
            LOGGER.info("createdIds : " + createdIds.size());
            LOGGER.info("serial block inserted: " + assetBlockDetailsSaveOrUpd.size());
            assetBlockDetailRepository.saveAll(assetBlockDetailsSaveOrUpd);

            List<ProjectInventorySerial> projectInventorySerialsSave = new ArrayList<>();
            ProjectInventory projectInventory2 = projectInventory;
            if (projectInventory2 == null) {
             projectInventory2 = projectInventoryRepository.save(ProjectInventory.builder().projectId(projectId)
                        .assetId(assetId).build());
            }

            for (Map.Entry<String,Long> entry : getAssetRefId.entrySet()){
                projectInventorySerialsSave.add(ProjectInventorySerial.builder()
                        .projectInventory(projectInventory2)
                        .assetSerialNumberId(entry.getValue())
                        .build());
            }
            projectInventorySerialRepository.saveAll(projectInventorySerialsSave);
        }
        return UploadResponse.builder().entityType(EUploadEntitiy.PROJECT_INVENTORY_SERIALS.toString())
                .created(getAssetRefId.size() !=0 ? getAssetRefId.size() : 0)
                .createdIds(createdIds.size()!=0 ? createdIds : null)
                .updated(assetSerialNumbersExists.size() !=0 ? assetSerialNumbersExists.size() : 0)
                .build();
    }

}
