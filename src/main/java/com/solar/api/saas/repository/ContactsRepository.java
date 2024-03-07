package com.solar.api.saas.repository;

import com.solar.api.saas.model.contact.Contacts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactsRepository extends JpaRepository<Contacts, Long> {

    List<Contacts> findBySourceId(Long sourceId);
    List<Contacts> findBySource(String source);

}