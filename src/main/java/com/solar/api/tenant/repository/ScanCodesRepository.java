package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.assetHead.ScanCodes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanCodesRepository extends JpaRepository<ScanCodes, Long> {
}
