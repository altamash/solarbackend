package com.solar.api.tenant.repository.workflow;

import com.solar.api.tenant.model.workflow.MessageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {
    Optional<MessageTemplate> findByMsgTmplName(String msgTmplName);
}
