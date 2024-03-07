package com.solar.api.tenant.repository.workflow;

import com.solar.api.tenant.model.workflow.MessagePlaceholder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessagePlaceholderRepository extends JpaRepository<MessagePlaceholder, Long> {
    List<MessagePlaceholder> findByPlaceholderNameIn(List<String> placeholderNames);
}
