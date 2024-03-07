package com.solar.api.tenant.mapper.extended.service;

import com.solar.api.tenant.model.extended.service.ServiceDetail;
import com.solar.api.tenant.model.extended.service.ServiceHead;
import com.solar.api.tenant.model.extended.service.ServiceResources;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceMapper {

    // ServiceHead ////////////////////////////////////////////////
    public static ServiceHead toServiceHead(ServiceHeadDTO serviceHeadDTO) {
        if (serviceHeadDTO == null) {
            return null;
        }
        return ServiceHead.builder()
                .serviceId(serviceHeadDTO.getServiceId())
                .registerId(serviceHeadDTO.getRegisterId())
                .summary(serviceHeadDTO.getSummary())
                .description(serviceHeadDTO.getDescription())
                .createDate(serviceHeadDTO.getCreateDate())
                .status(serviceHeadDTO.getStatus())
                .updateDate(serviceHeadDTO.getUpdateDate())
                .build();
    }

    public static ServiceHeadDTO toServiceHeadDTO(ServiceHead serviceHead) {
        if (serviceHead == null) {
            return null;
        }
        return ServiceHeadDTO.builder()
                .serviceId(serviceHead.getServiceId())
                .registerId(serviceHead.getRegisterId())
                .summary(serviceHead.getSummary())
                .description(serviceHead.getDescription())
                .createDate(serviceHead.getCreateDate())
                .status(serviceHead.getStatus())
                .updateDate(serviceHead.getUpdateDate())
                .createdAt(serviceHead.getCreatedAt())
                .updatedAt(serviceHead.getUpdatedAt())
                .build();
    }

    public static ServiceHead toUpdatedServiceHead(ServiceHead serviceHead, ServiceHead serviceHeadUpdate) {
        serviceHead.setRegisterId(serviceHeadUpdate.getRegisterId() == null ? serviceHead.getRegisterId() :
                serviceHeadUpdate.getRegisterId());
        serviceHead.setSummary(serviceHeadUpdate.getSummary() == null ? serviceHead.getSummary() :
                serviceHeadUpdate.getSummary());
        serviceHead.setDescription(serviceHeadUpdate.getDescription() == null ? serviceHead.getDescription() :
                serviceHeadUpdate.getDescription());
        serviceHead.setCreateDate(serviceHeadUpdate.getCreateDate() == null ? serviceHead.getCreateDate() :
                serviceHeadUpdate.getCreateDate());
        serviceHead.setStatus(serviceHeadUpdate.getStatus() == null ? serviceHead.getStatus() :
                serviceHeadUpdate.getStatus());
        serviceHead.setUpdateDate(serviceHeadUpdate.getUpdateDate() == null ? serviceHead.getUpdateDate() :
                serviceHeadUpdate.getUpdateDate());
        return serviceHead;
    }

    public static List<ServiceHead> toServiceHeads(List<ServiceHeadDTO> serviceHeadDTOS) {
        return serviceHeadDTOS.stream().map(s -> toServiceHead(s)).collect(Collectors.toList());
    }

    public static List<ServiceHeadDTO> toServiceHeadDTOs(List<ServiceHead> serviceHeads) {
        return serviceHeads.stream().map(s -> toServiceHeadDTO(s)).collect(Collectors.toList());
    }

    // ServiceDetail ////////////////////////////////////////////////
    public static ServiceDetail toServiceDetail(ServiceDetailDTO serviceDetailDTO) {
        if (serviceDetailDTO == null) {
            return null;
        }
        return ServiceDetail.builder()
                .id(serviceDetailDTO.getId())
                .serviceId(serviceDetailDTO.getServiceId())
                .measureCode(serviceDetailDTO.getMeasureCode())
                .value(serviceDetailDTO.getValue())
                .lastUpdateOn(serviceDetailDTO.getLastUpdateOn())
                .lastUpdateBy(serviceDetailDTO.getLastUpdateBy())
                .validationRule(serviceDetailDTO.getValidationRule())
                .validationParams(serviceDetailDTO.getValidationParams())
                .build();
    }

    public static ServiceDetailDTO toServiceDetailDTO(ServiceDetail serviceDetail) {
        if (serviceDetail == null) {
            return null;
        }
        return ServiceDetailDTO.builder()
                .id(serviceDetail.getId())
                .serviceId(serviceDetail.getServiceId())
                .measureCode(serviceDetail.getMeasureCode())
                .value(serviceDetail.getValue())
                .lastUpdateOn(serviceDetail.getLastUpdateOn())
                .lastUpdateBy(serviceDetail.getLastUpdateBy())
                .validationRule(serviceDetail.getValidationRule())
                .validationParams(serviceDetail.getValidationParams())
                .createdAt(serviceDetail.getCreatedAt())
                .updatedAt(serviceDetail.getUpdatedAt())
                .build();
    }

    public static ServiceDetail toUpdatedServiceDetail(ServiceDetail serviceDetail, ServiceDetail serviceDetailUpdate) {
        serviceDetail.setServiceId(serviceDetailUpdate.getServiceId() == null ? serviceDetail.getServiceId() :
                serviceDetailUpdate.getServiceId());
        serviceDetail.setMeasureCode(serviceDetailUpdate.getMeasureCode() == null ? serviceDetail.getMeasureCode() :
                serviceDetailUpdate.getMeasureCode());
        serviceDetail.setValue(serviceDetailUpdate.getValue() == null ? serviceDetail.getValue() :
                serviceDetailUpdate.getValue());
        serviceDetail.setLastUpdateOn(serviceDetailUpdate.getLastUpdateOn() == null ?
                serviceDetail.getLastUpdateOn() : serviceDetailUpdate.getLastUpdateOn());
        serviceDetail.setLastUpdateBy(serviceDetailUpdate.getLastUpdateBy() == null ?
                serviceDetail.getLastUpdateBy() : serviceDetailUpdate.getLastUpdateBy());
        serviceDetail.setValidationRule(serviceDetailUpdate.getValidationRule() == null ?
                serviceDetail.getValidationRule() : serviceDetailUpdate.getValidationRule());
        serviceDetail.setValidationParams(serviceDetailUpdate.getValidationParams() == null ?
                serviceDetail.getValidationParams() : serviceDetailUpdate.getValidationParams());

        return serviceDetail;
    }

    public static List<ServiceDetail> toServiceDetails(List<ServiceDetailDTO> serviceDetailDTOS) {
        return serviceDetailDTOS.stream().map(s -> toServiceDetail(s)).collect(Collectors.toList());
    }

    public static List<ServiceDetailDTO> toServiceDetailDTOs(List<ServiceDetail> serviceDetails) {
        return serviceDetails.stream().map(s -> toServiceDetailDTO(s)).collect(Collectors.toList());
    }

    // ServiceResources ////////////////////////////////////////////////
    public static ServiceResources toServiceResources(ServiceResourcesDTO serviceResourcesDTO) {
        if (serviceResourcesDTO == null) {
            return null;
        }
        return ServiceResources.builder()
                .id(serviceResourcesDTO.getId())
                .assignmentId(serviceResourcesDTO.getAssignmentId())
                .empId(serviceResourcesDTO.getEmpId())
                .assignedTo(serviceResourcesDTO.getAssignedTo())
                .assignmentRefId(serviceResourcesDTO.getAssignmentRefId())
                .role(serviceResourcesDTO.getRole())
                .build();
    }

    public static ServiceResourcesDTO toServiceResourcesDTO(ServiceResources serviceResources) {
        if (serviceResources == null) {
            return null;
        }
        return ServiceResourcesDTO.builder()
                .id(serviceResources.getId())
                .assignmentId(serviceResources.getAssignmentId())
                .empId(serviceResources.getEmpId())
                .assignedTo(serviceResources.getAssignedTo())
                .assignmentRefId(serviceResources.getAssignmentRefId())
                .role(serviceResources.getRole())
                .createdAt(serviceResources.getCreatedAt())
                .updatedAt(serviceResources.getUpdatedAt())
                .build();
    }

    public static ServiceResources toUpdatedServiceResources(ServiceResources serviceResources,
                                                             ServiceResources serviceResourcesUpdate) {
        serviceResources.setAssignmentId(serviceResourcesUpdate.getAssignmentId() == null ?
                serviceResources.getAssignmentId() : serviceResourcesUpdate.getAssignmentId());
        serviceResources.setEmpId(serviceResourcesUpdate.getEmpId() == null ? serviceResources.getEmpId() :
                serviceResourcesUpdate.getEmpId());
        serviceResources.setAssignedTo(serviceResourcesUpdate.getAssignedTo() == null ?
                serviceResources.getAssignedTo() : serviceResourcesUpdate.getAssignedTo());
        serviceResources.setAssignmentRefId(serviceResourcesUpdate.getAssignmentRefId() == null ?
                serviceResources.getAssignmentRefId() : serviceResourcesUpdate.getAssignmentRefId());
        serviceResources.setRole(serviceResourcesUpdate.getRole() == null ? serviceResources.getRole() :
                serviceResourcesUpdate.getRole());
        return serviceResources;
    }

    public static List<ServiceResources> toServiceResources(List<ServiceResourcesDTO> serviceResourcesDTOS) {
        return serviceResourcesDTOS.stream().map(s -> toServiceResources(s)).collect(Collectors.toList());
    }

    public static List<ServiceResourcesDTO> toServiceResourcesDTOs(List<ServiceResources> serviceResourcess) {
        return serviceResourcess.stream().map(s -> toServiceResourcesDTO(s)).collect(Collectors.toList());
    }
}
