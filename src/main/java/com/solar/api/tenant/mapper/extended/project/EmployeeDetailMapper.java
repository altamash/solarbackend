package com.solar.api.tenant.mapper.extended.project;

import com.solar.api.tenant.model.extended.project.EmployeeDetail;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeeDetailMapper {

    public static EmployeeDetail toEmployeeDetail(EmployeeDetailDTO employeeDetailDTO) {
        if (employeeDetailDTO == null) {
            return null;
        }
        return EmployeeDetail.builder()
                .id(employeeDetailDTO.getId())
                .designation(employeeDetailDTO.getDesignation())
                .department(employeeDetailDTO.getDepartment())
                .dateOfJoining(employeeDetailDTO.getDateOfJoining())
                .emergencyContactName(employeeDetailDTO.getEmergencyContactName())
                .emergencyContactRelation(employeeDetailDTO.getEmergencyContactRelation())
                .emergencyContactNumber(employeeDetailDTO.getEmergencyContactNumber())
                .entityId(employeeDetailDTO.getEntityId())
                .employmentType(employeeDetailDTO.getEmploymentType())
                .ethnicity(employeeDetailDTO.getEthnicity())
                .nextOfKinContactNumber(employeeDetailDTO.getNextOfKinContactNumber())
                .nextOfKinRelation(employeeDetailDTO.getNextOfKinRelation())
                .nextOfKinName(employeeDetailDTO.getNextOfKinName())
                .firstName(employeeDetailDTO.getFirstName())
                .lastName(employeeDetailDTO.getLastName())
                .gender(employeeDetailDTO.getGender())
                .personalEmail(employeeDetailDTO.getPersonalEmail())
                .phoneNumber(employeeDetailDTO.getPhoneNumber())
                .primaryOffice(employeeDetailDTO.getPrimaryOffice())
                .salutation(employeeDetailDTO.getSalutation())
                .reportingManager(employeeDetailDTO.getReportingManager())
                .hierarchyLevel(employeeDetailDTO.getHierarchyLevel())
                .dateOfBirth(employeeDetailDTO.getDateOfBirth())
                .terminationDate(employeeDetailDTO.getTerminationDate())
                .hasLogin(employeeDetailDTO.getHasLogin() != null ? employeeDetailDTO.getHasLogin() : false)
                .mobileAllowed(employeeDetailDTO.getMobileAllowed() != null ? employeeDetailDTO.getMobileAllowed() : false)
                .isActive(employeeDetailDTO.getIsActive() != null ? employeeDetailDTO.getIsActive() : false).build();

    }

    public static EmployeeDetailDTO toEmployeeDetailDTO(EmployeeDetail employeeDetail) {
        if (employeeDetail == null) {
            return null;
        }
        return EmployeeDetailDTO.builder()
                .id(employeeDetail.getId())
                .designation(employeeDetail.getDesignation())
                .department(employeeDetail.getDepartment())
                .dateOfJoining(employeeDetail.getDateOfJoining())
                .emergencyContactName(employeeDetail.getEmergencyContactName())
                .emergencyContactRelation(employeeDetail.getEmergencyContactRelation())
                .emergencyContactNumber(employeeDetail.getEmergencyContactNumber())
                .entityId(employeeDetail.getEntityId())
                .employmentType(employeeDetail.getEmploymentType())
                .ethnicity(employeeDetail.getEthnicity())
                .nextOfKinContactNumber(employeeDetail.getNextOfKinContactNumber())
                .nextOfKinRelation(employeeDetail.getNextOfKinRelation())
                .nextOfKinName(employeeDetail.getNextOfKinName())
                .firstName(employeeDetail.getFirstName())
                .lastName(employeeDetail.getLastName())
                .gender(employeeDetail.getGender())
                .personalEmail(employeeDetail.getPersonalEmail())
                .phoneNumber(employeeDetail.getPhoneNumber())
                .primaryOffice(employeeDetail.getPrimaryOffice())
                .salutation(employeeDetail.getSalutation())
                .reportingManager(employeeDetail.getReportingManager())
                .hierarchyLevel(employeeDetail.getHierarchyLevel())
                .isActive(employeeDetail.getIsActive())
                .dateOfBirth(employeeDetail.getDateOfBirth())
                .hasLogin(employeeDetail.getHasLogin())
                .mobileAllowed(employeeDetail.getMobileAllowed()).build();

    }

    public static EmployeeDetail toUpdatedEmployeeDetail(EmployeeDetail employeeDetail, EmployeeDetail employeeDetailUpdate) {
        employeeDetail.setId(employeeDetailUpdate.getId() == null ? employeeDetail.getId() :
                employeeDetailUpdate.getId());
        employeeDetail.setDepartment(employeeDetailUpdate.getDepartment() == null ? employeeDetail.getDepartment() :
                employeeDetailUpdate.getDepartment());
        employeeDetail.setDesignation(employeeDetailUpdate.getDesignation() == null ? employeeDetail.getDesignation() :
                employeeDetailUpdate.getDesignation());
        employeeDetail.setEmploymentType(employeeDetailUpdate.getEmploymentType() == null ? employeeDetail.getEmploymentType() :
                employeeDetailUpdate.getEmploymentType());
        employeeDetail.setDateOfJoining(employeeDetailUpdate.getDateOfJoining() == null ? employeeDetail.getDateOfJoining() :
                employeeDetailUpdate.getDateOfJoining());
        employeeDetail.setEmergencyContactName(employeeDetailUpdate.getEmergencyContactName() == null ? employeeDetail.getEmergencyContactNumber() :
                employeeDetailUpdate.getEmergencyContactName());
        employeeDetail.setEmergencyContactRelation(employeeDetailUpdate.getEmergencyContactRelation() == null ? employeeDetail.getEmergencyContactRelation() :
                employeeDetailUpdate.getEmergencyContactRelation());
        employeeDetail.setEmergencyContactNumber(employeeDetailUpdate.getNextOfKinContactNumber() == null ? employeeDetail.getEmergencyContactNumber() :
                employeeDetailUpdate.getEmergencyContactNumber());
        employeeDetail.setNextOfKinName(employeeDetailUpdate.getNextOfKinName() == null ? employeeDetail.getNextOfKinName() :
                employeeDetailUpdate.getNextOfKinName());
        employeeDetail.setNextOfKinRelation(employeeDetailUpdate.getNextOfKinRelation() == null ? employeeDetail.getNextOfKinRelation() :
                employeeDetailUpdate.getNextOfKinRelation());
        employeeDetail.setNextOfKinContactNumber(employeeDetailUpdate.getNextOfKinContactNumber() == null ? employeeDetail.getNextOfKinContactNumber() :
                employeeDetailUpdate.getNextOfKinContactNumber());
        employeeDetail.setEntityId(employeeDetailUpdate.getEntityId() == null ? employeeDetail.getEntityId() :
                employeeDetailUpdate.getEntityId());
        employeeDetail.setEthnicity(employeeDetailUpdate.getEthnicity() == null ? employeeDetail.getEthnicity() :
                employeeDetailUpdate.getEthnicity());
        employeeDetail.setFirstName(employeeDetailUpdate.getFirstName() == null ? employeeDetail.getFirstName() :
                employeeDetailUpdate.getFirstName());
        employeeDetail.setLastName(employeeDetailUpdate.getLastName() == null ? employeeDetail.getLastName() :
                employeeDetailUpdate.getLastName());
        employeeDetail.setGender(employeeDetailUpdate.getGender() == null ? employeeDetail.getGender() :
                employeeDetailUpdate.getGender());
        employeeDetail.setPersonalEmail(employeeDetailUpdate.getPersonalEmail() == null ? employeeDetail.getPersonalEmail() :
                employeeDetailUpdate.getPersonalEmail());
        employeeDetail.setPhoneNumber(employeeDetailUpdate.getPhoneNumber() == null ? employeeDetail.getPhoneNumber() :
                employeeDetailUpdate.getPhoneNumber());
        employeeDetail.setPrimaryOffice(employeeDetailUpdate.getPrimaryOffice() == null ? employeeDetail.getPrimaryOffice() :
                employeeDetailUpdate.getPrimaryOffice());
        employeeDetail.setSalutation(employeeDetailUpdate.getSalutation() == null ? employeeDetail.getSalutation() :
                employeeDetailUpdate.getSalutation());
        employeeDetail.setReportingManager(employeeDetailUpdate.getReportingManager() == null ? employeeDetail.getReportingManager() :
                employeeDetailUpdate.getReportingManager());
        employeeDetail.setHierarchyLevel(employeeDetailUpdate.getHierarchyLevel() == null ? employeeDetail.getHierarchyLevel() :
                employeeDetailUpdate.getHierarchyLevel());
        employeeDetail.setIsActive(employeeDetailUpdate.getIsActive() == null ? employeeDetail.getIsActive() :
                employeeDetailUpdate.getIsActive());
        employeeDetail.setHasLogin(employeeDetailUpdate.getHasLogin() == null ? employeeDetail.getHasLogin() :
                employeeDetailUpdate.getIsActive());
        employeeDetail.setDateOfBirth(employeeDetailUpdate.getDateOfBirth() == null ? employeeDetail.getDateOfBirth() :
                employeeDetailUpdate.getDateOfBirth());
        employeeDetail.setMobileAllowed(employeeDetailUpdate.getMobileAllowed() == null ? employeeDetail.getMobileAllowed() :
                employeeDetailUpdate.getMobileAllowed());
        return employeeDetail;
    }

    public static List<EmployeeDetail> toEmployeeDetails(List<EmployeeDetailDTO> employeeDetailDTOS) {
        return employeeDetailDTOS.stream().map(p -> toEmployeeDetail(p)).collect(Collectors.toList());
    }

    public static List<EmployeeDetailDTO> toEmployeeDetailDTOs(List<EmployeeDetail> employeeDetails) {
        return employeeDetails.stream().map(p -> toEmployeeDetailDTO(p)).collect(Collectors.toList());
    }
}
