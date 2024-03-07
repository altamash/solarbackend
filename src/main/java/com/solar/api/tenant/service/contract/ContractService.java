package com.solar.api.tenant.service.contract;

import com.solar.api.tenant.mapper.contract.ContractByEntityDTO;
import com.solar.api.tenant.mapper.contract.ContractDTO;
import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.ContractMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ContractService {
    Contract add(String authorization, Contract contract, Long entityId, String refCode, List<MultipartFile> multipartFiles);

    Contract update(String authorization, Contract contract);

    Contract findById(Long id);

    List<Contract> findAll();

    //  Contract Mapping
    List<ContractMapping> addContractMapping(Long contractId, List<Long> subscriptionIds);

    ContractDTO addMasterAgreementToSelfRegisterUser(String authorization, Long entityId);

    List<ContractByEntityDTO> getAllContractsByEntityId(Long entityId);

}