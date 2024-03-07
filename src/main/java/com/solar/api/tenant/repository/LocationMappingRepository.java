package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.physicalLocation.LocationMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationMappingRepository extends JpaRepository<LocationMapping, Long> {

    LocationMapping findByLocationId(Long locationId);

    List<LocationMapping> findAllBySourceIdAndSourceType(Long sourceId, String sourceType);

    LocationMapping findByLocationIdAndSourceType(Long locationId, String sourceType);

    LocationMapping findBySourceIdAndSourceTypeAndPrimaryInd(Long sourceId, String sourceType, String primaryInd);

    LocationMapping findBySourceIdAndSourceTypeAndPrimaryIndAndLocationId(Long sourceId, String sourceType, String primaryInd, Long locationId);

    List<LocationMapping> findAllBySourceIdAndSourceTypeAndPrimaryInd(Long sourceId, String sourceType, String primaryInd);

    LocationMapping findBySourceIdAndSourceTypeAndLocationId(Long sourceId, String sourceType, Long locationId);

    List<LocationMapping> findAllBySourceId(Long sourceId);

    @Query("select count(lm.locationId) from LocationMapping lm where lm.sourceId = :sourceId")
    Integer findCountBySourceId(@Param("sourceId") Long sourceId);
}
