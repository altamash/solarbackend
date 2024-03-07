package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.user.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {
    Stage findByExternalId(String externalId);

    @Query("select externalId from Stage where user.acctId in (select u.acctId from User u where acctId not in " +
            "(select portalAccount.acctId from PaymentInfo))")
    List<Long> findExternalIdsWithoutPaymentInfo();

    @Query("select MAX(externalId) + 1 from Stage s")
    Long getNextExternalId();
}
