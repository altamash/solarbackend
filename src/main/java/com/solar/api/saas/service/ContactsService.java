package com.solar.api.saas.service;

import com.solar.api.saas.model.contact.Contacts;

import java.util.List;

public interface ContactsService {

    public List<Contacts> findBySourceId(Long sourceId);
    public List<Contacts> findBySource(String source);

}
