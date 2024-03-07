package com.solar.api.tenant.mapper.extended;

import com.solar.api.tenant.mapper.extended.service.ServiceHeadDTO;
import com.solar.api.tenant.model.extended.service.ServiceHead;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceHeadMapper {

    public static ServiceHead toServiceHead(ServiceHeadDTO serviceHeadDTO) {
        if (serviceHeadDTO == null) {
            return null;
        }
        return ServiceHead.builder()
                .serviceId(serviceHeadDTO.getServiceId())
                .registerId(serviceHeadDTO.getRegisterId())
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
}
