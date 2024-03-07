package com.solar.api.tenant.repository.ca;

import com.solar.api.tenant.model.ca.CaUtility;
import com.solar.api.tenant.model.contract.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaUtilityRepository extends JpaRepository<CaUtility,Long> {
    List<CaUtility> findByEntity(Entity entity);
    CaUtility findByReferenceId(String referenceId);

}
