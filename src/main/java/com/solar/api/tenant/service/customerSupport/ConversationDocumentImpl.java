package com.solar.api.tenant.service.customerSupport;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.exception.ForbiddenException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.StorageService;
import com.solar.api.tenant.mapper.customerSupport.ConversationHeadMapper;
import com.solar.api.tenant.model.customerSupport.ConversationHead;
import com.solar.api.tenant.model.customerSupport.ConversationHistory;
import com.solar.api.tenant.model.customerSupport.ConversationReference;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.repository.CodeTypeRefMapRepository;
import com.solar.api.tenant.repository.ConversationHeadRepository;
import com.solar.api.tenant.repository.ConversationHistoryRepository;
import com.solar.api.tenant.repository.ConversationReferenceRepository;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.extended.document.DocuLibraryService;
import com.solar.api.tenant.service.lookup.codetyperefmap.CodeRefType;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Getter
@Component
public class ConversationDocumentImpl implements CodeRefType {
    @Autowired
    private StorageService storageService;

    @Autowired
    private DocuLibraryService docuLibraryService;

    @Autowired
    private ConversationHeadRepository conversationHeadRepository;

    @Autowired
    private ConversationHistoryRepository conversationHistoryRepository;

    @Autowired
    private ConversationReferenceRepository conversationReferenceRepository;

    @Autowired
    private CodeTypeRefMapRepository codeTypeRefMapRepository;

    @Autowired
    private Utility utility;

    @Autowired
    private ConversationHistoryService conversationHistoryService;
    @Autowired
    private UserService userService;

    @Value("${app.profile}")
    private String appProfile;

    @Override
    public Object doPostConversationHead(Object... params) throws URISyntaxException, IOException, StorageException {
        ConversationHead conversationHead = (ConversationHead) params[0];
        ConversationHead conversationHeadData = null;
        if (conversationHead.getSourceId() == null) {
            conversationHeadData = conversationHeadRepository.save((ConversationHead) params[0]);
        } else {
            conversationHeadData = updateConversationHead(conversationHead);
        }
        if (params[1] != null) {
            for (MultipartFile multipartFile : (List<MultipartFile>) params[1]) {
                ConversationReference conversationReference = new ConversationReference();
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
                String fileNameWithOutExt = FilenameUtils.removeExtension(multipartFile.getOriginalFilename());
                String fileExt = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

                String uri = storageService.storeInContainer(multipartFile, appProfile, "tenant/"
                                + utility.getCompKey() + AppConstants.CUSTOMER_SUPPORT_PATH,
                        timeStamp + "-" + fileNameWithOutExt + "." + fileExt, utility.getCompKey()
                        , true);

                DocuLibrary docuLibrary = docuLibraryService.saveOrUpdate(DocuLibrary.builder()
                        .docuName(multipartFile.getOriginalFilename())
                        .uri(uri)
                        .docuType(multipartFile.getContentType())
                        .visibilityKey(true)
                        .referenceTime(timeStamp)
                        .build());

                conversationReference.setReferenceType("DOCU");
                conversationReference.setReferenceId(docuLibrary.getDocuId());
                conversationReference.setConversationHead(conversationHeadData);
                conversationReferenceRepository.save(conversationReference);
            }
        }
        return conversationHeadData;
    }

    private ConversationHead updateConversationHead(ConversationHead conversationHead) {
        ConversationHead conversationHeadData = null;
        if (conversationHead.getId() != null) {
            ConversationHead finalConversationHead = conversationHead;
            conversationHeadData = conversationHeadRepository.findById(conversationHead.getId()).orElseThrow(() ->
                    new NotFoundException(ConversationHead.class, finalConversationHead.getId()));
            conversationHead = ConversationHeadMapper.toUpdateConversationHead(conversationHeadData, conversationHead);
        }
        return conversationHeadRepository.save(conversationHead);
    }

    private ConversationHead findConversationHeadById(Long id) {
        User user = userService.getLoggedInUser();
        ConversationHead conversationHead =
                conversationHeadRepository.findById(id).orElseThrow(() -> new NotFoundException(ConversationHead.class, id));
        List<ConversationHistory> conversationHistoryList = conversationHistoryRepository.findByConversationHead(conversationHead);
        conversationHead.setConversationHistoryList(conversationHistoryList);
        if (EUserType.CUSTOMER.getName().equals(user.getUserType())) {
            Long raisedBy = conversationHead.getRaisedBy();
            if (raisedBy != user.getAcctId()) {
                throw new ForbiddenException("Forbidden", 0L);
            }
        }
        return conversationHead;
    }

    @Override
    public Object doPostConversationHistory(Object... params) throws URISyntaxException, IOException, StorageException {
        ConversationHead conversationHead = findConversationHeadById((Long) params[2]);
        ConversationHistory conversationHistory = (ConversationHistory) params[0];

        if (conversationHead == null) {
            throw new NotFoundException(ConversationHistory.class, (Long) params[2]);
        }

        conversationHistory.setConversationHead(conversationHead);
        ConversationHistory conversationHistoryData = null;
        try {
            conversationHistoryData = conversationHistoryService.saveOrUpdate(conversationHistory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (params[1] != null) {
            for (MultipartFile multipartFile : (List<MultipartFile>) params[1]) {
                ConversationReference conversationReference = new ConversationReference();
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
                String fileNameWithOutExt = FilenameUtils.removeExtension(multipartFile.getOriginalFilename());
                String fileExt = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

                String uri = storageService.storeInContainer(multipartFile, appProfile, "tenant/"
                                + utility.getCompKey() + AppConstants.CUSTOMER_SUPPORT_PATH,
                        timeStamp + "-" + fileNameWithOutExt + "." + fileExt, utility.getCompKey()
                        , true);

                DocuLibrary docuLibrary = docuLibraryService.saveOrUpdate(DocuLibrary.builder()
                        .docuName(multipartFile.getOriginalFilename())
                        .uri(uri)
                        .docuType(multipartFile.getContentType())
                        .visibilityKey(true)
                        .referenceTime(timeStamp)
                        .build());

//                conversationReference.setConversationHead(conversationHead);
                conversationReference.setReferenceType("DOCU");
                conversationReference.setReferenceId(docuLibrary.getDocuId());
                conversationReference.setConversationHistory(conversationHistoryData);
                conversationReferenceRepository.save(conversationReference);
            }
        }
        return conversationHistoryData;
    }

    @Override
    public Object doPostConversationHistoryReply(Object... params) {
        ConversationHead conversationHead = findConversationHeadById((Long) params[2]);
        ConversationHistory conversationHistory = (ConversationHistory) params[0];
        if (conversationHead == null) {
            throw new NotFoundException(ConversationHistory.class, (Long) params[2]);
        }
        conversationHistory.setConversationHead(conversationHead);
        ConversationHistory conversationHistoryChild;
        try {
            conversationHistoryChild = conversationHistoryService.saveOrUpdate(conversationHistory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ConversationHistory conversationHistoryParent = conversationHistoryRepository.getById(conversationHistoryChild.getParentId());
        ConversationReference conversationReference = new ConversationReference();
        conversationReference.setConversationHead(conversationHead);
        conversationReference.setReferenceType("CONVY");
        conversationReference.setReferenceId(conversationHistoryParent.getId());
        conversationReference.setConversationHistory(conversationHistoryChild);
        conversationReferenceRepository.save(conversationReference);
        return conversationHistoryChild;
    }
}
