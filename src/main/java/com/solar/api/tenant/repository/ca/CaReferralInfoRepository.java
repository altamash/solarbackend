package com.solar.api.tenant.repository.ca;

import com.solar.api.tenant.model.ca.CaReferralInfo;
import com.solar.api.tenant.model.contract.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaReferralInfoRepository extends JpaRepository<CaReferralInfo,Long> {

    CaReferralInfo findByEntity(Entity entity);

    @Query("Select ri from CaReferralInfo ri where ri.entity.id in (:entityIds)")
    List<CaReferralInfo> findAllByEntityIds(@Param("entityIds") List<Long> entityIds);
}
