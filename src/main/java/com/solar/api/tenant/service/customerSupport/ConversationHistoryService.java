package com.solar.api.tenant.service.customerSupport;

import com.solar.api.tenant.mapper.customerSupport.ConversationHistoryDTO;
import com.solar.api.tenant.model.customerSupport.ConversationHistory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ConversationHistoryService {

    ConversationHistory saveOrUpdate(ConversationHistory conversationHistory) throws Exception;
    ConversationHistory add(ConversationHistory conversationHistory, Long conversationHeadId, List<MultipartFile> multipartFiles, String refCode) throws Exception;

    List<ConversationHistory> findByConversationHeadId(Long conversationHeadId);

    ConversationHistory findById(Long id);

    List<ConversationHistoryDTO> setReplies(Long id);

    List<ConversationHistory> findAll();

    List<ConversationHistory> findByResponderUserId(Long id);
}
