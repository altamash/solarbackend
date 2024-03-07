package com.solar.api.tenant.service.extended.register;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.service.PortalAttributeSAASService;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.mapper.extended.register.RegisterMapper;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.model.extended.register.RegisterDetail;
import com.solar.api.tenant.model.extended.register.RegisterHead;
import com.solar.api.tenant.model.extended.register.RegisterHierarchy;
import com.solar.api.tenant.model.extended.resources.MeasureBlockHead;
import com.solar.api.tenant.repository.MeasureBlockHeadRepository;
import com.solar.api.tenant.repository.MeasureDefinitionTenantRepository;
import com.solar.api.tenant.repository.RegisterDetailRepository;
import com.solar.api.tenant.repository.RegisterHeadRepository;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
//@Transactional("tenantTransactionManager")
public class RegisterHeadServiceImpl implements RegisterHeadService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private RegisterHeadRepository repository;
    @Autowired
    private MeasureDefinitionTenantRepository measureDefinitionTenantRepository;
    @Autowired
    private PortalAttributeSAASService portalAttributeSAASService;
//    @Autowired
//    private PortalAttributeTenantService portalAttributeTenantService;
    @Autowired
    private PortalAttributeOverrideService attributeOverrideService;
    @Autowired
    RegisterDetailRepository registerDetailRepository;
    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    private RegisterHierarchyService registerHierarchyService;
    @Autowired
    private MeasureBlockHeadRepository measureBlockHeadRepository;

    @Override
    public RegisterHead save(RegisterHead registerHead) {
        RegisterHead registerHeadSaved = repository.save(registerHead);
        List<RegisterDetail> registerDetails;
        if(registerHead.getRegisterDetails().size()!=0){
            registerDetails = new ArrayList<>();
            for (int i = 0; i < registerHead.getRegisterDetails().size(); i++) {
                if (registerHead.getRegisterDetails().get(i).getMeasureId() != null) {
                    registerHeadSaved.getRegisterDetails().get(i).setRegisterHead(registerHeadSaved);
                    MeasureDefinitionTenantDTO measureDefinitionTenant =
                            measureDefinitionOverrideService.findById(registerHead.getRegisterDetails().get(i).getMeasureId());
                    registerHeadSaved.getRegisterDetails().get(i).setMeasureDefinitionTenant(measureDefinitionTenant);
                    registerHeadSaved.getRegisterDetails().get(i).setMeasureCodeId(registerHead.getRegisterDetails().get(i).getMeasureId());
                    registerHeadSaved.getRegisterDetails().get(i).setMeasureCode(measureDefinitionTenant.getMeasure());
                    registerDetails.add(registerHeadSaved.getRegisterDetails().get(i));
                    //saving MeasureBlock
                    if (registerHead.getRegisterDetails().get(i).getMeasureBlockId() != null) {
                        Long measureBlockHeadId = registerHead.getRegisterDetails().get(i).getMeasureBlockId();
                        MeasureBlockHead measureBlockHead =
                                measureBlockHeadRepository.findById(measureBlockHeadId).orElseThrow(() -> new NotFoundException(MeasureBlockHead.class, measureBlockHeadId));
                        registerHeadSaved.getRegisterDetails().get(i).setMeasureBlockHead(measureBlockHead);
                        registerHeadSaved.getRegisterDetails().get(i).setMeasureBlockId(registerHead.getRegisterDetails().get(i).getMeasureBlockId());
                        registerHeadSaved.getRegisterDetails().get(i).setBlockName(measureBlockHead.getBlockName());
                        registerDetails.add(registerHeadSaved.getRegisterDetails().get(i));
                    }
                }
            }
            if(registerDetails.size()!=0){
                registerDetailRepository.saveAll(registerDetails);
            }
        }
        RegisterHierarchy registerHierarchy = registerHierarchyService.findById(registerHead.getRegModuleId());
        registerHierarchy.setRegistered(true);
        registerHierarchyService.save(registerHierarchy);
        return registerHeadSaved;
    }

    @Override
    public RegisterHead update(RegisterHead registerHead) {
        RegisterHead registerHeadDb = findById(registerHead.getId());
        registerHead = RegisterMapper.toUpdatedRegisterHead(registerHeadDb, registerHead);
        return repository.save(registerHead);
    }

    @Override
    public RegisterHead findById(Long id) {
        RegisterHead registerHead = repository.findById(id).orElseThrow(() -> new NotFoundException(RegisterHead.class, id));
        registerHead.getRegisterDetails().forEach(detail -> {
            detail = setMeasureAndBlock(detail);
        });
        return registerHead;
    }

    private RegisterDetail setMeasureAndBlock(RegisterDetail registerDetail){
        MeasureDefinitionTenantDTO measureDefinitionTenant =
                measureDefinitionOverrideService.findById(registerDetail.getMeasureCodeId());
        registerDetail.setMeasureDefinitionTenant(measureDefinitionTenant);
        registerDetail.setMeasureCode(measureDefinitionTenant.getMeasure());

        if(registerDetail.getMeasureBlockId() != null){
            MeasureBlockHead measureBlockHead =
                    measureBlockHeadRepository.findById(registerDetail.getMeasureBlockId()).orElseThrow(() -> new NotFoundException(MeasureBlockHead.class, registerDetail.getMeasureBlockId()));
            registerDetail.setBlockName(measureBlockHead.getBlockName());
            //details.setMeasureBlockHead(measureBlockHead);
        }
        return registerDetail;
    }

    @Override
    public List<RegisterHead> findAll() {
        return repository.findAll();
    }

    // aka SubscriptionRateMatrixHead
    @Override
    public RegisterHead findByRefName(String refName) {
        return repository.findByRefName(refName);
    }

    @Override
    public RegisterHead findMeasureByRegisterId(Long registerHeadId) {
        RegisterHead registerHead = findById(registerHeadId);
        RegisterHierarchy registerHierarchy = registerHierarchyService.getTopLevelHierarchy(registerHead.getRegModuleId());
        List<MeasureBlockHead> measureBlockHeads =
                measureBlockHeadRepository.findAllByRegModuleIdOrderByIdDesc(registerHierarchy.getId());
        if (measureBlockHeads.size()!=0){
            JSONArray blockArray = new JSONArray();
            measureBlockHeads.forEach(block-> {
                JSONObject blockObj= new JSONObject();
                blockObj.put("id",block.getId());
                blockObj.put("blockName",block.getBlockName());
                blockArray.put(blockObj);
            });
            registerHead.setBlocks(blockArray.toString());
        }
        // set measureDefinition in registerDetail
        registerHead.getRegisterDetails().forEach(detail -> {
            if (Objects.nonNull(detail.getMeasureDefinitionTenant())) {
                detail = setMeasureAndBlock(detail);
                //for portalAttributeValues
                if (detail.getMeasureDefinitionTenant().getAttributeIdRef()!=null && !detail.getMeasureDefinitionTenant().getAttributeIdRef().isEmpty()) {
                    List<PortalAttributeValueTenantDTO> portalAttributeValues =
                            attributeOverrideService.findByPortalAttributeId(Long.parseLong(detail.getMeasureDefinitionTenant().getAttributeIdRef()));
                    /*JSONArray jsonArray = new JSONArray();
                    portalAttributeValues.forEach(attribute -> {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id", attribute.getId());
                            jsonObject.put("value", attribute.getAttributeValue());
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            LOGGER.warn(e.getMessage());
                        }
                    });
                    String attrNames = jsonArray.toString();*/
//                    detail.getMeasureDefinitionTenant().setPortalAttributeValues(attrNames);
                    detail.getMeasureDefinitionTenant().setPortalAttributeValues(portalAttributeValues.stream().map(pa -> pa.getAttributeValue()).collect(Collectors.toList()));
                    detail.getMeasureDefinitionTenant().setPortalAttributeValueDTOs(portalAttributeValues);
                }
            }
        });
        return registerHead;
    }

