package com.solar.api.tenant.repository.workflow;

import com.solar.api.tenant.model.workflow.MessagePlaceholder;
import com.solar.api.tenant.model.workflow.MessageTemplatePlaceholder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageTemplatePlaceholderRepository extends JpaRepository<MessageTemplatePlaceholder, Long> {
    MessageTemplatePlaceholder findByMessagePlaceholder(MessagePlaceholder messagePlaceholder);
    List<MessageTemplatePlaceholder> findByMsgTmpId(Long messageTemplateId);
}
