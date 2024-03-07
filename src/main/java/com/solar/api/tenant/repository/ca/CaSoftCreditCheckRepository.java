package com.solar.api.tenant.repository.ca;

import com.solar.api.tenant.model.ca.CaSoftCreditCheck;
import com.solar.api.tenant.model.contract.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaSoftCreditCheckRepository extends JpaRepository<CaSoftCreditCheck,Long> {

    CaSoftCreditCheck findByEntity(Entity entity);
}
