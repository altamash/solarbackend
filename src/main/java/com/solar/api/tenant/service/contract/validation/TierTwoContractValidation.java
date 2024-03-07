package com.solar.api.tenant.service.contract.validation;

import com.solar.api.AppConstants;
import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.EContractType;
import com.solar.api.tenant.repository.contract.ContractRepository;
import com.solar.api.tenant.repository.contract.EntityRepository;
import com.solar.api.tenant.repository.contract.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TierTwoContractValidation implements IContractValidation {
    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Override
    public Boolean isOrganizationAllowed() {
        Integer activeCount = organizationRepository.findAllByStatus(AppConstants.ACTIVE_STATUS).size();
        return activeCount < TierOneContractValidation.EValidation.ACTIVE_ORGS_ALLOWED.getNums();
    }

    @Override
    public Boolean isMasterAgreementAllowed(List<Contract> masterContracts) {
        return masterContracts.size() < TierTwoContractValidation.EValidation.ACTIVE_MASTER_CONTRACTS_ALLOWED.getNums();
    }

    @Override
    public Boolean isMasterAgreementPresentForOtherContracts(String contractType, List<Contract> masterContracts) {
        if (!EContractType.MASTER_AGREEMENT.getContractType().equals(contractType)) {
            if (masterContracts.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    enum EValidation {
        ACTIVE_ORGS_ALLOWED(2),
        ACTIVE_MASTER_CONTRACTS_ALLOWED(10);
        int nums;

        EValidation(int nums) {
            this.nums = nums;
        }

        public int getNums() {
            return nums;
        }
    }
}
