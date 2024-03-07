package com.solar.api.tenant.mapper.extended.physicalLocation;

import com.solar.api.saas.service.process.upload.mapper.CustomerAddress;
import com.solar.api.tenant.mapper.contract.OrganizationDetailDTO;
import com.solar.api.tenant.mapper.contract.OrganizationMapper;
import com.solar.api.tenant.model.extended.physicalLocation.LocationMapping;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PhysicalLocationMapper {

    public static PhysicalLocation toPhysicalLocation(PhysicalLocationDTO physicalLocationDTO) {
        if (physicalLocationDTO == null) {
            return null;
        }
        return PhysicalLocation.builder()
                .id(physicalLocationDTO.getId())
                .locationName(physicalLocationDTO.getLocationName())
                .locationType(physicalLocationDTO.getLocationType())
                .otherDetails(physicalLocationDTO.getOtherDetails())
                .add1(physicalLocationDTO.getAdd1())
                .add2(physicalLocationDTO.getAdd2())
                .add3(physicalLocationDTO.getAdd3())
                .contactPerson(physicalLocationDTO.getContactPerson())
                .phone(physicalLocationDTO.getPhone())
                .category(physicalLocationDTO.getCategory())
                //.ext(physicalLocationDTO.getExt())
                .email(physicalLocationDTO.getEmail())
                .externalRefId(physicalLocationDTO.getExternalRefId())
                .geoLat(physicalLocationDTO.getGeoLat())
                .geoLong(physicalLocationDTO.getGeoLong())
                .googleCoordinates(physicalLocationDTO.getGoogleCoordinates())
                .terrainType(physicalLocationDTO.getTerrainType())
                .status(physicalLocationDTO.getStatus())
                .active(physicalLocationDTO.getActive())
                .ext1(physicalLocationDTO.getExt1())
                .ext2(physicalLocationDTO.getExt2())
                .ext3(physicalLocationDTO.getExt3())
                .ext4(physicalLocationDTO.getExt4())
                .correspondenceAddress(physicalLocationDTO.getCorrespondenceAddress())
                .primaryIndex(physicalLocationDTO.getPrimaryIndex())
                .entityId(physicalLocationDTO.getEntityId())
                .zipCode(physicalLocationDTO.getZipCode())
                .isPrimary(physicalLocationDTO.getIsPrimary())
                .isDeleted(physicalLocationDTO.getIsDeleted())
                .organization(physicalLocationDTO.getOrganizationDTO() != null ? OrganizationMapper.toOrganization(physicalLocationDTO.getOrganizationDTO()) : null)
                .acctId(physicalLocationDTO.getAcctId() != null ? physicalLocationDTO.getAcctId() : null).build();
    }

    public static PhysicalLocationDTO toPhysicalLocationDTO(PhysicalLocation physicalLocation) {
        if (physicalLocation == null) {
            return null;
        }
        return PhysicalLocationDTO.builder()
                .id(physicalLocation.getId())
                .locationName(physicalLocation.getLocationName())
                .locationType(physicalLocation.getLocationType())
                .otherDetails(physicalLocation.getOtherDetails())
                .add1(physicalLocation.getAdd1())
                .add2(physicalLocation.getAdd2())
                .add3(physicalLocation.getAdd3())
                .contactPerson(physicalLocation.getContactPerson())
                .phone(physicalLocation.getPhone())
                .category(physicalLocation.getCategory())
                //.ext(physicalLocation.getExt())
                .email(physicalLocation.getEmail())
                .externalRefId(physicalLocation.getExternalRefId())
                .geoLat(physicalLocation.getGeoLat())
                .geoLong(physicalLocation.getGeoLong())
                .googleCoordinates(physicalLocation.getGoogleCoordinates())
                .terrainType(physicalLocation.getTerrainType())
                .status(physicalLocation.getStatus())
                .active(physicalLocation.getActive())
                .ext1(physicalLocation.getExt1())
                .ext2(physicalLocation.getExt2())
                .ext3(physicalLocation.getExt3())
                .ext4(physicalLocation.getExt4())
                .correspondenceAddress(physicalLocation.getCorrespondenceAddress())
                .primaryIndex(physicalLocation.getPrimaryIndex())
                .zipCode(physicalLocation.getZipCode())
                .entityId(physicalLocation.getEntityId())
                .isPrimary(physicalLocation.getIsPrimary())
                .organizationDTO(physicalLocation.getOrganization() != null ? OrganizationMapper.toOrganizationDTOPhysicalLocation(physicalLocation.getOrganization()) : null)
                .acctId(physicalLocation.getAcctId() != null ? physicalLocation.getAcctId() : null).build();
    }
    public static PhysicalLocationDTO toPhysicalLocationDTO(PhysicalLocation physicalLocation,List<LocationMapping> locationMappings) {
        Optional<LocationMapping> locationMappingOptional  = null;
        if (physicalLocation == null) {
            return null;
        }else{
            locationMappingOptional =  locationMappings.stream().filter(a -> a.getLocationId().equals(physicalLocation.getId())).findFirst();
        }
        return PhysicalLocationDTO.builder()
                .id(physicalLocation.getId())
                .locationName(physicalLocation.getLocationName())
                .locationType(physicalLocation.getLocationType())
                .otherDetails(physicalLocation.getOtherDetails())
                .add1(physicalLocation.getAdd1())
                .add2(physicalLocation.getAdd2())
                .add3(physicalLocation.getAdd3())
                .contactPerson(physicalLocation.getContactPerson())
                .phone(physicalLocation.getPhone())
                .category(physicalLocation.getCategory())
                //.ext(physicalLocation.getExt())
                .email(physicalLocation.getEmail())
                .externalRefId(physicalLocation.getExternalRefId())
                .geoLat(physicalLocation.getGeoLat())
                .geoLong(physicalLocation.getGeoLong())
                .googleCoordinates(physicalLocation.getGoogleCoordinates())
                .terrainType(physicalLocation.getTerrainType())
                .status(physicalLocation.getStatus())
                .active(physicalLocation.getActive())
                .ext1(physicalLocation.getExt1())
                .ext2(physicalLocation.getExt2())
                .ext3(physicalLocation.getExt3())
                .ext4(physicalLocation.getExt4())
                .correspondenceAddress(physicalLocation.getCorrespondenceAddress())
                .primaryIndex(physicalLocation.getPrimaryIndex())
                .zipCode(physicalLocation.getZipCode())
                .entityId(physicalLocation.getEntityId())
                .isPrimaryInd(locationMappingOptional!= null && locationMappingOptional.isPresent() ? locationMappingOptional.get().getPrimaryInd() : "N")
                .organizationDTO(physicalLocation.getOrganization() != null ? OrganizationMapper.toOrganizationDTOPhysicalLocation(physicalLocation.getOrganization()) : null)
                .build();
    }

    public static PhysicalLocation toPhysicalLocation(CustomerAddress customerAddress) {
        if (customerAddress == null) {
            return null;
        }
        return PhysicalLocation.builder()
                .action(customerAddress.getAction())
                .entityId(customerAddress.getEntityId())
                .id(customerAddress.getLocationId() != null ? null : customerAddress.getLocationId())
                .locationName(customerAddress.getLocationName() != null && customerAddress.getLocationName().equals("") ? null : customerAddress.getLocationName())
                .locationType(customerAddress.getLocationType() != null && customerAddress.getLocationType().equals("") ? null : customerAddress.getLocationType())
                .otherDetails(customerAddress.getOtherDetails() != null && customerAddress.getOtherDetails().equals("") ? null : customerAddress.getOtherDetails())
                .add1(customerAddress.getAdd1() != null && customerAddress.getAdd1().equals("") ? null : customerAddress.getAdd1())
                .add2(customerAddress.getAdd2() != null && customerAddress.getAdd2().equals("") ? null : customerAddress.getAdd2())
                .add3(customerAddress.getAdd1() != null && customerAddress.getAdd1().equals("") ? null : customerAddress.getAdd1())
                .contactPerson(customerAddress.getContactPerson() != null && customerAddress.getContactPerson().equals("") ? null : customerAddress.getContactPerson())
                .phone(customerAddress.getPhone() != null && customerAddress.getPhone().equals("") ? null : customerAddress.getPhone())
                .category(customerAddress.getCategory() != null && customerAddress.getCategory().equals("") ? null : customerAddress.getCategory())
                .email(customerAddress.getEmail() != null && customerAddress.getEmail().equals("") ? null : customerAddress.getEmail())
                .externalRefId(customerAddress.getExternalRefId() != null ? null : customerAddress.getExternalRefId())
                .geoLat(customerAddress.getGeoLat() != null && customerAddress.getGeoLat().equals("") ? null : customerAddress.getGeoLat())
                .geoLong(customerAddress.getGeoLong() != null && customerAddress.getGeoLong().equals("") ? null : customerAddress.getGeoLong())
                .googleCoordinates(customerAddress.getGoogleCoordinates() != null && customerAddress.getPhone().equals("") ? null : customerAddress.getPhone())
                .terrainType(customerAddress.getTerrainType() != null && customerAddress.getTerrainType().equals("") ? null : customerAddress.getTerrainType())
                .status(customerAddress.getStatus() != null && customerAddress.getStatus().equals("") ? null : customerAddress.getStatus())
                .active(customerAddress.getActive() != null && customerAddress.getActive().equals("") ? null : customerAddress.getActive())
                .ext1(customerAddress.getExt1() != null && customerAddress.getExt1().equals("") ? null : customerAddress.getExt1())
                .ext2(customerAddress.getExt2() != null && customerAddress.getExt2().equals("") ? null : customerAddress.getExt2())
                .ext3(customerAddress.getExt3() != null && customerAddress.getExt3().equals("") ? null : customerAddress.getExt3())
                .ext4(customerAddress.getExt4() != null && customerAddress.getExt4().equals("") ? null : customerAddress.getExt4())
                .correspondenceAddress(customerAddress.getCorrespondenceAddress() != null ? null : customerAddress.getCorrespondenceAddress())
                .primaryIndex(customerAddress.getPrimaryIndex() != null ? null : customerAddress.getPrimaryIndex())
                .zipCode(customerAddress.getZipCode() != null ? null : customerAddress.getZipCode())
                .build();
    }

    public static PhysicalLocation toUpdatedPhysicalLocation(PhysicalLocation physicalLocation, PhysicalLocation physicalLocationUpdate) {
        physicalLocation.setId(physicalLocationUpdate.getId() == null ? physicalLocation.getId() : physicalLocationUpdate.getId());
        physicalLocation.setLocationName(physicalLocationUpdate.getLocationName() == null ? physicalLocation.getLocationName() : physicalLocationUpdate.getLocationName());
        physicalLocation.setLocationType(physicalLocationUpdate.getLocationType() == null ? physicalLocation.getLocationType() : physicalLocationUpdate.getLocationType());
        physicalLocation.setOtherDetails(physicalLocationUpdate.getOtherDetails() == null ? physicalLocation.getOtherDetails() : physicalLocationUpdate.getOtherDetails());
        physicalLocation.setAdd1(physicalLocationUpdate.getAdd1() == null ? physicalLocation.getAdd1() : physicalLocationUpdate.getAdd1());
        physicalLocation.setAdd2(physicalLocationUpdate.getAdd2() == null ? physicalLocation.getAdd2() : physicalLocationUpdate.getAdd2());
        physicalLocation.setAdd3(physicalLocationUpdate.getAdd3() == null ? physicalLocation.getAdd3() : physicalLocationUpdate.getAdd3());
        physicalLocation.setContactPerson(physicalLocationUpdate.getContactPerson() == null ? physicalLocation.getContactPerson() : physicalLocationUpdate.getContactPerson());
        physicalLocation.setPhone(physicalLocationUpdate.getPhone() == null ? physicalLocation.getPhone() : physicalLocationUpdate.getPhone());
        physicalLocation.setCategory(physicalLocationUpdate.getCategory() == null ? physicalLocation.getCategory() : physicalLocationUpdate.getCategory());
        //physicalLocation.setExt(physicalLocationUpdate.getExt() == null ? physicalLocation.getExt() : physicalLocationUpdate.getExt());
        physicalLocation.setEmail(physicalLocationUpdate.getEmail() == null ? physicalLocation.getEmail() : physicalLocationUpdate.getEmail());
        physicalLocation.setExternalRefId(physicalLocationUpdate.getExternalRefId() == null ? physicalLocation.getExternalRefId() : physicalLocationUpdate.getExternalRefId());
        physicalLocation.setGeoLat(physicalLocationUpdate.getGeoLat() == null ? physicalLocation.getGeoLat() : physicalLocationUpdate.getGeoLat());
        physicalLocation.setGeoLong(physicalLocationUpdate.getGeoLong() == null ? physicalLocation.getGeoLong() : physicalLocationUpdate.getGeoLong());
        physicalLocation.setGoogleCoordinates(physicalLocationUpdate.getGoogleCoordinates() == null ? physicalLocation.getGoogleCoordinates() : physicalLocationUpdate.getGoogleCoordinates());
        physicalLocation.setTerrainType(physicalLocationUpdate.getTerrainType() == null ? physicalLocation.getTerrainType() : physicalLocationUpdate.getTerrainType());
        physicalLocation.setStatus(physicalLocationUpdate.getStatus() == null ? physicalLocation.getStatus() : physicalLocationUpdate.getStatus());
        physicalLocation.setActive(physicalLocationUpdate.getActive() == null ? physicalLocation.getActive() : physicalLocationUpdate.getActive());
        physicalLocation.setExt1(physicalLocationUpdate.getExt1() == null ? physicalLocation.getExt1() : physicalLocationUpdate.getExt1());
        physicalLocation.setExt2(physicalLocationUpdate.getExt2() == null ? physicalLocation.getExt2() : physicalLocationUpdate.getExt2());
        physicalLocation.setExt3(physicalLocationUpdate.getExt3() == null ? physicalLocation.getExt3() : physicalLocationUpdate.getExt3());
        physicalLocation.setExt4(physicalLocationUpdate.getExt4() == null ? physicalLocation.getExt4() : physicalLocationUpdate.getExt4());
        physicalLocation.setZipCode(physicalLocationUpdate.getZipCode() == null ? physicalLocation.getZipCode() : physicalLocationUpdate.getZipCode());
        physicalLocation.setCorrespondenceAddress(physicalLocationUpdate.getCorrespondenceAddress() == null ? physicalLocation.getCorrespondenceAddress() : physicalLocationUpdate.getCorrespondenceAddress());
        physicalLocation.setPrimaryIndex(physicalLocationUpdate.getPrimaryIndex() == null ? physicalLocation.getPrimaryIndex() : physicalLocationUpdate.getPrimaryIndex());
        physicalLocation.setEntityId(physicalLocationUpdate.getEntityId() == null ? physicalLocation.getEntityId() : physicalLocationUpdate.getEntityId());
        return physicalLocation;
    }

    public static List<PhysicalLocation> toPhysicalLocations(List<PhysicalLocationDTO> physicalLocationDTOS) {
        return physicalLocationDTOS.stream().map(a -> toPhysicalLocation(a)).collect(Collectors.toList());
    }

    public static List<PhysicalLocation> addressesToPhysicalLocations(List<CustomerAddress> customerAddresses) {
        return customerAddresses.stream().map(a -> toPhysicalLocation(a)).collect(Collectors.toList());
    }

    public static List<PhysicalLocationDTO> toPhysicalLocationDTOs(List<PhysicalLocation> physicalLocations) {
        return physicalLocations.stream().map(a -> toPhysicalLocationDTO(a)).collect(Collectors.toList());
    }

    public static PhysicalLocation toPhysicalLocation(OrganizationDetailDTO organizationDetailDTO) {

        return PhysicalLocation.builder()
//                .id(physicalLocationDTO.getId())
                .locationName("Business Unit")
                .locationType("Business Unit")
//                .otherDetails(physicalLocationDTO.getOtherDetails())
//                .add1(organizationDetailDTO.get)
//                .add2(physicalLocationDTO.getAdd2())
//                .add3(physicalLocationDTO.getAdd3())
//                .contactPerson(physicalLocationDTO.getContactPerson())
//                .phone(physicalLocationDTO.getPhone())
//                .category(physicalLocationDTO.getCategory())
//                .ext(physicalLocationDTO.getExt())
//                .email(physicalLocationDTO.getEmail())
//                .externalRefId(physicalLocationDTO.getExternalRefId())
//                .geoLat(physicalLocationDTO.getGeoLat())
//                .geoLong(physicalLocationDTO.getGeoLong())
//                .googleCoordinates(physicalLocationDTO.getGoogleCoordinates())
//                .terrainType(physicalLocationDTO.getTerrainType())
//                .status(physicalLocationDTO.getStatus())
//                .active(physicalLocationDTO.getActive())
//                .ext1(physicalLocationDTO.getExt1())
//                .ext2(physicalLocationDTO.getExt2())
//                .ext3(physicalLocationDTO.getExt3())
//                .ext4(physicalLocationDTO.getExt4())
//                .correspondenceAddress(physicalLocationDTO.getCorrespondenceAddress())
//                .entityId(physicalLocationDTO.getEntityId())
//                .zipCode(physicalLocationDTO.getZipCode())
                .build();
    }

    public static LocationMapping getLocationMappings(PhysicalLocation physicalLocations, Long unitId) {

        return LocationMapping.builder()
                .primaryInd("Y")
                .sourceId(unitId)
                .siteId(unitId)
                .sourceType("Business Unit")
                .locationId(physicalLocations.getId())
                .status("Active")
                .createdAt(LocalDateTime.now())
                .build();
    }
    public static List<PhysicalLocationDTO> toPhysicalLocationDTOWithPrimary(List<PhysicalLocation> physicalLocations, List<LocationMapping> locationMappings) {
        return physicalLocations.stream().map(a -> toPhysicalLocationDTO(a,locationMappings)).collect(Collectors.toList());
    }
    public static PhysicalLocationDTO toPhysicalLocationDTOOM(PhysicalLocation physicalLocation) {
        if (physicalLocation == null) {
            return null;
        }
        return PhysicalLocationDTO.builder()
                .id(physicalLocation.getId())
                .locationName(physicalLocation.getLocationName())
                .locationType(physicalLocation.getLocationType())
                .otherDetails(physicalLocation.getOtherDetails())
                .add1(physicalLocation.getAdd1())
                .add2(physicalLocation.getAdd2())
                .add3(physicalLocation.getAdd3())
                .contactPerson(physicalLocation.getContactPerson())
                .phone(physicalLocation.getPhone())
                .category(physicalLocation.getCategory())
                //.ext(physicalLocation.getExt())
                .email(physicalLocation.getEmail())
                .externalRefId(physicalLocation.getExternalRefId())
                .geoLat(physicalLocation.getGeoLat())
                .geoLong(physicalLocation.getGeoLong())
                .googleCoordinates(physicalLocation.getGoogleCoordinates())
                .terrainType(physicalLocation.getTerrainType())
                .status(physicalLocation.getStatus())
                .active(physicalLocation.getActive())
                .ext1(physicalLocation.getExt1())
                .ext2(physicalLocation.getExt2())
                .ext3(physicalLocation.getExt3())
                .ext4(physicalLocation.getExt4())
                .correspondenceAddress(physicalLocation.getCorrespondenceAddress())
                .primaryIndex(physicalLocation.getPrimaryIndex())
                .zipCode(physicalLocation.getZipCode())
                .entityId(physicalLocation.getEntityId())
                .isPrimary(physicalLocation.getIsPrimary())
                .acctId(physicalLocation.getAcctId() != null ? physicalLocation.getAcctId() : null).build();
    }
    public static List<PhysicalLocationDTO> toPhysicalLocationDTOsOM(List<PhysicalLocation> physicalLocations) {
        return physicalLocations.stream().map(a -> toPhysicalLocationDTOOM(a)).collect(Collectors.toList());
    }
}
