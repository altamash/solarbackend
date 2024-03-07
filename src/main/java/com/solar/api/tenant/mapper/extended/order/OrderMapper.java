package com.solar.api.tenant.mapper.extended.order;

import com.solar.api.tenant.model.extended.order.ClaimFile;
import com.solar.api.tenant.model.extended.order.OrderDetail;
import com.solar.api.tenant.model.extended.order.OrderHead;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    // OrderHead ////////////////////////////////////////////////
    public static OrderHead toOrderHead(OrderHeadDTO orderHeadDTO) {
        if (orderHeadDTO == null) {
            return null;
        }
        return OrderHead.builder()
                .id(orderHeadDTO.getId())
                .registerId(orderHeadDTO.getRegisterId())
                .orderDate(orderHeadDTO.getOrderDate())
                .build();
    }

    public static OrderHeadDTO toOrderHeadDTO(OrderHead orderHead) {
        if (orderHead == null) {
            return null;
        }
        return OrderHeadDTO.builder()
                .id(orderHead.getId())
                .registerId(orderHead.getRegisterId())
                .orderDate(orderHead.getOrderDate())
                .createdAt(orderHead.getCreatedAt())
                .updatedAt(orderHead.getUpdatedAt())
                .build();
    }

    public static OrderHead toUpdatedOrderHead(OrderHead orderHead, OrderHead orderHeadUpdate) {
        orderHead.setRegisterId(orderHeadUpdate.getRegisterId() == null ? orderHead.getRegisterId() :
                orderHeadUpdate.getRegisterId());
        orderHead.setOrderDate(orderHeadUpdate.getOrderDate() == null ? orderHead.getOrderDate() :
                orderHeadUpdate.getOrderDate());
        return orderHead;
    }

    public static List<OrderHead> toOrderHeads(List<OrderHeadDTO> orderHeadDTOS) {
        return orderHeadDTOS.stream().map(o -> toOrderHead(o)).collect(Collectors.toList());
    }

    public static List<OrderHeadDTO> toOrderHeadDTOs(List<OrderHead> orderHeads) {
        return orderHeads.stream().map(o -> toOrderHeadDTO(o)).collect(Collectors.toList());
    }

    // OrderDetail ////////////////////////////////////////////////
    public static OrderDetail toOrderDetail(OrderDetailDTO orderDetailDTO) {
        if (orderDetailDTO == null) {
            return null;
        }
        return OrderDetail.builder()
                .id(orderDetailDTO.getId())
                .orderId(orderDetailDTO.getOrderId())
                .measure(orderDetailDTO.getMeasure())
                .value(orderDetailDTO.getValue())
                .updateDate(orderDetailDTO.getUpdateDate())
                .build();
    }

    public static OrderDetailDTO toOrderDetailDTO(OrderDetail orderDetail) {
        if (orderDetail == null) {
            return null;
        }
        return OrderDetailDTO.builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrderId())
                .measure(orderDetail.getMeasure())
                .value(orderDetail.getValue())
                .updateDate(orderDetail.getUpdateDate())
                .createdAt(orderDetail.getCreatedAt())
                .updatedAt(orderDetail.getUpdatedAt())
                .build();
    }

    public static OrderDetail toUpdatedOrderDetail(OrderDetail orderDetail, OrderDetail orderDetailUpdate) {
        orderDetail.setOrderId(orderDetailUpdate.getOrderId() == null ? orderDetail.getOrderId() :
                orderDetailUpdate.getOrderId());
        orderDetail.setMeasure(orderDetailUpdate.getMeasure() == null ? orderDetail.getMeasure() :
                orderDetailUpdate.getMeasure());
        orderDetail.setValue(orderDetailUpdate.getValue() == null ? orderDetail.getValue() :
                orderDetailUpdate.getValue());
        orderDetail.setUpdateDate(orderDetailUpdate.getUpdateDate() == null ? orderDetail.getUpdateDate() :
                orderDetailUpdate.getUpdateDate());
        return orderDetail;
    }

    public static List<OrderDetail> toOrderDetails(List<OrderDetailDTO> orderDetailDTOS) {
        return orderDetailDTOS.stream().map(o -> toOrderDetail(o)).collect(Collectors.toList());
    }

    public static List<OrderDetailDTO> toOrderDetailDTOs(List<OrderDetail> orderDetails) {
        return orderDetails.stream().map(o -> toOrderDetailDTO(o)).collect(Collectors.toList());
    }

    // ClaimFile ////////////////////////////////////////////////
    public static ClaimFile toClaimFile(ClaimFileDTO claimFileDTO) {
        if (claimFileDTO == null) {
            return null;
        }
        return ClaimFile.builder()
                .id(claimFileDTO.getId())
                .claimType(claimFileDTO.getClaimType())
                .partnerId(claimFileDTO.getPartnerId())
                .assetId(claimFileDTO.getAssetId())
                .locationId(claimFileDTO.getLocationId())
                .filer(claimFileDTO.getFiler())
                .claimDateTime(claimFileDTO.getClaimDateTime())
                .status(claimFileDTO.getStatus())
                .approvedAmt(claimFileDTO.getApprovedAmt())
                .approvalType(claimFileDTO.getApprovalType())
                .approver(claimFileDTO.getApprover())
                .approveDateTime(claimFileDTO.getApproveDateTime())
                .reasonDetails(claimFileDTO.getReasonDetails())
                .build();
    }

    public static ClaimFileDTO toClaimFileDTO(ClaimFile claimFile) {
        if (claimFile == null) {
            return null;
        }
        return ClaimFileDTO.builder()
                .id(claimFile.getId())
                .claimType(claimFile.getClaimType())
                .partnerId(claimFile.getPartnerId())
                .assetId(claimFile.getAssetId())
                .locationId(claimFile.getLocationId())
                .filer(claimFile.getFiler())
                .claimDateTime(claimFile.getClaimDateTime())
                .status(claimFile.getStatus())
                .approvedAmt(claimFile.getApprovedAmt())
                .approvalType(claimFile.getApprovalType())
                .approver(claimFile.getApprover())
                .approveDateTime(claimFile.getApproveDateTime())
                .reasonDetails(claimFile.getReasonDetails())
                .createdAt(claimFile.getCreatedAt())
                .updatedAt(claimFile.getUpdatedAt())
                .build();
    }

    public static ClaimFile toUpdatedClaimFile(ClaimFile claimFile, ClaimFile claimFileUpdate) {
        claimFile.setClaimType(claimFileUpdate.getClaimType() == null ? claimFile.getClaimType() :
                claimFileUpdate.getClaimType());
        claimFile.setPartnerId(claimFileUpdate.getPartnerId() == null ? claimFile.getPartnerId() :
                claimFileUpdate.getPartnerId());
        claimFile.setAssetId(claimFileUpdate.getAssetId() == null ? claimFile.getAssetId() :
                claimFileUpdate.getAssetId());
        claimFile.setLocationId(claimFileUpdate.getLocationId() == null ? claimFile.getLocationId() :
                claimFileUpdate.getLocationId());
        claimFile.setFiler(claimFileUpdate.getFiler() == null ? claimFile.getFiler() : claimFileUpdate.getFiler());
        claimFile.setClaimDateTime(claimFileUpdate.getClaimDateTime() == null ? claimFile.getClaimDateTime() :
                claimFileUpdate.getClaimDateTime());
        claimFile.setStatus(claimFileUpdate.getStatus() == null ? claimFile.getStatus() : claimFileUpdate.getStatus());
        claimFile.setApprovedAmt(claimFileUpdate.getApprovedAmt() == null ? claimFile.getApprovedAmt() :
                claimFileUpdate.getApprovedAmt());
        claimFile.setApprovalType(claimFileUpdate.getApprovalType() == null ? claimFile.getApprovalType() :
                claimFileUpdate.getApprovalType());
        claimFile.setApprover(claimFileUpdate.getApprover() == null ? claimFile.getApprover() :
                claimFileUpdate.getApprover());
        claimFile.setApproveDateTime(claimFileUpdate.getApproveDateTime() == null ? claimFile.getApproveDateTime() :
                claimFileUpdate.getApproveDateTime());
        claimFile.setReasonDetails(claimFileUpdate.getReasonDetails() == null ? claimFile.getReasonDetails() :
                claimFileUpdate.getReasonDetails());
        return claimFile;
    }

    public static List<ClaimFile> toClaimFiles(List<ClaimFileDTO> claimFileDTOS) {
        return claimFileDTOS.stream().map(c -> toClaimFile(c)).collect(Collectors.toList());
    }

    public static List<ClaimFileDTO> toClaimFileDTOs(List<ClaimFile> claimFiles) {
        return claimFiles.stream().map(c -> toClaimFileDTO(c)).collect(Collectors.toList());
    }
}
