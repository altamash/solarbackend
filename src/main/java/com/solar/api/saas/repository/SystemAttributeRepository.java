package com.solar.api.saas.repository;

import com.solar.api.saas.model.attribute.SystemAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SystemAttributeRepository extends JpaRepository<SystemAttribute, Long> {
    SystemAttribute findByAttribute(String attribute);

    Optional<SystemAttribute> findByAttributeKey(String attributeKey);

    List<SystemAttribute> findByParentAttribute(String parentAttribute);

    List<SystemAttribute> findAllByAttributeKey(String parentAttribute);
}
