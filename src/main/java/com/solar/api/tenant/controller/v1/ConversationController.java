package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.customerSupport.*;
import com.solar.api.tenant.mapper.tiles.CustomerSupportTicket;
import com.solar.api.tenant.model.customerSupport.ConversationHistory;
import com.solar.api.tenant.service.customerSupport.ConversationHeadService;
import com.solar.api.tenant.service.customerSupport.ConversationHistoryService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ConversationController")
@RequestMapping(value = "/conversation")
public class ConversationController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private ConversationHeadService conversationHeadService;
    @Autowired
    private ConversationHistoryService conversationHistoryService;

    @PostMapping("/conversationHead/{refCode}")
    public ConversationHeadDTO add(@RequestParam("conversationHeadDTO") String conversationHeadDTO,
                                   @RequestParam(name = "multipartFiles", required = false) List<MultipartFile> multipartFiles,
                                   @PathVariable String refCode) throws Exception {
        ConversationHeadDTO headDTO = new ObjectMapper().readValue(conversationHeadDTO, ConversationHeadDTO.class);
        return ConversationHeadMapper.toConversationHeadDTO(
                conversationHeadService.add(ConversationHeadMapper.toConversationHead(headDTO), multipartFiles, refCode));
    }

    @PutMapping("/conversationHead")
    public ConversationHeadDTO update(@RequestBody ConversationHeadDTO conversationHeadDTO) {
        return ConversationHeadMapper.toConversationHeadDTO(
                conversationHeadService.update(ConversationHeadMapper.toConversationHead(conversationHeadDTO)));
    }

    @GetMapping("/conversationHead/{id}")
    public ConversationHeadDTO findById(@PathVariable Long id) {
        return ConversationHeadMapper.toConversationHeadDTO(conversationHeadService.findById(id));
    }

    @GetMapping("/conversationHead/getBySource/{sourceId}")
    public ConversationHeadDTO findBySourceId(@PathVariable String sourceId) {
        ConversationHeadDTO conversationHeadDTO = ConversationHeadMapper.toConversationHeadDTO(conversationHeadService.findBySourceId(sourceId));
        if (conversationHeadDTO != null) {
            conversationHeadDTO.setConversationHistoryDTOList(conversationHistoryService.setReplies(conversationHeadDTO.getId()));
        }
        return conversationHeadDTO;
    }

    /**
     * Component: Customer Management
     * Feature: Support Ticket
     * Tile: CustomerSupportTicket
     *
     * @param sourceType
     * @param sourceId
     * @return
     * @author: Shariq
     * Created At: 22/12/2022
     */
    @GetMapping("/head/sourceType/{sourceType}/sourceId/{sourceId}")
    public List<CustomerSupportTicket> supportTicketList(@PathVariable String sourceType, @PathVariable String sourceId) {
        return conversationHeadService.supportTicketList(sourceType, sourceId);
    }

    @GetMapping("/conversationHead/getByCategoryAndSource/{category}/{sourceId}")
    public ConversationHeadDTO findBySourceIdAndCategory(@PathVariable String category, @PathVariable String sourceId) {
        ConversationHeadDTO conversationHeadDTO = ConversationHeadMapper.toConversationHeadDTO(conversationHeadService.findBySourceIdAndCategory(sourceId, category));
        if (conversationHeadDTO != null) {
            conversationHeadDTO.setConversationHistoryDTOList(conversationHistoryService.setReplies(conversationHeadDTO.getId()));
        }
        return conversationHeadDTO;
    }


    @GetMapping("/conversationHead")
    public List<ConversationHeadDTO> findAll(@RequestParam("flag") String flag) {
        return ConversationHeadMapper.toConversationHeadDTOList(conversationHeadService.findAll(flag));
    }

    @PostMapping("/conversationHistory/{conversationHeadId}/{refCode}")
    public ObjectNode add(@RequestParam("conversationHistoryDTO") String conversationHistoryDTO,
                          @PathVariable Long conversationHeadId,
                          @RequestParam(name = "multipartFiles", required = false) List<MultipartFile> multipartFiles,
                          @PathVariable String refCode) throws Exception {
        ObjectNode returnJson = new ObjectMapper().createObjectNode();

        ConversationHistoryDTO historyDTO = new ObjectMapper().readValue(conversationHistoryDTO, ConversationHistoryDTO.class);
        ConversationHistory conversationHistory = conversationHistoryService.saveOrUpdate(ConversationHistoryMapper.toConversationHistory(historyDTO));
        List<String> refCodeList =
                Arrays.stream(refCode.split(",")).map(String::trim).collect(Collectors.toList());
        refCodeList.forEach(ref -> {
            try {
                ConversationHistoryMapper.toConversationHistoryDTO(
                        conversationHistoryService.add(conversationHistory, conversationHeadId, multipartFiles, ref));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
        return returnJson;
    }

    @GetMapping("/conversationHistory/{id}")
    public ConversationHistoryDTO findRequestHistoryById(@PathVariable Long id) {
        return ConversationHistoryMapper.toConversationHistoryDTO(conversationHistoryService.findById(id));
    }

    @GetMapping("/conversationHistory")
    public List<ConversationHistoryDTO> findAllRequestHistories() {
        return ConversationHistoryMapper.toConversationHistoryDTOList(conversationHistoryService.findAll());
    }

    @PostMapping("/createCustomerSupportTicket")
    public ConversationHeadDTO createCustomerSupportTicket(@RequestParam("conversationHeadDTO") String conversationHeadDTO) throws Exception {
        ConversationHeadDTO conversationHeadDtoObj = new ObjectMapper().readValue(conversationHeadDTO, ConversationHeadDTO.class);
        ConversationHeadDTO headDTO = conversationHeadService.createCustomerSupportTicket(conversationHeadDtoObj);
        return ConversationHeadMapper.toConversationHeadDTO(
                conversationHeadService.add(ConversationHeadMapper.toConversationHead(headDTO), null, "DOCU"));
    }


    @PostMapping("/resolvedOrCloseTicket/{id}")
    public ResponseEntity<Map<String, String>> resolveOrCloseCustomerTicket(@PathVariable Long id, @RequestParam("status") String status, @RequestParam("remarks") String remarks) {

        Map<String, String> response = new HashMap<>();
        String remarksStr = "";
        try {
            ConversationHeadDTO conversationHeadDTO = conversationHeadService.resolveOrCloseCustomerTicket(id, status, remarks);
            if (conversationHeadDTO.getRemarks() != null && !conversationHeadDTO.getRemarks().isEmpty()) {
                remarksStr = conversationHeadDTO.getRemarks();
            }
            response.put("code", HttpStatus.OK.value() + "");
            response.put("message", "ticket status changed successfully");
            response.put("data", "id : " + conversationHeadDTO.getId() + ", subject : " + conversationHeadDTO.getSummary() + ", remarks : " + remarksStr);
        } catch (NotFoundException e) {
            response.put("code", HttpStatus.NOT_FOUND.value() + "");
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllTickets")
    public ResponseEntity<Map<String, Object>> getAllCustomerTickets(@RequestParam("flag") String flag) {

        List<ConversationHeadTemplateWoDTO> conversationHeadDTOList = null;
        Map<String, Object> response = new HashMap<>();
        try {
            conversationHeadDTOList = conversationHeadService.findAllCustomerTickets(flag);
            response.put("code", HttpStatus.OK.value());
            response.put("message", "get tickets successfully");
            response.put("data", conversationHeadDTOList);
        } catch (NotFoundException e) {
            response.put("code", HttpStatus.NOT_FOUND.value());
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Description :  this api created to return list of conversation head for each source.
     *
     * @param category = CUST_ACQ
     * @param sourceId = entity id
     * @return created_At : 12-04-2023
     * created_by : sana
     */

    @GetMapping("/conversationHead/findAllBySourceIdAndCategory/{category}/{sourceId}")
    public List<ConversationHeadDTO> findAllBySourceIdAndCategory(@PathVariable String category, @PathVariable String sourceId) {
        return conversationHeadService.findAllBySourceIdAndCategory(sourceId, category);
    }

    @GetMapping("/conversationHead/findAllBySourceIdAndCategoryV2/{category}/{sourceId}")
    public List<ConversationHeadDTO> findAllBySourceIdAndCategoryV2(@PathVariable String category,
                                                                    @PathVariable String sourceId,
                                                                    @RequestParam("pageNumber") Integer pageNumber,
                                                                    @RequestParam("pageSize") Integer pageSize) {
        return conversationHeadService.findAllBySourceIdAndCategoryV2(sourceId, category, pageNumber, pageSize);
    }


    @ApiOperation(value = "Close case Api to update the status of the ticket(close, resolved,deferred)")
    @PostMapping("/bulkCloseCustomerTicket")
    public ResponseEntity<?> bulkCloseCustomerTicket(@RequestBody List<ConversationHeadDTO> conversationHeadDTOS) {
        return conversationHeadService.resolveOrCloseCustomerTicketV2(conversationHeadDTOS);
    }

    @ApiOperation(value = "assign support agent to ticket api")
    @PostMapping("/assignSupportAgent")
    public ResponseEntity<?> assignSupportAgent(@RequestBody List<ConversationHeadDTO> conversationHeadDTOS) {
        return conversationHeadService.update(conversationHeadDTOS);
    }

    @ApiOperation(value = "Get All tickets By Module(service request etc)")
    @GetMapping("/conversationHead/getAllTicketsByModule/{moduleName}")
    public BaseResponse getAllTicketsByModule(@PathVariable String moduleName,
                                              @RequestParam(value = "searchWord", required = false) String searchWord,
                                              @RequestParam("groupBy") String groupBy,
                                              @RequestParam(value = "groupByName", required = false) String groupByName,
                                              @RequestParam(value = "ticketType", required = false) String ticketType,
                                              @RequestParam(value = "priority", required = false) String priority,
                                              @RequestParam(value = "category", required = false) String category,
                                              @RequestParam(value = "subCategory", required = false) String subCategory,
                                              @RequestParam(value = "status", required = false) String status,
                                              @RequestParam("pageNumber") Integer pageNumber,
                                              @RequestParam("pageSize") Integer pageSize) {
        return conversationHeadService.getAllTicketsByModuleV2(moduleName, searchWord, pageNumber, pageSize, groupBy, groupByName, ticketType, priority, category, subCategory, status);
    }

    @ApiOperation(value = "Get unique customer name request along with Tickets")
    @GetMapping("/conversationHead/findAllUniqueRequesterByModuleV2/{moduleName}")
    public ResponseEntity<?> findAllUniqueRequesterByModuleV2(@PathVariable String moduleName,
                                                              @RequestParam(value = "searchWord", required = false) String searchWord) {
        return conversationHeadService.findAllUniqueRequesterByModuleV2(moduleName, searchWord);
    }

    @ApiOperation(value = "Get ticket detail by conversation head id")
    @GetMapping("/conversationHead/findHeadDetailById/{id}")
    public ResponseEntity<?> findHeadDetailById(@RequestHeader("Comp-Key") Long compKey,
                                                @PathVariable Long id) {
        return conversationHeadService.findHeadDetailById(id, compKey);
    }

    @GetMapping("/getFiltersDataForCustomerSupport")
    public BaseResponse findFiltersData() {
        return conversationHeadService.getFiltersData();
    }
}