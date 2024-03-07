package com.solar.api.tenant.repository.contract;

import com.solar.api.tenant.model.contract.ContractMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractMappingRepository extends JpaRepository<ContractMapping, Long> {
    List<ContractMapping> findByContractIdIn(List<Long> ids);
}
