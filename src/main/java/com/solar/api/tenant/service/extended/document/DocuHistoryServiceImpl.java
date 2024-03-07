package com.solar.api.tenant.service.extended.document;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.document.DocuHistory;
import com.solar.api.tenant.repository.DocuHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class DocuHistoryServiceImpl implements DocuHistoryService {

    @Autowired
    private DocuHistoryRepository repository;

    @Override
    public DocuHistory save(DocuHistory docuHistory) {
        return repository.save(docuHistory);
    }

    @Override
    public DocuHistory update(DocuHistory docuHistory) {
        return repository.save(docuHistory);
    }

    @Override
    public DocuHistory findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(DocuHistory.class, id));
    }

    @Override
    public List<DocuHistory> findAll() {
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
