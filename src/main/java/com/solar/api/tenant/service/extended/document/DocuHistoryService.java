package com.solar.api.tenant.service.extended.document;

import com.solar.api.tenant.model.extended.document.DocuHistory;

import java.util.List;

public interface DocuHistoryService {

    DocuHistory save(DocuHistory docuHistory);

    DocuHistory update(DocuHistory docuHistory);

    DocuHistory findById(Long id);

    List<DocuHistory> findAll();

    void delete(Long id);

    void deleteAll();
}
