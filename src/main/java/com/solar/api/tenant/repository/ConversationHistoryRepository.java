package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.customerSupport.ConversationHead;
import com.solar.api.tenant.model.customerSupport.ConversationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationHistoryRepository extends JpaRepository<ConversationHistory, Long> {
    List<ConversationHistory> findByResponderUserId(Long id);

    List<ConversationHistory> findByConversationHead(ConversationHead conversationHead);
}
