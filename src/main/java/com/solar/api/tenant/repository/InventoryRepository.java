package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.assetHead.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
