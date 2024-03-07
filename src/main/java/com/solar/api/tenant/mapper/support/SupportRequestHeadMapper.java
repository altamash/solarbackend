package com.solar.api.tenant.mapper.support;

import com.solar.api.tenant.model.support.SupportRequestHead;

import java.util.List;
import java.util.stream.Collectors;

public class SupportRequestHeadMapper {

    public static SupportRequestHead toSupportRequestHead(SupportRequestHeadDTO supportRequestHeadDTO) {
        return SupportRequestHead.builder()
                .id(supportRequestHeadDTO.getId())
                .accountId(supportRequestHeadDTO.getAccountId())
                .firstName(supportRequestHeadDTO.getFirstName())
                .lastName(supportRequestHeadDTO.getLastName())
                .subscriptionId(supportRequestHeadDTO.getSubscriptionId())
                .status(supportRequestHeadDTO.getStatus() != null ? supportRequestHeadDTO.getStatus() : "NEW")
                .requestAction(supportRequestHeadDTO.getRequestAction())
                .description(supportRequestHeadDTO.getDescription())
                .raisedBy(supportRequestHeadDTO.getRaisedBy())
                .role(supportRequestHeadDTO.getRole())
                .supportRequestHistories(supportRequestHeadDTO.getSupportRequestHistoryList() != null ?
                        SupportRequestHistoryMapper.toSupportRequestHistorys(supportRequestHeadDTO.getSupportRequestHistoryList()) : null)
                .priority(supportRequestHeadDTO.getPriority())
                .createdAt(supportRequestHeadDTO.getCreatedAt())
                .build();
    }

    public static SupportRequestHeadDTO toSupportRequestHeadDTO(SupportRequestHead supportRequestHead) {
        if (supportRequestHead == null) {
            return null;
        }

        return SupportRequestHeadDTO.builder()
                .id(supportRequestHead.getId())
                .accountId(supportRequestHead.getAccountId())
                .firstName(supportRequestHead.getFirstName())
                .lastName(supportRequestHead.getLastName())
                .subscriptionId(supportRequestHead.getSubscriptionId())
                .status(supportRequestHead.getStatus())
                .requestAction(supportRequestHead.getRequestAction())
                .description(supportRequestHead.getDescription())
                .raisedBy(supportRequestHead.getRaisedBy())
                .role(supportRequestHead.getRole())
                .supportRequestHistoryList(supportRequestHead.getSupportRequestHistories() != null ?
                        SupportRequestHistoryMapper.toSupportRequestHistoryDTOs(supportRequestHead.getSupportRequestHistories()) : null)
                .priority(supportRequestHead.getPriority())
                .createdAt(supportRequestHead.getCreatedAt())
                .build();
    }

    public static SupportRequestHead toUpdateSupportRequestHead(SupportRequestHead supportRequestHead,
                                                                SupportRequestHead supportRequestHeadUpdate) {
        supportRequestHead.setRequestAction(supportRequestHeadUpdate.getRequestAction() == null ?
                supportRequestHead.getRequestAction() : supportRequestHeadUpdate.getRequestAction());
        supportRequestHead.setStatus(supportRequestHeadUpdate.getStatus() == null ? supportRequestHead.getStatus() :
                supportRequestHeadUpdate.getStatus());
        supportRequestHead.setDescription(supportRequestHeadUpdate.getDescription() == null ?
                supportRequestHead.getDescription() : supportRequestHeadUpdate.getDescription());
        supportRequestHead.setRaisedBy(supportRequestHeadUpdate.getRaisedBy() == null ?
                supportRequestHead.getRaisedBy() : supportRequestHeadUpdate.getRaisedBy());
        supportRequestHead.setPriority(supportRequestHeadUpdate.getPriority() == null ?
                supportRequestHead.getPriority() : supportRequestHeadUpdate.getPriority());
        return supportRequestHead;
    }

    /**
     * @param supportRequestHeadDTOS
     * @return
     */
    public static List<SupportRequestHead> toSupportRequestHeads(List<SupportRequestHeadDTO> supportRequestHeadDTOS) {
        return supportRequestHeadDTOS.stream().map(SupportRequestHeadMapper::toSupportRequestHead).collect(Collectors.toList());
    }

    /**
     * @param supportRequestHeads
     * @return
     */
    public static List<SupportRequestHeadDTO> toSupportRequestHeadDTOs(List<SupportRequestHead> supportRequestHeads) {
        return supportRequestHeads.stream().map(SupportRequestHeadMapper::toSupportRequestHeadDTO).collect(Collectors.toList());
    }

}
