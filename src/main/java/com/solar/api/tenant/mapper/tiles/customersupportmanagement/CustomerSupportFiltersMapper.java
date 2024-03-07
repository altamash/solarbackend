package com.solar.api.tenant.mapper.tiles.customersupportmanagement;

import com.solar.api.tenant.mapper.customerSupport.ConversationHeadTemplateDTO;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.PhysicalLocationOMTemplate;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.PhysicalLocationOMTile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerSupportFiltersMapper {
    public static CustomerSupportFiltersTile toCustomerSupportFiltersTile(CustomerSupportFiltersTemplate customerSupportFiltersTemplate) {
        return CustomerSupportFiltersTile.builder()
                .ticketType(customerSupportFiltersTemplate.getSourceTypes() != null ? Arrays.stream(customerSupportFiltersTemplate.getSourceTypes().split(
                        ",\\s*")).collect(Collectors.toList()) : new ArrayList<>())
                .priority(customerSupportFiltersTemplate.getPriorities()!= null? Arrays.stream(customerSupportFiltersTemplate.getPriorities()
                        .split(",\\s*")).collect(Collectors.toList()) : new ArrayList<>())
                .category(customerSupportFiltersTemplate.getCategories()!= null? Arrays.stream(customerSupportFiltersTemplate.getCategories()
                        .split(",\\s*")).collect(Collectors.toList()) : new ArrayList<>())
                .subCategory(customerSupportFiltersTemplate.getSubCategories()!= null? Arrays.stream(customerSupportFiltersTemplate.getSubCategories()
                        .split(",\\s*")).collect(Collectors.toList()) : new ArrayList<>())
                .status(customerSupportFiltersTemplate.getStatuses()!= null? Arrays.stream(customerSupportFiltersTemplate.getStatuses()
                        .split(",\\s*")).collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }
    public static List<CustomerSupportTemplateTile> toConversationHeadTilesGroupBy(List<ConversationHeadTemplateDTO> conversationHeadTemplateDTOS) {
        return conversationHeadTemplateDTOS.stream().map(ut -> toConversationHeadTileGroupBy(ut)).collect(Collectors.toList());
    }
    public static CustomerSupportTemplateTile toConversationHeadTileGroupBy(ConversationHeadTemplateDTO conversationHeadTemplateDTO) {
        if (conversationHeadTemplateDTO == null) {
            return null;
        }
        return CustomerSupportTemplateTile.builder()
                .groupBy(conversationHeadTemplateDTO.getGroupBy())
                .build();
    }
    public static List<CustomerSupportTemplateTile> toConversationHeadOMTiles(List<ConversationHeadTemplateDTO> conversationHeadTemplateDTOS) {
        return conversationHeadTemplateDTOS.stream().map(ut -> toConversationHeadOMTile(ut)).collect(Collectors.toList());
    }
    public static CustomerSupportTemplateTile toConversationHeadOMTile(ConversationHeadTemplateDTO conversationHeadTemplateDTO) {
        if (conversationHeadTemplateDTO == null) {
            return null;
        }
        return CustomerSupportTemplateTile.builder()
                .Id(conversationHeadTemplateDTO.getId())
                .AssigneeEntityName(conversationHeadTemplateDTO.getAssigneeEntityName())
                .AssigneeEntityRoleId(conversationHeadTemplateDTO.getAssigneeEntityRoleId())
                .AssigneeImgUri(conversationHeadTemplateDTO.getAssigneeImgUri())
                .Category(conversationHeadTemplateDTO.getCategory())
                .FirstName(conversationHeadTemplateDTO.getFirstName())
                .LastName(conversationHeadTemplateDTO.getLastName())
                .FormattedCreatedAt(conversationHeadTemplateDTO.getFormattedCreatedAt())
                .GardenImgUri(conversationHeadTemplateDTO.getGardenImgUri())
                .GardenName(conversationHeadTemplateDTO.getGardenName())
                .Priority(conversationHeadTemplateDTO.getPriority())
                .RaisedBy(conversationHeadTemplateDTO.getRaisedBy())
                .RaisedByImgUri(conversationHeadTemplateDTO.getRaisedByImgUri())
                .Message(conversationHeadTemplateDTO.getMessage())
                .RequesterEmail(conversationHeadTemplateDTO.getRequesterEmail())
                .Role(conversationHeadTemplateDTO.getRole())
                .RequesterImgUri(conversationHeadTemplateDTO.getRequesterImgUri())
                .RequesterPhone(conversationHeadTemplateDTO.getRequesterPhone())
                .RequesterType(conversationHeadTemplateDTO.getRequesterType())
                .SourceId(conversationHeadTemplateDTO.getSourceId())
                .Status(conversationHeadTemplateDTO.getStatus())
                .RequesterName(conversationHeadTemplateDTO.getRequesterName())
                .SubCategory(conversationHeadTemplateDTO.getSubCategory())
                .Summary(conversationHeadTemplateDTO.getSummary())
                .SubscriptionId(conversationHeadTemplateDTO.getSubscriptionId())
                .SubscriptionName(conversationHeadTemplateDTO.getSubscriptionName())
                .SourceType(conversationHeadTemplateDTO.getSourceType())
                .VariantId(conversationHeadTemplateDTO.getVariantId())
                .SourceType(conversationHeadTemplateDTO.getSourceType())
                .groupBy(conversationHeadTemplateDTO.getGroupBy())
                .build();
    }
}
