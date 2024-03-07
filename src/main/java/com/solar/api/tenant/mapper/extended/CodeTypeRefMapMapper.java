package com.solar.api.tenant.mapper.extended;

import com.solar.api.tenant.model.extended.CodeTypeRefMap;

import java.util.List;
import java.util.stream.Collectors;

public class CodeTypeRefMapMapper {

    public static CodeTypeRefMap toCodeTypeRefMap(CodeTypeRefMapDTO codeTypeRefMapDTO) {
        if (codeTypeRefMapDTO == null) {
            return null;
        }
        return CodeTypeRefMap.builder()
                .id(codeTypeRefMapDTO.getId())
                .regCode(codeTypeRefMapDTO.getRegCode())
                .regModuleId(codeTypeRefMapDTO.getRegModuleId())
                .type(codeTypeRefMapDTO.getType())
                .refTable(codeTypeRefMapDTO.getRefTable())
                .build();
    }

    public static CodeTypeRefMapDTO toCodeTypeRefMapDTO(CodeTypeRefMap codeTypeRefMap) {
        if (codeTypeRefMap == null) {
            return null;
        }
        return CodeTypeRefMapDTO.builder()
                .id(codeTypeRefMap.getId())
                .regCode(codeTypeRefMap.getRegCode())
                .regModuleId(codeTypeRefMap.getRegModuleId())
                .type(codeTypeRefMap.getType())
                .refTable(codeTypeRefMap.getRefTable())
                .createdAt(codeTypeRefMap.getCreatedAt())
                .updatedAt(codeTypeRefMap.getUpdatedAt())
                .build();
    }

    public static CodeTypeRefMap toUpdatedCodeTypeRefMap(CodeTypeRefMap codeTypeRefMap,
                                                         CodeTypeRefMap codeTypeRefMapUpdate) {
        codeTypeRefMap.setRegCode(codeTypeRefMapUpdate.getRegCode() == null ? codeTypeRefMap.getRegCode() :
                codeTypeRefMapUpdate.getRegCode());
        codeTypeRefMap.setRegModuleId(codeTypeRefMapUpdate.getRegModuleId() == null ? codeTypeRefMap.getRegModuleId() :
                codeTypeRefMapUpdate.getRegModuleId());
        codeTypeRefMap.setType(codeTypeRefMapUpdate.getType() == null ? codeTypeRefMap.getType() :
                codeTypeRefMapUpdate.getType());
        codeTypeRefMap.setRefTable(codeTypeRefMapUpdate.getRefTable() == null ? codeTypeRefMap.getRefTable() :
                codeTypeRefMapUpdate.getRefTable());
        return codeTypeRefMap;
    }

    public static List<CodeTypeRefMap> toCodeTypeRefMaps(List<CodeTypeRefMapDTO> codeTypeRefMapDTOS) {
        return codeTypeRefMapDTOS.stream().map(c -> toCodeTypeRefMap(c)).collect(Collectors.toList());
    }

    public static List<CodeTypeRefMapDTO> toCodeTypeRefMapDTOs(List<CodeTypeRefMap> codeTypeRefMaps) {
        return codeTypeRefMaps.stream().map(c -> toCodeTypeRefMapDTO(c)).collect(Collectors.toList());
    }
}
