package com.solar.api.tenant.mapper.customerSupport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.controlPanel.ControlPanelStaticDataDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationHeadDTO {
    private Long id;
    private String summary;
    private String message;
    private String category;
    private String subCategory;
    private String priority;
    private Long parentRequestId;
    private Long grandParentRequestId;
    private String sourceType; // Service desk, Project
    private String sourceId; // It is not applicable on Service desk
    private Long requestId; // Auto - It should be unique by Source type
    private Long orgId;
    private String status;
    private Long raisedBy;  //  Logged in user id
    private String firstName;
    private String lastName;
    private String role;
    private Long assignee;  // Marked to
    private String channel;  // Web, Mobile
    private String contractId;
    private String subscriptionId;
    private Boolean internal;   // To mark if the conversation is internal
    private Long customerId;    //  It will not be visible on first time
    private String customerName;
    private Long workflowId;
    private String variantId;
    private List<ConversationHistoryDTO> conversationHistoryDTOList;
    private List<ConversationReferenceDTO> conversationReferenceDTOList;
    private LocalDateTime createdAt;
    private String createdBy;
    private Long adminId;
    private List<ControlPanelStaticDataDTO> variants;
    private String reason;
    private String reasonStatus;
    private String remarks;
    private String raisedByImg;
    private String formattedCreatedAt;
    private String attachmentImg;
//    private Long totalElements;
    private  String coverage;
    private Double estimatedHours;
    private Double estimatedBudget;
    private String productId;
    private String module;
    private String raisedByImgUri;
    private String attachments;
    private Long assigneeEntityRoleId;
    private String assigneeEntityName;
    private String assigneeImgUri;
    private String requesterName;
    private String requesterImgUri;
    private String gardenName;
    private String subscriptionName;
    private String gardenImgUri;

    private String createdAtStr;

    private String requesterEmail;

    private String requesterPhone;

    private String requesterType;

    private String headIds;

    private Long ticketCount;

    private List<CustomerConversationHeadDTO> customerConversationHeadDTOList;

    private List<String> headAttachments;

    private Long assigneeEntityId;
    private String subsAddress;
    private String variantAddress;


    public ConversationHeadDTO(Long id, String summary, String message, String category, String subCategory, String priority, String sourceId, String status, Long raisedBy, String firstName, String lastName, String role, String formattedCreatedAt,  String raisedByImg, String attachmentImg) {
        this.id = id;
        this.summary = summary;
        this.message = message;
        this.category = category;
        this.subCategory = subCategory;
        this.priority = priority;
        this.sourceId = sourceId;
        this.status = status;
        this.raisedBy = raisedBy;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.formattedCreatedAt = formattedCreatedAt;
        this. raisedByImg= raisedByImg;
        this.attachmentImg=attachmentImg;
    }

    public ConversationHeadDTO(Long id, String message, String category, String subCategory, String priority, String sourceId) {
        this.id = id;
        this.message = message;
        this.category = category;
        this.subCategory = subCategory;
        this.priority = priority;
        this.sourceId = sourceId;
    }

  public  ConversationHeadDTO(Long id, String summary, String message, String category, String subCategory,
                              String priority, String sourceId, String status, Long raisedBy, String firstName,
                              String lastName, String role, String createdAt,
                              String raisedByImgUri, String attachments, Long assigneeEntityRoleId,
                              String assigneeEntityName,String assigneeImgUri,String requesterName, String requesterImgUri,
                              String sourceType, String variantId,String gardenName,
                              String subscriptionId ,String subscriptionName, String gardenImgUri
                               ){
        this.id = id;
        this.summary = summary;
        this.message = message;
        this.category = category;
        this.subCategory = subCategory;
        this.priority = priority;
        this.sourceId = sourceId;
        this.status = status;
        this.raisedBy = raisedBy;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.createdAtStr = createdAt;
        this.raisedByImgUri= raisedByImgUri;
        this.attachments = attachments;
        this.assigneeEntityRoleId = assigneeEntityRoleId;
        this.assigneeEntityName = assigneeEntityName;
        this.assigneeImgUri = assigneeImgUri;
        this.requesterName = requesterName;
        this.requesterImgUri = requesterImgUri;
        this.sourceType = sourceType;
        this.variantId = variantId;
        this.gardenName = gardenName;
        this.subscriptionId = subscriptionId;
        this.subscriptionName = subscriptionName;
        this.gardenImgUri = gardenImgUri;
  }

    public ConversationHeadDTO(Long headId,String attachments) {
        this.id = headId;
        this.attachments = attachments;
    }
}
