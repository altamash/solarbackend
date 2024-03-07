package com.solar.api.tenant.service.contract;

import com.solar.api.AppConstants;
import com.solar.api.configuration.JwtTokenUtil;
import com.solar.api.exception.ForbiddenException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.exception.SolarApiException;
import com.solar.api.tenant.mapper.contract.*;
import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.ContractMapping;
import com.solar.api.tenant.model.contract.EContractType;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.repository.contract.ContractMappingRepository;
import com.solar.api.tenant.repository.contract.ContractRepository;
import com.solar.api.tenant.repository.contract.EntityRepository;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.contract.validation.IContractValidation;
import com.solar.api.tenant.service.contract.validation.ValidationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContractServiceImpl implements ContractService {
    private static String ERROR_MSG_UPGRADE_ACCT = "Please upgrade your account";

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private ContractMappingRepository contractMappingRepository;

    @Autowired
    private ValidationFactory validationFactory;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private EntityService entityService;

    @Override
    public Contract add(String authorization, Contract contract, Long entityId, String refCode, List<MultipartFile> multipartFiles) {
        if (contract.getContractType() == null) {
            throw new SolarApiException("Contract type is required");
        }
        Entity entity = entityRepository.findById(entityId)
                .orElseThrow(() -> new NotFoundException(Entity.class, entityId));

        IContractValidation validation = validationFactory.get(jwtTokenUtil.getMasterTenant(authorization).getTenantTier());

        List<Contract> masterContracts = contractRepository.findAllByEntityAndContractTypeAndStatus(entity,
                EContractType.MASTER_AGREEMENT.getContractType(), AppConstants.ACTIVE_STATUS);

        if (!validation.isMasterAgreementAllowed(masterContracts)) {
            throw new ForbiddenException(ERROR_MSG_UPGRADE_ACCT, 0L);
        }
        if (!validation.isMasterAgreementPresentForOtherContracts(contract.getContractType(), masterContracts)) {
            throw new ForbiddenException(ERROR_MSG_UPGRADE_ACCT, 0L);
        }
        contract.setEntity(entity);
        //  TODO: Parent contract to be implemented
//        if (!EContractType.MASTER_AGREEMENT.getContractType().equals(contractType)) {
//            contract.setMasterAgreementContractId(masterContract.getId());
//        }
        return contractRepository.save(contract);
    }

    @Override
    public Contract update(String authorization, Contract contract) {
        Contract contractData = null;
        if (contract.getId() != null) {
            contractData = contractRepository.findById(contract.getId()).orElseThrow(() ->
                    new NotFoundException(Contract.class, contract.getId()));
            contractData = contractRepository.save(contractData);
        }
        return contractData;
    }

    @Override
    public Contract findById(Long id) {
        return contractRepository.findById(id).orElseThrow(() -> new NotFoundException(Contract.class, id));
    }

    @Override
    public List<Contract> findAll() {
        return contractRepository.findAll();
    }

    //  Contract Mapping
    @Override
    public List<ContractMapping> addContractMapping(Long contractId, List<Long> subscriptionIds) {
        List<ContractMapping> contractMappings = new ArrayList<>();
        Contract contract = findById(contractId);
        subscriptionIds.forEach(subscriptionId -> contractMappings.add(ContractMapping.builder()
                .contract(contract)
                .subContract(subscriptionService.findCustomerSubscriptionById(subscriptionId))
                .build()));
        return contractMappingRepository.saveAll(contractMappings);
    }

    @Override
    public ContractDTO addMasterAgreementToSelfRegisterUser(String authorization, Long userId) {
        Entity entity = entityService.findEntityByUserId(userId);
        if(entity != null) {
            List<Contract> contracts = entity.getContracts();
            if(contracts != null && contracts.size() >0 ){
                return ContractMapper.toContractDTO(contracts.stream().findFirst().get());
            }
            else {
                EntityDTO entityDto = EntityMapper.toEntityDTO(entity);
                LocalDateTime now = LocalDateTime.now();
                String refCode = "DOCU";
                ContractDTO dto = ContractDTO.builder().build();
                dto.setContractType(EContractType.MASTER_AGREEMENT.getContractType());
                dto.setContractName("Master Agreement Contract "+now);
                dto.setStatus("ACTIVE");
                dto.setIsDocAttached(false);
                dto.setIsRenewable(false);
                dto.setCreatedAt(now);
                dto.setPrimaryIndicator(true);
                return ContractMapper.toContractDTO(add(authorization, ContractMapper.toContract(dto), entityDto.getId(), refCode, null));
            }
        }
        return  null;
    }

    @Override
    public List<ContractByEntityDTO> getAllContractsByEntityId(Long entityId) {
        List<ContractByEntityDTO> entityIds = contractRepository.findAllContractsByEntityId(entityId);
        return entityIds;
    }
}
