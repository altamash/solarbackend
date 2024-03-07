package com.solar.api.saas.mapper.permission.component;

import com.solar.api.saas.mapper.extended.ComponentsByRoleDTO;
import com.solar.api.saas.model.extended.ComponentsByRole;
import com.solar.api.saas.model.permission.component.ComponentLibrary;
import com.solar.api.saas.model.permission.component.ComponentTypeProvision;

import java.util.List;
import java.util.stream.Collectors;

public class ComponentLibraryMapper {

    // ComponentLibrary ////////////////////////////////////////////////////////////////
    public static ComponentLibrary toComponentLibrary(ComponentLibraryDTO componentLibraryDTO) {
        if (componentLibraryDTO == null) {
            return null;
        }
        return ComponentLibrary.builder()
                .id(componentLibraryDTO.getId())
                .componentName(componentLibraryDTO.getComponentName())
                .description(componentLibraryDTO.getDescription())
                .level(componentLibraryDTO.getLevel())
                .parentId(componentLibraryDTO.getParentId())
                .moduleId(componentLibraryDTO.getModuleId())
                .subModuleId(componentLibraryDTO.getSubModuleId())
//                .compReference(toComponentTypeProvision(componentLibraryDTO.getCompReference()))
                .compType(componentLibraryDTO.getCompType())
                .source(componentLibraryDTO.getSource())
                .build();
    }

    public static ComponentLibraryDTO toComponentLibraryDTO(ComponentLibrary componentLibrary) {
        if (componentLibrary == null) {
            return null;
        }
        return ComponentLibraryDTO.builder()
                .id(componentLibrary.getId())
                .componentName(componentLibrary.getComponentName())
                .description(componentLibrary.getDescription())
                .level(componentLibrary.getLevel())
                .parentId(componentLibrary.getParentId())
                .moduleId(componentLibrary.getModuleId())
                .subModuleId(componentLibrary.getSubModuleId())
//                .compReference(toComponentTypeProvisionDTO(componentLibrary.getCompReference()))
                .compType(componentLibrary.getCompType())
                .source(componentLibrary.getSource())
                .createdAt(componentLibrary.getCreatedAt())
                .updatedAt(componentLibrary.getUpdatedAt())
                .build();
    }

    public static ComponentLibrary toUpdatedComponentLibrary(ComponentLibrary componentLibrary,
                                                             ComponentLibrary componentLibraryUpdate) {
        componentLibrary.setComponentName(componentLibraryUpdate.getComponentName() == null ?
                componentLibrary.getComponentName() : componentLibraryUpdate.getComponentName());
        componentLibrary.setDescription(componentLibraryUpdate.getDescription() == null ?
                componentLibrary.getDescription() : componentLibraryUpdate.getDescription());
        componentLibrary.setLevel(componentLibraryUpdate.getLevel() == null ? componentLibrary.getLevel() :
                componentLibraryUpdate.getLevel());
        componentLibrary.setParentId(componentLibraryUpdate.getParentId() == null ? componentLibrary.getParentId() :
                componentLibraryUpdate.getParentId());
        componentLibrary.setModuleId(componentLibraryUpdate.getModuleId() == null ? componentLibrary.getModuleId() :
                componentLibraryUpdate.getModuleId());
        componentLibrary.setSubModuleId(componentLibraryUpdate.getSubModuleId() == null ?
                componentLibrary.getSubModuleId() : componentLibraryUpdate.getSubModuleId());
//        componentLibrary.setCompReference(componentLibraryUpdate.getCompReference() == null ?
//                componentLibrary.getCompReference() : componentLibraryUpdate.getCompReference());
        componentLibrary.setCompType(componentLibraryUpdate.getCompType() == null ? componentLibrary.getCompType() :
                componentLibraryUpdate.getCompType());
        componentLibrary.setSource(componentLibraryUpdate.getSource() == null ? componentLibrary.getSource() :
                componentLibraryUpdate.getSource());
        return componentLibrary;
    }

    public static List<ComponentLibrary> toComponentLibraries(List<ComponentLibraryDTO> componentLibraryDTOS) {
        return componentLibraryDTOS.stream().map(c -> toComponentLibrary(c)).collect(Collectors.toList());
    }

    public static List<ComponentLibraryDTO> toComponentLibraryDTOs(List<ComponentLibrary> componentLibrarys) {
        return componentLibrarys.stream().map(c -> toComponentLibraryDTO(c)).collect(Collectors.toList());
    }

    // ComponentTypeProvision ////////////////////////////////////////////////////////////////
    public static ComponentTypeProvision toComponentTypeProvision(ComponentTypeProvisionDTO componentTypeProvisionDTO) {
        if (componentTypeProvisionDTO == null) {
            return null;
        }
        return ComponentTypeProvision.builder()
                .id(componentTypeProvisionDTO.getId())
                .compReference(componentTypeProvisionDTO.getCompReference())
                .ra(componentTypeProvisionDTO.getReadAll())
                .r(componentTypeProvisionDTO.getRead())
                .w(componentTypeProvisionDTO.getWrite())
                .e(componentTypeProvisionDTO.getExecute())
                .d(componentTypeProvisionDTO.getDelete())
                .u(componentTypeProvisionDTO.getUpdate())
                .build();
    }

    public static ComponentTypeProvisionDTO toComponentTypeProvisionDTO(ComponentTypeProvision componentTypeProvision) {
        if (componentTypeProvision == null) {
            return null;
        }
        return ComponentTypeProvisionDTO.builder()
                .id(componentTypeProvision.getId())
                .compReference(componentTypeProvision.getCompReference())
                .readAll(componentTypeProvision.getRa())
                .read(componentTypeProvision.getR())
                .write(componentTypeProvision.getW())
                .execute(componentTypeProvision.getE())
                .delete(componentTypeProvision.getD())
                .update(componentTypeProvision.getU())
                .createdAt(componentTypeProvision.getCreatedAt())
                .updatedAt(componentTypeProvision.getUpdatedAt())
                .build();
    }

