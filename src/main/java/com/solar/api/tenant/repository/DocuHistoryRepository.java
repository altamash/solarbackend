package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.document.DocuHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocuHistoryRepository extends JpaRepository<DocuHistory, Long> {
}
