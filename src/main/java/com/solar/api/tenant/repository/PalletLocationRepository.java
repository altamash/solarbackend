package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.pallet.PalletLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PalletLocationRepository extends JpaRepository<PalletLocation, Long> {
}
