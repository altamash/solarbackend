package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.support.SupportRequestHead;
import com.solar.api.tenant.model.support.SupportRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportRequestHistoryRepository extends JpaRepository<SupportRequestHistory, Long> {

    List<SupportRequestHistory> findByResponderUserId(Long id);

    List<SupportRequestHistory> findBySupportRequestHead(SupportRequestHead supportRequestHead);
}
