package com.solar.api.tenant.service.contract;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.contract.OrgDTO;
import com.solar.api.tenant.mapper.contract.OrgDetailDTO;
import com.solar.api.tenant.mapper.contract.OrganizationDetailDTO;
import com.solar.api.tenant.mapper.organization.OrganizationResponseDTO;
import com.solar.api.tenant.model.contract.Organization;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface OrganizationService {
    Organization add(String authorization, Organization organization, String refCode, List<MultipartFile> multipartFiles)
            throws URISyntaxException, IOException, StorageException;

    Organization update(String authorization, Organization organization);

    Organization findById(Long id);

    List<Organization> findAll();

    List<OrganizationResponseDTO> findAllOrgUnits();

    Organization findByStatusAndPrimaryIndicator(String status, Boolean ind);

    OrganizationDetailDTO saveUpdateOrgUnit(String organizationDetailDTO, Long compId)
            throws URISyntaxException, IOException, StorageException;

    OrgDetailDTO findOrgDetails(Long id, Boolean isMaster);

    OrganizationDetailDTO findOrganizationDetails(Long id, Boolean isMaster);

    Organization disableOrgUnit(Long id);

    OrganizationDetailDTO updateUnit(String organization, String multiPartFileDto, Long compId)
            throws URISyntaxException, IOException, StorageException;

    OrgDTO findOrganizationDetailWithLocation(Long id, Boolean isMaster);

    Organization enableOrgUnit(Long id);

    Organization findByStatusAndPrimaryIndicatorAndParentOrgId(String status, Boolean ind, Long parentOrgId);

    BaseResponse getAllOrganizationList(Long orgId, Integer size, int pageNumber, String searchedWord);

    BaseResponse getOrganizationDetailsV2(Long orgId, Boolean isMaster);

    BaseResponse getAllOrganizationCustomerList(Long orgId, String searchWord, Boolean isMaster, Integer size, int pageNumber);

    BaseResponse getAllOfficeList(Long orgId, Boolean isMaster, String groupBy, String groupByName,String locationCategory,String locationType,String businessUnit,String searchWords, Integer size, int pageNumber);

    BaseResponse addOrUpdateOrganizationV2(Long compKey, Boolean isMaster, String reqType, MultipartFile image, String organizationDTOString);

    BaseResponse getAllOrganizationEmployeesList(Long orgId, String searchWord, Boolean isMaster, Integer size, int pageNumber);

    BaseResponse addOrUpdateConfigurations(Long compKey, Long orgId, String reqType, String companyPreferenceDTOString);

    BaseResponse addLandingPageImages(Long compKey, Long orgId, Long companyPreferenceId, String reqType, List<MultipartFile> file);

    BaseResponse getConfigurations(Long orgId);

    BaseResponse toggleOrgUnitStatus(List<Long> orgId, Boolean isActive, Boolean isMaster);

    BaseResponse getAvailableOffices(Long compKey, Long masterOrgId, Long subOrgId, String unitCategory, String unitType,String searchWords);
    BaseResponse getOrgOfficeFilters(Long orgId);
    BaseResponse getAvailableEmployeesList(Long masterOrgId,Long orgId,String searchWord);

    BaseResponse getLinkedSites(Long businessUnitId, String searchWord, String groupBy, String groupByName, String gardenType, String gardenOwner, String startDateRegistrationDate, String endDateRegistrationDate,String startDateGoLiveDate, String endDateGoLiveDate, Integer pageNumber, Integer pageSize);

    BaseResponse getFiltersData();
}
