package com.solar.api.tenant.service.extended.assetHead;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.assetHead.AssetSupplier;
import com.solar.api.tenant.repository.AssetSupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class AssetSupplierServiceImpl implements AssetSupplierService {

    @Autowired
    private AssetSupplierRepository repository;

    @Override
    public AssetSupplier save(AssetSupplier assetSupplier) {
        return repository.save(assetSupplier);
    }

    @Override
    public AssetSupplier update(AssetSupplier assetSupplier) {
        return repository.save(assetSupplier);
    }

    @Override
    public AssetSupplier findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(AssetSupplier.class, id));
    }

    @Override
    public List<AssetSupplier> findAll() {
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
