package com.solar.api.tenant.service.extended;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.AllocHead;
import com.solar.api.tenant.repository.AllocHeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class AllocHeadServiceImpl implements AllocHeadService {

    @Autowired
    private AllocHeadRepository repository;

    @Override
    public AllocHead save(AllocHead allocHead) {
        return repository.save(allocHead);
    }

    @Override
    public AllocHead update(AllocHead allocHead) {
        return repository.save(allocHead);
    }

    @Override
    public AllocHead findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(AllocHead.class, id));
    }

    @Override
    public List<AllocHead> findAll() {
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