/*
    @Override
    public List<RegisterHead> findByRegisterCodeAndStatus(String registerCode, String status) {
        return repository.findByRegisterCodeAndStatus(registerCode, status);
    }
*/

    @Override
    public List<RegisterHead> findByRegisterIdsIn(List<Long> ids) {
        return repository.findByIdsIn(ids);
    }

 /*    @Override
    public List<RegisterHead> findByRegisterCode(String registerCode) {
        return repository.findByRegisterCode(registerCode);
    }*/

    @Override
    public List<RegisterHead> findAllByRegModuleId(Long regModuleId) {
        RegisterHierarchy registerHierarchy = registerHierarchyService.findById(regModuleId);
        List<RegisterHead> registerHeads = repository.findAllByRegModuleId(regModuleId);
        registerHeads.forEach(heads -> {
            heads.setRegisterHierarchy(registerHierarchy);
            heads.getRegisterDetails().forEach(detail -> {
                if (detail.getMeasureCodeId() != null) {
                    detail = setMeasureAndBlock(detail);
                }
            });
        });
        return registerHeads;
    }
    // aka SubscriptionRateMatrixHead

    @Override
    public void delete(Long id) {
        RegisterHead registerHead = findById(id);
        RegisterHierarchy registerHierarchy = registerHierarchyService.findById(registerHead.getRegModuleId());
        registerHierarchy.setRegistered(null);
        registerHierarchyService.save(registerHierarchy);
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        List<Long> ids = new ArrayList<>();
        findAll().forEach(h -> ids.add(h.getRegModuleId()));
        ArrayList<RegisterHierarchy> registerHierarchies = new ArrayList<>();
        ids.forEach(i -> {
            RegisterHierarchy hierarchy = registerHierarchyService.findById(i);
            hierarchy.setRegistered(null);
            registerHierarchies.add(hierarchy);
        });
        registerHierarchyService.saveAll(registerHierarchies);
        repository.deleteAll();
    }
}
