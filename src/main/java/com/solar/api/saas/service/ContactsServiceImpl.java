package com.solar.api.saas.service;

import com.solar.api.saas.model.contact.Contacts;
import com.solar.api.saas.repository.ContactsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactsServiceImpl implements ContactsService {

    @Autowired
    ContactsRepository contactsRepository;

    @Override
    public List<Contacts> findBySourceId(Long sourceId) {
        List<Contacts> contacts = contactsRepository.findBySourceId(sourceId);
        return contacts;
    }

    @Override
    public List<Contacts> findBySource(String source) {
        return contactsRepository.findBySource(source);
    }
}
