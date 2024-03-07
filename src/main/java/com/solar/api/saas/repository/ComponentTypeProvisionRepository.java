package com.solar.api.saas.repository;

import com.solar.api.saas.model.permission.component.ComponentTypeProvision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComponentTypeProvisionRepository extends JpaRepository<ComponentTypeProvision, Long> {

    ComponentTypeProvision findByCompReference(String compReference);
}
