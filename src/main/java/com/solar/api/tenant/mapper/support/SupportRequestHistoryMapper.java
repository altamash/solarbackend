package com.solar.api.tenant.mapper.support;

import com.solar.api.tenant.model.support.SupportRequestHistory;

import java.util.List;
import java.util.stream.Collectors;

public class SupportRequestHistoryMapper {

    public static SupportRequestHistory toSupportRequestHistory(SupportRequestHistoryDTO supportRequestHistoryDTO) {
        return SupportRequestHistory.builder()
                .id(supportRequestHistoryDTO.getId())
                .srId(supportRequestHistoryDTO.getSrId())
                .message(supportRequestHistoryDTO.getMessage())
                .firstName(supportRequestHistoryDTO.getFirstName())
                .lastName(supportRequestHistoryDTO.getLastName())
                .requestAction(supportRequestHistoryDTO.getRequestAction())
                .role(supportRequestHistoryDTO.getRole())
                .responderUserId(supportRequestHistoryDTO.getResponderUserId())
                .nowWaitingOn(supportRequestHistoryDTO.getNowWaitingOn())
                .responseDateTime(supportRequestHistoryDTO.getResponseDateTime())
                .sequenceNo(supportRequestHistoryDTO.getSequenceNo())
                .createdAt(supportRequestHistoryDTO.getCreatedAt())
                .build();
    }

    public static SupportRequestHistoryDTO toSupportRequestHistoryDTO(SupportRequestHistory supportRequestHistory) {
        if (supportRequestHistory == null) {
            return null;
        }

        return SupportRequestHistoryDTO.builder()
                .id(supportRequestHistory.getId())
                .srId(supportRequestHistory.getSrId())
                .message(supportRequestHistory.getMessage())
                .responderUserId(supportRequestHistory.getResponderUserId())
                .firstName(supportRequestHistory.getFirstName())
                .lastName(supportRequestHistory.getLastName())
                .requestAction(supportRequestHistory.getRequestAction())
                .role(supportRequestHistory.getRole())
                .nowWaitingOn(supportRequestHistory.getNowWaitingOn())
                .responseDateTime(supportRequestHistory.getResponseDateTime())
                .sequenceNo(supportRequestHistory.getSequenceNo())
                .createdAt(supportRequestHistory.getCreatedAt())
                .build();
    }

    public static SupportRequestHistory toUpdateSupportRequestHistory(SupportRequestHistory supportRequestHistory,
                                                                      SupportRequestHistory supportRequestHistoryUpdate) {
        supportRequestHistory.setMessage(supportRequestHistoryUpdate.getMessage() == null ?
                supportRequestHistory.getMessage() : supportRequestHistoryUpdate.getMessage());
        supportRequestHistory.setResponderUserId(supportRequestHistoryUpdate.getResponderUserId() == null ?
                supportRequestHistory.getResponderUserId() : supportRequestHistoryUpdate.getResponderUserId());
        supportRequestHistory.setNowWaitingOn(supportRequestHistoryUpdate.getNowWaitingOn() == null ?
                supportRequestHistory.getNowWaitingOn() : supportRequestHistoryUpdate.getNowWaitingOn());
        supportRequestHistory.setResponseDateTime(supportRequestHistoryUpdate.getResponseDateTime() == null ?
                supportRequestHistory.getResponseDateTime() : supportRequestHistoryUpdate.getResponseDateTime());
        supportRequestHistory.setSequenceNo(supportRequestHistoryUpdate.getSequenceNo() == null ?
                supportRequestHistory.getSequenceNo() : supportRequestHistoryUpdate.getSequenceNo());
        return supportRequestHistory;
    }

    /**
     * @param supportRequestHistoryDTOS
     * @return
     */
    public static List<SupportRequestHistory> toSupportRequestHistorys(List<SupportRequestHistoryDTO> supportRequestHistoryDTOS) {
        return supportRequestHistoryDTOS.stream().map(SupportRequestHistoryMapper::toSupportRequestHistory).collect(Collectors.toList());
    }

    /**
     * @param supportRequestHistories
     * @return
     */
    public static List<SupportRequestHistoryDTO> toSupportRequestHistoryDTOs(List<SupportRequestHistory> supportRequestHistories) {
        return supportRequestHistories.stream().map(SupportRequestHistoryMapper::toSupportRequestHistoryDTO).collect(Collectors.toList());
    }
}
