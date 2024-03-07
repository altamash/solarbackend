package com.solar.api.tenant.repository.order;

import com.solar.api.tenant.model.extended.order.ClaimFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimFileRepository extends JpaRepository<ClaimFile, Long> {
}
