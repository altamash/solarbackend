package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.billing.ImportFileMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportFileMapRepository extends JpaRepository<ImportFileMap, Long> {

    ImportFileMap findByHeaderColumnName(String headerColumnName);
}
