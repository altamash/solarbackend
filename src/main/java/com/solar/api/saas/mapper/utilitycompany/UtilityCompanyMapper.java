package com.solar.api.saas.mapper.utilitycompany;

import com.solar.api.saas.model.UtilityCompany;

import java.util.List;
import java.util.stream.Collectors;

public class UtilityCompanyMapper {
    public static UtilityCompany toUtilityCompany(UtilityCompanyDTO utilityCompanyDTO) {
        if (utilityCompanyDTO == null) {
            return null;
        }
        return UtilityCompany.builder()
                .utilityCompanyId(utilityCompanyDTO.getUtilityCompanyId())
                .companyName(utilityCompanyDTO.getCompanyName())
                .utilityType(utilityCompanyDTO.getUtilityType())
                .contactAddress(utilityCompanyDTO.getContactAddress())
                .contactPhone(utilityCompanyDTO.getContactPhone())
                .contactMobile(utilityCompanyDTO.getContactMobile())
                .country(utilityCompanyDTO.getCountry())
                .city(utilityCompanyDTO.getCity())
                .state(utilityCompanyDTO.getState())
                .county(utilityCompanyDTO.getCounty())
                .postalCode(utilityCompanyDTO.getPostalCode())
                .poBox(utilityCompanyDTO.getPoBox())
                .latitude(utilityCompanyDTO.getLatitude())
                .longitude(utilityCompanyDTO.getLongitude())
                .contactPerson(utilityCompanyDTO.getContactPerson())
                .email(utilityCompanyDTO.getEmail())
                .subscriptionReferences(utilityCompanyDTO.getSubscriptionReferences())
                .parentCompanyId(utilityCompanyDTO.getParentCompanyId())
                .field_1(utilityCompanyDTO.getField_1())
                .field_2(utilityCompanyDTO.getField_2())
                .field_3(utilityCompanyDTO.getField_3())
                .build();
    }

    public static UtilityCompanyDTO toUtilityCompanyDTO(UtilityCompany utilityCompany) {
        if (utilityCompany == null) {
            return null;
        }
        return UtilityCompanyDTO.builder()
                .utilityCompanyId(utilityCompany.getUtilityCompanyId())
                .companyName(utilityCompany.getCompanyName())
                .utilityType(utilityCompany.getUtilityType())
                .contactAddress(utilityCompany.getContactAddress())
                .contactPhone(utilityCompany.getContactPhone())
                .contactMobile(utilityCompany.getContactMobile())
                .country(utilityCompany.getCountry())
                .city(utilityCompany.getCity())
                .state(utilityCompany.getState())
                .county(utilityCompany.getCounty())
                .postalCode(utilityCompany.getPostalCode())
                .poBox(utilityCompany.getPoBox())
                .latitude(utilityCompany.getLatitude())
                .longitude(utilityCompany.getLongitude())
                .contactPerson(utilityCompany.getContactPerson())
                .email(utilityCompany.getEmail())
                .subscriptionReferences(utilityCompany.getSubscriptionReferences())
                .parentCompanyId(utilityCompany.getParentCompanyId())
                .field_1(utilityCompany.getField_1())
                .field_2(utilityCompany.getField_2())
                .field_3(utilityCompany.getField_3())
                .build();
    }

    public static UtilityCompany toUpdatedUtilityCompany(UtilityCompany utilityCompany,
                                                         UtilityCompany utilityCompanyUpdate) {
        utilityCompany.setCompanyName(utilityCompanyUpdate.getCompanyName() == null ?
                utilityCompany.getCompanyName() : utilityCompanyUpdate.getCompanyName());
        utilityCompany.setUtilityType(utilityCompanyUpdate.getUtilityType() == null ?
                utilityCompany.getUtilityType() : utilityCompanyUpdate.getUtilityType());
        utilityCompany.setContactAddress(utilityCompanyUpdate.getContactAddress() == null ?
                utilityCompany.getContactAddress() : utilityCompanyUpdate.getContactAddress());
        utilityCompany.setContactPhone(utilityCompanyUpdate.getContactPhone() == null ?
                utilityCompany.getContactPhone() : utilityCompanyUpdate.getContactPhone());
        utilityCompany.setContactMobile(utilityCompanyUpdate.getContactMobile() == null ?
                utilityCompany.getContactMobile() : utilityCompanyUpdate.getContactMobile());
        utilityCompany.setCountry(utilityCompanyUpdate.getCountry() == null ? utilityCompany.getCountry() :
                utilityCompanyUpdate.getCountry());
        utilityCompany.setCity(utilityCompanyUpdate.getCity() == null ? utilityCompany.getCity() :
                utilityCompanyUpdate.getCity());
        utilityCompany.setState(utilityCompanyUpdate.getState() == null ? utilityCompany.getState() :
                utilityCompanyUpdate.getState());
        utilityCompany.setCounty(utilityCompanyUpdate.getCounty() == null ? utilityCompany.getCounty() :
                utilityCompanyUpdate.getCounty());
        utilityCompany.setPostalCode(utilityCompanyUpdate.getPostalCode() == null ? utilityCompany.getPostalCode() :
                utilityCompanyUpdate.getPostalCode());
        utilityCompany.setPoBox(utilityCompanyUpdate.getPoBox() == null ? utilityCompany.getPoBox() :
                utilityCompanyUpdate.getPoBox());
        utilityCompany.setLatitude(utilityCompanyUpdate.getLatitude() == null ? utilityCompany.getLatitude() :
                utilityCompanyUpdate.getLatitude());
        utilityCompany.setLongitude(utilityCompanyUpdate.getLongitude() == null ? utilityCompany.getLongitude() :
                utilityCompanyUpdate.getLongitude());
        utilityCompany.setContactPerson(utilityCompanyUpdate.getContactPerson() == null ?
                utilityCompany.getContactPerson() : utilityCompanyUpdate.getContactPerson());
        utilityCompany.setEmail(utilityCompanyUpdate.getEmail() == null ? utilityCompany.getEmail() :
                utilityCompanyUpdate.getEmail());
        utilityCompany.setSubscriptionReferences(utilityCompanyUpdate.getSubscriptionReferences() == null ?
                utilityCompany.getSubscriptionReferences() : utilityCompanyUpdate.getSubscriptionReferences());
        utilityCompany.setParentCompanyId(utilityCompanyUpdate.getParentCompanyId() == null ?
                utilityCompany.getParentCompanyId() : utilityCompanyUpdate.getParentCompanyId());
        utilityCompany.setField_1(utilityCompanyUpdate.getField_1() == null ? utilityCompany.getField_1() :
                utilityCompanyUpdate.getField_1());
        utilityCompany.setField_2(utilityCompanyUpdate.getField_2() == null ? utilityCompany.getField_2() :
                utilityCompanyUpdate.getField_2());
        utilityCompany.setField_3(utilityCompanyUpdate.getField_3() == null ? utilityCompany.getField_3() :
                utilityCompanyUpdate.getField_3());
        return utilityCompany;
    }

    /**
     * @param utilityCompanyDTOs
     * @return
     */
    public static List<UtilityCompany> toUtilityCompanyes(List<UtilityCompanyDTO> utilityCompanyDTOs) {
        return utilityCompanyDTOs.stream().map(uc -> toUtilityCompany(uc)).collect(Collectors.toList());
    }

    /**
     * @param utilityCompany
     * @return
     */
    public static List<UtilityCompanyDTO> toUtilityCompanyDTOs(List<UtilityCompany> utilityCompany) {
        return utilityCompany.stream().map(uc -> toUtilityCompanyDTO(uc)).collect(Collectors.toList());
    }
}
