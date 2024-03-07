package com.solar.api.tenant.mapper.tiles.workorder;

import com.solar.api.tenant.mapper.customerSupport.ConversationHeadDTO;
import com.solar.api.tenant.mapper.customerSupport.CustomerConversationHeadDTO;
import com.solar.api.tenant.mapper.customerSupport.CustomerDTO;
import com.solar.api.tenant.mapper.tiles.workorder.filter.WorkOrderManagementFilterDTO;
import com.solar.api.tenant.model.dataexport.employee.EmployeeDataDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WorkOrderManagementMapper {
    public static WorkOrderManagementTile toWorkOrderManagementTile(WorkOrderManagementTemplate workOrderManagementTemplate) {
        return WorkOrderManagementTile.builder()
                .projectId(workOrderManagementTemplate.getProjectId())
                .workOrderId(workOrderManagementTemplate.getWorkOrderId())
                .conversationHeadId(workOrderManagementTemplate.getConversationHeadId())
                .workOrderTitle(workOrderManagementTemplate.getWorkOrderTitle())
                .workOrderType(workOrderManagementTemplate.getWorkOrderType())
                .ticketId(workOrderManagementTemplate.getTicketId())
                .businessUnitName(workOrderManagementTemplate.getBusinessUnitName())
                .status(workOrderManagementTemplate.getStatus())
                .requestorAcctId(workOrderManagementTemplate.getRequesterAcctId())
                .requestorEntityId(workOrderManagementTemplate.getRequesterEntityId())
                .requestorName(workOrderManagementTemplate.getRequesterName())
                .requestorImage(workOrderManagementTemplate.getRequesterImage())
                .requestorType(workOrderManagementTemplate.getRequesterType())
                .supportAgentAcctId(workOrderManagementTemplate.getSupportAgentAcctId())
                .supportAgentEntityId(workOrderManagementTemplate.getSupportAgentEntityId())
                .supportAgentName(workOrderManagementTemplate.getSupportAgentName())
                .supportAgentImage(workOrderManagementTemplate.getSupportAgentImage())
                .plannedDate(workOrderManagementTemplate.getPlannedDate())
                .timeRequired(workOrderManagementTemplate.getTimeRequired())
                .assignedResources(workOrderManagementTemplate.getAssignedResources())
                .billable(workOrderManagementTemplate.getBillable())
                .isLeaf((workOrderManagementTemplate.getIsLeaf() != null && workOrderManagementTemplate.getIsLeaf() == 1)).build();
    }

    public static List<WorkOrderManagementTile> toWorkOrderManagementTiles(List<WorkOrderManagementTemplate> workOrderManagementTemplates) {
        return workOrderManagementTemplates.stream().map(WorkOrderManagementMapper::toWorkOrderManagementTile).collect(Collectors.toList());
    }

    public static WorkOrderInformationTile toWorkOrderInformationTile(WorkOrderInformationTemplate workOrderInformationTemplate) {
        return WorkOrderInformationTile.builder()
                .subscriptionId(workOrderInformationTemplate.getWorkOrderId())
                .message(workOrderInformationTemplate.getTitle())
                .category(workOrderInformationTemplate.getType())
                .parentRequestId(workOrderInformationTemplate.getServiceReferenceId())
                .status(workOrderInformationTemplate.getStatus())
                .organizationName(workOrderInformationTemplate.getBoard())
                .plannedDate(workOrderInformationTemplate.getPlannedDate())
                .estimatedHours(workOrderInformationTemplate.getTimeRequired())
                .assignedResource(workOrderInformationTemplate.getAssignedResource())
                .billable(workOrderInformationTemplate.getBillable())
                .updatedAt(workOrderInformationTemplate.getUpdatedAt())
                .entityName(workOrderInformationTemplate.getName())
                .uri(workOrderInformationTemplate.getUrl())
                .build();
    }

    public static WorkOrderCustomerDetailTile toWorkOrderCustomerDetailTile(WorkOrderCustomerDetailTemplate workOrderCustomerDetailTemplate) {
        List<String> headIds = workOrderCustomerDetailTemplate.getHeadIds() != null ? Arrays.asList(workOrderCustomerDetailTemplate.getHeadIds().split(",")) : Collections.emptyList();
        List<String> status = workOrderCustomerDetailTemplate.getStatus() != null ? Arrays.asList(workOrderCustomerDetailTemplate.getStatus().split(",")) : Collections.emptyList();
        List<String> sourceTypes = workOrderCustomerDetailTemplate.getSourceType() != null ? Arrays.asList(workOrderCustomerDetailTemplate.getSourceType().split(",")) : Collections.emptyList();
        List<String> workOrderIds = workOrderCustomerDetailTemplate.getWorkOrderIds() != null ? Arrays.asList(workOrderCustomerDetailTemplate.getWorkOrderIds().split(",")) : Collections.emptyList();
        List<String> organizationNames = workOrderCustomerDetailTemplate.getOrganizationNames() != null ? Arrays.asList(workOrderCustomerDetailTemplate.getOrganizationNames().split(",")) : Collections.emptyList();


        List<CustomerConversationHeadDTO> customerConversationHeadDTOS = new ArrayList<>();
        int maxLength = Collections.max(Arrays.asList(headIds.size(), status.size(), sourceTypes.size(), workOrderIds.size(), organizationNames.size()));

        for (int i = 0; i < maxLength; i++) {
            CustomerConversationHeadDTO customerConversationHeadDTO = CustomerConversationHeadDTO.builder()
                    .conversationHeadId(i < headIds.size() ? Long.valueOf(headIds.get(i)) : null)
                    .status(i < status.size() ? status.get(i) : null)
                    .sourceType(i < sourceTypes.size() ? sourceTypes.get(i) : null)
                    .workOrderId(i < workOrderIds.size() ? workOrderIds.get(i) : null)
                    .organizationName(i < organizationNames.size() ? organizationNames.get(i) : null)
                    .build();
            customerConversationHeadDTOS.add(customerConversationHeadDTO);
        }

        return WorkOrderCustomerDetailTile.builder()
                .accountId(workOrderCustomerDetailTemplate.getAccountId())
                .uri(workOrderCustomerDetailTemplate.getUri())
                .customerType(workOrderCustomerDetailTemplate.getCustomerType())
                .email(workOrderCustomerDetailTemplate.getEmail())
                .phoneNumber(workOrderCustomerDetailTemplate.getPhoneNumber())
                .name(workOrderCustomerDetailTemplate.getName())
                .entityId(workOrderCustomerDetailTemplate.getEntityId())
                .workOrderCount(workOrderCustomerDetailTemplate.getWorkOrderCount())
                .customerConversationHeadDTOList(customerConversationHeadDTOS)
                .build();
    }


    public static List<WorkOrderCustomerDetailTile> toWorkOrderCustomerDetailTiles(List<WorkOrderCustomerDetailTemplate> workOrderCustomerDetailTemplates) {
        return workOrderCustomerDetailTemplates.stream().map(WorkOrderManagementMapper::toWorkOrderCustomerDetailTile).collect(Collectors.toList());
    }

    public static WorkOrderGardenDetailTile toWorkOrderGardenDetailTile(WorkOrderGardenDetailTemplate workOrderGardenDetailTemplate) {
        List<String> headIds = workOrderGardenDetailTemplate.getHeadIds() != null ? Arrays.asList(workOrderGardenDetailTemplate.getHeadIds().split(",")) : Collections.emptyList();
        List<String> status = workOrderGardenDetailTemplate.getStatus() != null ? Arrays.asList(workOrderGardenDetailTemplate.getStatus().split(",")) : Collections.emptyList();
        List<String> sourceTypes = workOrderGardenDetailTemplate.getSourceType() != null ? Arrays.asList(workOrderGardenDetailTemplate.getSourceType().split(",")) : Collections.emptyList();
        List<String> workOrderIds = workOrderGardenDetailTemplate.getWorkOrderIds() != null ? Arrays.asList(workOrderGardenDetailTemplate.getWorkOrderIds().split(",")) : Collections.emptyList();
        List<String> organizationNames = workOrderGardenDetailTemplate.getOrganizationNames() != null ? Arrays.asList(workOrderGardenDetailTemplate.getOrganizationNames().split(",")) : Collections.emptyList();


        List<CustomerConversationHeadDTO> customerConversationHeadDTOS = new ArrayList<>();
        int maxLength = Collections.max(Arrays.asList(headIds.size(), status.size(), sourceTypes.size(), workOrderIds.size(), organizationNames.size()));

        for (int i = 0; i < maxLength; i++) {
            CustomerConversationHeadDTO customerConversationHeadDTO = CustomerConversationHeadDTO.builder()
                    .conversationHeadId(i < headIds.size() ? Long.valueOf(headIds.get(i)) : null)
                    .status(i < status.size() ? status.get(i) : null)
                    .sourceType(i < sourceTypes.size() ? sourceTypes.get(i) : null)
                    .workOrderId(i < workOrderIds.size() ? workOrderIds.get(i) : null)
                    .organizationName(i < organizationNames.size() ? organizationNames.get(i) : null)
                    .build();
            customerConversationHeadDTOS.add(customerConversationHeadDTO);
        }

        return WorkOrderGardenDetailTile.builder()
                .accountId(workOrderGardenDetailTemplate.getAccountId())
                .uri(workOrderGardenDetailTemplate.getUri())
                .refId(workOrderGardenDetailTemplate.getRefId())
                .refType(workOrderGardenDetailTemplate.getRefType())
                .productName(workOrderGardenDetailTemplate.getProductName())
                .subsStatus(workOrderGardenDetailTemplate.getSubsStatus())
                .workOrderCount(workOrderGardenDetailTemplate.getWorkOrderCount())
                .customerConversationHeadDTOList(customerConversationHeadDTOS)
                .build();
    }

    public static List<WorkOrderGardenDetailTile> toWorkOrderGardenDetailTiles(List<WorkOrderGardenDetailTemplate> workOrderGardenDetailTemplates) {
        return workOrderGardenDetailTemplates.stream().map(WorkOrderManagementMapper::toWorkOrderGardenDetailTile).collect(Collectors.toList());
    }

    public static WorkOrderManagementTile toWorkOrderManagementTileGroupBy(WorkOrderManagementTemplate workOrderManagementTemplate) {
        if (workOrderManagementTemplate == null) {
            return null;
        }
        return WorkOrderManagementTile.builder()
                .groupBy(workOrderManagementTemplate.getGroupBy())
                .isLeaf((workOrderManagementTemplate.getIsLeaf() != null && workOrderManagementTemplate.getIsLeaf() == 1))
                .build();
    }

    public static List<WorkOrderManagementTile> toWorkOrderManagementTilesGroupBy(List<WorkOrderManagementTemplate> workOrderManagementTemplates) {
        return workOrderManagementTemplates.stream().map(ut -> toWorkOrderManagementTileGroupBy(ut)).collect(Collectors.toList());
    }

    public static WorkOrderManagementFilterDTO toWorkOrderManagementFilterDTO(WorkOrderManagementTemplate workOrderManagementTemplate) {
        if (workOrderManagementTemplate == null) {
            return null;
        }
        List<String> status = workOrderManagementTemplate.getStatus() != null ? Arrays.asList(workOrderManagementTemplate.getStatus().split(",")) : Collections.emptyList();
        List<String> type = workOrderManagementTemplate.getWorkOrderType() != null ? Arrays.asList(workOrderManagementTemplate.getWorkOrderType().split(",")) : Collections.emptyList();
        List<String> requesterType = workOrderManagementTemplate.getRequesterType() != null ? Arrays.asList(workOrderManagementTemplate.getRequesterType().split(",")) : Collections.emptyList();

        List<CustomerDTO> requester = workOrderManagementTemplate.getRequesterName() != null ?
                Arrays.stream(workOrderManagementTemplate.getRequesterName().split(","))
                        .map(s -> s.split("-"))
                        .filter(parts -> parts.length == 2 && isNumeric(parts[1]))
                        .map(parts -> new CustomerDTO(parts[0], Long.parseLong(parts[1])))
                        .collect(Collectors.toList()) : Collections.emptyList();

        List<EmployeeDataDTO> supportAgentName = workOrderManagementTemplate.getSupportAgentName() != null ?
                Arrays.stream(workOrderManagementTemplate.getSupportAgentName().split(","))
                        .map(s -> s.split("-"))
                        .filter(parts -> parts.length == 2 && isNumeric(parts[1]))
                        .map(parts -> new EmployeeDataDTO(parts[0], Long.parseLong(parts[1])))
                        .collect(Collectors.toList()) : Collections.emptyList();

        List<String> billable = workOrderManagementTemplate.getBillable() != null ? Arrays.asList(workOrderManagementTemplate.getBillable().split(",")) : Collections.emptyList();

        return WorkOrderManagementFilterDTO.builder()
                .status(status)
                .type(type)
                .requesterType(requesterType)
                .requester(requester)
                .supportAgent(supportAgentName)
                .billable(billable)
                .build();
    }

    private static boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static ConversationHeadDTO toConversationHeadDTO(WorkOrderManagementTemplate workOrderManagementTemplate) {
        if (workOrderManagementTemplate == null) {
            return null;
        }
        return ConversationHeadDTO.builder()
                .requesterName(workOrderManagementTemplate.getRequesterName())
                .customerId(workOrderManagementTemplate.getRequesterAcctId())
                .requesterImgUri(workOrderManagementTemplate.getRequesterImage())
                .sourceId(workOrderManagementTemplate.getRequesterEntityId() != null ? String.valueOf(workOrderManagementTemplate.getRequesterEntityId()) : null)
                .subscriptionName(workOrderManagementTemplate.getSubsName())
                .subscriptionId(workOrderManagementTemplate.getSubsId())
                .subsAddress(workOrderManagementTemplate.getSubsAddress())
                .gardenName(workOrderManagementTemplate.getVariantName())
                .variantId(workOrderManagementTemplate.getVariantId())
                .gardenImgUri(workOrderManagementTemplate.getVariantImage())
                .variantAddress(workOrderManagementTemplate.getVariantAddress())
                .sourceType(workOrderManagementTemplate.getWorkOrderType())
                .build();
    }
}
