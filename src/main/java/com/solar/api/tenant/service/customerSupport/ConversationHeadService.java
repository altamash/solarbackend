package com.solar.api.tenant.service.customerSupport;

import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.customerSupport.ConversationHeadDTO;
import com.solar.api.tenant.mapper.customerSupport.ConversationHeadTemplateWoDTO;
import com.solar.api.tenant.mapper.tiles.CustomerSupportTicket;
import com.solar.api.tenant.model.customerSupport.ConversationHead;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ConversationHeadService {
    ConversationHead add(ConversationHead conversationHead, List<MultipartFile> multipartFiles, String refCode) throws Exception;

    ConversationHead update(ConversationHead conversationHead);

    ConversationHead findById(Long id);

    ConversationHead findBySourceId(String sourceId);

    ConversationHead findBySourceIdAndCategory(String sourceId, String category);

    List<ConversationHead> findBySourceTypeAndSourceId(String sourceType, String sourceId);

    List<CustomerSupportTicket> supportTicketList(String sourceType, String sourceId);

    List<ConversationHead> findAll(String flag);

    //this is used for self register phase 3
    ConversationHeadDTO createCustomerSupportTicket(ConversationHeadDTO conversationHeadDTO);

    ConversationHeadDTO resolveOrCloseCustomerTicket(Long id,String status, String remarks);

    List<ConversationHeadTemplateWoDTO> findAllCustomerTickets(String flag);

    ConversationHeadDTO associateWorkOrderToConversationHead(Long id, String workOrderId);

    List<ConversationHeadDTO> findAllBySourceIdAndCategory(String sourceId, String category);

    List<ConversationHeadDTO> findAllBySourceIdAndCategoryV2(String sourceId, String category,Integer pageNumber, Integer pageSize);

    ResponseEntity<?> resolveOrCloseCustomerTicketV2(List<ConversationHeadDTO> conversationHeadDTOS);

    ResponseEntity<?> update(List<ConversationHeadDTO> conversationHeadDTOS);

    BaseResponse getAllTicketsByModuleV2(String moduleName, String searchWord, Integer pageNumber, Integer pageSize, String groupBy, String groupByName,String ticketType,String priority,String category,String subCategory,String status);

    ResponseEntity<?> findAllUniqueRequesterByModuleV2(String moduleName, String searchWord);

    ConversationHead findByIdV2(Long id);

    ResponseEntity<?> findHeadDetailById(Long id, Long compkey);

    BaseResponse getFiltersData();
}
