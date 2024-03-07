package com.solar.api.tenant.service.contract;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.saas.module.com.solar.utility.BatchEngineUtilityServiceImpl;
import com.solar.api.saas.service.StorageService;
import com.solar.api.tenant.mapper.tiles.entityDetail.EntityDetailTile;
import com.solar.api.tenant.model.contract.EntityDetail;
import com.solar.api.tenant.repository.contract.EntityDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Service
public class EntityDetailServiceImpl implements EntityDetailService {

    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private StorageService storageService;
    @Autowired
    private EntityService entityService;
    @Autowired
    private EntityDetailRepository entityDetailRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityDetailServiceImpl.class);


    @Override
    public EntityDetail uploadToStorage(MultipartFile file, Long entityId, Long compKey) throws URISyntaxException, IOException, StorageException {
        try {
            String uri = storageService.storeInContainer(file, appProfile, "tenant/" + compKey
                    + AppConstants.PATHS.ENTITY_PROFILE_PICTURE, file.getOriginalFilename(), compKey, false);
            EntityDetail entityDetail = getEntityDetail(entityId, uri, file.getOriginalFilename());
            return entityDetailRepository.save(entityDetail);
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage());
            return null;
        }
    }

    private EntityDetail getEntityDetail(Long entityId, String uri, String fileName) {
        EntityDetail entityDetail = new EntityDetail();
        EntityDetail entityDetailExist = findByEntityId(entityId);
        if (entityDetailExist != null) {
            entityDetail.setId(entityDetailExist.getId());
        }
        entityDetail.setEntity(entityService.findById(entityId));
        entityDetail.setUri(uri);
        return entityDetail;
    }

    @Override
    public EntityDetail updateToStorage(MultipartFile file, Long entityId, Long compKey) throws URISyntaxException, IOException, StorageException {
        try {
            String uri = storageService.storeInContainer(file, appProfile, "tenant/" + compKey
                    + AppConstants.PATHS.ENTITY_PROFILE_PICTURE, file.getOriginalFilename(), compKey, false);
            EntityDetail entityDetail = getEntityDetailToUpdate(entityId, uri, file.getOriginalFilename());
            return entityDetailRepository.save(entityDetail);
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage());
            return null;
        }
    }

    private EntityDetail getEntityDetailToUpdate(Long entityId, String uri, String fileName) {
        EntityDetail entityDetail = entityDetailRepository.findByEntityId(entityId);
        if (entityDetail == null) {
            entityDetail = new EntityDetail();
        }
        entityDetail.setEntity(entityService.findById(entityId));
        entityDetail.setUri(uri);
        return entityDetail;
    }

    @Override
    public EntityDetail findByEntityId(Long entityId) {
        return entityDetailRepository.findByEntityId(entityId);
    }

    @Override
    public List<EntityDetail> findAllByEntityIdIn(List<Long> entityIds) {
        return entityDetailRepository.findAllByEntityIdIn(entityIds);
    }

    @Override
    public EntityDetailTile getImageByEntityId(Long entityId) {
        return entityDetailRepository.getImageByEntityId(entityId);
    }

    @Override
    public List<EntityDetailTile> findProfilePictureByAccountIds(List<Long> acctIds) {
        return entityDetailRepository.findByAccountIds(acctIds);
    }

    @Override
    public List<EntityDetailTile> findProfilePictureByEntityDetailIds(List<Long> entityDetailIds) {
        return entityDetailRepository.findProfilePictureByEntityDetailIds(entityDetailIds);
    }
}
