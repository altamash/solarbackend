package com.solar.api.tenant.mapper.customerSupport;

import com.solar.api.tenant.model.customerSupport.ConversationReference;

import java.util.List;
import java.util.stream.Collectors;

public class ConversationReferenceMapper {
    public static ConversationReference toConversationReference(ConversationReferenceDTO conversationReferenceDTO) {
        return ConversationReference.builder()
                .id(conversationReferenceDTO.getId())
                .uri(conversationReferenceDTO.getUri())
                .referenceId(conversationReferenceDTO.getReferenceId())
                .referenceType(conversationReferenceDTO.getReferenceType())
                .build();
    }

    public static ConversationReferenceDTO toConversationReferenceDTO(ConversationReference conversationReference) {
        if (conversationReference == null) {
            return null;
        }

        return ConversationReferenceDTO.builder()
                .id(conversationReference.getId())
                .uri(conversationReference.getUri())
                .referenceId(conversationReference.getReferenceId())
                .referenceType(conversationReference.getReferenceType())
                .build();
    }

    public static List<ConversationReference> toConversationReferenceList(List<ConversationReferenceDTO> conversationReferenceDTOList) {
        return conversationReferenceDTOList.stream().map(ConversationReferenceMapper::toConversationReference).collect(Collectors.toList());
    }

    public static List<ConversationReferenceDTO> toConversationReferenceDTOList(List<ConversationReference> conversationReferenceList) {
        return conversationReferenceList.stream().map(ConversationReferenceMapper::toConversationReferenceDTO).collect(Collectors.toList());
    }
}
