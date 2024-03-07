package com.solar.api.tenant.mapper.extended.register;

import com.solar.api.tenant.model.extended.register.RegisterDetail;
import com.solar.api.tenant.model.extended.register.RegisterHead;
import com.solar.api.tenant.model.extended.register.RegisterHierarchy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RegisterMapper {

    // RegisterHierarchy ////////////////////////////////////////////////
    public static RegisterHierarchy toRegisterHierarchy(RegisterHierarchyDTO registerHierarchyDTO) {
        if (registerHierarchyDTO == null) {
            return null;
        }
        return RegisterHierarchy.builder()
                .id(registerHierarchyDTO.getId())
                .level(registerHierarchyDTO.getLevel())
                .name(registerHierarchyDTO.getName())
                .code(registerHierarchyDTO.getCode())
                .sequenceNo(registerHierarchyDTO.getSequenceNo())
                .alias(registerHierarchyDTO.getAlias())
                .description(registerHierarchyDTO.getDescription())
                .category(registerHierarchyDTO.getCategory())
                .parent(registerHierarchyDTO.getParent())
                .parentId(registerHierarchyDTO.getParentId())
                .registered(registerHierarchyDTO.getRegistered())
                .build();
    }

    public static RegisterHierarchyDTO toRegisterHierarchyDTO(RegisterHierarchy registerHierarchy) {
        if (registerHierarchy == null) {
            return null;
        }
        return RegisterHierarchyDTO.builder()
                .id(registerHierarchy.getId())
                .level(registerHierarchy.getLevel())
                .name(registerHierarchy.getName())
                .code(registerHierarchy.getCode())
                .sequenceNo(registerHierarchy.getSequenceNo())
                .alias(registerHierarchy.getAlias())
                .description(registerHierarchy.getDescription())
                .category(registerHierarchy.getCategory())
                .parent(registerHierarchy.getParent())
                .parentId(registerHierarchy.getParentId())
                .registered(registerHierarchy.getRegistered())
                .subHierarchies(registerHierarchy.getSubHierarchies() != null ?
                        toRegisterHierarchyDTOs(registerHierarchy.getSubHierarchies()) : Collections.emptyList())
//                .createdAt(registerHierarchy.getCreatedAt())
//                .updatedAt(registerHierarchy.getUpdatedAt())
                .build();
    }

    public static RegisterHierarchy toUpdatedRegisterHierarchy(RegisterHierarchy registerHierarchy,
                                                               RegisterHierarchy registerHierarchyUpdate) {
        registerHierarchy.setLevel(registerHierarchyUpdate.getLevel() == null ? registerHierarchy.getLevel() :
                registerHierarchyUpdate.getLevel());
        registerHierarchy.setName(registerHierarchyUpdate.getName() == null ? registerHierarchy.getName() :
                registerHierarchyUpdate.getName());
        registerHierarchy.setCode(registerHierarchyUpdate.getCode() == null ? registerHierarchy.getCode() :
                registerHierarchyUpdate.getCode());
        registerHierarchy.setSequenceNo(registerHierarchyUpdate.getSequenceNo() == null ?
                registerHierarchy.getSequenceNo() : registerHierarchyUpdate.getSequenceNo());
        registerHierarchy.setAlias(registerHierarchyUpdate.getAlias() == null ? registerHierarchy.getAlias() :
                registerHierarchyUpdate.getAlias());
        registerHierarchy.setDescription(registerHierarchyUpdate.getDescription() == null ?
                registerHierarchy.getDescription() : registerHierarchyUpdate.getDescription());
        registerHierarchy.setCategory(registerHierarchyUpdate.getCategory() == null ?
                registerHierarchy.getCategory() : registerHierarchyUpdate.getCategory());
        registerHierarchy.setParent(registerHierarchyUpdate.getParent() == null ? registerHierarchy.getParent() :
                registerHierarchyUpdate.getParent());
        registerHierarchy.setParentId(registerHierarchyUpdate.getParentId() == null ?
                registerHierarchy.getParentId() : registerHierarchyUpdate.getParentId());
        registerHierarchy.setRegistered(registerHierarchyUpdate.getRegistered() == null ?
                registerHierarchy.getRegistered() : registerHierarchyUpdate.getRegistered());
        return registerHierarchy;
    }

    public static List<RegisterHierarchy> toRegisterHierarchies(List<RegisterHierarchyDTO> registerHierarchyDTOS) {
        return registerHierarchyDTOS.stream().map(r -> toRegisterHierarchy(r)).collect(Collectors.toList());
    }

    public static List<RegisterHierarchyDTO> toRegisterHierarchyDTOs(List<RegisterHierarchy> registerHierarchies) {
        return registerHierarchies.stream().map(r -> toRegisterHierarchyDTO(r)).collect(Collectors.toList());
    }

    // RegisterHead ////////////////////////////////////////////////
    public static RegisterHead toRegisterHead(RegisterHeadDTO registerHeadDTO) {
        if (registerHeadDTO == null) {
            return null;
        }
        return RegisterHead.builder()
                .id(registerHeadDTO.getId())
                .refName(registerHeadDTO.getRefName())
                .regModuleId(registerHeadDTO.getRegModuleId())
                .recordLevelInd(registerHeadDTO.getRecordLevelInd())
                .description(registerHeadDTO.getDescription())
                .status(registerHeadDTO.getStatus())
                .registerDetails(registerHeadDTO.getRegisterDetails() != null ?
                        toRegisterDetails(registerHeadDTO.getRegisterDetails()) : Collections.emptyList())
                .createDate(registerHeadDTO.getCreateDate())
                .build();
    }

    public static RegisterHeadDTO toRegisterHeadDTO(RegisterHead registerHead) {
        if (registerHead == null) {
            return null;
        }
        return RegisterHeadDTO.builder()
                .id(registerHead.getId())
                .refName(registerHead.getRefName())
                //.regModuleId(registerHead.getRegModuleId())
                .recordLevelInd(registerHead.getRecordLevelInd())
                .regModuleId(registerHead.getRegModuleId())
                .description(registerHead.getDescription())
                .status(registerHead.getStatus())
                .blocks(registerHead.getBlocks())
                .registerDetails(registerHead.getRegisterDetails() != null ?
                        toRegisterDetailDTOs(registerHead.getRegisterDetails()) : Collections.emptyList())
                .createDate(registerHead.getCreateDate())
                .createdAt(registerHead.getCreatedAt())
                .updatedAt(registerHead.getUpdatedAt())
                .registerHierarchy(registerHead.getRegisterHierarchy() != null ?
                        toRegisterHierarchyDTO(registerHead.getRegisterHierarchy()) : null)
                .build();
    }

    public static RegisterHead toUpdatedRegisterHead(RegisterHead registerHead, RegisterHead registerHeadUpdate) {
        registerHead.setRefName(registerHeadUpdate.getRefName() == null ? registerHead.getRefName() :
                registerHeadUpdate.getRefName());
        //registerHead.setRegisterCode(registerHeadUpdate.getRegisterCode() == null ? registerHead.getRegisterCode()
        // : registerHeadUpdate.getRegisterCode());
        registerHead.setRecordLevelInd(registerHeadUpdate.getRecordLevelInd() == null ?
                registerHead.getRecordLevelInd() : registerHeadUpdate.getRecordLevelInd());
        registerHead.setDescription(registerHeadUpdate.getDescription() == null ? registerHead.getDescription() :
                registerHeadUpdate.getDescription());
        registerHead.setStatus(registerHeadUpdate.getStatus() == null ? registerHead.getStatus() :
                registerHeadUpdate.getStatus());
        registerHead.setCreateDate(registerHeadUpdate.getCreateDate() == null ? registerHead.getCreateDate() :
                registerHeadUpdate.getCreateDate());
        return registerHead;
    }

    public static List<RegisterHead> toRegisterHeads(List<RegisterHeadDTO> registerHeadDTOS) {
        return registerHeadDTOS.stream().map(r -> toRegisterHead(r)).collect(Collectors.toList());
    }

    public static List<RegisterHeadDTO> toRegisterHeadDTOs(List<RegisterHead> registerHeads) {
        return registerHeads.stream().map(r -> toRegisterHeadDTO(r)).collect(Collectors.toList());
    }

    // RegisterDetail ////////////////////////////////////////////////
    public static RegisterDetail toRegisterDetail(RegisterDetailDTO registerDetailDTO) {
        if (registerDetailDTO == null) {
            return null;
        }
        return RegisterDetail.builder()
                .id(registerDetailDTO.getId())
                .registerHeadId(registerDetailDTO.getRegisterHeadId())
                .blockName(registerDetailDTO.getBlockName())
                .measureBlockId(registerDetailDTO.getMeasureBlockId())
                .measureCode(registerDetailDTO.getMeasureCode())
                .measureCodeId(registerDetailDTO.getMeasureId())
                //.measureDefinition(registerDetailDTO.getMeasureDefinition()!=null ? MeasureDefinitionMapper
                // .toMeasureDefinition(registerDetailDTO.getMeasureDefinition()) : null)
                .measureId(registerDetailDTO.getMeasureId())
                .defaultValue(registerDetailDTO.getDefaultValue())
                .level(registerDetailDTO.getLevel())
                .category(registerDetailDTO.getCategory())
                .sequenceNumber(registerDetailDTO.getSequenceNumber())
                .multiEntry(registerDetailDTO.getMultiEntry())
                .measureUnique(registerDetailDTO.getMeasureUnique())
                .mandatory(registerDetailDTO.getMandatory())
                .filterByInd(registerDetailDTO.getFilterByInd())
                .variableByDetail(registerDetailDTO.getVariableByDetail())
                .flags(registerDetailDTO.getFlags())
                .build();
    }

    public static RegisterDetailDTO toRegisterDetailDTO(RegisterDetail registerDetail) {
        if (registerDetail == null) {
            return null;
        }
        return RegisterDetailDTO.builder()
                .id(registerDetail.getId())
                .registerHeadId(registerDetail.getRegisterHeadId())
                //.registerHead(registerDetail.getRegisterHead())
                .measureCode(registerDetail.getMeasureCode())
                .measureId(registerDetail.getMeasureCodeId())
                .measureDefinition(registerDetail.getMeasureDefinitionTenant())
                .blockName(registerDetail.getBlockName())
                .measureBlockId(registerDetail.getMeasureBlockId())
                //.measureBlockHead(registerDetail.getMeasureBlockHead() != null ?
                //      MeasureBlockMapper.toMeasureBlockHeadDTO(registerDetail.getMeasureBlockHead()) : null)
                .defaultValue(registerDetail.getDefaultValue())
                .level(registerDetail.getLevel())
                .category(registerDetail.getCategory())
                .sequenceNumber(registerDetail.getSequenceNumber())
                .multiEntry(registerDetail.getMultiEntry())
                .measureUnique(registerDetail.getMeasureUnique())
                .mandatory(registerDetail.getMandatory())
                .filterByInd(registerDetail.getFilterByInd())
                .variableByDetail(registerDetail.getVariableByDetail())
                .flags(registerDetail.getFlags())
                .createdAt(registerDetail.getCreatedAt())
                .updatedAt(registerDetail.getUpdatedAt())
                .build();
    }

    public static RegisterDetail toUpdatedRegisterDetail(RegisterDetail registerDetail,
                                                         RegisterDetail registerDetailUpdate) {
        //registerDetail.setRegisterId(registerDetailUpdate.getRegisterId() == null ? registerDetail.getRegisterId()
        // : registerDetailUpdate.getRegisterId());
        //registerDetail.setRegisterHead(registerDetailUpdate.getRegisterHead()==null ? registerDetail
        // .getRegisterHead() : registerDetailUpdate.getRegisterHead());
        //registerDetail.setMeasureCodeId(Objects.isNull(registerDetailUpdate.getMeasureCodeId()) ? registerDetail
        // .getMeasureId() : registerDetailUpdate.getMeasureCodeId());
        registerDetail.setMeasureBlockId(registerDetailUpdate.getMeasureBlockId() == null ?
                registerDetail.getMeasureBlockId() : registerDetailUpdate.getMeasureBlockId());
        registerDetail.setMeasureCode(registerDetailUpdate.getMeasureCode() == null ?
                registerDetail.getMeasureCode() : registerDetailUpdate.getMeasureCode());
        registerDetail.setMeasureDefinitionTenant(registerDetailUpdate.getMeasureDefinitionTenant() == null ?
                registerDetail.getMeasureDefinitionTenant() : registerDetailUpdate.getMeasureDefinitionTenant());
        registerDetail.setDefaultValue(registerDetailUpdate.getDefaultValue() == null ?
                registerDetail.getDefaultValue() : registerDetailUpdate.getDefaultValue());
        registerDetail.setLevel(registerDetailUpdate.getLevel() == null ? registerDetail.getLevel() :
                registerDetailUpdate.getLevel());
        registerDetail.setCategory(registerDetailUpdate.getCategory() == null ? registerDetail.getCategory() :
                registerDetailUpdate.getCategory());
        registerDetail.setSequenceNumber(registerDetailUpdate.getSequenceNumber() == null ?
                registerDetail.getSequenceNumber() : registerDetailUpdate.getSequenceNumber());
        registerDetail.setMultiEntry(registerDetailUpdate.getMultiEntry() == null ? registerDetail.getMultiEntry() :
                registerDetailUpdate.getMultiEntry());
        registerDetail.setMeasureUnique(registerDetailUpdate.getMeasureUnique() == null ? registerDetail.getMeasureUnique() :
                registerDetailUpdate.getMeasureUnique());
        registerDetail.setMandatory(registerDetailUpdate.getMandatory() == null ? registerDetail.getMandatory() :
                registerDetailUpdate.getMandatory());
        registerDetail.setFilterByInd(registerDetailUpdate.getFilterByInd() == null ?
                registerDetail.getFilterByInd() : registerDetailUpdate.getFilterByInd());
        registerDetail.setVariableByDetail(registerDetailUpdate.getVariableByDetail() == null ?
                registerDetail.getVariableByDetail() : registerDetailUpdate.getVariableByDetail());
        registerDetail.setFlags(registerDetailUpdate.getFlags() == null ? registerDetail.getFlags() :
                registerDetailUpdate.getFlags());
        return registerDetail;
    }

    public static List<RegisterDetail> toRegisterDetails(List<RegisterDetailDTO> registerDetailDTOS) {
        return registerDetailDTOS.stream().map(r -> toRegisterDetail(r)).collect(Collectors.toList());
    }

    public static List<RegisterDetailDTO> toRegisterDetailDTOs(List<RegisterDetail> registerDetails) {
        return registerDetails.stream().map(r -> toRegisterDetailDTO(r)).collect(Collectors.toList());
    }
}
