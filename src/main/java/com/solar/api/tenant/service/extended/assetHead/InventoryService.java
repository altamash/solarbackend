package com.solar.api.tenant.service.extended.assetHead;

import com.solar.api.tenant.model.extended.assetHead.Inventory;

import java.util.List;

public interface InventoryService {

    Inventory save(Inventory inventory);

    Inventory update(Inventory inventory);

    Inventory findById(Long id);

    List<Inventory> findAll();

    void delete(Long id);

    void deleteAll();
}
