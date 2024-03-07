package com.solar.api.tenant.service.contract.validation;

import com.solar.api.tenant.model.contract.Contract;

import java.util.List;

public interface IContractValidation {
    //  Organization validations
    Boolean isOrganizationAllowed();

    //  Entity validations

    //  Contract validations
    Boolean isMasterAgreementAllowed(List<Contract> masterContracts);

    Boolean isMasterAgreementPresentForOtherContracts(String contractType, List<Contract> masterContracts);
}
