package com.solar.api.tenant.service.extended.register;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.extended.CodeTypeRefMap;
import com.solar.api.tenant.model.extended.ECodeTypeRefMap;
import com.solar.api.tenant.model.extended.assetHead.AssetBlockDetail;
import com.solar.api.tenant.model.extended.assetHead.AssetDetail;
import com.solar.api.tenant.model.extended.assetHead.AssetHead;
import com.solar.api.tenant.model.extended.partner.PartnerDetail;
import com.solar.api.tenant.model.extended.partner.PartnerHead;
import com.solar.api.tenant.model.extended.project.ProjectDetail;
import com.solar.api.tenant.model.extended.project.ProjectHead;
import com.solar.api.tenant.model.extended.register.RegisterDetail;
import com.solar.api.tenant.model.extended.register.RegisterHead;
import com.solar.api.tenant.model.extended.register.RegisterHierarchy;
import com.solar.api.tenant.model.extended.resources.HRDetail;
import com.solar.api.tenant.model.extended.resources.HRHead;
import com.solar.api.tenant.model.extended.resources.MeasureBlockHead;
import com.solar.api.tenant.repository.*;
import com.solar.api.tenant.repository.project.ProjectDetailRepository;
import com.solar.api.tenant.service.extended.assetHead.AssetBlockDetailService;
import com.solar.api.tenant.service.extended.project.ProjectService;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.extended.register.RegisterMapper.toUpdatedRegisterDetail;

@Service
//@Transactional("tenantTransactionManager")
public class RegisterDetailServiceImpl implements RegisterDetailService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegisterDetailRepository repository;
    @Autowired
    private RegisterHeadService registerHeadService;
    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    private AssetHeadRepository assetHeadRepository;
    @Autowired
    private AssetDetailRepository assetDetailRepository;
    @Autowired
    private RegisterHierarchyService registerHierarchyService;
    @Autowired
    private HRHeadRepository hrHeadRepository;
    @Autowired
    private HRDetailRepository hrDetailRepository;
    @Autowired
    private PartnerHeadRepository partnerHeadRepository;
    @Autowired
    private PartnerDetailRepository partnerDetailRepository;
    @Autowired
    private RegisterDetailRepository registerDetailRepository;
    @Autowired
    private CodeTypeRefMapRepository codeTypeRefMapRepository;
