package com.solar.api.tenant.repository.contract;

import com.solar.api.tenant.mapper.contract.ContractByEntityDTO;
import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findAllByEntityAndContractTypeAndStatus(Entity entity, String contractType, String status);

//    @Query("select new com.solar.api.tenant.mapper.subscription.subscriptionType.SubscriptionTypeDTO(st.primaryGroup as primaryGroup, st.code as code) from SubscriptionType st")
//    List<SubscriptionTypeDTO> findAllSubscriptionTypesWithPrimaryGroup();

    @Query("select new com.solar.api.tenant.mapper.contract.ContractByEntityDTO(st.docuId as docuId, st.uri as uri,st.docuName as docuName,st.docuType as docuType,st.size as docuSize) from DocuLibrary st where st.codeRefType = 'SIGNREQ' AND st.entity.id = :entityId")
    List<ContractByEntityDTO> findAllContractsByEntityId(@Param("entityId") Long entityId);
}
