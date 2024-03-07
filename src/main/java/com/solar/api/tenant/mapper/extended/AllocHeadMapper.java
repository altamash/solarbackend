package com.solar.api.tenant.mapper.extended;

import com.solar.api.tenant.model.extended.AllocHead;

import java.util.List;
import java.util.stream.Collectors;

public class AllocHeadMapper {

    public static AllocHead toAllocHead(AllocHeadDTO allocHeadDTO) {
        if (allocHeadDTO == null) {
            return null;
        }
        return AllocHead.builder()
                .allocId(allocHeadDTO.getAllocId())
                .orderId(allocHeadDTO.getOrderId())
                .assetId(allocHeadDTO.getAssetId())
                .locationId(allocHeadDTO.getLocationId())
                .qty(allocHeadDTO.getQty())
                .status(allocHeadDTO.getStatus())
                .description(allocHeadDTO.getDescription())
                .dateTime(allocHeadDTO.getDateTime())
                .submittedBy(allocHeadDTO.getSubmittedBy())
                .approverId(allocHeadDTO.getApproverId())
                .approveDateTime(allocHeadDTO.getApproveDateTime())
                .build();
    }

    public static AllocHeadDTO toAllocHeadDTO(AllocHead allocHead) {
        if (allocHead == null) {
            return null;
        }
        return AllocHeadDTO.builder()
                .allocId(allocHead.getAllocId())
                .orderId(allocHead.getOrderId())
                .assetId(allocHead.getAssetId())
                .locationId(allocHead.getLocationId())
                .qty(allocHead.getQty())
                .status(allocHead.getStatus())
                .description(allocHead.getDescription())
                .dateTime(allocHead.getDateTime())
                .submittedBy(allocHead.getSubmittedBy())
                .approverId(allocHead.getApproverId())
                .approveDateTime(allocHead.getApproveDateTime())
                .createdAt(allocHead.getCreatedAt())
                .updatedAt(allocHead.getUpdatedAt())
                .build();
    }

    public static AllocHead toUpdatedAllocHead(AllocHead allocHead, AllocHead allocHeadUpdate) {
        allocHead.setOrderId(allocHeadUpdate.getOrderId() == null ? allocHead.getOrderId() :
                allocHeadUpdate.getOrderId());
        allocHead.setAssetId(allocHeadUpdate.getAssetId() == null ? allocHead.getAssetId() :
                allocHeadUpdate.getAssetId());
        allocHead.setLocationId(allocHeadUpdate.getLocationId() == null ? allocHead.getLocationId() :
                allocHeadUpdate.getLocationId());
        allocHead.setQty(allocHeadUpdate.getQty() == null ? allocHead.getQty() : allocHeadUpdate.getQty());
        allocHead.setStatus(allocHeadUpdate.getStatus() == null ? allocHead.getStatus() : allocHeadUpdate.getStatus());
        allocHead.setDescription(allocHeadUpdate.getDescription() == null ? allocHead.getDescription() :
                allocHeadUpdate.getDescription());
        allocHead.setDateTime(allocHeadUpdate.getDateTime() == null ? allocHead.getDateTime() :
                allocHeadUpdate.getDateTime());
        allocHead.setSubmittedBy(allocHeadUpdate.getSubmittedBy() == null ? allocHead.getSubmittedBy() :
                allocHeadUpdate.getSubmittedBy());
        allocHead.setApproverId(allocHeadUpdate.getApproverId() == null ? allocHead.getApproverId() :
                allocHeadUpdate.getApproverId());
        allocHead.setApproveDateTime(allocHeadUpdate.getApproveDateTime() == null ? allocHead.getApproveDateTime() :
                allocHeadUpdate.getApproveDateTime());
        return allocHead;
    }

    public static List<AllocHead> toAllocHeads(List<AllocHeadDTO> allocHeadDTOS) {
        return allocHeadDTOS.stream().map(a -> toAllocHead(a)).collect(Collectors.toList());
    }

    public static List<AllocHeadDTO> toAllocHeadDTOs(List<AllocHead> allocHeads) {
        return allocHeads.stream().map(a -> toAllocHeadDTO(a)).collect(Collectors.toList());
    }
}
