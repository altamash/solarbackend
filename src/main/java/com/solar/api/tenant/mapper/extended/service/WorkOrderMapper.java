package com.solar.api.tenant.mapper.extended.service;

import com.solar.api.tenant.model.extended.service.WorkOrderComms;
import com.solar.api.tenant.model.extended.service.WorkOrderDetail;
import com.solar.api.tenant.model.extended.service.WorkOrderHead;

import java.util.List;
import java.util.stream.Collectors;

public class WorkOrderMapper {

    // WorkOrderHead ////////////////////////////////////////////////
    public static WorkOrderHead toWorkOrderHead(WorkOrderHeadDTO workOrderHeadDTO) {
        if (workOrderHeadDTO == null) {
            return null;
        }
        return WorkOrderHead.builder()
                .id(workOrderHeadDTO.getId())
                .serviceId(workOrderHeadDTO.getServiceId())
                .registerId(workOrderHeadDTO.getRegisterId())
                .summary(workOrderHeadDTO.getSummary())
                .status(workOrderHeadDTO.getStatus())
                .createDateTime(workOrderHeadDTO.getCreateDateTime())
                .createdBy(workOrderHeadDTO.getCreatedBy())
                .build();
    }

    public static WorkOrderHeadDTO toWorkOrderHeadDTO(WorkOrderHead workOrderHead) {
        if (workOrderHead == null) {
            return null;
        }
        return WorkOrderHeadDTO.builder()
                .id(workOrderHead.getId())
                .serviceId(workOrderHead.getServiceId())
                .registerId(workOrderHead.getRegisterId())
                .summary(workOrderHead.getSummary())
                .status(workOrderHead.getStatus())
                .createDateTime(workOrderHead.getCreateDateTime())
                .createdBy(workOrderHead.getCreatedBy())
                .createdAt(workOrderHead.getCreatedAt())
                .updatedAt(workOrderHead.getUpdatedAt())
                .build();
    }

    public static WorkOrderHead toUpdatedWorkOrderHead(WorkOrderHead workOrderHead, WorkOrderHead workOrderHeadUpdate) {
        workOrderHead.setServiceId(workOrderHeadUpdate.getServiceId() == null ? workOrderHead.getServiceId() :
                workOrderHeadUpdate.getServiceId());
        workOrderHead.setRegisterId(workOrderHeadUpdate.getRegisterId() == null ? workOrderHead.getRegisterId() :
                workOrderHeadUpdate.getRegisterId());
        workOrderHead.setSummary(workOrderHeadUpdate.getSummary() == null ? workOrderHead.getSummary() :
                workOrderHeadUpdate.getSummary());
        workOrderHead.setStatus(workOrderHeadUpdate.getStatus() == null ? workOrderHead.getStatus() :
                workOrderHeadUpdate.getStatus());
        workOrderHead.setCreateDateTime(workOrderHeadUpdate.getCreateDateTime() == null ?
                workOrderHead.getCreateDateTime() : workOrderHeadUpdate.getCreateDateTime());
        workOrderHead.setCreatedBy(workOrderHeadUpdate.getCreatedBy() == null ? workOrderHead.getCreatedBy() :
                workOrderHeadUpdate.getCreatedBy());
        return workOrderHead;
    }

    public static List<WorkOrderHead> toWorkOrderHeads(List<WorkOrderHeadDTO> workOrderHeadDTOS) {
        return workOrderHeadDTOS.stream().map(w -> toWorkOrderHead(w)).collect(Collectors.toList());
    }

    public static List<WorkOrderHeadDTO> toWorkOrderHeadDTOs(List<WorkOrderHead> workOrderHeads) {
        return workOrderHeads.stream().map(w -> toWorkOrderHeadDTO(w)).collect(Collectors.toList());
    }

    // WorkOrderDetail ////////////////////////////////////////////////
    public static WorkOrderDetail toWorkOrderDetail(WorkOrderDetailDTO workOrderDetailDTO) {
        if (workOrderDetailDTO == null) {
            return null;
        }
        return WorkOrderDetail.builder()
                .id(workOrderDetailDTO.getId())
                .workOrderId(workOrderDetailDTO.getWorkOrderId())
                .measure(workOrderDetailDTO.getMeasure())
                .value(workOrderDetailDTO.getValue())
                .createdBy(workOrderDetailDTO.getCreatedBy())
                .datetime(workOrderDetailDTO.getDatetime())
                .build();
    }

    public static WorkOrderDetailDTO toWorkOrderDetailDTO(WorkOrderDetail workOrderDetail) {
        if (workOrderDetail == null) {
            return null;
        }
        return WorkOrderDetailDTO.builder()
                .id(workOrderDetail.getId())
                .workOrderId(workOrderDetail.getWorkOrderId())
                .measure(workOrderDetail.getMeasure())
                .value(workOrderDetail.getValue())
                .createdBy(workOrderDetail.getCreatedBy())
                .datetime(workOrderDetail.getDatetime())
                .createdAt(workOrderDetail.getCreatedAt())
                .updatedAt(workOrderDetail.getUpdatedAt())
                .build();
    }

