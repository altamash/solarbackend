package com.solar.api.tenant.service.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.tenant.mapper.contract.LinkedContractDTO;
import com.solar.api.tenant.model.contract.OrganizationDetail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface OrganizationDetailService {

    OrganizationDetail add(String authorization, OrganizationDetail organizationDetail)
            throws URISyntaxException, IOException, StorageException;

    OrganizationDetail save(OrganizationDetail organizationDetail)
            throws URISyntaxException, IOException, StorageException;

    OrganizationDetail update(String authorization, OrganizationDetail organizationDetail);

    OrganizationDetail findById(Long id);

    List<OrganizationDetail> findAll();

    List<OrganizationDetail> findByOrgId(Long orgId);

    OrganizationDetail findByOrgIdAndRefId(Long orgId, Long refId);

    OrganizationDetail findRefIdByOrgId(Long orgId);

    List<OrganizationDetail> addAll(List<OrganizationDetail> organizationDetails)
            throws URISyntaxException, IOException, StorageException;

    List<OrganizationDetail> getLinkedContractsByMasterOrUnit(Long orgId);

    void removeAll(List<OrganizationDetail> organizationDetails)
            throws URISyntaxException, IOException, StorageException;

    Boolean removeLinkedContractByMasterAndUnit(Long orgId, Long linkedContractId);

    List<LinkedContractDTO> getModifiedContracts(String contractsList, Long orgId, Long... unitTypeId) throws JsonProcessingException;

    List<OrganizationDetail> findByVariantId(String variantId);

    OrganizationDetail findByBusinessUnitId(Long businessUnitId);

    OrganizationDetail saveOrUpdate(Long businessUnitId,String projectId);
}

