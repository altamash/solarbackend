package com.solar.api.tenant.service.extended.order;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.order.ClaimFile;
import com.solar.api.tenant.repository.order.ClaimFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class ClaimFileServiceImpl implements ClaimFileService {

    @Autowired
    private ClaimFileRepository repository;

    @Override
    public ClaimFile save(ClaimFile claimFile) {
        return repository.save(claimFile);
    }

    @Override
    public ClaimFile update(ClaimFile claimFile) {
        return repository.save(claimFile);
    }

    @Override
    public ClaimFile findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(ClaimFile.class, id));
    }

    @Override
    public List<ClaimFile> findAll() {
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
