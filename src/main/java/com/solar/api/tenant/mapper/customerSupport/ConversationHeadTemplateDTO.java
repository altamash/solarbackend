package com.solar.api.tenant.mapper.customerSupport;

import org.jetbrains.annotations.Nullable;

public interface ConversationHeadTemplateDTO {

    Long getId();

    @Nullable
    String getSummary();

    @Nullable
    String getMessage();

    @Nullable
    String getCategory();

    @Nullable
    String getSubCategory();

    @Nullable
    String getPriority();

    @Nullable
    String getSourceId();

    @Nullable
    String getStatus();

    @Nullable
    Long getRaisedBy();

    @Nullable
    String getFirstName();

    @Nullable
    String getLastName();

    @Nullable
    String getRole();

    @Nullable
    String getFormattedCreatedAt();

    @Nullable
    String getRaisedByImgUri();

    @Nullable
    Long getAssigneeEntityRoleId();

    @Nullable
    String getAssigneeEntityName();

    @Nullable
    String getAssigneeImgUri();

    @Nullable
    String getRequesterName();

    @Nullable
    String getRequesterImgUri();

    @Nullable
    String getRequesterEmail();

    @Nullable
    String getRequesterPhone();

    @Nullable
    String getRequesterType();

    @Nullable
    String getSourceType();

    @Nullable
    String getVariantId();

    @Nullable
    String getGardenName();

    @Nullable
    String getSubscriptionId();

    @Nullable
    String getSubscriptionName();

    @Nullable
    String getGardenImgUri();
    @Nullable
    String getGroupBy();

}
