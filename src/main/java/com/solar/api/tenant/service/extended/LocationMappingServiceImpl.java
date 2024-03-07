package com.solar.api.tenant.service.extended;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.physicalLocation.LocationMapping;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.repository.LocationMappingRepository;
import com.solar.api.tenant.repository.PhysicalLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationMappingServiceImpl implements LocationMappingService {

    @Autowired
    private LocationMappingRepository locationMappingRepository;
    @Autowired
    private PhysicalLocationRepository physicalLocationRepository;

    @Override
    public LocationMapping findByLocationId(Long locationId) {
        LocationMapping siteLocation = locationMappingRepository.findByLocationId(locationId);
        if (siteLocation != null) {
            siteLocation.setPhysicalLocation(physicalLocationRepository.findById(locationId)
                    .orElseThrow(() -> new NotFoundException(PhysicalLocation.class, locationId)));
        }
        return siteLocation;
    }

    @Override
    public LocationMapping findByLocationIdAndSourceType(Long locationId, String sourceType) {
        return locationMappingRepository.findByLocationIdAndSourceType(locationId, sourceType);
    }

    @Override
    public void save(LocationMapping siteLocation) {
        locationMappingRepository.save(siteLocation);
    }

    @Override
    public void saveAll(List<LocationMapping> locationMappings) {
        locationMappingRepository.saveAll(locationMappings);
    }

    @Override
    public LocationMapping findBySourceIdAndSourceTypeAndPrimaryInd(Long sourceId, String sourceType, String primaryInd) {
        return locationMappingRepository.findBySourceIdAndSourceTypeAndPrimaryInd(sourceId, sourceType, primaryInd);
    }

    @Override
    public List<LocationMapping> findBySourceId(Long sourceId) {
        return locationMappingRepository.findAllBySourceId(sourceId);
    }

    @Override
    public LocationMapping findBySourceIdAndSourceTypeAndPrimaryIndAndLocationId(Long sourceId, String sourceType, String primaryInd, Long locationId) {
        return locationMappingRepository.findBySourceIdAndSourceTypeAndPrimaryIndAndLocationId(sourceId, sourceType, primaryInd, locationId);
    }

    @Override
    public List<LocationMapping> findAllBySourceIdAndSourceTypeAndPrimaryInd(Long sourceId, String sourceType, String primaryInd) {
        return locationMappingRepository.findAllBySourceIdAndSourceTypeAndPrimaryInd(sourceId, sourceType, primaryInd);
    }

    @Override
    public LocationMapping findBySourceIdAndSourceTypeAndLocationId(Long sourceId, String sourceType, Long locationId) {
        return locationMappingRepository.findBySourceIdAndSourceTypeAndLocationId(sourceId, sourceType, locationId);
    }

    @Override
    public List<LocationMapping> findAllBySourceIdAndSourceType(Long sourceId, String sourceType) {
        return locationMappingRepository.findAllBySourceIdAndSourceType(sourceId, sourceType);
    }

    @Override
    public List<LocationMapping> findAllBySourceId(Long sourceId) {
        return locationMappingRepository.findAllBySourceId(sourceId);
    }

    @Override
    public void deleteAllByIds(List<Long> locMapIds) {
        locationMappingRepository.deleteAllById(locMapIds);
    }
}
