package com.solar.api.tenant.controller.acquisition;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.helper.Utility;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.tenant.mapper.DocumentSigningTemplateDTO;
import com.solar.api.tenant.mapper.ca.CaReferralInfoDTO;
import com.solar.api.tenant.mapper.ca.CaUserTemplateDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.ca.CaSoftCreditCheckDTO;
import com.solar.api.tenant.mapper.user.SalesRepMapper;
import com.solar.api.tenant.mapper.user.SalesRepresentativeDTO;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.model.ca.CaUtility;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.helper.Utility;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.user.SalesRepMapper;
import com.solar.api.tenant.mapper.user.SalesRepresentativeDTO;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.ERole;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.service.RoleService;
import com.solar.api.tenant.service.acquisition.AcquisitionService;
import com.solar.api.tenant.service.ca.CaUtilityService;
import com.solar.api.tenant.service.contract.EntityDetailService;
import com.solar.api.tenant.service.contract.EntityService;
import com.solar.api.tenant.service.ca.CaUtilityService;
import org.bouncycastle.cert.ocsp.Req;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
//@PreAuthorize("checkAccess()")
@RestController("AcquisitionController")
@RequestMapping(value = "/acquisition")
public class AcquisitionController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private AcquisitionService acquisitionService;
    @Autowired
    RoleService roleService;
    @Autowired
    Utility utility;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MasterTenantService masterTenantService;
    @Autowired
    private MasterTenantRepository masterTenantRepository;
    @Autowired
    CaUtilityService caUtilityService;

    @PreAuthorize("checkAccess()")
    @GetMapping("/getContracts/{entityId}")
    public ResponseEntity<UserDTO> getContracts(@PathVariable Long entityId) {
        return new ResponseEntity<>(acquisitionService.getContracts(entityId), HttpStatus.OK);
    }
    @PreAuthorize("checkAccess()")
    @PostMapping("/assignLeads")
    public ResponseEntity<?> assignLeads(@RequestParam("entityIds") String entityIds,
                                         @RequestParam("salesRepRoleId") Long salesRepRoleId,
                                         @RequestParam("acctId") Long acctId) {
        List<Long> entityIdList = Arrays.stream(entityIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return acquisitionService.assignLeads(entityIdList, salesRepRoleId, acctId);
    }

    // sales agent list show it from the roles table with the role name sales agent
    @PreAuthorize("checkAccess()")
    @GetMapping("/getAllSalesRepresentatives")
    public ResponseEntity<?> getAllSalesRepresentatives() {
       return acquisitionService.getAllSalesRepresentatives();
    }
    @PostMapping("/saveOrUpdateCaUtility")
    public List<CaUtility> saveOrUpdateCaUtility(@RequestParam(value = "userDTO", required = true) String userDTO,
                                            @RequestParam(value = "utilityMultipartFiles", required = false) List<MultipartFile> utilityMultipartFiles
    ) throws JsonProcessingException {
        UserDTO userdto = objectMapper.readValue(userDTO, UserDTO.class);
        try{
            List<CaUtility> caUtilities = acquisitionService.saveOrUpdateCaUtility(userdto ,utilityMultipartFiles);
            return caUtilities;
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("Error while save or update caUtility", e);
        }
        return null;
    }
    @PreAuthorize("checkAccess()")
    @GetMapping("/getAllPhysicalLocation")
    public List<PhysicalLocationDTO> getAllPhysicalLocation(@RequestParam("acctId") Long acctId){
        return acquisitionService.getAllPhysicalLocation(acctId);
    }

    @PreAuthorize("checkAccess()")
    @PutMapping("/markUtilityAsPrimary")
    public ObjectNode markLocationAsPrimary(@RequestParam("utilityId") Long utilityId, @RequestParam("entityId") Long entityId) {
        return caUtilityService.markUtilityAsPrimary(entityId,utilityId);
    }
    @PreAuthorize("checkAccess()")
    @PostMapping("/caReferralInfo")
    public UserDTO caReferralInfo(@RequestParam("userDTO") String userDTO) throws JsonProcessingException {
       UserDTO userdto = objectMapper.readValue(userDTO, UserDTO.class);
       CaReferralInfoDTO caReferralInfoDTO = userdto.getCaReferralInfo();
        return acquisitionService.saveAndUpdateReferralInfo(caReferralInfoDTO,userdto);
    }
    @PreAuthorize("checkAccess()")
    @PostMapping("/caSoftCreditCheckService")
    public UserDTO caSoftCreditCheck( @RequestParam("userDTO") String userDTO) throws JsonProcessingException {
        UserDTO userdto = objectMapper.readValue(userDTO, UserDTO.class);
        CaSoftCreditCheckDTO caSoftCreditCheckDTO = userdto.getCaSoftCreditCheck();
        return acquisitionService.saveAndUpdateSoftCreditCheck(caSoftCreditCheckDTO,userdto);
    }
    @PreAuthorize("checkAccess()")
    @PostMapping("/sendForSigningWithMultipleIds")
    public BaseResponse sendForSigningWithMultipleIds(@RequestParam(value = "userDTO", required = true) String userDTO,
                                     @RequestParam(value = "sendForSigning", required = false, defaultValue = "false") Boolean sendForSigning,
                                     @RequestParam(value = "templateIds", required = false) String ids,
                                     @RequestHeader("Comp-Key") Long compKey

    ) throws JsonProcessingException {
        List<Long> templateIdList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        UserDTO userdto = objectMapper.readValue(userDTO, UserDTO.class);
        BaseResponse baseResponse = acquisitionService.saveOrUpdateCaUser(userdto, sendForSigning, templateIdList);
        return baseResponse;
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/v2/getAllCaUsersByType")
    public BaseResponse getAllCaUsersByType(@RequestParam("leadType") String leadType,
                                            @RequestParam(value = "statuses", required = false) String statuses,
                                            @RequestParam(value = "zipCodes", required = false) String zipCodes,
                                            @RequestParam(value = "agentIds", required = false) String agentIds,
                                            @RequestParam(value = "startDate", required = false) String startDate,
                                            @RequestParam(value = "endDate", required = false) String endDate,
                                            @RequestParam(value = "searchedWord", required = false) String searchedWord,
                                            @RequestParam("pageNumber") Integer pageNumber,
                                            @RequestParam("pageSize") Integer pageSize) {
        return acquisitionService.getAllCAUsers(leadType,statuses,zipCodes,agentIds,startDate,endDate,searchedWord,pageNumber, pageSize);
    }
    @PreAuthorize("checkAccess()")
    @GetMapping("/loadCaFilterData")
    public BaseResponse loadCaFilterData() {
        return acquisitionService.loadCaFilterData();
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/decodeBase64String")
    public String decodeBase64String(@RequestParam String encodedString) {
        return acquisitionService.decodeBase64String(encodedString);

    }
    @PostMapping("/v2/registerYourInterest")
    public ResponseEntity<?> registerYourInterest(@RequestParam("template") String template,
                                                  @RequestParam("userDTO") String userDTO,
                                                  @RequestHeader("Comp-Key") Long compKey
    ) throws JsonProcessingException {
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        UserDTO userdto = objectMapper.readValue(userDTO, UserDTO.class);
        masterTenantService.setCurrentDb(userdto.getCompKey());
        return acquisitionService.saveRegisterInterest(userdto, template);
    }
    @PostMapping("/storeUploadedContractFile/v2/entityId/{entityId}")
    public String storeUploadedContractFile(@PathVariable("entityId") Long
                                                    entityId, @RequestParam("file") MultipartFile file, @RequestParam(value = "notes", required = false) String
                                                    notes,@RequestParam("contractDocument") String contractDocument) {
        DocumentSigningTemplateDTO template = null;
        try {
            template = new ObjectMapper().readValue(contractDocument, DocumentSigningTemplateDTO.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return acquisitionService.getUploadedDocumentUrl(file, notes, entityId, template);
    }
    @PostMapping("/onSubmit/v2")
    public ResponseEntity<?> onSubmit(@RequestParam("userDTO") String userDTO) {
        UserDTO userdto = null;
        try {
            userdto = objectMapper.readValue(userDTO, UserDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return acquisitionService.onSubmit(userdto);
    }
    /**
    Api to get all ca users by correspondence count all user whose correspondence count is greater than zero
     */
    @PreAuthorize("checkAccess()")
    @GetMapping("/v2/getAllCaUsersByCorrespondenceCount")
    public List<CaUserTemplateDTO> getAllCaUsersByCorrespondenceCount(
                                            @RequestParam(value = "statuses", required = false) String statuses,
                                            @RequestParam(value = "zipCodes", required = false) String zipCodes,
                                            @RequestParam(value = "agentIds", required = false) String agentIds,
                                            @RequestParam(value = "startDate", required = false) String startDate,
                                            @RequestParam(value = "endDate", required = false) String endDate,
                                            @RequestParam(value = "searchedWord", required = false) String searchedWord) {
        return acquisitionService.getAllCaUsersByCorrespondenceCount(statuses,zipCodes,agentIds,startDate,endDate,searchedWord);
    }
}
