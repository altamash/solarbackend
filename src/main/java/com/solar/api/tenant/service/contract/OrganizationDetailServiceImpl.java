package com.solar.api.tenant.service.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.contract.LinkedContractDTO;
import com.solar.api.tenant.mapper.contract.LinkedContractResultDTO;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.contract.OrganizationDetail;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.contract.OrganizationDetailRepository;
import com.solar.api.tenant.repository.contract.OrganizationRepository;
import com.solar.api.tenant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.solar.api.tenant.mapper.contract.LinkedContractMapper.toLinkedContractDTOList;


@Service
public class OrganizationDetailServiceImpl implements OrganizationDetailService {

    @Autowired
    private OrganizationDetailRepository organizationDetailRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;

    @Override
    public OrganizationDetail add(String authorization, OrganizationDetail organizationDetail) throws URISyntaxException, IOException, StorageException {
        return null;
    }

    @Override
    public OrganizationDetail save(OrganizationDetail organizationDetail) throws URISyntaxException, IOException, StorageException {
        return organizationDetailRepository.save(organizationDetail);
    }

    @Override
    public OrganizationDetail update(String authorization, OrganizationDetail organizationDetail) {
        OrganizationDetail organizationDetailData = null;
        if (organizationDetail.getId() != null) {
            organizationDetailData = organizationDetailRepository.findById(organizationDetail.getId()).orElseThrow(() ->
                    new NotFoundException(OrganizationDetail.class, organizationDetail.getId()));
            organizationDetailData = organizationDetailRepository.save(organizationDetail);
        }

        return organizationDetailData;
    }

    @Override
    public OrganizationDetail findById(Long id) {
        return organizationDetailRepository.getById(id);
    }

    @Override
    public List<OrganizationDetail> findAll() {
        return organizationDetailRepository.findAll();
    }

    @Override
    public List<OrganizationDetail> findByOrgId(Long orgId) {
        return organizationDetailRepository.findByOrgId(orgId);
    }

    @Override
    public OrganizationDetail findByOrgIdAndRefId(Long orgId, Long refId) {
        return organizationDetailRepository.findByOrgIdAndMongoRefId(orgId, refId);
    }

    @Override
    public OrganizationDetail findRefIdByOrgId(Long orgId) {
        return organizationDetailRepository.findRefIdByOrgId(orgId);
    }

    @Override
    public List<OrganizationDetail> addAll(List<OrganizationDetail> organizationDetails) throws URISyntaxException, IOException, StorageException {
        return organizationDetailRepository.saveAll(organizationDetails);
    }

    //linked contracts are basically gardens
    @Override
    public List<OrganizationDetail> getLinkedContractsByMasterOrUnit(Long orgId) {
        Boolean isDeleted = false;
        List<OrganizationDetail> organizationDetailList = organizationDetailRepository.findByOrgIdAndIsDeleted(orgId, isDeleted);
//        return organizationDetailList.stream().filter(organizationDetail -> organizationDetail.getRefId() == null).collect(Collectors.toList());
        return organizationDetailList;
    }


    @Override
    public void removeAll(List<OrganizationDetail> organizationDetails) throws URISyntaxException, IOException, StorageException {
        organizationDetailRepository.deleteAll(organizationDetails);
    }

    @Override
    public Boolean removeLinkedContractByMasterAndUnit(Long orgId, Long linkedContractId) {
        OrganizationDetail organizationDetail = organizationDetailRepository.findById(linkedContractId).orElseThrow(null);
        organizationDetail.setIsDeleted(Boolean.TRUE);
        organizationDetail = organizationDetailRepository.save(organizationDetail);
        return organizationDetail.getIsDeleted();
    }

    /*
     *orgId = master org id
     */
    @Override
    public List<LinkedContractDTO> getModifiedContracts(String contractsList, Long orgId, Long... unitTypeId) throws JsonProcessingException {
        List<LinkedContractDTO> linkedContractDTOS = null;
        List<LinkedContractDTO> organizationDetails = organizationDetailRepository.findAllLinkedContractsMaster(orgId); //We are finding contracts linked to Master Org
        if (unitTypeId.length == 0) { //If we need to show linked contracts for masterOrg
            List<LinkedContractResultDTO> linkedContractResultDto = mapper.readValue(contractsList, new TypeReference<List<LinkedContractResultDTO>>() {
            });
            linkedContractDTOS = toLinkedContractDTOList(linkedContractResultDto); //Contracts fetched from mongo
            linkedContractDTOS.forEach(mongoContract -> {
                organizationDetails.forEach(contractDTO -> {
                    if ((contractDTO.getProductId().equals(mongoContract.getProductId()))
                            && (mongoContract.getVariantId().equals(contractDTO.getVariantId()))) {
                        mongoContract.setFlag(true);
                    }
                });
                if (mongoContract.getFlag() == null) {
                    mongoContract.setFlag(false);
                }
            });
            return linkedContractDTOS;
        } else {
            List<LinkedContractDTO> sameUnitTypeList = organizationDetailRepository.findAllLinkedContractsByUnitType(unitTypeId[0], orgId);
            organizationDetails.forEach(masterOrgContract -> {
                sameUnitTypeList.forEach(businessUnitContract -> {
                    if (businessUnitContract.getProductId().equals(masterOrgContract.getProductId())
                            && masterOrgContract.getVariantId().equals(businessUnitContract.getVariantId())) {
                        masterOrgContract.setFlag(true);
                        masterOrgContract.setId(null);
                    }
                });
                if (masterOrgContract.getFlag() == null) {
                    masterOrgContract.setFlag(false);
                    masterOrgContract.setId(null);
                }
            });
            return organizationDetails;
        }
    }

    @Override
    public List<OrganizationDetail> findByVariantId(String variantId) {
        return null;
    }

    @Override
    public OrganizationDetail findByBusinessUnitId(Long businessUnitId) {
        return organizationDetailRepository.findByBusinessUnitId(businessUnitId);
    }

    @Override
    public OrganizationDetail saveOrUpdate(Long businessUnitId, String projectId) {
        OrganizationDetail organizationDetail = new OrganizationDetail();
        Organization businessUnit = organizationRepository.findById(businessUnitId).get();
        User loggedInUser = userService.getLoggedInUser();
        UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.userLevelPrivilegeByAccountId(loggedInUser.getAcctId());
        OrganizationDetail existingOrgDetail = organizationDetailRepository.findByOrgIdAndBusinessUnitIdAndMongoRefIdAndIsDeleted(businessUnit.getParentOrgId(), businessUnitId, projectId, false);
        if (existingOrgDetail != null) {
            organizationDetail.setId(existingOrgDetail.getId());
        }
        organizationDetail.setBusinessUnitId(businessUnitId);
        organizationDetail.setOrgId(businessUnit.getParentOrgId());
        organizationDetail.setMongoRefId(projectId);
        organizationDetail.setIsDeleted(false);
        organizationDetail.setUpdatedBy(userLevelPrivilege.getEntity().getId());
        return organizationDetailRepository.save(organizationDetail);
    }
}