//    @Autowired
//    private MeasureBlockService measureBlockService;
    @Autowired
    private MeasureBlockHeadRepository measureBlockHeadRepository;
    @Autowired
    private AssetBlockDetailService assetBlockDetailService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectDetailRepository projectDetailRepository;

    @Override
    public RegisterDetail save(RegisterDetail registerDetail) {
        registerDetail.setRegisterHead(registerHeadService.findById(registerDetail.getRegisterHeadId()));
        MeasureDefinitionTenantDTO measureDefinitionTenant = measureDefinitionOverrideService.findById(registerDetail.getMeasureId());
        registerDetail.setMeasureDefinitionTenant(measureDefinitionTenant);
        registerDetail.setMeasureCode(measureDefinitionTenant.getCode());
        return repository.save(registerDetail);
    }

    @Override
    public List<RegisterDetail> saveAll(List<RegisterDetail> registerDetails) {
        List<RegisterDetail> savingRegisterDetails = new ArrayList<>();
        registerDetails.forEach(registerDetail -> {
            registerDetail.setRegisterHead(registerHeadService.findById(registerDetail.getRegisterHeadId()));
            if (registerDetail.getMeasureId()!= null) {
                MeasureDefinitionTenantDTO measureDefinitionTenant =
                        measureDefinitionOverrideService.findById(registerDetail.getMeasureId());
                registerDetail.setMeasureDefinitionTenant(measureDefinitionTenant);
                registerDetail.setMeasureCode(measureDefinitionTenant.getMeasure());
                if (registerDetail.getMeasureBlockId()!= null) {
                    MeasureBlockHead measureBlockHead = measureBlockHeadRepository.findById(registerDetail.getMeasureBlockId())
                            .orElseThrow(() -> new NotFoundException(MeasureBlockHead.class, registerDetail.getMeasureBlockId()));
//                            measureBlockService.findById(registerDetail.getMeasureBlockId());
                    registerDetail.setBlockName(measureBlockHead.getBlockName());
                    //registerDetail.setMeasureBlockHead(measureBlockHead);
                }
            }
            savingRegisterDetails.add(registerDetail);
        });
        return repository.saveAll(savingRegisterDetails);
    }

    @Override
    public List<RegisterDetail> update(List<RegisterDetail> registerDetails, RegisterHead registerHead) {
        //removing measure from register detail
        List<RegisterDetail> registerDetailDAO= registerDetailRepository.findByRegisterHead(registerHead);
        List<Long> registerDaoIds = registerDetailDAO.stream().map(RegisterDetail::getId).collect(Collectors.toList());
        List<Long> registerDtoIds = registerDetails.stream().filter(id -> id.getId()!=0).map(RegisterDetail::getId).collect(Collectors.toList());
        List<Long> deleteRegIds = new ArrayList<>(CollectionUtils.subtract(registerDaoIds, registerDtoIds));
        List<RegisterDetail> registerDetailUpdated = new ArrayList<>();
        List<Long> measureIds = new ArrayList<>();

        registerDetails.forEach(detail -> {
            RegisterDetail registerDetailDb = new RegisterDetail();
            if (detail.getId() != 0) {
                registerDetailDb = findById(detail.getId());
            }

            registerDetailDb = toUpdatedRegisterDetail(registerDetailDb, detail);
            //add new measure
            if (detail.getMeasureId()!=null){
                measureIds.add(detail.getMeasureId());
                MeasureDefinitionTenantDTO measureDefinitionTenantDb = measureDefinitionOverrideService.findById(detail.getMeasureId());
                registerDetailDb.setMeasureDefinitionTenant(measureDefinitionTenantDb);
                registerDetailDb.setMeasureCodeId(detail.getMeasureId());
                registerDetailDb.setRegisterHead(registerHead);
                if(detail.getMeasureBlockId()!=null){
                    MeasureBlockHead measureBlockHead = measureBlockHeadRepository.findById(detail.getMeasureBlockId())
                            .orElseThrow(() -> new NotFoundException(MeasureBlockHead.class, detail.getMeasureBlockId()));
//                            measureBlockService.findById(detail.getMeasureBlockId());
                    registerDetailDb.setMeasureBlockHead(measureBlockHead);
                    registerDetailDb.setMeasureBlockId(detail.getMeasureBlockId());
                }
            }
            registerDetailUpdated.add(registerDetailDb);
        });
        List<RegisterDetail> registerDetailList = repository.saveAll(registerDetailUpdated);
        //measure and block needs to be removed from register detail
        if (deleteRegIds.size()!=0){
            List<RegisterDetail> removeRegisterDetails = new ArrayList<>();

            deleteRegIds.forEach(removeReg ->{
                RegisterDetail registerDetail = registerDetailDAO.stream().filter(id-> id.getId()==removeReg).findFirst().get();
                removeRegisterDetails.add(registerDetail);
            });
            deleteAll(removeRegisterDetails);
        }
        updateLowLevelRegisters(registerDetails, registerHead, measureIds);
        return registerDetailList;
    }

    @Async
    //TODO: add and remove measures from assetHead,partnerHead and ResourcesHead
    private void updateLowLevelRegisters(List<RegisterDetail> registerDetailDTOs, RegisterHead registerHead, List<Long> measureIds){
        RegisterHierarchy registerHierarchy = registerHierarchyService.getTopLevelHierarchy(registerHead.getRegModuleId());
        //for removal
        List<Long> dtoMeasureIds = registerDetailDTOs.stream().filter(id -> id.getId()!=0 && id.getMeasureId()!=null).map(RegisterDetail::getMeasureCodeId).collect(Collectors.toList());
       // List<Long> dtoRemoveBlockIds = registerDetailDTOs.stream().filter(id -> id.getMeasureBlockId()!=null).map(RegisterDetail::getMeasureBlockId).collect(Collectors.toList());
        List<String> codeTypeRefMaps =
                codeTypeRefMapRepository.findAllByRegModuleIdAndTypeIn(registerHierarchy.getId(),
                        Arrays.asList(ECodeTypeRefMap.REGISTER.name(), ECodeTypeRefMap.MEASURE_BLOCK.name())).stream().map(CodeTypeRefMap::getRefTable).collect(Collectors.toList());
        //update measures in asset if any new record find
        List<Long> subIds = measureIds;

        if (codeTypeRefMaps.contains(ECodeTypeRefMap.ASSET_HEAD.name())) {
            List<AssetDetail> assetDetails = new ArrayList<>();
            List<AssetDetail> removedAssetDetails = null; List<AssetHead> assetHeadsDb = null;
            assetHeadsDb = assetHeadRepository.findAllByRegisterId(registerHead.getId());
            if (Objects.nonNull(assetHeadsDb)) {
                removedAssetDetails = new ArrayList<>();

                for (AssetHead assetHead : assetHeadsDb) {
                    List<Long> listMeasureCodes =
                            assetHead.getAssetDetails().stream().map(AssetDetail::getMeasureCodeId).collect(Collectors.toList());
                    //removed old ids
                    subIds.removeAll(listMeasureCodes);
                    //add measure
                    if (subIds.size() != 0) {
                        subIds.stream().forEach(measureId -> {
                            AssetDetail assetDetail = new AssetDetail();
                            assetDetail.setMeasureCodeId(measureId);
                            assetDetail.setAssetHead(assetHead);
                            RegisterDetail registerDetail = registerDetailRepository.findByRegisterHeadAndMeasureCodeId(registerHead,measureId);
                            assetDetail.setCategory(registerDetail.getCategory());
                            assetDetails.add(assetDetail);
                        });
                    }
                    List<AssetDetail> removeAssetDetailIds = assetHead.getAssetDetails().stream().filter(detail -> !dtoMeasureIds.contains(detail.getMeasureCodeId())).collect(Collectors.toList());                        // remove measure
                    // remove measure
                    if (removeAssetDetailIds.size() != 0) {
                        removedAssetDetails.addAll(removeAssetDetailIds);
                    }
                }
                assetDetailRepository.saveAll(assetDetails);
                LOGGER.info("Added Asset measures {} completed.");
                //if asset detail have remove ids
                if (removedAssetDetails.size() != 0) {
                    assetDetailRepository.deleteAll(removedAssetDetails);
                    LOGGER.info("Deleted Asset measures {} completed.");
                    ///block
                    if (codeTypeRefMaps.contains(ECodeTypeRefMap.ASSET_BLOCK_DETAIL.name())) {
                        List<Long> daoAssetIds = assetHeadsDb.stream().map(AssetHead::getId).collect(Collectors.toList());
                        List<AssetBlockDetail> assetBlockDetailsDao = assetBlockDetailService.findAllByAssetIdIn(daoAssetIds);
                        // blocks are deleted
                        assetBlockDetailsDao = assetBlockDetailsDao.stream().filter(assetBlock-> !dtoMeasureIds.contains(assetBlock.getMeasureId())).collect(Collectors.toList());
                        assetBlockDetailService.deleteAll(assetBlockDetailsDao);
                        LOGGER.info("Deleted Asset Block {} completed.");
                    }
                }
            }

        } else if (codeTypeRefMaps.contains(ECodeTypeRefMap.HR_HEAD.name())) {
            List<HRHead> hrHeadsDb = null;  List<HRDetail> hrDetails = new ArrayList<>();
            List<HRDetail> removedHRDetails = null;

            if (registerHead.getId() != null) {
                removedHRDetails = new ArrayList<>();
                hrHeadsDb = hrHeadRepository.findAllByRegisterId(registerHead.getId());

                if (Objects.nonNull(hrHeadsDb)) {
                    for (HRHead hrHead : hrHeadsDb) {
                        List<Long> listMeasureCodes =
                                hrHead.getHrDetails().stream().map(HRDetail::getMeasureCodeId).collect(Collectors.toList());
                        //removed old ids
                        subIds.removeAll(listMeasureCodes);

                        if (subIds.size()!=0){
                            subIds.stream().forEach(measureId -> {
                                HRDetail hrDetail = new HRDetail();
                                hrDetail.setMeasureCodeId(measureId);
                                hrDetail.setHrHead(hrHead);
                                hrDetails.add(hrDetail);
                            });
                        }

                        List<HRDetail> removeHRDetailIds = hrHead.getHrDetails().stream().filter(detail-> !dtoMeasureIds.contains(detail.getMeasureCodeId())).collect(Collectors.toList());                        // remove measure
                        // remove measure
                        if (removeHRDetailIds.size()!=0){
                            removedHRDetails.addAll(removeHRDetailIds);
                        }
                    }
                    hrDetailRepository.saveAll(hrDetails);
                    LOGGER.info("Added HR measures {} completed.");
                    //if hr detail have remove ids
                    if (removedHRDetails.size()!=0){
                        hrDetailRepository.deleteAll(removedHRDetails);
                        LOGGER.info("Deleted HR measures {} completed.");
                    }


                }
            }
        } else if (codeTypeRefMaps.contains(ECodeTypeRefMap.PARTNER_HEAD.name())) {
            List<PartnerHead> partnerHeadsDb = null; List<PartnerDetail> partnerDetails = new ArrayList<>();
            List<PartnerDetail> removedPartnerDetails = null;

            if (registerHead.getId() != null) {
                removedPartnerDetails = new ArrayList<>();
                partnerHeadsDb = partnerHeadRepository.findAllByRegisterId(registerHead.getId());

                if (Objects.nonNull(partnerHeadsDb)) {
                    for (PartnerHead partnerHead : partnerHeadsDb) {
                        List<Long> listMeasureCodes =
                                partnerHead.getPartnerDetails().stream().map(PartnerDetail::getMeasureCodeId).collect(Collectors.toList());
                        //removed old ids
                        subIds.removeAll(listMeasureCodes);

                        if (subIds.size()!=0){
                            subIds.stream().forEach(measureId -> {
                                PartnerDetail partnerDetail = new PartnerDetail();
                                partnerDetail.setMeasureCodeId(measureId);
                                partnerDetail.setPartnerHead(partnerHead);
                                partnerDetails.add(partnerDetail);
                            });
                        }

                        List<PartnerDetail> removePartnerDetailIds = partnerHead.getPartnerDetails().stream().filter(detail-> !dtoMeasureIds.contains(detail.getMeasureCodeId())).collect(Collectors.toList());                        // remove measure
                        // remove measure
                        if (removePartnerDetailIds.size()!=0){
                            removedPartnerDetails.addAll(removePartnerDetailIds);
                        }
                    }
                    partnerDetailRepository.saveAll(partnerDetails);
                    LOGGER.info("Added Partner measures {} completed.");
                    //remove measure ids from partner
                    if (removedPartnerDetails.size()!=0){
                        partnerDetailRepository.deleteAll(removedPartnerDetails);
                        LOGGER.info("Deleted Partner measures {} completed.");
                    }
                }
            }

        } else if (codeTypeRefMaps.contains(ECodeTypeRefMap.PROJECT_HEAD.name())) {
            List<ProjectDetail> projectDetails = new ArrayList<>();
            List<ProjectDetail> removedProjectDetails = null; List<ProjectHead> projectHeadsDb = null;
            projectHeadsDb = projectService.findAllByRegisterId(registerHead.getId());
            if (Objects.nonNull(projectHeadsDb)) {
                removedProjectDetails = new ArrayList<>();

                for (ProjectHead projectHead : projectHeadsDb) {
                    List<Long> listMeasureCodes =
                            projectHead.getProjectDetails().stream().map(ProjectDetail::getMeasureCodeId).collect(Collectors.toList());
                    //removed old ids
                    subIds.removeAll(listMeasureCodes);
                    //add measure
                    if (subIds.size() != 0) {
                        subIds.stream().forEach(measureId -> {
                            ProjectDetail projectDetail = new ProjectDetail();
                            projectDetail.setMeasureCodeId(measureId);
                            projectDetail.setProjectHead(projectHead);
                            RegisterDetail registerDetail = registerDetailRepository.findByRegisterHeadAndMeasureCodeId(registerHead,measureId);
                            projectDetail.setCategory(registerDetail.getCategory());
                            projectDetails.add(projectDetail);
                        });
                    }
                    List<ProjectDetail> removeProjectDetailIds = projectHead.getProjectDetails().stream().filter(detail -> !dtoMeasureIds.contains(detail.getMeasureCodeId())).collect(Collectors.toList());                        // remove measure
                    // remove measure
                    if (removeProjectDetailIds.size() != 0) {
                        removedProjectDetails.addAll(removeProjectDetailIds);
                    }
                }
                projectDetailRepository.saveAll(projectDetails);
                LOGGER.info("Added Project measures {} completed.");
                //if asset detail have remove ids
                if (removedProjectDetails.size() != 0) {
                    projectDetailRepository.deleteAll(removedProjectDetails);
                    LOGGER.info("Deleted Project measures {} completed.");

                }
            }
        }
    }

    @Override
    public RegisterDetail findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(RegisterDetail.class, id));
    }

    @Override
    public List<RegisterDetail> findByRegister(RegisterHead registerHead) {
        return repository.findByRegisterHead(registerHead);
    }

    @Override
    public List<RegisterDetail> findByRegisterIdAndBlockIdNotNull(Long registerId) {
        return repository.findAllByRegisterIdAndBlockIdNotNull(registerId);
    }

    @Override
    public List<RegisterDetail> findAll() {
        return repository.findAll();
    }

    // aka SubscriptionRateMatrixDetail
    @Override
    public RegisterDetail findByMeasureCode(String measureCode) {
        return repository.findByMeasureCode(measureCode);
    }

    @Override
    public List<String> findMeasureCodesByVariableByDetail(String variableByDetail) {
        return repository.findMeasureCodesByVariableByDetail(variableByDetail);
    }

    @Override
    public List<RegisterDetail> findAllByRegisterAndBlockId(RegisterHead registerHead, Long blockId) {
        return repository.findAllByRegisterHeadAndMeasureBlockId(registerHead,blockId);
    }

    // aka SubscriptionRateMatrixDetail

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public void deleteAll(List<RegisterDetail> registerDetails) {
        repository.deleteAll(registerDetails);
    }
}
