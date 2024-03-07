package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.Constants;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.contract.OrganizationDTO;
import com.solar.api.tenant.mapper.contract.OrganizationDetailDTO;
import com.solar.api.tenant.mapper.contract.OrganizationMapper;
import com.solar.api.tenant.mapper.organization.OrganizationResponseDTO;

import com.solar.api.tenant.mapper.tiles.customersupportmanagement.CustomerSupportFiltersMapper;
import com.solar.api.tenant.mapper.tiles.customersupportmanagement.CustomerSupportFiltersTile;
import com.solar.api.tenant.service.contract.OrganizationService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.organization.OrganizationResponseMapper.toOrganizationDTO;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("OrganizationController")
@RequestMapping(value = "/organization")
public class OrganizationController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private OrganizationService organizationService;

    @PostMapping("/refCode/{refCode}")
    public OrganizationDTO add(@RequestHeader("Authorization") String authorization,
                               @RequestParam("organizationDTO") String organizationDTO,
                               @PathVariable String refCode,
                               @RequestParam(name = "multipartFiles", required = false) List<MultipartFile> multipartFiles)
            throws Exception {
        OrganizationDTO dto = new ObjectMapper().readValue(organizationDTO, OrganizationDTO.class);
        return OrganizationMapper.toOrganizationDTO(
                organizationService.add(authorization, OrganizationMapper.toOrganization(dto), refCode, multipartFiles));
    }

    @PutMapping("/refCode/{refCode}")
    public OrganizationDTO update(@RequestHeader("Authorization") String authorization, @RequestBody OrganizationDTO organizationDTO) {
        return OrganizationMapper.toOrganizationDTO(
                organizationService.update(authorization, OrganizationMapper.toOrganization(organizationDTO)));
    }

    @GetMapping("/{id}")
    public OrganizationDTO findById(@PathVariable Long id) {
        return OrganizationMapper.toOrganizationDTO(organizationService.findById(id));
    }

    @GetMapping
    public List<OrganizationDTO> findAll() {
        return OrganizationMapper.toOrganizationDTOList(organizationService.findAll());
    }

    @GetMapping("/summary")
    public List<OrganizationDTO> findAllSummary() {
        return OrganizationMapper.toOrganizationSummaryDTOList(organizationService.findAll());
    }

    @GetMapping("/findAllOrganizationUnits")
    public List<OrganizationResponseDTO> findAllOrganizationUnits() {
        return organizationService.findAllOrgUnits();
    }

    @PostMapping("/addUpdateOrganization")
    public ResponseEntity<Map<String, String>> addUnit(@RequestHeader(value = "Comp-Key", required = false) Long compKey,
                                                       @RequestParam("organizationDetailDTO") String organizationDetailDTO) {
        OrganizationDetailDTO data = null;
        Map<String, String> response = new HashMap();

        try {
            if (organizationDetailDTO != null) {
                data = organizationService.saveUpdateOrgUnit(organizationDetailDTO, compKey);

            }
            response.put("code", HttpStatus.OK.value() + "");
        } catch (URISyntaxException | IOException | StorageException e) {
            LOGGER.error(e.getMessage(), e);
            response.put("data", null);
            response.put("message", e.getMessage());
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value() + "");
            return ResponseEntity.ok(response);
        }

        response.put("data", data.toString());
        response.put("message", "Successfully save/update the org details");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findOrganizationDetail/{id}")
    public OrganizationDetailDTO findOrganizationDetail(@PathVariable Long id, @QueryParam("isMaster") Boolean isMaster) {
        return organizationService.findOrganizationDetails(id, isMaster);
    }
    @Deprecated

    @PatchMapping("/disable/{id}")
    public OrganizationResponseDTO disableOrgUnit(@PathVariable Long id) {
        return toOrganizationDTO(organizationService.disableOrgUnit(id));
    }
    @Deprecated
    @PatchMapping("/enable/{id}")
    public OrganizationResponseDTO enableOrgUnit(@PathVariable Long id) {
        return toOrganizationDTO(organizationService.enableOrgUnit(id));
    }
    @Deprecated
    @GetMapping("/findMasterOrg")
    public OrganizationDTO findMasterOrg() {
        return OrganizationMapper.toOrganizationDTO(organizationService.findByStatusAndPrimaryIndicatorAndParentOrgId(Constants.PHYSICAL_LOCATIONS.STATUS_ACTIVE, true, null));
    }

    @GetMapping("/getAllOrganizationLists")
    public BaseResponse getAllOrganizationList(@RequestParam(name = ("orgId"), required = false) Long orgId,
                                               @RequestParam("pageNumber") Integer pageNumber,
                                               @RequestParam("pageSize") Integer pageSize,
                                               @RequestParam(value = "searchedWord", required = false) String searchedWord ) {
        return organizationService.getAllOrganizationList(orgId, pageSize, pageNumber, searchedWord);
    }

    @GetMapping("/getOrganizationDetailsV2")
    public BaseResponse getOrganizationDetail(@RequestHeader("Comp-Key") Long compKey,
                                              @RequestParam("orgId") Long orgId,
                                              @RequestParam("isMaster") Boolean isMaster) {
        return organizationService.getOrganizationDetailsV2(orgId, isMaster);
    }

    @ApiOperation(value = "Used in Organization Management")
    @GetMapping("/getAllOrganizationCustomerList")
    public BaseResponse getAllOrganCustomerLists(@RequestHeader("Comp-Key") Long compKey,
                                                 @RequestParam("orgId") Long orgId,
                                                 @RequestParam(name = "searchWord", required = false) String searchWord,
                                                 @RequestParam("isMaster") Boolean isMaster,
                                                 @RequestParam("pageNumber") Integer pageNumber,
                                                 @RequestParam("pageSize") Integer pageSize) {
        return organizationService.getAllOrganizationCustomerList(orgId, searchWord, isMaster, pageSize, pageNumber);
    }

    @GetMapping("/getAllOfficeList")
    public BaseResponse getAllOfficeList(@RequestHeader("Comp-Key") Long compKey,
                                         @RequestParam("orgId") Long orgId,
                                         @RequestParam("isMaster") Boolean isMaster,
                                         @RequestParam("groupBy") String groupBy,
                                         @RequestParam(value = "groupByName", required = false) String groupByName,
                                         @RequestParam(value = "locationCategory", required = false) String locationCategory,
                                         @RequestParam(value = "locationType", required = false) String locationType,
                                         @RequestParam(value = "businessUnit", required = false) String businessUnit,
                                         @RequestParam(value = "searchWords", required = false) String searchWords,
                                         @RequestParam("pageNumber") Integer pageNumber,
                                         @RequestParam("pageSize") Integer pageSize) {
        return organizationService.getAllOfficeList(orgId, isMaster, groupBy, groupByName, locationCategory, locationType, businessUnit, searchWords,pageSize, pageNumber);
    }

    @ApiOperation(value = "Used in Employee Management")
    @GetMapping("/getAllOrganizationEmployeesList")
    public BaseResponse getAllOrganEmployeesList(@RequestParam("orgId") Long orgId,
                                                 @RequestParam(name = "searchWord", required = false) String searchWord,
                                                 @RequestParam("pageNumber") Integer pageNumber,
                                                 @RequestParam("pageSize") Integer pageSize,
                                                 @RequestParam("isMaster") Boolean isMaster) {
        return organizationService.getAllOrganizationEmployeesList(orgId, searchWord, isMaster, pageSize, pageNumber);
    }

    @PostMapping("/addOrUpdateOrganizationV2/")
    public BaseResponse addOrUpdateOrganizationV2(@RequestHeader("Comp-Key") Long compKey,
                                                  @RequestParam("isMaster") Boolean isMaster,
                                                  @RequestParam("reqType") String reqType,
                                                  @RequestParam(value = "image", required = false) MultipartFile image,
                                                  @RequestParam(value = "organizationDTO") String organizationDTOString) {
        return organizationService.addOrUpdateOrganizationV2(compKey, isMaster, reqType, image, organizationDTOString);
    }

    @PostMapping("/addOrUpdateConfigurations/")
    public BaseResponse addOrUpdateConfigurations(@RequestHeader("Comp-Key") Long compKey,
                                                   @RequestParam("orgId") Long orgId,
                                                   @RequestParam("reqType") String reqType,
                                                   @RequestParam(value = "companyPreferenceDTO") String companyPreferenceDTOString) {
        return organizationService.addOrUpdateConfigurations(compKey, orgId, reqType, companyPreferenceDTOString);
    }

    @PostMapping("/addLandingPageImages/")
    public BaseResponse addLandingPageImages(@RequestHeader("Comp-Key") Long compKey,
                                             @RequestParam("orgId") Long orgId,
                                             @RequestParam("companyPreferenceId") Long companyPreferenceId,
                                             @RequestParam("reqType") String reqType,
                                             @RequestParam(value = "file") List<MultipartFile> file
    ) {
        return organizationService.addLandingPageImages(compKey, orgId, companyPreferenceId, reqType, file);
    }

    @GetMapping("/getConfigurations")
    public BaseResponse getConfigurations(@RequestHeader("Comp-Key") Long compKey,
                                          @RequestParam("orgId") Long orgId) {
        return organizationService.getConfigurations(orgId);
    }

    //enableDisableOrganizationStatus
    @PatchMapping("/toggleOrgStatus")
    public BaseResponse toggleOrgUnitStatus(@RequestHeader("Comp-Key") Long compKey,
                                            @RequestParam("orgId") String id,
                                            @RequestParam("isActive") Boolean isActive,
                                            @RequestParam("isMaster") Boolean isMaster) {
        List<Long> orgIds = Arrays.stream(id.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return organizationService.toggleOrgUnitStatus(orgIds, isActive, isMaster);
    }

    @GetMapping("/getAvailableOffices")
    public BaseResponse getAvailableOffices(@RequestHeader("Comp-Key") Long compKey,
                                            @RequestParam("masterOrgId") Long masterOrgId,
                                            @RequestParam(value = "subOrgId", required = false) Long subOrgId,
                                            @RequestParam("unitCategory") String unitCategory,
                                            @RequestParam(value ="unitType" ,required = false) String unitType,
                                            @RequestParam(value = "searchWords", required = false) String searchWords) {
        return organizationService.getAvailableOffices(compKey, masterOrgId, subOrgId, unitCategory, unitType, searchWords);
    }

    @GetMapping("/getOrgOfficeFilters")
    public BaseResponse getOrgOfficeFilters(@RequestHeader("Comp-Key") Long compKey,
                                            @RequestParam("masterOrgId") Long masterOrgId) {
        return organizationService.getOrgOfficeFilters(masterOrgId);
    }

    @ApiOperation(value = "Used in Employee Management")
    @GetMapping("/getAvailableEmployeesList")
    public BaseResponse getAvailableEmployeesList(@RequestParam("masterOrgId") Long masterOrgId,
                                                  @RequestParam(name="orgId",required = false) Long orgId,
                                                  @RequestParam(name = "searchWord", required = false) String searchWord) {
        return organizationService.getAvailableEmployeesList(masterOrgId,orgId, searchWord);
    }
    @GetMapping("/getLinkedSites")
    public BaseResponse getLinkedSites(@RequestParam("businessUnitId") Long businessUnitId,
                                       @RequestParam(name = "searchWord", required = false) String searchWord,
                                       @RequestParam("groupBy") String groupBy,
                                       @RequestParam(value = "groupByName", required = false) String groupByName,
                                       @RequestParam(value = "gardenType", required = false) String gardenType,
                                       @RequestParam(value = "gardenOwner", required = false) String gardenOwner,
                                       @RequestParam(value = "startDateRegistrationDate", required = false) String startDateRegistrationDate,
                                       @RequestParam(value = "endDateRegistrationDate", required = false) String endDateRegistrationDate,
                                       @RequestParam(value = "startDateGoLiveDate", required = false) String startDateGoLiveDate,
                                       @RequestParam(value = "endDateGoLiveDate", required = false) String endDateGoLiveDate,
                                       @RequestParam("pageNumber") Integer pageNumber,
                                       @RequestParam("pageSize") Integer pageSize) {
        return organizationService.getLinkedSites(businessUnitId, searchWord, groupBy, groupByName, gardenType, gardenOwner, startDateRegistrationDate
                , endDateRegistrationDate, startDateGoLiveDate, endDateGoLiveDate, pageNumber, pageSize);
    }
    @GetMapping("/getFiltersDataForLinkedSites")
    public BaseResponse findFiltersData() {
        return organizationService.getFiltersData();
    }
}
