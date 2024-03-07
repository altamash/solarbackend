package com.solar.api.tenant.service.contract;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.tenant.mapper.contract.EntityDetailDTO;
import com.solar.api.tenant.mapper.tiles.entityDetail.EntityDetailTile;
import com.solar.api.tenant.model.contract.EntityDetail;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface EntityDetailService {

    EntityDetail uploadToStorage(MultipartFile file, Long entityId, Long compKey) throws URISyntaxException, IOException, StorageException;
    EntityDetail updateToStorage(MultipartFile file, Long entityId, Long compKey) throws URISyntaxException, IOException, StorageException;
    EntityDetail findByEntityId(Long entityId);
    List<EntityDetail> findAllByEntityIdIn(List<Long> entityIds);


    EntityDetailTile getImageByEntityId(Long entityId);

    List<EntityDetailTile> findProfilePictureByAccountIds(List<Long> acctIds);

    List<EntityDetailTile> findProfilePictureByEntityDetailIds(List<Long> entityDetailIds);

}
