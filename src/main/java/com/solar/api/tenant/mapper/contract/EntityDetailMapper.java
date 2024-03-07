package com.solar.api.tenant.mapper.contract;

import com.solar.api.tenant.model.contract.EntityDetail;

import java.util.List;
import java.util.stream.Collectors;

public class EntityDetailMapper {

    public static EntityDetail toEntityDetail(EntityDetailDTO entityDetailDTO) {
        if (entityDetailDTO == null) {
            return null;
        }
        return EntityDetail.builder()
                .id(entityDetailDTO.getId())
                .fileName(entityDetailDTO.getFileName())
                .uri(entityDetailDTO.getUri())
                .entity(EntityMapper.toEntity(entityDetailDTO.getEntityDTO()))
                .build();
    }

    public static EntityDetailDTO toEntityDetailDTO(EntityDetail entityDetail) {
        if (entityDetail == null) {
            return null;
        }
        return EntityDetailDTO.builder()
                .id(entityDetail.getId())
                .fileName(entityDetail.getFileName())
                .uri(entityDetail.getUri())
                .entityDTO(EntityMapper.toEntityDTO(entityDetail.getEntity()))
                .build();
    }

    public static List<EntityDetail> toEntityDetailList(List<EntityDetailDTO> entityDetailDTOList) {
        return entityDetailDTOList.stream().map(EntityDetailMapper::toEntityDetail).collect(Collectors.toList());
    }

    public static List<EntityDetailDTO> toEntityDetailDTOList(List<EntityDetail> entityDetailList) {
        return entityDetailList.stream().map(EntityDetailMapper::toEntityDetailDTO).collect(Collectors.toList());
    }

    public static EntityDetail toUpdatedEntityDetail(EntityDetail entityDetail, EntityDetail EntityDetailUpdate) {
        entityDetail.setId(EntityDetailUpdate.getId() == null ? entityDetail.getId() : EntityDetailUpdate.getId());
        entityDetail.setEntity(EntityDetailUpdate.getEntity() == null ? entityDetail.getEntity() : EntityDetailUpdate.getEntity());
        entityDetail.setUri(EntityDetailUpdate.getUri() == null ? entityDetail.getUri() : EntityDetailUpdate.getUri());
        entityDetail.setFileName(EntityDetailUpdate.getFileName() == null ? entityDetail.getFileName() : EntityDetailUpdate.getFileName());
        return entityDetail;
    }
}
