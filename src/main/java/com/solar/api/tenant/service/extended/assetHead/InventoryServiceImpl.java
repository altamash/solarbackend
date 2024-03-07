package com.solar.api.tenant.service.extended.assetHead;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.assetHead.Inventory;
import com.solar.api.tenant.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository repository;

    @Override
    public Inventory save(Inventory inventory) {
        return repository.save(inventory);
    }

    @Override
    public Inventory update(Inventory inventory) {
        return repository.save(inventory);
    }

    @Override
    public Inventory findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(Inventory.class, id));
    }

    @Override
    public List<Inventory> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