    public static ComponentTypeProvision toUpdatedComponentTypeProvision(ComponentTypeProvision componentTypeProvision, ComponentTypeProvision componentTypeProvisionUpdate) {
        componentTypeProvision.setCompReference(componentTypeProvisionUpdate.getCompReference() == null ?
                componentTypeProvision.getCompReference() : componentTypeProvisionUpdate.getCompReference());
        componentTypeProvision.setRa(componentTypeProvisionUpdate.getRa() == null ?
                componentTypeProvision.getRa() : componentTypeProvisionUpdate.getRa());
        componentTypeProvision.setR(componentTypeProvisionUpdate.getR() == null ?
                componentTypeProvision.getR() : componentTypeProvisionUpdate.getR());
        componentTypeProvision.setW(componentTypeProvisionUpdate.getW() == null ?
                componentTypeProvision.getW() : componentTypeProvisionUpdate.getW());
        componentTypeProvision.setE(componentTypeProvisionUpdate.getE() == null ?
                componentTypeProvision.getE() : componentTypeProvisionUpdate.getE());
        componentTypeProvision.setD(componentTypeProvisionUpdate.getD() == null ?
                componentTypeProvision.getD() : componentTypeProvisionUpdate.getD());
        componentTypeProvision.setU(componentTypeProvisionUpdate.getU() == null ?
                componentTypeProvision.getU() : componentTypeProvisionUpdate.getU());
        return componentTypeProvision;
    }

    public static List<ComponentTypeProvision> toComponentTypeProvisions(List<ComponentTypeProvisionDTO> componentTypeProvisionDTOS) {
        return componentTypeProvisionDTOS.stream().map(c -> toComponentTypeProvision(c)).collect(Collectors.toList());
    }

    public static List<ComponentTypeProvisionDTO> toComponentTypeProvisionDTOs(List<ComponentTypeProvision> componentTypeProvisions) {
        return componentTypeProvisions.stream().map(c -> toComponentTypeProvisionDTO(c)).collect(Collectors.toList());
    }

    // ComponentsByRole ////////////////////////////////////////////////////////////////
    public static ComponentsByRole toComponentsByRole(ComponentsByRoleDTO componentsByRoleDTO) {
        if (componentsByRoleDTO == null) {
            return null;
        }
        return ComponentsByRole.builder()
                .id(componentsByRoleDTO.getId())
                .componentId(componentsByRoleDTO.getComponentId())
                .roleId(componentsByRoleDTO.getRoleId())
                .permissions(componentsByRoleDTO.getPermissions())
                .approverRole(componentsByRoleDTO.getApproverRole())
                .minimumThreshold(componentsByRoleDTO.getMinimumThreshold())
                .maximumThreshold(componentsByRoleDTO.getMaximumThreshold())
                .build();
    }

    public static ComponentsByRoleDTO toComponentsByRoleDTO(ComponentsByRole componentsByRole) {
        if (componentsByRole == null) {
            return null;
        }
        return ComponentsByRoleDTO.builder()
                .id(componentsByRole.getId())
                .componentId(componentsByRole.getComponentId())
                .roleId(componentsByRole.getRoleId())
                .permissions(componentsByRole.getPermissions())
                .approverRole(componentsByRole.getApproverRole())
                .minimumThreshold(componentsByRole.getMinimumThreshold())
                .maximumThreshold(componentsByRole.getMaximumThreshold())
                .createdAt(componentsByRole.getCreatedAt())
                .updatedAt(componentsByRole.getUpdatedAt())
                .build();
    }

    public static ComponentsByRole toUpdatedComponentsByRole(ComponentsByRole componentsByRole,
                                                             ComponentsByRole componentsByRoleUpdate) {
        componentsByRole.setComponentId(componentsByRoleUpdate.getComponentId() == null ?
                componentsByRole.getComponentId() : componentsByRoleUpdate.getComponentId());
        componentsByRole.setRoleId(componentsByRoleUpdate.getRoleId() == null ? componentsByRole.getRoleId() :
                componentsByRoleUpdate.getRoleId());
        componentsByRole.setPermissions(componentsByRoleUpdate.getPermissions() == null ?
                componentsByRole.getPermissions() : componentsByRoleUpdate.getPermissions());
        componentsByRole.setApproverRole(componentsByRoleUpdate.getApproverRole() == null ?
                componentsByRole.getApproverRole() : componentsByRoleUpdate.getApproverRole());
        componentsByRole.setMinimumThreshold(componentsByRoleUpdate.getMinimumThreshold() == null ?
                componentsByRole.getMinimumThreshold() : componentsByRoleUpdate.getMinimumThreshold());
        componentsByRole.setMaximumThreshold(componentsByRoleUpdate.getMaximumThreshold() == null ?
                componentsByRole.getMaximumThreshold() : componentsByRoleUpdate.getMaximumThreshold());
        return componentsByRole;
    }

    public static List<ComponentsByRole> toComponentsByRoles(List<ComponentsByRoleDTO> componentsByRoleDTOS) {
        return componentsByRoleDTOS.stream().map(c -> toComponentsByRole(c)).collect(Collectors.toList());
    }

    public static List<ComponentsByRoleDTO> toComponentsByRoleDTOs(List<ComponentsByRole> componentsByRoles) {
        return componentsByRoles.stream().map(c -> toComponentsByRoleDTO(c)).collect(Collectors.toList());
    }
}
