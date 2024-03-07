package com.solar.api.tenant.mapper.customerSupport;

import com.solar.api.tenant.model.customerSupport.ConversationHead;
import com.solar.api.tenant.model.customerSupport.EConversationStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConversationHeadMapper {
    public static ConversationHead toConversationHead(ConversationHeadDTO conversationHeadDTO) {
        return ConversationHead.builder()
                .id(conversationHeadDTO.getId())
                .summary(conversationHeadDTO.getSummary())
                .message(conversationHeadDTO.getMessage())
                .category(conversationHeadDTO.getCategory())
                .subCategory(conversationHeadDTO.getSubCategory())
                .priority(conversationHeadDTO.getPriority())
                .parentRequestId(conversationHeadDTO.getParentRequestId())
                .grandParentRequestId(conversationHeadDTO.getGrandParentRequestId())
                .sourceType(conversationHeadDTO.getSourceType()!= null? conversationHeadDTO.getSourceType() : null)
                .sourceId(conversationHeadDTO.getSourceId()!= null? conversationHeadDTO.getSourceId() : null)
                .requestId(conversationHeadDTO.getRequestId()!= null? conversationHeadDTO.getRequestId() : null)
                .orgId(conversationHeadDTO.getOrgId()!= null? conversationHeadDTO.getOrgId() : null)
                .status(conversationHeadDTO.getStatus() != null ? conversationHeadDTO.getStatus() : EConversationStatus.OPEN.getName())
                .raisedBy(conversationHeadDTO.getRaisedBy())
                .firstName(conversationHeadDTO.getFirstName())
                .lastName(conversationHeadDTO.getLastName())
                .role(conversationHeadDTO.getRole())
                .assignee(conversationHeadDTO.getAssignee())
                .channel(conversationHeadDTO.getChannel())
                .contractId(conversationHeadDTO.getContractId())
                .internal(conversationHeadDTO.getInternal())
                .customerId(conversationHeadDTO.getCustomerId()!= null? conversationHeadDTO.getCustomerId() : null)
                .conversationHistoryList(conversationHeadDTO.getConversationHistoryDTOList() != null ?
                        ConversationHistoryMapper.toConversationHistoryList(conversationHeadDTO.getConversationHistoryDTOList()) : null)
                .conversationReferenceList(conversationHeadDTO.getConversationReferenceDTOList() != null ?
                        ConversationReferenceMapper.toConversationReferenceList(conversationHeadDTO.getConversationReferenceDTOList()) : null)
                .createdAt(conversationHeadDTO.getCreatedAt())
                .subscriptionId(conversationHeadDTO.getSubscriptionId())
                .workflowId(conversationHeadDTO.getWorkflowId())
                .variantId(conversationHeadDTO.getVariantId())
                .coverage(conversationHeadDTO.getCoverage()!= null? conversationHeadDTO.getCoverage() : null)
                .estimatedBudget(conversationHeadDTO.getEstimatedBudget()!= null? conversationHeadDTO.getEstimatedBudget() : null)
                .estimatedHours(conversationHeadDTO.getEstimatedHours()!= null? conversationHeadDTO.getEstimatedHours() : null)
                .productId(conversationHeadDTO.getProductId()!= null? conversationHeadDTO.getProductId() : null)
                .module(conversationHeadDTO.getModule()!= null? conversationHeadDTO.getModule() : null)
                .build();
    }

    public static ConversationHeadDTO toConversationHeadDTO(ConversationHead conversationHead) {
        if (conversationHead == null) {
            return null;
        }

        return ConversationHeadDTO.builder()
                .id(conversationHead.getId())
                .summary(conversationHead.getSummary())
                .message(conversationHead.getMessage())
                .category(conversationHead.getCategory())
                .subCategory(conversationHead.getSubCategory())
                .priority(conversationHead.getPriority())
                .parentRequestId(conversationHead.getParentRequestId())
                .grandParentRequestId(conversationHead.getGrandParentRequestId())
                .sourceType(conversationHead.getSourceType())
                .sourceId(conversationHead.getSourceId())
                .requestId(conversationHead.getRequestId())
                .orgId(conversationHead.getOrgId())
                .status(conversationHead.getStatus())
                .raisedBy(conversationHead.getRaisedBy())
                .firstName(conversationHead.getFirstName())
                .lastName(conversationHead.getLastName())
                .role(conversationHead.getRole())
                .assignee(conversationHead.getAssignee())
                .channel(conversationHead.getChannel())
                .contractId(conversationHead.getContractId())
                .subscriptionId(conversationHead.getSubscriptionId())
                .internal(conversationHead.getInternal())
                .customerId(conversationHead.getCustomerId())
                .conversationHistoryDTOList(conversationHead.getConversationHistoryList() != null ?
                        ConversationHistoryMapper.toConversationHistoryDTOList(conversationHead.getConversationHistoryList()) : null)
                .conversationReferenceDTOList(conversationHead.getConversationReferenceList() != null ?
                        ConversationReferenceMapper.toConversationReferenceDTOList(conversationHead.getConversationReferenceList()) : null)
                .createdAt(conversationHead.getCreatedAt())
                .workflowId(conversationHead.getWorkflowId())
                .variantId(conversationHead.getVariantId())
                .remarks(conversationHead != null && conversationHead.getRemarks() != null ? conversationHead.getRemarks() : null)
                .module(conversationHead.getModule()!= null? conversationHead.getModule() : null)
                .coverage(conversationHead!= null && conversationHead.getCoverage()!= null? conversationHead.getCoverage() : null)
                .estimatedBudget(conversationHead!= null && conversationHead.getEstimatedBudget()!= null? conversationHead.getEstimatedBudget() :null)
                .estimatedHours(conversationHead!= null && conversationHead.getEstimatedHours()!= null? conversationHead.getEstimatedHours():null)
                .productId(conversationHead.getProductId()!= null? conversationHead.getProductId() : null)
                .build();
    }

    public static ConversationHead toUpdateConversationHead(ConversationHead conversationHead, ConversationHead conversationHeadUpdate) {
        conversationHead.setSummary(conversationHeadUpdate.getSummary() == null ?
                conversationHead.getSummary() : conversationHeadUpdate.getSummary());
        conversationHead.setMessage(conversationHeadUpdate.getMessage() == null ?
                conversationHead.getMessage() : conversationHeadUpdate.getMessage());
        conversationHead.setCategory(conversationHeadUpdate.getCategory() == null ?
                conversationHead.getCategory() : conversationHeadUpdate.getCategory());
        conversationHead.setSubCategory(conversationHeadUpdate.getSubCategory() == null ?
                conversationHead.getSubCategory() : conversationHeadUpdate.getSubCategory());
        conversationHead.setPriority(conversationHeadUpdate.getPriority() == null ?
                conversationHead.getPriority() : conversationHeadUpdate.getPriority());
        conversationHead.setParentRequestId(conversationHeadUpdate.getParentRequestId() == null ?
                conversationHead.getParentRequestId() : conversationHeadUpdate.getParentRequestId());
        conversationHead.setGrandParentRequestId(conversationHeadUpdate.getGrandParentRequestId() == null ?
                conversationHead.getGrandParentRequestId() : conversationHeadUpdate.getGrandParentRequestId());
        conversationHead.setSourceType(conversationHeadUpdate.getSourceType() == null ?
                conversationHead.getSourceType() : conversationHeadUpdate.getSourceType());
        conversationHead.setSourceId(conversationHeadUpdate.getSourceId() == null ?
                conversationHead.getSourceId() : conversationHeadUpdate.getSourceId());
        conversationHead.setRequestId(conversationHeadUpdate.getRequestId() == null ?
                conversationHead.getRequestId() : conversationHeadUpdate.getRequestId());
        conversationHead.setOrgId(conversationHeadUpdate.getOrgId() == null ?
                conversationHead.getOrgId() : conversationHeadUpdate.getOrgId());
        conversationHead.setStatus(conversationHeadUpdate.getStatus() == null ?
                conversationHead.getStatus() : conversationHeadUpdate.getStatus());
        conversationHead.setRaisedBy(conversationHeadUpdate.getRaisedBy() == null ?
                conversationHead.getRaisedBy() : conversationHeadUpdate.getRaisedBy());
        conversationHead.setFirstName(conversationHeadUpdate.getFirstName() == null ?
                conversationHead.getFirstName() : conversationHeadUpdate.getFirstName());
        conversationHead.setLastName(conversationHeadUpdate.getLastName() == null ?
                conversationHead.getLastName() : conversationHeadUpdate.getLastName());
        conversationHead.setRole(conversationHeadUpdate.getRole() == null ?
                conversationHead.getRole() : conversationHeadUpdate.getRole());
        conversationHead.setFirstName(conversationHeadUpdate.getFirstName() == null ?
                conversationHead.getFirstName() : conversationHeadUpdate.getFirstName());
        conversationHead.setAssignee(conversationHeadUpdate.getAssignee() == null ?
                conversationHead.getAssignee() : conversationHeadUpdate.getAssignee());
        conversationHead.setChannel(conversationHeadUpdate.getChannel() == null ?
                conversationHead.getChannel() : conversationHeadUpdate.getChannel());
        conversationHead.setContractId(conversationHeadUpdate.getContractId() == null ?
                conversationHead.getContractId() : conversationHeadUpdate.getContractId());
        conversationHead.setInternal(conversationHeadUpdate.getInternal() == null ?
                conversationHead.getInternal() : conversationHeadUpdate.getInternal());
        conversationHead.setCustomerId(conversationHeadUpdate.getCustomerId() == null ?
                conversationHead.getCustomerId() : conversationHeadUpdate.getCustomerId());
        conversationHead.setSubscriptionId(conversationHeadUpdate.getSubscriptionId() == null ?
                conversationHead.getSubscriptionId() : conversationHeadUpdate.getSubscriptionId());
        conversationHead.setWorkflowId(conversationHeadUpdate.getWorkflowId() == null ?
                conversationHead.getWorkflowId() : conversationHeadUpdate.getWorkflowId());
        conversationHead.setVariantId(conversationHeadUpdate.getVariantId() == null ?
                conversationHead.getVariantId() : conversationHeadUpdate.getVariantId());
        conversationHead.setRemarks(conversationHeadUpdate.getRemarks() == null?
                conversationHead.getRemarks() : conversationHeadUpdate.getRemarks());

        return conversationHead;
    }

    public static List<ConversationHead> toConversationHeadList(List<ConversationHeadDTO> conversationHeadDTOList) {
        return conversationHeadDTOList.stream().map(ConversationHeadMapper::toConversationHead).collect(Collectors.toList());
    }

    public static List<ConversationHeadDTO> toConversationHeadDTOList(List<ConversationHead> conversationHeadList) {
        return conversationHeadList.stream().map(ConversationHeadMapper::toConversationHeadDTO).collect(Collectors.toList());
    }

    public static ConversationHeadDTO toConversationHeadDTOCustom(ConversationHead conversationHead) {
        if (conversationHead == null) {
            return null;
        }

        return ConversationHeadDTO.builder()
                .id(conversationHead.getId())
                .summary(conversationHead.getSummary())
                .message(conversationHead.getMessage())
                .category(conversationHead.getCategory())
                .subCategory(conversationHead.getSubCategory())
                .sourceType(conversationHead.getSourceType())
                .sourceId(conversationHead.getSourceId())
                .status(conversationHead.getStatus())
                .assignee(conversationHead.getAssignee())
                .channel(conversationHead.getChannel())
                .subscriptionId(conversationHead.getSubscriptionId())
                .customerId(conversationHead.getCustomerId())
                .variantId(conversationHead.getVariantId())
                .remarks(conversationHead != null && conversationHead.getRemarks() != null ? conversationHead.getRemarks() : null)
                .module(conversationHead.getModule()!= null? conversationHead.getModule() : null)
                .build();
    }

    public static ConversationHeadDTO toConversationHeadDTOCustom(ConversationHeadTemplateDTO conversationHeadTemplateDTO) {
        return ConversationHeadDTO.builder()
                .id(conversationHeadTemplateDTO.getId())
                .summary(conversationHeadTemplateDTO.getSummary())
                .message(conversationHeadTemplateDTO.getMessage())
                .category(conversationHeadTemplateDTO.getCategory())
                .subCategory(conversationHeadTemplateDTO.getSubCategory())
                .priority(conversationHeadTemplateDTO.getPriority())
                .sourceId(conversationHeadTemplateDTO.getSourceId()) //requesterId
                .status(conversationHeadTemplateDTO.getStatus())
                .raisedBy(conversationHeadTemplateDTO.getRaisedBy())
                .firstName(conversationHeadTemplateDTO.getFirstName())
                .lastName(conversationHeadTemplateDTO.getLastName())
                .createdAtStr(conversationHeadTemplateDTO.getFormattedCreatedAt())
                .raisedByImgUri(conversationHeadTemplateDTO.getRaisedByImgUri())
                .assignee(conversationHeadTemplateDTO.getAssigneeEntityRoleId())
                .assigneeEntityName(conversationHeadTemplateDTO.getAssigneeEntityName())
                .assigneeImgUri(conversationHeadTemplateDTO.getAssigneeImgUri())
                .sourceType(conversationHeadTemplateDTO.getSourceType())
                .requesterName(conversationHeadTemplateDTO.getRequesterName())
                .requesterImgUri(conversationHeadTemplateDTO.getRequesterImgUri())
                .subscriptionId(conversationHeadTemplateDTO.getSubscriptionId())
                .variantId(conversationHeadTemplateDTO.getVariantId())
                .gardenName(conversationHeadTemplateDTO.getGardenName())
                .gardenImgUri(conversationHeadTemplateDTO.getGardenImgUri())
                .requesterEmail(conversationHeadTemplateDTO.getRequesterEmail())
                .requesterPhone(conversationHeadTemplateDTO.getRequesterPhone())
                .requesterType(conversationHeadTemplateDTO.getRequesterType())
                .build();
    }
    public static ConversationHeadDTO toConversationHeadDTOCustom(ConversationHeadCustomersTemplateDTO conversationHeadTemplateDTO) {
        List<String> headIds = Arrays.asList(conversationHeadTemplateDTO.getHeadIds().split(","));
        List<String> status = Arrays.asList(conversationHeadTemplateDTO.getStatus().split(","));
        List<String> sourceTypes = Arrays.asList(conversationHeadTemplateDTO.getSourceType().split(","));
        List<CustomerConversationHeadDTO> customerConversationHeadDTOS =  new ArrayList<>();
        //ToDO: 2020/11/23 10:55 have to recheck this code
        for(int i=0; i<headIds.size(); i++) {
            CustomerConversationHeadDTO customerConversationHeadDTO = CustomerConversationHeadDTO.builder()
                    .conversationHeadId(headIds.get(i)!= null? Long.valueOf(headIds.get(i)) :null).status(status.get(i)).sourceType(sourceTypes.get(i)).build();
            customerConversationHeadDTOS.add(customerConversationHeadDTO);
        }//--
        return ConversationHeadDTO.builder()
                .ticketCount(conversationHeadTemplateDTO.getTicketCount()!= null? conversationHeadTemplateDTO.getTicketCount() : 0)
                .sourceId(conversationHeadTemplateDTO.getSourceId()!=null? conversationHeadTemplateDTO.getSourceId() : "") //requesterId
                .requesterName(conversationHeadTemplateDTO.getRequesterName()!= null? conversationHeadTemplateDTO.getRequesterName() : "")
                .requesterImgUri(conversationHeadTemplateDTO.getRequesterImgUri()!= null? conversationHeadTemplateDTO.getRequesterImgUri() : "")
                .requesterEmail(conversationHeadTemplateDTO.getRequesterEmail()!= null? conversationHeadTemplateDTO.getRequesterEmail() : "")
                .requesterPhone(conversationHeadTemplateDTO.getRequesterPhone()!= null? conversationHeadTemplateDTO.getRequesterPhone() : "")
                .requesterType(conversationHeadTemplateDTO.getRequesterType()!= null? conversationHeadTemplateDTO.getRequesterType() : "")
                .customerConversationHeadDTOList(customerConversationHeadDTOS)
                .build();
    }
}
