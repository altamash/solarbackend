package com.solar.api.tenant.mapper.customerSupport;

import com.solar.api.tenant.model.customerSupport.ConversationHistory;

import java.util.List;
import java.util.stream.Collectors;

public class ConversationHistoryMapper {
    public static ConversationHistory toConversationHistory(ConversationHistoryDTO conversationHistoryDTO) {
        return ConversationHistory.builder()
                .id(conversationHistoryDTO.getId())
                .parentId(conversationHistoryDTO.getParentId())
                .message(conversationHistoryDTO.getMessage())
                .responderUserId(conversationHistoryDTO.getResponderUserId())
                .firstName(conversationHistoryDTO.getFirstName())
                .lastName(conversationHistoryDTO.getLastName())
                .role(conversationHistoryDTO.getRole())
                .parentId(conversationHistoryDTO.getParentId())
                .internal(conversationHistoryDTO.getInternal())
                .entryType(conversationHistoryDTO.getEntryType())
                .approvedIndicator(conversationHistoryDTO.getApprovedIndicator())
                .priority(conversationHistoryDTO.getPriority())
                .referenceHistoryId(conversationHistoryDTO.getReferenceHistoryId())
                .scheduleId(conversationHistoryDTO.getScheduleId())
                .conversationReferenceList(conversationHistoryDTO.getConversationReferenceDTOList() != null ?
                        ConversationReferenceMapper.toConversationReferenceList(conversationHistoryDTO.getConversationReferenceDTOList()) : null)
                .createdAt(conversationHistoryDTO.getCreatedAt())
                .build();
    }

    public static ConversationHistoryDTO toConversationHistoryDTO(ConversationHistory conversationHistory) {
        if (conversationHistory == null) {
            return null;
        }

        return ConversationHistoryDTO.builder()
                .id(conversationHistory.getId())
                .parentId(conversationHistory.getParentId())
                .message(conversationHistory.getMessage())
                .responderUserId(conversationHistory.getResponderUserId())
                .firstName(conversationHistory.getFirstName())
                .lastName(conversationHistory.getLastName())
                .parentId(conversationHistory.getParentId())
                .role(conversationHistory.getRole())
                .internal(conversationHistory.getInternal())
                .entryType(conversationHistory.getEntryType())
                .approvedIndicator(conversationHistory.getApprovedIndicator())
                .priority(conversationHistory.getPriority())
                .referenceHistoryId(conversationHistory.getReferenceHistoryId())
                .scheduleId(conversationHistory.getScheduleId())
                .conversationReferenceDTOList(conversationHistory.getConversationReferenceList() != null ?
                        ConversationReferenceMapper.toConversationReferenceDTOList(conversationHistory.getConversationReferenceList()) : null)
                .createdAt(conversationHistory.getCreatedAt())
                .build();
    }

    public static ConversationHistory toUpdateConversationHistory(ConversationHistory conversationHistory, ConversationHistory conversationHistoryUpdate) {
        conversationHistory.setParentId(conversationHistoryUpdate.getParentId() == null ?
                conversationHistory.getParentId() : conversationHistoryUpdate.getParentId());
        conversationHistory.setMessage(conversationHistoryUpdate.getMessage() == null ?
                conversationHistory.getMessage() : conversationHistoryUpdate.getMessage());
        conversationHistory.setMessage(conversationHistoryUpdate.getMessage() == null ?
                conversationHistory.getMessage() : conversationHistoryUpdate.getMessage());
        conversationHistory.setResponderUserId(conversationHistoryUpdate.getResponderUserId() == null ?
                conversationHistory.getResponderUserId() : conversationHistoryUpdate.getResponderUserId());
        conversationHistory.setPriority(conversationHistoryUpdate.getPriority() == null ?
                conversationHistory.getPriority() : conversationHistoryUpdate.getPriority());
        conversationHistory.setLastName(conversationHistoryUpdate.getLastName() == null ?
                conversationHistory.getLastName() : conversationHistoryUpdate.getLastName());
        conversationHistory.setRole(conversationHistoryUpdate.getRole() == null ?
                conversationHistory.getRole() : conversationHistoryUpdate.getRole());
        conversationHistory.setInternal(conversationHistoryUpdate.getInternal() == null ?
                conversationHistory.getInternal() : conversationHistoryUpdate.getInternal());
        conversationHistory.setEntryType(conversationHistoryUpdate.getEntryType() == null ?
                conversationHistory.getEntryType() : conversationHistoryUpdate.getEntryType());
        conversationHistory.setEntryType(conversationHistoryUpdate.getEntryType() == null ?
                conversationHistory.getEntryType() : conversationHistoryUpdate.getEntryType());
        conversationHistory.setApprovedIndicator(conversationHistoryUpdate.getApprovedIndicator() == null ?
                conversationHistory.getApprovedIndicator() : conversationHistoryUpdate.getApprovedIndicator());
        conversationHistory.setReferenceHistoryId(conversationHistoryUpdate.getReferenceHistoryId() == null ?
                conversationHistory.getReferenceHistoryId() : conversationHistoryUpdate.getReferenceHistoryId());
        conversationHistory.setScheduleId(conversationHistoryUpdate.getScheduleId() == null ?
                conversationHistory.getScheduleId() : conversationHistoryUpdate.getScheduleId());
        conversationHistory.setUpdatedAt(conversationHistoryUpdate.getUpdatedAt() == null ?
                conversationHistory.getUpdatedAt() : conversationHistoryUpdate.getUpdatedAt());
        return conversationHistory;
    }

    public static List<ConversationHistory> toConversationHistoryList(List<ConversationHistoryDTO> conversationHistoryDTOList) {
        return conversationHistoryDTOList.stream().map(ConversationHistoryMapper::toConversationHistory).collect(Collectors.toList());
    }

    public static List<ConversationHistoryDTO> toConversationHistoryDTOList(List<ConversationHistory> conversationHistoryList) {
        return conversationHistoryList.stream().map(ConversationHistoryMapper::toConversationHistoryDTO).collect(Collectors.toList());
    }
}
