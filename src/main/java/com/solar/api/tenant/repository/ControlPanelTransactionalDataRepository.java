package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.controlPanel.ControlPanelTransactionalData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ControlPanelTransactionalDataRepository  extends JpaRepository<ControlPanelTransactionalData, Long> {

    ControlPanelTransactionalData findControlPanelTransactionalDataByVariantId(Long variantId);

}
