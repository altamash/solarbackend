package com.solar.api.tenant.service.extended.assetHead;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.assetHead.ScanCodes;
import com.solar.api.tenant.repository.ScanCodesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class ScanCodesServiceImpl implements ScanCodesService {

    private ScanCodesRepository repository;

    @Override
    public ScanCodes save(ScanCodes scanCodes) {
        return repository.save(scanCodes);
    }

    @Override
    public ScanCodes update(ScanCodes scanCodes) {
        return repository.save(scanCodes);
    }

    @Override
    public ScanCodes findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(ScanCodes.class, id));
    }

    @Override
    public List<ScanCodes> findAll() {
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
