package com.solar.api.tenant.service.extended.assetHead;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.extended.assetHead.AssetMapper;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.extended.assetHead.AssetDetail;
import com.solar.api.tenant.model.extended.assetHead.AssetHead;
import com.solar.api.tenant.model.extended.register.RegisterDetail;
import com.solar.api.tenant.model.extended.register.RegisterHead;
import com.solar.api.tenant.model.extended.register.RegisterHierarchy;
import com.solar.api.tenant.model.extended.resources.MeasureBlockHead;
import com.solar.api.tenant.repository.*;
import com.solar.api.tenant.service.extended.register.RegisterHierarchyService;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
//@Transactional("tenantTransactionManager")
public class AssetHeadServiceImpl implements AssetHeadService {

    @Autowired
    private AssetHeadRepository repository;
    @Autowired
    private AssetDetailRepository assetDetailRepository;
    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    private MeasureBlockHeadRepository measureBlockHeadRepository;
    @Autowired
    private RegisterHierarchyService registerHierarchyService;
    @Autowired
    private RegisterHeadRepository registerHeadRepository;
    @Autowired
    private AssetBlockDetailRepository assetBlockDetailRepository;

    @Override
    public AssetHead save(AssetHead assetHead) {
        AssetHead assetHead1 = repository.save(assetHead);
        List<AssetDetail> assetDetailsUpd;
        if (assetHead1.getAssetDetails().size() != 0) {
            assetDetailsUpd = new ArrayList<>();
            assetHead1.getAssetDetails().forEach(assetDetail -> {
                if (assetDetail.getMeasureCodeId() != null) {
                    MeasureDefinitionTenantDTO measureDefinitionTenantDb =
                            measureDefinitionOverrideService.findById(assetDetail.getMeasureCodeId());
                    assetDetail.setAssetHead(assetHead1);
                    assetDetail.setMeasureCodeId(assetDetail.getMeasureCodeId());
                    assetDetail.setMeasure(assetDetail.getMeasure());
                    assetDetail.setMeasureDefinitionTenant(measureDefinitionTenantDb);
                    assetDetailsUpd.add(assetDetail);
                }
            });
            if (assetDetailsUpd.size() != 0) {
                assetDetailRepository.saveAll(assetDetailsUpd);
            }
        }
        return assetHead1;
    }

    @Override
    public AssetHead update(AssetHead assetHead) {
        AssetHead assetHeadDb = findById(assetHead.getId());
        assetHeadDb = AssetMapper.toUpdatedAssetHead(assetHeadDb, assetHead);
        return repository.save(assetHeadDb);
    }

    @Override
    public AssetHead findById(Long id) {
        AssetHead assetHead = repository.findById(id).orElseThrow(() -> new NotFoundException(AssetHead.class, id));
        if(assetHead.getAssetDetails().size()!=0){
            assetHead.getAssetDetails().forEach(detail -> {
                MeasureDefinitionTenantDTO measureDefinitionTenantDb = measureDefinitionOverrideService.findById(detail.getMeasureCodeId());
                detail.setMeasureCodeId(detail.getMeasureCodeId());
                detail.setMeasureDefinitionTenant(measureDefinitionTenantDb);
                detail.setMeasure(measureDefinitionTenantDb.getMeasure());
            });
        }
        RegisterHead registerHead = registerHeadRepository.findById(assetHead.getRegisterId()).get();
        List<Long> measureBlockIds =
                registerHead.getRegisterDetails().stream().filter(d->d.getMeasureBlockId()!=null).map(RegisterDetail::getMeasureBlockId).distinct().collect(Collectors.toList());
        RegisterHierarchy registerHierarchy = registerHierarchyService.findById(registerHead.getRegModuleId());
        assetHead.setRegisterHierarchy(registerHierarchy);
        //blockNames with id
        //RegisterHierarchy registerTopHierarchy = registerHierarchyService.getTopLevelHierarchy(registerHead.getRegModuleId());
        List<MeasureBlockHead> measureBlockHeads = measureBlockHeadRepository.findAllByIdInOrderByIdAsc(measureBlockIds);
        if (measureBlockHeads.size()!=0){
            JSONArray blockArray = new JSONArray();
            measureBlockHeads.forEach(block-> {
                JSONObject blockObj= new JSONObject();
                blockObj.put("id",block.getId());
                blockObj.put("blockName",block.getBlockName());
                blockArray.put(blockObj);
            });
            assetHead.setBlocks(blockArray.toString());
        }
        return assetHead;
    }

    @Override
    public List<AssetHead> findAll() {
        return repository.findAll();
    }

    @Override
    public List<AssetHead> findAllByRegisterId(Long registerHeadId) {
        return repository.findAllByRegisterId(registerHeadId);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
