package com.solar.api.tenant.service.extended.assetHead;

import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.model.SaasSchema;
import com.solar.api.saas.service.StorageService;
import com.solar.api.tenant.mapper.extended.assetHead.AssetBlockDetailMapper;
import com.solar.api.tenant.mapper.extended.assetHead.AssetMapper;
import com.solar.api.tenant.mapper.extended.assetHead.PagedAssetBlockDetailDTO;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.extended.assetHead.AssetBlockDetail;
import com.solar.api.tenant.model.extended.assetHead.AssetBlockDetailTemplate;
import com.solar.api.tenant.model.extended.assetHead.AssetHead;
import com.solar.api.tenant.model.extended.assetHead.AssetSerialNumber;
import com.solar.api.tenant.model.extended.project.ProjectInventorySerial;
import com.solar.api.tenant.model.extended.register.RegisterHead;
import com.solar.api.tenant.repository.AssetBlockDetailRepository;
import com.solar.api.tenant.repository.AssetHeadRepository;
import com.solar.api.tenant.repository.AssetSerialNumberRepository;
import com.solar.api.tenant.repository.project.ProjectInventorySerialRepository;
import com.solar.api.tenant.service.extended.measure.MeasureBlockService;
import com.solar.api.tenant.service.extended.register.RegisterHeadService;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AssetBlockDetailServiceImpl implements AssetBlockDetailService{

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private AssetBlockDetailRepository assetBlockDetailRepository;
    @Autowired
    private MeasureBlockService measureBlockService;
    @Autowired
    private RegisterHeadService registerHeadService;
    @Autowired
    private AssetSerialNumberRepository assetSerialNumberRepository;
    @Autowired
    private AssetHeadRepository assetHeadRepository;
    @Autowired
    private StorageService storageService;
    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private Utility utility;
    @Autowired
    private ProjectInventorySerialRepository projectInventorySerialRepository;

    @Override
    public AssetBlockDetail save(AssetBlockDetail assetBlockDetail) {
        return assetBlockDetailRepository.save(assetBlockDetail);
    }

    @Override
    public List<AssetBlockDetail> saveAll(List<AssetBlockDetail> assetBlockDetails) {
        long maxRecordId = assetBlockDetailRepository.getMaxRecordNumber(assetBlockDetails.get(0).getAssetId());
        long maxCounter ;
        if (maxRecordId>0) {
            maxCounter = maxRecordId+1L;
        } else {
            maxCounter = 1L;
        }
        assetBlockDetails.forEach(block-> {
            //unique check
            if (block.getMeasureUnique()) {
                Long dupDataFound = assetBlockDetailRepository.countByAssetIdAndRefBlockIdAndMeasureIdAndMeasureValue(block.getAssetId(),
                        block.getRefBlockId(),block.getMeasureId(),block.getMeasureValue());
                if (dupDataFound!=0) {
                    throw new AlreadyExistsException(AssetBlockDetail.class,block.getMeasureId().toString(),block.getMeasureValue());
                }
            }
            block.setRecordNumber(maxCounter);
        });
        return assetBlockDetailRepository.saveAll(assetBlockDetails);
    }

    @Override
    public List<AssetBlockDetail> saveAllFromCSVUpload(List<AssetBlockDetail> assetBlockDetails) {
        return assetBlockDetailRepository.saveAll(assetBlockDetails);
    }


        @Override
    public List<AssetBlockDetail> update(List<AssetBlockDetail> assetBlockDetails) {
        List<AssetBlockDetail> assetBlockDetailsSave = new ArrayList<>();
        List<AssetSerialNumber> assetSerialNumbersUpdate = new ArrayList<>();
        if (assetBlockDetails != null) {
            assetBlockDetails.forEach(uniqueCheck -> {
                if (uniqueCheck.getMeasureUnique()) {
                    Long dupDataFound = assetBlockDetailRepository.countByAssetIdAndRefBlockIdAndMeasureIdAndRecordNumberNotAndMeasureValue(uniqueCheck.getAssetId(),
                            uniqueCheck.getRefBlockId(),uniqueCheck.getMeasureId(),uniqueCheck.getRecordNumber(),uniqueCheck.getMeasureValue());
                    if (dupDataFound!=0) {
                        throw new AlreadyExistsException(AssetBlockDetail.class,"Measure Value",uniqueCheck.getMeasureValue());
                    }
                }
                AssetBlockDetail assetBlockDetailDb = assetBlockDetailRepository.findByAssetIdAndRefBlockIdAndMeasureIdAndRecordNumber(uniqueCheck.getAssetId(),
                        uniqueCheck.getRefBlockId(),uniqueCheck.getMeasureId(),uniqueCheck.getRecordNumber());

                if (assetBlockDetailDb != null) {
                    assetBlockDetailsSave.add(AssetBlockDetailMapper.toUpdatedAssetBlockDetail(assetBlockDetailDb,uniqueCheck));
                } else {
                    assetBlockDetailsSave.add(uniqueCheck);
                }
            });

            if (assetBlockDetails.get(0).getAssetRefId()!=null) {
                AssetSerialNumber assetSerialNumberDb = assetSerialNumberRepository.findById(assetBlockDetails.get(0).getAssetRefId()).get();
                assetSerialNumberRepository.save(AssetMapper.toUpdatedAssetSerialNumbers(assetSerialNumberDb,AssetSerialNumber.builder()
                        .id(assetBlockDetails.get(0).getAssetRefId()).palletNo(assetBlockDetails.get(0).getPalletNo()).build()));
            }
        }
        return assetBlockDetailRepository.saveAll(assetBlockDetailsSave);
    }

    @Override
    public AssetBlockDetail findById(Long id) {
        return assetBlockDetailRepository.findById(id).get();
    }

    @Override
    public List<AssetBlockDetail> findAll() {
        return assetBlockDetailRepository.findAll();
    }

    @Override
    public List<AssetBlockDetail> findAllByAssetId(Long assetId) {
        List<AssetBlockDetail> assetBlockDetails = assetBlockDetailRepository.findAllByAssetIdOrderByRecordNumberAscRefBlockIdAsc(assetId);
        return assetBlockDetails;
    }

    @Override
    public List<AssetBlockDetail> findAllByAssetIdIn(List<Long> assetIds) {
        return assetBlockDetailRepository.findAllByAssetIdIn(assetIds);
    }

    @Override
    public List<AssetBlockDetail> findAllByAssetIdAndRefBlockIdIn(Long assetId, List<Long> refBlockIds) {
        return assetBlockDetailRepository.findAllByAssetIdAndRefBlockIdIn(assetId, refBlockIds);
    }

    @Override
    public PagedAssetBlockDetailDTO getAllBlockValuesByAssetId(Long registerId, Long assetId, Long blockId, int pageNumber, Integer pageSize, String sort) {
        String header = measureBlockService.getBlockHeaderAndFormat(registerId,blockId);
        JSONArray jsonArray = new JSONArray(header);
        String[] measureIds = jsonArray.getJSONArray(0).get(0).toString().split(",");
        String[] heads = jsonArray.getJSONArray(1).get(0).toString().split(",");

        RegisterHead registerHead = registerHeadService.findById(registerId);
        List<AssetSerialNumber> assetSerialNumbers = assetSerialNumberRepository.findAllByAssetId(assetId);
        Map<Long,Boolean> idsAndUniq = new HashMap<>();
        registerHead.getRegisterDetails().forEach(
                v -> idsAndUniq.put(v.getMeasureCodeId(),v.getMeasureUnique())) ;

        JSONArray newHeads = new JSONArray();
        for (int mIds=0; mIds<measureIds.length;mIds++) {
           StringBuffer bf = new StringBuffer();
           newHeads.put(bf.append(measureIds[mIds]).append(":").append(heads[mIds]).append(":").append(idsAndUniq.get(Long.parseLong(measureIds[mIds]))));
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize == null ? SaasSchema.PAGE_SIZE : pageSize);
        List<AssetBlockDetailTemplate> blockValues = assetBlockDetailRepository.getAllBlockValuesByAssetId(assetId,blockId);
        int totalElements = blockValues.size();
        if (pageable == null) {
            pageable = PageRequest.of(0, 10);
        }
        int fromIndex = pageable.getPageSize() * pageable.getPageNumber();
        int toIndex = pageable.getPageSize() * (pageable.getPageNumber() + 1);
        if (toIndex > totalElements) {
            toIndex = totalElements;
        }
        List<AssetBlockDetailTemplate> indexObjects = blockValues.subList(fromIndex, toIndex);
        Page<AssetBlockDetailTemplate> filteredBlockValues = new PageImpl<AssetBlockDetailTemplate>(indexObjects
                , pageable, totalElements);

        JSONArray finalArray= new JSONArray();
        if (filteredBlockValues.getTotalElements()!=0) {
            finalArray.put(newHeads.put("Pallet Number,Asset Ref Id,Record Number"));
            filteredBlockValues.forEach(block -> {
                StringBuffer bf = new StringBuffer();
                JSONArray blockArray = new JSONArray();
                ///row record1
                String[] mIds = block.getMeasureIds().split(",");
                String[] mValues = block.getMeasureValues()!=null? block.getMeasureValues().split(",") : null;
                String recordNumber = block.getRecordNumber().toString();
                Long assetRefId = block.getAssetRefId()!=null ? block.getAssetRefId() : 0 ;
                Optional<String> getPalletNo = assetSerialNumbers.stream().filter(pallet -> pallet.getId().equals(assetRefId) && pallet.getPalletNo()!=null ).map(AssetSerialNumber::getPalletNo).findFirst();
                int a=0;
                StringJoiner values = new StringJoiner(",");
                for (String str : measureIds) {
                    if(ArrayUtils.contains(mIds, str)){
                        values.add(mValues[a++]);
                    }else{
                        values.add("");
                    }
                }
                blockArray.put(values.add(getPalletNo.isPresent() ? getPalletNo.get() : ""));
                blockArray.put(values.add(assetRefId.toString()));
                blockArray.put(values.add(recordNumber));
                finalArray.put(blockArray);
            });

        } else {
         return PagedAssetBlockDetailDTO.builder()
                 .blockValues(String.valueOf(new NotFoundException(AssetHead.class, assetId))).build();
        }
        return PagedAssetBlockDetailDTO.builder()
                .blockValues(finalArray.toString())
                .totalItems(filteredBlockValues.getTotalElements()).build();
    }

    @Override
    public String deleteAssetBlock(Long assetRefId) {
        ProjectInventorySerial projectInventorySerial = projectInventorySerialRepository.findByAssetSerialNumberId(assetRefId);
        if (projectInventorySerial!=null) {
            return "Can't delete the record, it is linked with project.";
        }
        List<AssetBlockDetail> assetBlockDetails = assetBlockDetailRepository.findAllByAssetRefId(assetRefId);
        Optional<AssetSerialNumber> assetSerialNumber = assetSerialNumberRepository.findById(assetRefId);
        //List<AssetBlockDetail> assetBlockDetails = assetBlockDetailRepository.findAllByAssetIdAndRefBlockIdAndRecordNumber(assetId,blockId,recordNumber);
        if(assetBlockDetails.size()!=0){
            assetBlockDetailRepository.deleteAll(assetBlockDetails);
            if (assetSerialNumber.isPresent()) {
                assetSerialNumberRepository.delete(assetSerialNumber.get());
            }
        }
        return "Record deleted";
    }

    @Override
    public void delete(Long id) {
        assetBlockDetailRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        assetBlockDetailRepository.deleteAll();
    }

    @Override
    public void deleteAll(List<AssetBlockDetail> assetBlockDetails) {
        assetBlockDetailRepository.deleteAll(assetBlockDetails);
    }

    @Override
    public String getSerialNumbersForCSVExport(Long assetId, Long projectId) {

        String blobUrl = "";
        String serialNumberFile = "Exported_AssetId["+assetId+"]_ProjectId["+projectId+"].csv";

        try {

            List<MeasureDefinitionTenantDTO> measureDefinitionTenantDTOS = measureBlockService.getSerialHeaderForCSV(assetId);
            measureDefinitionTenantDTOS.sort(Comparator.comparing(MeasureDefinitionTenantDTO::getId));

            List<String> csvHeader = new ArrayList<>();
            csvHeader.add("Pallet Number");
            for (MeasureDefinitionTenantDTO m : measureDefinitionTenantDTOS) {
                csvHeader.add(m.getMeasure());
            }

            List<AssetBlockDetailTemplate> assetBlockDetailTemplates = assetBlockDetailRepository.getAllAssignedSerialToProject(assetId,projectId);
            List<List> csvRowData = new ArrayList<>();

            for (AssetBlockDetailTemplate value:assetBlockDetailTemplates) {

                String[] measureValues = value.getMeasureValues().split(",");
                List<String> mIds = Arrays.asList(value.getMeasureIds().split(","));
                List<String> colsForRow = new ArrayList<>();
                colsForRow.add(value.getPalletNo()!=null ? value.getPalletNo(): "");

                for (int i=0;i<measureDefinitionTenantDTOS.size();i++) {
                    if(mIds.contains(measureDefinitionTenantDTOS.get(i).getId().toString())){
                       int index = mIds.indexOf(measureDefinitionTenantDTOS.get(i).getId().toString());
                        colsForRow.add(measureValues[index]);
                    } else {
                        colsForRow.add("");
                    }

                }
                csvRowData.add(colsForRow);
            }

            byte[] byteArray = Utility.getCSVBytes(csvHeader,csvRowData);
            blobUrl = Utility.uploadToStorage(storageService,byteArray,appProfile,"tenant/" + utility.getCompKey()
                            + "/project/inventorySerial",serialNumberFile,
                    utility.getCompKey(), false);

        } catch(Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return "Failed to export serial numbers file.";
        }
        return blobUrl;
    }

}
