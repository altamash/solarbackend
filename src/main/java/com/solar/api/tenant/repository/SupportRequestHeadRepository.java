package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.support.SupportRequestHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupportRequestHeadRepository extends JpaRepository<SupportRequestHead, Long> {

    List<SupportRequestHead> findByAccountId(Long id);

    List<SupportRequestHead> findBySubscriptionId(Long id);

    List<SupportRequestHead> findByStatus(String status);

    @Query("SELECT srh FROM SupportRequestHead srh LEFT JOIN FETCH srh.supportRequestHistories")
    List<SupportRequestHead> findAllFetchSupportRequestHistories();
}
