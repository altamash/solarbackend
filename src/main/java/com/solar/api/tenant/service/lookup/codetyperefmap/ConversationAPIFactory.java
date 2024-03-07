package com.solar.api.tenant.service.lookup.codetyperefmap;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.tenant.model.customerSupport.ConversationHead;
import com.solar.api.tenant.model.customerSupport.ConversationHistory;
import com.solar.api.tenant.model.extended.CodeTypeRefMap;
import com.solar.api.tenant.repository.CodeTypeRefMapRepository;
import com.solar.api.tenant.service.customerSupport.ConversationDocumentImpl;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

@Getter
@Component
public class ConversationAPIFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private CodeTypeRefMapRepository codeTypeRefMapRepository;

    @Autowired
    private ConversationDocumentImpl conversationDocumentImpl;

    public Object post(Object... params) throws URISyntaxException, IOException, StorageException {
        String refCode = (String) params[2];    //  params[2] contains refCode
        String tableName = getRefTable(refCode);
        Object object = params[0];
        switch (tableName) {
            case "DOCU_LIBRARY":
            case "PROJECT_HEAD":
            case "ACTIVITY_HEAD":
            case "TASK_HEAD":
                if (object instanceof ConversationHead) {
                    object = conversationDocumentImpl.doPostConversationHead(params[0], params[1]); //  params[0] = ConversationHead, params[1] = List<MultipartFile>
                }
                if (object instanceof ConversationHistory) {
                    object = conversationDocumentImpl.doPostConversationHistory(params[0], params[1], params[3]);
                }
                break;
            case "CONVERSATION_HISTORY":
                if (object instanceof ConversationHistory) {
                    object = conversationDocumentImpl.doPostConversationHistoryReply(params[0], params[1], params[3]);//  params[0] = ConversationHistory, params[1] = List<MultipartFile>, params[0] = ConversationHeadId
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + tableName);
        }
        return object;
    }

    public String getRefTable(String refCode) {
        CodeTypeRefMap codeTypeRefMap = codeTypeRefMapRepository.findByRefCode(refCode);
        return codeTypeRefMap.getRefTable();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
