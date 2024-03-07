package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.customerSupport.ConversationReference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationReferenceRepository extends JpaRepository<ConversationReference, Long> {
}
