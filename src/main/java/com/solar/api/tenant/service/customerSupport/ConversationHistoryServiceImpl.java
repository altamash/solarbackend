package com.solar.api.tenant.service.customerSupport;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.exception.ForbiddenException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.StorageService;
import com.solar.api.tenant.mapper.customerSupport.ConversationHistoryDTO;
import com.solar.api.tenant.mapper.customerSupport.ConversationHistoryMapper;
import com.solar.api.tenant.mapper.extended.document.DocuMapper;
import com.solar.api.tenant.model.customerSupport.ConversationHead;
import com.solar.api.tenant.model.customerSupport.ConversationHistory;
import com.solar.api.tenant.model.customerSupport.ConversationReference;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.repository.ConversationHeadRepository;
import com.solar.api.tenant.repository.ConversationHistoryRepository;
import com.solar.api.tenant.repository.ConversationReferenceRepository;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.extended.document.DocuLibraryService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ConversationHistoryServiceImpl implements ConversationHistoryService {
    @Autowired
    private ConversationHistoryRepository conversationHistoryRepository;

    @Autowired
    private ConversationHeadRepository conversationHeadRepository;
    @Autowired
    private ConversationReferenceRepository conversationReferenceRepository;
    @Autowired
    UserService userService;
    @Autowired
    private DocuLibraryService docuLibraryService;
    @Autowired
    private StorageService storageService;

    @Autowired
    private Utility utility;

    @Value("${app.profile}")
    private String appProfile;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public ConversationHistory saveOrUpdate(ConversationHistory conversationHistory) throws Exception {
        if (conversationHistory.getId() != null) {
            ConversationHistory conversationHistoryData = findById(conversationHistory.getId());
            if (conversationHistoryData == null) {
                throw new NotFoundException(ConversationHistory.class, conversationHistory.getId());
            }
            conversationHistoryData = ConversationHistoryMapper.toUpdateConversationHistory(conversationHistoryData,
                    conversationHistory);
            return conversationHistoryRepository.save(conversationHistoryData);
        }
        return conversationHistoryRepository.save(conversationHistory);
    }

    @Override
    public ConversationHistory add(ConversationHistory conversationHistory, Long conversationHeadId, List<MultipartFile> multipartFiles, String refCode)
            throws Exception {
        switch (refCode) {
            case "DOCU":
            case "CONVY":
                return (ConversationHistory) doPostConversationHistoryReply(conversationHistory, multipartFiles, conversationHeadId);
            default:
                throw new IllegalStateException("Unexpected value: " + refCode);
        }
    }

    public Object doPostConversationHistoryReply(Object... params) {
        ConversationHead conversationHead = findConversationHeadById((Long) params[2]);
        ConversationHistory conversationHistory = (ConversationHistory) params[0];
        if (conversationHead == null) {
            throw new NotFoundException(ConversationHistory.class, (Long) params[2]);
        }
        conversationHistory.setConversationHead(conversationHead);
        ConversationHistory conversationHistoryChild = null;
        try {
            conversationHistoryChild = saveOrUpdate(conversationHistory);
            ConversationHistory conversationHistoryParent = conversationHistoryRepository.getById(conversationHistoryChild.getParentId());
            ConversationReference conversationReference = new ConversationReference();
            conversationReference.setConversationHead(conversationHead);
            conversationReference.setReferenceType("CONVY");
            conversationReference.setReferenceId(conversationHistoryParent.getId());
            conversationReference.setConversationHistory(conversationHistoryChild);
            conversationReferenceRepository.save(conversationReference);
        } catch (Exception e) {
//            throw new RuntimeException(e);
            logger.error(e.getMessage());
        }
        // added 13/04/2023 to save document in chat section
        if (params[1] != null) {
            for (MultipartFile multipartFile : (List<MultipartFile>) params[1]) {
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
                String fileNameWithOutExt = FilenameUtils.removeExtension(multipartFile.getOriginalFilename());
                String fileExt = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
                String uri = null;
                try {
                    uri = storageService.storeInContainer(multipartFile, appProfile, "tenant/"
                                    + utility.getCompKey() + AppConstants.CUSTOMER_SUPPORT_PATH,
                            timeStamp + "-" + fileNameWithOutExt + "." + fileExt, utility.getCompKey()
                            , true);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (StorageException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                DocuLibrary docuLibrary = docuLibraryService.saveOrUpdate(DocuLibrary.builder()
                        .docuName(multipartFile.getOriginalFilename())
                        .uri(uri)
                        .docuType(multipartFile.getContentType())
                        .visibilityKey(true)
                        .referenceTime(timeStamp)
                        .build());
                ConversationReference conversationReferenceDocu = new ConversationReference();
                conversationReferenceDocu.setReferenceType("DOCU");
                conversationReferenceDocu.setReferenceId(docuLibrary.getDocuId());
                conversationReferenceDocu.setConversationHistory(conversationHistoryChild);
                conversationReferenceRepository.save(conversationReferenceDocu);
            }
        }
        return conversationHistoryChild;
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
    public List<ConversationHistory> findByConversationHeadId(Long conversationHeadId) {
        ConversationHead conversationHead = conversationHeadRepository.findById(conversationHeadId)
                .orElseThrow(() -> new NotFoundException(ConversationHistory.class, conversationHeadId));
        return conversationHistoryRepository.findByConversationHead(conversationHead);
    }

    @Override
    public ConversationHistory findById(Long id) {
        User user = userService.getLoggedInUser();
        ConversationHistory conversationHistory = conversationHistoryRepository.findById(id).
                orElseThrow(() -> new NotFoundException(ConversationHistory.class, id));
        if (EUserType.CUSTOMER.getName().equals(user.getUserType())) {
            Long raisedBy = conversationHistory.getConversationHead().getRaisedBy();
            if (raisedBy != user.getAcctId()) {
                throw new ForbiddenException("Forbidden", 0L);
            }
        }
        return conversationHistory;
    }

    @Override
    public List<ConversationHistoryDTO> setReplies(Long id) {

        List<ConversationHistoryDTO> conversationHistoryList =
                ConversationHistoryMapper.toConversationHistoryDTOList(findByConversationHeadId(id));

        conversationHistoryList.forEach(chl -> {
            if (chl.getParentId() != null) {
                List<ConversationHistoryDTO> conversationHistoryParentList = new ArrayList<>();
                ConversationHistoryDTO parent =
                        ConversationHistoryMapper.toConversationHistoryDTO(findById(chl.getParentId()));
                conversationHistoryParentList.add(parent);
                chl.setConversationHistoryDTOS(conversationHistoryParentList);
            }
            chl.getConversationReferenceDTOList().forEach(crl -> {
                if (crl.getReferenceType().equals("DOCU")) {
                    DocuLibrary docuLibrary = docuLibraryService.findById(crl.getReferenceId());
//                    crl.setDocuLibrary(docuLibrary);  //earlier commented out
                    //sana added this for customer acq correspondence 13/04/2023
                    crl.setDocuLibrary(DocuMapper.toDocuLibraryDTO(docuLibrary));

                }
            });
        });

        return conversationHistoryList;
    }

    @Override
    public List<ConversationHistory> findAll() {
        return conversationHistoryRepository.findAll();
    }

    @Override
    public List<ConversationHistory> findByResponderUserId(Long id) {
        return conversationHistoryRepository.findByResponderUserId(id);
    }

}
