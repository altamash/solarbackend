package com.solar.api.tenant.service.extended;

import com.solar.api.tenant.model.extended.physicalLocation.LocationMapping;

import java.util.List;

public interface LocationMappingService {
    LocationMapping findByLocationId(Long locationId);
    LocationMapping findByLocationIdAndSourceType(Long locationId, String sourceType);
    void save (LocationMapping locationMapping);
    void saveAll (List<LocationMapping> locationMappingList);
    LocationMapping findBySourceIdAndSourceTypeAndPrimaryInd(Long sourceId,String sourceType, String primaryInd);
    List<LocationMapping> findBySourceId(Long sourceId);
    LocationMapping findBySourceIdAndSourceTypeAndPrimaryIndAndLocationId(Long sourceId,String sourceType, String primaryInd, Long locationId);
    List<LocationMapping> findAllBySourceIdAndSourceTypeAndPrimaryInd(Long sourceId,String sourceType,String primaryInd);
    LocationMapping findBySourceIdAndSourceTypeAndLocationId(Long sourceId,String sourceType, Long locationId);
    List<LocationMapping> findAllBySourceIdAndSourceType(Long sourceId,String sourceType);

    List<LocationMapping> findAllBySourceId(Long sourceId);

    void deleteAllByIds(List<Long> locMapIds);
}
