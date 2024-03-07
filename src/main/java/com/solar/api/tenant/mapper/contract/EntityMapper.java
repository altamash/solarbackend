package com.solar.api.tenant.mapper.contract;

import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class EntityMapper {
    public static Entity toEntity(EntityDTO entityDTO) {
        if (entityDTO == null) {
            return null;
        }
        return Entity.builder()
                .id(entityDTO.getId())
                .entityName(entityDTO.getEntityName())
                .entityType(entityDTO.getEntityType())
                .status(entityDTO.getStatus() != null ? entityDTO.getStatus() : "INACTIVE")
                .isDocAttached(entityDTO.getIsDocAttached())
                .isDeleted(entityDTO.getIsDeleted())
                .contracts(entityDTO.getContractDTOList() != null ?
                        ContractMapper.toContractList(entityDTO.getContractDTOList()) : null)
                //.organization(entityDTO.getOrganizationDTO() != null ?
                //      OrganizationMapper.toOrganization(entityDTO.getOrganizationDTO()) : null)
                .createdAt(entityDTO.getCreatedAt())
                .updatedAt(entityDTO.getUpdatedAt())
                .companyName(entityDTO.getCompanyName())
                .contactPersonEmail(entityDTO.getContactPersonEmail())
                .contactPersonPhone(entityDTO.getContactPersonPhone())
                .website(entityDTO.getWebsite())
                .isActive(entityDTO.getIsActive() != null ? entityDTO.getIsActive() : false)
                .registerId(entityDTO.getRegisterId() == null ? null : entityDTO.getRegisterId())
                .registerType(entityDTO.getRegisterType() == null ? null : entityDTO.getRegisterType())
                .build();
    }

    public static EntityDTO toEntityDTO(Entity entity) {
        if (entity == null) {
            return null;
        }
        return EntityDTO.builder()
                .id(entity.getId())
                .entityName(entity.getEntityName())
                .entityType(entity.getEntityType())
                .status(entity.getStatus())
                .isDocAttached(entity.getIsDocAttached())
                .isDeleted(entity.getIsDeleted())
                .contractDTOList(entity.getContracts() != null ?
                        ContractMapper.toContractDTOList(entity.getContracts()) : null)
                //.organizationDTO(entity.getOrganization() != null ?
                //      OrganizationMapper.toOrganizationDTO(entity.getOrganization()) : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .companyName(entity.getCompanyName())
                .contactPersonEmail(entity.getContactPersonEmail())
                .contactPersonPhone(entity.getContactPersonPhone())
                .website(entity.getWebsite())
                .build();
    }


    public static EntityDTO toEntityDTOSkimmed(Entity entity) {
        if (entity == null) {
            return null;
        }
        return EntityDTO.builder()
                .id(entity.getId())
                .entityName(entity.getEntityName())
                .entityType(entity.getEntityType())
                .status(entity.getStatus())
                .isDocAttached(entity.getIsDocAttached())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .companyName(entity.getCompanyName())
                .contactPersonEmail(entity.getContactPersonEmail())
                .contactPersonPhone(entity.getContactPersonPhone())
                .website(entity.getWebsite())
                .build();
    }

    public static List<Entity> toEntityList(List<EntityDTO> entityDTOList) {
        return entityDTOList.stream().map(EntityMapper::toEntity).collect(Collectors.toList());
    }

    public static List<EntityDTO> toEntityDTOList(List<Entity> entityList) {
        return entityList.stream().map(EntityMapper::toEntityDTO).collect(Collectors.toList());
    }

    public static List<EntityDTO> toEntityDTOSkimmedList(List<Entity> entityList) {
        return entityList.stream().map(EntityMapper::toEntityDTOSkimmed).collect(Collectors.toList());
    }

    public static EntityDTO userDTOtoEntity(UserDTO userDto, User user, String entityType, String status, Boolean isActive) {
        return EntityDTO.builder()
                .entityName(user.getFirstName().concat(" ").concat(user.getLastName()))
                .entityType(entityType)
                .status(status)
                .isDocAttached(userDto.getIsAttachment())
                .companyName(userDto.getBusinessCompanyName())
                .contactPersonEmail(userDto.getBusinessEmail())
                .contactPersonPhone(userDto.getBusinessPhone())
                .website(userDto.getBusinessWebsite())
                .contactPersonEmail(user.getEmailAddress())
                .contactPersonPhone((userDto.getCountryCode()!=null && userDto.getPhone()!=null)? "(+"+userDto.getCountryCode()+")"+userDto.getPhone(): null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isActive(isActive)
                .build();
    }

    public static Entity toUpdatedEntity(Entity entity, Entity EntityUpdate) {
        entity.setEntityName(EntityUpdate.getEntityName() == null ? entity.getEntityName() : EntityUpdate.getEntityName());
        entity.setEntityType(EntityUpdate.getEntityType() == null ? entity.getEntityType() : EntityUpdate.getEntityType());
        entity.setStatus(EntityUpdate.getStatus() == null ? entity.getStatus() : EntityUpdate.getStatus());
        entity.setIsDocAttached(EntityUpdate.getIsDocAttached() == null ? entity.getIsDocAttached() : EntityUpdate.getIsDocAttached());
        entity.setIsDeleted(EntityUpdate.getIsDeleted() == null ? entity.getIsDeleted() : EntityUpdate.getIsDeleted());
        entity.setCompanyName(EntityUpdate.getCompanyName() == null ? entity.getCompanyName() : EntityUpdate.getCompanyName());
        entity.setContactPersonEmail(EntityUpdate.getContactPersonEmail() == null ? entity.getContactPersonEmail() : EntityUpdate.getContactPersonEmail());
        entity.setContactPersonPhone(EntityUpdate.getContactPersonPhone() == null ? entity.getContactPersonPhone() : EntityUpdate.getContactPersonPhone());
        entity.setWebsite(EntityUpdate.getWebsite() == null ? entity.getWebsite() : EntityUpdate.getWebsite());
        entity.setIsActive(EntityUpdate.getIsActive() == null && entity.getIsActive() ? entity.getIsActive() : EntityUpdate.getIsActive());
        entity.setRegisterType(EntityUpdate.getRegisterType() == null ? entity.getRegisterType() : EntityUpdate.getRegisterType());
        entity.setRegisterId(EntityUpdate.getRegisterId() == null ? entity.getRegisterId() : EntityUpdate.getRegisterId());
        entity.setCreatedAt(EntityUpdate.getCreatedAt() == null ? entity.getCreatedAt() : EntityUpdate.getCreatedAt());
        entity.setUpdatedAt(EntityUpdate.getUpdatedAt() == null ? entity.getUpdatedAt() : EntityUpdate.getUpdatedAt());
        return entity;
    }
}
