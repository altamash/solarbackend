package com.solar.api.tenant.service.acquisition;

import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.DocumentSigningTemplateDTO;
import com.solar.api.tenant.mapper.ca.CaUserTemplateDTO;
import com.solar.api.tenant.mapper.contract.EntityDetailDTO;
import com.solar.api.tenant.mapper.ca.CaReferralInfoDTO;
import com.solar.api.tenant.mapper.ca.CaSoftCreditCheckDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.user.SalesRepresentativeDTO;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.model.ca.CaUtility;
import com.solar.api.tenant.model.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;

import java.util.List;

public interface AcquisitionService {

    UserDTO getContracts(Long entityId);

    List<CaUtility> saveOrUpdateCaUtility(UserDTO userdto, List<MultipartFile> utilityMultipartFiles);

    List<PhysicalLocationDTO> getAllPhysicalLocation(Long acctId);
    BaseResponse saveOrUpdateCaUser(UserDTO userDTO, Boolean sendForSigning, List<Long> ids);

    ResponseEntity<?> getAllSalesRepresentatives();

    UserDTO saveAndUpdateReferralInfo(CaReferralInfoDTO caReferralInfoDTO,UserDTO userDTO);

    UserDTO saveAndUpdateSoftCreditCheck(CaSoftCreditCheckDTO caSoftCreditCheckDTO,UserDTO userDTO);

    ResponseEntity<?> assignLeads(List<Long> entityIdList, Long salesRepRoleId, Long acctId);

    public BaseResponse getAllCAUsers(String leadType, String statuses,String zipCodes,
                                      String agentIds, String startDate, String endDate,String searchedWords,Integer pageNumber, Integer pageSize);
    public BaseResponse loadCaFilterData();
    String decodeBase64String(String encodedString);
    User findByEmailAddress(String email);

    ResponseEntity<Object> saveRegisterInterest(UserDTO userdto, String template);
    String getUploadedDocumentUrl(MultipartFile file, String notes, Long entityId, DocumentSigningTemplateDTO template);
    User saveUser(User user);

    ResponseEntity<Object> onSubmit(UserDTO userDTO);

    List<CaUserTemplateDTO> getAllCaUsersByCorrespondenceCount(String statuses, String zipCodes, String agentIds, String startDate, String endDate, String searchedWord);
}
