package com.solar.api.tenant.mapper.customerSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationHistoryDTO {
    private Long id;
    private Long parentId;
    private String message;
    private Long responderUserId;    //  Responder
    private String firstName;
    private String lastName;
    private String role;
    private Boolean internal;   // To mark if the conversation is internal
    private Boolean entryType;
    private Boolean approvedIndicator;
    private String priority;
    private Long referenceHistoryId;
    private Long scheduleId;
    private List<ConversationHistoryDTO> conversationHistoryDTOS;
    private List<ConversationReferenceDTO> conversationReferenceDTOList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
