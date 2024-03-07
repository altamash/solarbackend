package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.pallet.PalletDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PalletDefinitionRepository extends JpaRepository<PalletDefinition, Long> {
}
