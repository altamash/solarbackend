package com.solar.api.tenant.repository.docuSign;

import com.solar.api.tenant.model.docuSign.ExternalCallBackLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalCallBackLogRepository extends JpaRepository<ExternalCallBackLog, Long> {
}
