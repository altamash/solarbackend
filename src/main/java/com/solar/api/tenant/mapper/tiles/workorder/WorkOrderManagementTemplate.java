package com.solar.api.tenant.mapper.tiles.workorder;

 public interface WorkOrderManagementTemplate {
      String getGroupBy();
      Long getIsLeaf();
      String getProjectId();
      String getWorkOrderId();
      Long getConversationHeadId();
      String getWorkOrderTitle();
      String getWorkOrderType();
      Long getTicketId();
      String getBusinessUnitName();
      String getStatus();
      Long getRequesterAcctId();
      Long getRequesterEntityId();
      String getRequesterName();
      String getRequesterImage();
      String getRequesterType();
      Long getSupportAgentAcctId();
      Long getSupportAgentEntityId();
      String getSupportAgentName();
      String getSupportAgentImage();
      String getPlannedDate();
      Double getTimeRequired();
      Long getAssignedResources();
      String getBillable();
      String getSubsName();
      String getSubsId();
      String getSubsAddress();
      String getVariantName();
      String getVariantId();
      String getVariantImage();
      String getVariantAddress();
}
