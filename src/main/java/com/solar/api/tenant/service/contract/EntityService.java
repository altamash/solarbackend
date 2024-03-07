package com.solar.api.tenant.service.contract;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.tenant.mapper.EntityCountDTO;
import com.solar.api.tenant.mapper.contract.EntityResponseDTO;
import com.solar.api.tenant.model.contract.Entity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public interface EntityService {
    Entity add(Entity entity, Long organizationId, String refCode, List<MultipartFile> multipartFiles) throws URISyntaxException, IOException, StorageException;

    Entity update(Entity entity);

    Entity findById(Long id);

    List<Entity> findAll();

    Entity save(Entity entity);

    Entity findEntityByUserId(Long userId);

    List<Entity> findByEntityName(String entityName);

    boolean isValidateEmail(String emailId);

    List<EntityResponseDTO> findAllByEntityType(String entityType);

    Entity findByIdAndEntityType(Long id, String entityType);

    Entity findByEmailAddressAndEntityType(String emailAddress, String entityType);

    List<Entity> findActiveByEntityType(String entityType);

    Entity findByEmailAddressAndEntityTypeAndIsDeleted(String emailAddress, String entityType, Boolean isDeleted);

    List<Entity> findByEntityTypeAndStatus(String entityType);

    boolean isValidateEmployeeEmail(String emailId);

    Map<String, String> isValidateCustomerEmail(String emailId);

    Map<String, Object> getCustomerProfileByUserId(Long userId);

    EntityCountDTO countByEntityType();
    List<Long> findAllIdsByOrgId(List<Long> orgIds, String entityType,Boolean isDeleted);

    List<Entity> findByEmailAddressAndEntityTypeIn(String emailAddress, String entityType);

}