    public static WorkOrderDetail toUpdatedWorkOrderDetail(WorkOrderDetail workOrderDetail,
                                                           WorkOrderDetail workOrderDetailUpdate) {
        workOrderDetail.setWorkOrderId(workOrderDetailUpdate.getWorkOrderId() == null ?
                workOrderDetail.getWorkOrderId() : workOrderDetailUpdate.getWorkOrderId());
        workOrderDetail.setMeasure(workOrderDetailUpdate.getMeasure() == null ? workOrderDetail.getMeasure() :
                workOrderDetailUpdate.getMeasure());
        workOrderDetail.setValue(workOrderDetailUpdate.getValue() == null ? workOrderDetail.getValue() :
                workOrderDetailUpdate.getValue());
        workOrderDetail.setCreatedBy(workOrderDetailUpdate.getCreatedBy() == null ? workOrderDetail.getCreatedBy() :
                workOrderDetailUpdate.getCreatedBy());
        workOrderDetail.setDatetime(workOrderDetailUpdate.getDatetime() == null ? workOrderDetail.getDatetime() :
                workOrderDetailUpdate.getDatetime());
        return workOrderDetail;
    }

    public static List<WorkOrderDetail> toWorkOrderDetails(List<WorkOrderDetailDTO> workOrderDetailDTOS) {
        return workOrderDetailDTOS.stream().map(w -> toWorkOrderDetail(w)).collect(Collectors.toList());
    }

    public static List<WorkOrderDetailDTO> toWorkOrderDetailDTOs(List<WorkOrderDetail> workOrderDetails) {
        return workOrderDetails.stream().map(w -> toWorkOrderDetailDTO(w)).collect(Collectors.toList());
    }

    // WorkOrderComms ////////////////////////////////////////////////
    public static WorkOrderComms toWorkOrderComms(WorkOrderCommsDTO workOrderCommsDTO) {
        if (workOrderCommsDTO == null) {
            return null;
        }
        return WorkOrderComms.builder()
                .id(workOrderCommsDTO.getId())
                .workOrderId(workOrderCommsDTO.getWorkOrderId())
                .seq(workOrderCommsDTO.getSeq())
                .message(workOrderCommsDTO.getMessage())
                .type(workOrderCommsDTO.getType())
                .refCode(workOrderCommsDTO.getRefCode())
                .refId(workOrderCommsDTO.getRefId())
                .status(workOrderCommsDTO.getStatus())
                .dependingOn(workOrderCommsDTO.getDependingOn())
                .closingRemarks(workOrderCommsDTO.getClosingRemarks())
                .build();
    }

    public static WorkOrderCommsDTO toWorkOrderCommsDTO(WorkOrderComms workOrderComms) {
        if (workOrderComms == null) {
            return null;
        }
        return WorkOrderCommsDTO.builder()
                .id(workOrderComms.getId())
                .workOrderId(workOrderComms.getWorkOrderId())
                .seq(workOrderComms.getSeq())
                .message(workOrderComms.getMessage())
                .type(workOrderComms.getType())
                .refCode(workOrderComms.getRefCode())
                .refId(workOrderComms.getRefId())
                .status(workOrderComms.getStatus())
                .dependingOn(workOrderComms.getDependingOn())
                .closingRemarks(workOrderComms.getClosingRemarks())
                .createdAt(workOrderComms.getCreatedAt())
                .updatedAt(workOrderComms.getUpdatedAt())
                .build();
    }

    public static WorkOrderComms toUpdatedWorkOrderComms(WorkOrderComms workOrderComms,
                                                         WorkOrderComms workOrderCommsUpdate) {
        workOrderComms.setWorkOrderId(workOrderCommsUpdate.getWorkOrderId() == null ?
                workOrderComms.getWorkOrderId() : workOrderCommsUpdate.getWorkOrderId());
        workOrderComms.setSeq(workOrderCommsUpdate.getSeq() == null ? workOrderComms.getSeq() :
                workOrderCommsUpdate.getSeq());
        workOrderComms.setMessage(workOrderCommsUpdate.getMessage() == null ? workOrderComms.getMessage() :
                workOrderCommsUpdate.getMessage());
        workOrderComms.setType(workOrderCommsUpdate.getType() == null ? workOrderComms.getType() :
                workOrderCommsUpdate.getType());
        workOrderComms.setRefCode(workOrderCommsUpdate.getRefCode() == null ? workOrderComms.getRefCode() :
                workOrderCommsUpdate.getRefCode());
        workOrderComms.setRefId(workOrderCommsUpdate.getRefId() == null ? workOrderComms.getRefId() :
                workOrderCommsUpdate.getRefId());
        workOrderComms.setStatus(workOrderCommsUpdate.getStatus() == null ? workOrderComms.getStatus() :
                workOrderCommsUpdate.getStatus());
        workOrderComms.setDependingOn(workOrderCommsUpdate.getDependingOn() == null ?
                workOrderComms.getDependingOn() : workOrderCommsUpdate.getDependingOn());
        workOrderComms.setClosingRemarks(workOrderCommsUpdate.getClosingRemarks() == null ?
                workOrderComms.getClosingRemarks() : workOrderCommsUpdate.getClosingRemarks());
        return workOrderComms;
    }

    public static List<WorkOrderComms> toWorkOrderComms(List<WorkOrderCommsDTO> workOrderCommsDTOS) {
        return workOrderCommsDTOS.stream().map(w -> toWorkOrderComms(w)).collect(Collectors.toList());
    }

    public static List<WorkOrderCommsDTO> toWorkOrderCommsDTOs(List<WorkOrderComms> workOrderCommss) {
        return workOrderCommss.stream().map(w -> toWorkOrderCommsDTO(w)).collect(Collectors.toList());
    }
}
