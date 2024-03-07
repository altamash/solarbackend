package com.solar.api.tenant.service.customerSupport;

import com.solar.api.AppConstants;
import com.solar.api.exception.ForbiddenException;
import com.solar.api.exception.InvalidValueException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.exception.SolarApiException;
import com.solar.api.helper.Utility;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.controlPanel.ControlPanelStaticDataDTO;
import com.solar.api.tenant.mapper.customerSupport.*;
import com.solar.api.tenant.mapper.extended.document.DocuMapper;
import com.solar.api.tenant.mapper.tiles.CustomerSupportTicket;
import com.solar.api.tenant.mapper.tiles.customersupportmanagement.*;
import com.solar.api.tenant.mapper.tiles.customermanagement.CustomerManagementGroupBy;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.PhysicalLocationOMPaginationTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.PhysicalLocationOMTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.PhysicalLocationOMTileMapper;
import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.EntityDetail;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.controlPanel.ControlPanelStaticData;
import com.solar.api.tenant.model.customerSupport.ConversationHead;
import com.solar.api.tenant.model.customerSupport.ConversationHistory;
import com.solar.api.tenant.model.customerSupport.ConversationReference;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.model.userGroup.EntityRole;
import com.solar.api.tenant.repository.ConversationHeadRepository;
import com.solar.api.tenant.repository.ConversationHistoryRepository;
import com.solar.api.tenant.repository.DocuLibraryRepository;
import com.solar.api.tenant.repository.UserGroup.EntityRoleRepository;
import com.solar.api.tenant.repository.contract.EntityDetailRepository;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.contract.ContractService;
import com.solar.api.tenant.service.contract.UserLevelPrivilegeService;
import com.solar.api.tenant.service.controlPanel.ControlPanelService;
import com.solar.api.tenant.service.extended.document.DocuLibraryService;
import com.solar.api.tenant.service.lookup.codetyperefmap.ConversationAPIFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConversationHeadServiceImpl implements ConversationHeadService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(WebUtils.class);

    @Autowired
    private ConversationHeadRepository conversationHeadRepository;
    @Autowired
    private ConversationHistoryRepository conversationHistoryRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ConversationAPIFactory conversationAPIFactory;
    @Autowired
    private DocuLibraryService docuLibraryService;
    @Autowired
    private ControlPanelService controlPanelService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;
    @Autowired
    private ConversationHistoryService conversationHistoryService;

    @Autowired
    private Utility utility;
    @Autowired
    private DocuLibraryRepository docuLibraryRepository;
    @Autowired
    private EntityRoleRepository entityRoleRepository;
    @Autowired
    private EntityDetailRepository entityDetailRepository;

    @Override
    public ConversationHead add(ConversationHead conversationHead, List<MultipartFile> multipartFiles, String refCode)
            throws Exception {

        switch (refCode) {
            case "CSTICKET":
                //on ticket creation variant id should not empty
                if (conversationHead.getVariantId() == null || conversationHead.getVariantId().isEmpty())
                    throw new InvalidValueException("variantId");
                return (ConversationHead) conversationAPIFactory.post(conversationHead, multipartFiles, refCode);
            case "DOCU":
            case "PROJECT":
            case "ACTIVITY":
            case "TASK":
                return (ConversationHead) conversationAPIFactory.post(conversationHead, multipartFiles, refCode);
            default:
                throw new IllegalStateException("Unexpected value: " + refCode);
        }
    }

    @Override
    public ConversationHead update(ConversationHead conversationHead) {
        ConversationHead conversationHeadData = null;
        if (conversationHead.getId() != null) {
            ConversationHead finalConversationHead = conversationHead;
            conversationHeadData = conversationHeadRepository.findById(conversationHead.getId()).orElseThrow(() ->
                    new NotFoundException(ConversationHead.class, finalConversationHead.getId()));
            conversationHead = ConversationHeadMapper.toUpdateConversationHead(conversationHeadData, conversationHead);
        }
        return conversationHeadRepository.save(conversationHead);
    }

    @Override
    public ConversationHead findById(Long id) {
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
    public ConversationHead findBySourceId(String sourceId) {
        return conversationHeadRepository.findBySourceId(sourceId);
    }

    @Override
    public ConversationHead findBySourceIdAndCategory(String sourceId, String category) {
        ConversationHead conversationHead = conversationHeadRepository.findBySourceIdAndCategory(sourceId, category);
        if (conversationHead == null) {
            throw new NotFoundException(ConversationHead.class, sourceId);
        }
        return conversationHead;
    }

    @Override
    public List<ConversationHead> findBySourceTypeAndSourceId(String sourceType, String sourceId) {
        return conversationHeadRepository.findBySourceTypeAndSourceId(sourceType, sourceId);
    }

    @Override
    public List<CustomerSupportTicket> supportTicketList(String sourceType, String sourceId) {
        return customerSupportTicketMapper(findBySourceTypeAndSourceId(sourceType, sourceId));
    }

    private List<CustomerSupportTicket> customerSupportTicketMapper(List<ConversationHead> conversationHeads) {

        List<CustomerSupportTicket> customerSupportTickets = new ArrayList<>();
        conversationHeads.forEach(conversationHead -> {
            customerSupportTickets.add(CustomerSupportTicket.builder()
                    .ticketNumber(conversationHead.getId())
                    .category(conversationHead.getCategory() != null ? conversationHead.getCategory() : null)
                    .priority(conversationHead.getPriority() != null ? conversationHead.getPriority() : null)
                    .status(conversationHead.getStatus() != null ? conversationHead.getStatus() : null)
                    .subject(conversationHead.getSummary() != null ? conversationHead.getSummary() : null)
                    .build());
        });
        return customerSupportTickets;
    }

    @Override
    public List<ConversationHead> findAll(String flag) {
        List<ConversationHead> conversationHeadList = conversationHeadRepository.findAll();
        List<ConversationHead> conversationHeads = new ArrayList<>();
        String external = "ext";
        List<ConversationHistory> conversationHistoryList;

        if (external.equals(flag)) {
            User user = userService.getLoggedInUser();
            conversationHeadList = conversationHeadRepository.findAllByRaisedBy(user.getAcctId())
                    .stream().filter(i -> i.getInternal() == null || i.getInternal() == false).collect(Collectors.toList());
        }

        for (ConversationHead conversationHead : conversationHeadList) {
            conversationHistoryList = conversationHead.getConversationHistoryList();
            if (!conversationHistoryList.isEmpty()) {
                for (ConversationHistory conversationHistory : conversationHistoryList) {
                    if (!conversationHistory.getConversationReferenceList().isEmpty()) {
                        for (ConversationReference conversationReference : conversationHistory.getConversationReferenceList()) {
                            if (conversationReference != null && conversationReference.getReferenceType().equals("DOCU")) {
                                DocuLibrary docuLibrary = docuLibraryService.findById(conversationReference.getReferenceId());
                                conversationReference.setReferenceId(docuLibrary.getDocuId());
                                conversationReference.setUri(docuLibrary.getUri());
                            }
                        }
                    }
                }
            }

            if (external.equals(flag)) {
                conversationHead.setConversationHistoryList(conversationHistoryList.stream().filter(i -> i.getInternal() == null || i.getInternal() == false)
                        .collect(Collectors.toList()));
            }

            if (!conversationHead.getConversationReferenceList().isEmpty()) {
                for (ConversationReference conversationReference : conversationHead.getConversationReferenceList()) {
                    if (conversationReference != null && conversationReference.getReferenceType().equals("DOCU")) {
                        DocuLibrary docuLibrary = docuLibraryService.findById(conversationReference.getReferenceId());
                        conversationReference.setReferenceId(docuLibrary.getDocuId());
                        conversationReference.setUri(docuLibrary.getUri());
                    }
                }
            }
            conversationHeads.add(conversationHead);
        }
        return conversationHeads;
    }

    public ConversationHeadDTO createCustomerSupportTicket(ConversationHeadDTO conversationHeadDTO) {
        ConversationHeadDTO headDTO = ConversationHeadDTO.builder().build();
        User user = null;
        List<ControlPanelStaticDataDTO> controlPanelStaticDataDTOList = conversationHeadDTO.getVariants();
        List<ControlPanelStaticData> staticList = null;
        if (conversationHeadDTO.getContractId() != null) {
            Contract contract = contractService.findById(Long.valueOf(conversationHeadDTO.getContractId()));
            Optional<UserLevelPrivilege> optionalUserLevelPrivilege = null;
            if (contract != null) {
                Entity entity = contract.getEntity();
                if (entity != null) {
                    optionalUserLevelPrivilege = userLevelPrivilegeService.UserLevelPrivilegeByEntity(entity).stream().findFirst();
                    if (optionalUserLevelPrivilege.isPresent()) {
                        user = optionalUserLevelPrivilege.get().getUser();
                        if (user != null) {
                            conversationHeadDTO.setFirstName(user.getUserName());
                            conversationHeadDTO.setCustomerId(user.getAcctId());
                        } else
                            throw new NotFoundException(User.class, "user not found with account id:,", user.getAcctId().toString());
                    } else
                        throw new NotFoundException(UserLevelPrivilege.class, "UserLevelPrivilege not found for contractId:", conversationHeadDTO.getContractId().toString());
                } else
                    throw new NotFoundException(Entity.class, "entity not found with contractId,", conversationHeadDTO.getContractId().toString());
            } else
                throw new NotFoundException(Contract.class, "contractId,", conversationHeadDTO.getContractId().toString());
            if (controlPanelStaticDataDTOList != null) {
                List<String> variantIds = controlPanelStaticDataDTOList.stream().map(ControlPanelStaticDataDTO::getVariantId).collect(Collectors.toList());
                staticList = controlPanelService.getAllStaticDataByIds(variantIds);
                if (staticList == null) {
                    throw new NotFoundException(ControlPanelStaticData.class, "variantIds,", variantIds.toString());
                }
            }
            StringBuilder stb = new StringBuilder();
            stb.append(conversationHeadDTO.getFirstName() + " (ID: " + conversationHeadDTO.getCustomerId() + " has submitted a new product request. Details are:\n");
            headDTO.setSummary("Product Request- Customer: " + conversationHeadDTO.getCustomerId() + "-" + conversationHeadDTO.getFirstName());
            headDTO.setCategory("Customer Support");
            headDTO.setSubCategory("Business Queries");
            int i = 0;
            long totalCapacity = 0;
            for (ControlPanelStaticData cp : staticList) {
                Optional<ControlPanelStaticDataDTO> optionalControlPanelStaticDataDTO = conversationHeadDTO.getVariants().stream().filter(cpObj -> cpObj.getVariantId().equalsIgnoreCase(cp.getVariantId())).findFirst();
                if (optionalControlPanelStaticDataDTO.isPresent()) {
                    String requestCapacity = optionalControlPanelStaticDataDTO.get().getRequestedCapacity();
                    i++;
                    stb.append(i + "." + cp.getVariantName() + ":" + requestCapacity + "kWh\n");
                    totalCapacity += Long.valueOf(requestCapacity);
                }
            }
            stb.append("total requested capacity = " + totalCapacity + " kWh.\n Please follow up and proceed accordingly\n");
            stb.append("Reason Status:" + conversationHeadDTO.getReasonStatus() + ", Reason:" + conversationHeadDTO.getReason());
            headDTO.setMessage(stb.toString());
            headDTO.setPriority("High");
            headDTO.setRaisedBy(conversationHeadDTO.getAdminId());
            headDTO.setFirstName(user.getUserName());
            headDTO.setStatus("Open");
            Optional<Role> role = user.getRoles().stream().findFirst();
            if (role.isPresent()) headDTO.setRole(role.get().getName());
        }
        StringBuilder stb = new StringBuilder();
        stb.append(conversationHeadDTO.getFirstName() + " (ID: " + headDTO.getCustomerId() + " has submitted a new product request. Details are:\n");
        headDTO.setSummary("Product Request- Customer: " + headDTO.getCustomerId() + "-" + conversationHeadDTO.getFirstName());
        headDTO.setCategory("Customer Support");
        headDTO.setSubCategory("Business Queries");
        int i = 0;
        long totalCapacity = 0;
        for (ControlPanelStaticData cp : staticList) {
            Optional<ControlPanelStaticDataDTO> optionalControlPanelStaticDataDTO = conversationHeadDTO.getVariants().stream().filter(cpObj -> cpObj.getVariantId().equalsIgnoreCase(cp.getVariantId())).findFirst();
            if (optionalControlPanelStaticDataDTO.isPresent()) {
                String requestCapacity = optionalControlPanelStaticDataDTO.get().getRequestedCapacity();
                i++;
                stb.append(i + "." + cp.getVariantName() + ":" + requestCapacity + "kWh\n");
                totalCapacity += Long.valueOf(requestCapacity);
            }
        }
        stb.append("total requested capacity = " + totalCapacity + " kWh.\n Please follow up and proceed accordingly\n");
        stb.append(conversationHeadDTO.getReasonStatus() + " " + conversationHeadDTO.getReason());
        headDTO.setMessage(stb.toString());
        headDTO.setPriority("High");
        headDTO.setRaisedBy(conversationHeadDTO.getAdminId());
        headDTO.setFirstName(user.getUserName());
        headDTO.setStatus("Open");
        Optional<Role> role = user.getRoles().stream().findFirst();
        if (role.isPresent()) headDTO.setRole(role.get().getName());
        return headDTO;
    }

    @Override
    public ConversationHeadDTO resolveOrCloseCustomerTicket(Long id, String status, String remarks) {

        ConversationHead conversationHead = null;
        try {
            conversationHead = findById(id);
            conversationHead.setStatus(status);
            conversationHead.setRemarks(remarks);
            conversationHead = update(conversationHead);
        } catch (ForbiddenException | NotFoundException e) {
            throw new SolarApiException(e.getMessage());
        }
        return ConversationHeadMapper.toConversationHeadDTO(conversationHead);
    }

    @Override
    public List<ConversationHeadTemplateWoDTO> findAllCustomerTickets(String flag) {
        List<ConversationHeadTemplateWoDTO> conversationHeads = new ArrayList<>();
        try {
            String external = "ext";
            if (!external.equals(flag))
                conversationHeads = conversationHeadRepository.findAllTickets();

            if (external.equals(flag)) {
                User user = userService.getLoggedInUser();
                conversationHeads = conversationHeadRepository.findAllTicketsByRaisedBy(user.getAcctId())
                        .stream().filter(i -> i.getInternal() == null || !i.getInternal()).collect(Collectors.toList());
            }
        } catch (Exception e) {
            throw new SolarApiException(e.getMessage());
        }
        return conversationHeads;
    }

    @Override
    public ConversationHeadDTO associateWorkOrderToConversationHead(Long id, String workOrderId) {

        ConversationHead conversationHead = null;
        try {
            conversationHead = findById(id);
            conversationHead.setSourceType(AppConstants.WORK_ORDER);
            conversationHead.setSourceId(workOrderId);
            conversationHead = update(conversationHead);
        } catch (ForbiddenException | NotFoundException e) {
            throw new SolarApiException(e.getMessage());
        }
        return ConversationHeadMapper.toConversationHeadDTO(conversationHead);
    }

    @Override

//    public List<ConversationHeadDTO> findAllBySourceIdAndCategory(String sourceId, String category) {
//        List<ConversationHeadDTO> conversationHeadDTOs = new ArrayList<>();
//        try {
//            conversationHeadDTOs = conversationHeadRepository.findAllBySourceIdAndCategory(sourceId, category);
//            conversationHeadDTOs.stream().forEach(conversationHeadDTO -> {
//                conversationHeadDTO.setConversationHistoryDTOList(conversationHistoryService.setReplies(conversationHeadDTO.getId()));
//            });
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//        return conversationHeadDTOs;
//    }

    public List<ConversationHeadDTO> findAllBySourceIdAndCategory(String sourceId, String category) {
        List<ConversationHeadDTO> conversationHeadDTOs = new ArrayList<>();
        try {
            conversationHeadDTOs = conversationHeadRepository.findAllBySourceIdAndCategory(sourceId, category);
            Map<Long, List<ConversationHeadDTO>> groupedById = conversationHeadDTOs.stream().collect(Collectors.groupingBy(ConversationHeadDTO::getId));

            List<ConversationHeadDTO> consolidatedList = groupedById.entrySet().stream()
                    .map(entry -> {
                        Long id = entry.getKey();
                        List<ConversationHeadDTO> dtos = entry.getValue();
                        ConversationHeadDTO firstDto = dtos.get(0);

                        String consolidatedAttachmentImgs = dtos.stream()
                                .map(ConversationHeadDTO::getAttachmentImg)
                                .filter(Objects::nonNull)
                                .collect(Collectors.joining(","));

                        ConversationHeadDTO consolidatedDto = new ConversationHeadDTO(
                                firstDto.getId(), firstDto.getSummary(), firstDto.getMessage(),
                                firstDto.getCategory(), firstDto.getSubCategory(), firstDto.getPriority(),
                                firstDto.getSourceId(), firstDto.getStatus(), firstDto.getRaisedBy(),
                                firstDto.getFirstName(), firstDto.getLastName(), firstDto.getRole(),
                                firstDto.getFormattedCreatedAt(), firstDto.getRaisedByImg(),
                                consolidatedAttachmentImgs);

                        consolidatedDto.setConversationHistoryDTOList(
                                conversationHistoryService.setReplies(consolidatedDto.getId()));

                        return consolidatedDto;
                    })
                    .collect(Collectors.toList());

            return consolidatedList;

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return conversationHeadDTOs;
    }

    @Override
    public List<ConversationHeadDTO> findAllBySourceIdAndCategoryV2(String sourceId, String category, Integer pageNumber, Integer pageSize) {
        List<ConversationHeadDTO> conversationHeadDTOs = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        try {
            Page<ConversationHeadDTO> conversationHeadDTOPage = conversationHeadRepository.findAllBySourceIdAndCategoryV2(sourceId, category, pageRequest);
            long totalElements = conversationHeadDTOPage.getTotalElements();
            long totalPages = conversationHeadDTOPage.getTotalPages();
            conversationHeadDTOs = conversationHeadDTOPage.getContent();

//            for (ConversationHeadDTO conversationHeadDTO : conversationHeadDTOs) {
//                conversationHeadDTO.setTotalElements(totalElements);
//            }

            conversationHeadDTOs.stream().forEach(conversationHeadDTO -> {
                conversationHeadDTO.setConversationHistoryDTOList(conversationHistoryService.setReplies(conversationHeadDTO.getId()));
            });
            LOGGER.info("Total Records: " + totalElements);
            LOGGER.info("Total Pages: " + totalPages);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return conversationHeadDTOs;
    }

    @Override
    public ResponseEntity<?> resolveOrCloseCustomerTicketV2(List<ConversationHeadDTO> conversationHeadDTOS) {
        List<ConversationHeadDTO> conversationHeadDTOList = new ArrayList<>();
        List<ConversationHead> conversationHeadDToUpdate = new ArrayList<>();
        try {
            List<Long> conversationHeadIds = conversationHeadDTOS.stream().map(ConversationHeadDTO::getId).collect(Collectors.toList());
            List<ConversationHead> conversationHeadList = conversationHeadRepository.findAllById(conversationHeadIds);
            for (ConversationHeadDTO conversationHeadDTO : conversationHeadDTOS) {
                Optional<ConversationHead> conversationHeadOptional = conversationHeadList.stream().filter(i -> i.getId().longValue() == conversationHeadDTO.getId().longValue()).findFirst();
                if (conversationHeadOptional.isPresent()) {
                    ConversationHead conversationHead = conversationHeadOptional.get();
                    conversationHead.setStatus(conversationHeadDTO.getStatus());
                    conversationHead.setRemarks(conversationHeadDTO.getRemarks());
                    conversationHeadDToUpdate.add(conversationHead);
                    conversationHeadDTOList.add(ConversationHeadMapper.toConversationHeadDTOCustom(conversationHead));
                }
            }
            if (conversationHeadDToUpdate.size() > 0) {
                conversationHeadRepository.saveAll(conversationHeadDToUpdate);
            } else if (conversationHeadDTOList.size() == 0 || conversationHeadDToUpdate.size() == 0) {
                return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Data not updated");
            }
        } catch (ForbiddenException | NotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Data not updated");
        }
        return utility.buildSuccessResponse(HttpStatus.OK, "Data updated successfully", conversationHeadDTOList);
    }

    @Override
    public ResponseEntity<?> update(List<ConversationHeadDTO> conversationHeadDTOS) {
        List<ConversationHeadDTO> conversationHeadDTOList = new ArrayList<>();
        List<ConversationHead> conversationHeadDToUpdate = new ArrayList<>();
        try {
            List<Long> conversationHeadIds = conversationHeadDTOS.stream().map(ConversationHeadDTO::getId).collect(Collectors.toList());
            List<ConversationHead> conversationHeadList = conversationHeadRepository.findAllById(conversationHeadIds);
            for (ConversationHeadDTO conversationHeadDTO : conversationHeadDTOS) {
                Optional<ConversationHead> conversationHeadOptional = conversationHeadList.stream().filter(i -> i.getId().longValue() == conversationHeadDTO.getId().longValue()).findFirst();
                if (conversationHeadOptional.isPresent()) {
                    ConversationHead conversationHead = conversationHeadOptional.get();
                    conversationHead.setAssignee(conversationHeadDTO.getAssignee() != null ? conversationHeadDTO.getAssignee() : conversationHead.getAssignee());
                    conversationHeadDToUpdate.add(conversationHead);
                    conversationHeadDTOList.add(ConversationHeadMapper.toConversationHeadDTOCustom(conversationHead));
                }
            }
            if (conversationHeadDToUpdate.size() > 0) {
                conversationHeadRepository.saveAll(conversationHeadDToUpdate);
            } else if (conversationHeadDTOList.size() == 0 || conversationHeadDToUpdate.size() == 0) {
                return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Data not updated");
            }
        } catch (ForbiddenException | NotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Support Agent not Assigned");
        }
        return utility.buildSuccessResponse(HttpStatus.OK, "Support Agent Assigned successfully", conversationHeadDTOList);
    }

    @Override
    public BaseResponse getAllTicketsByModuleV2(String moduleName, String searchWord, Integer pageNumber, Integer pageSize, String groupBy, String groupByName, String ticketType, String priority, String category, String subCategory, String status) {
        List<ConversationHeadDTO> conversationHeadDTOs = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<ConversationHeadTemplateDTO> conversationHeadTemplateDTOPage = null;
        CustomerSupportPaginationTile result = new CustomerSupportPaginationTile();
        try {
            CustomerSupportManagementGroupBy groupByType = CustomerSupportManagementGroupBy.get(groupBy);
            User currentUser = userService.getLoggedInUser();
            conversationHeadTemplateDTOPage = getTicketsGroupByResult(groupBy, groupByName, moduleName, searchWord, groupByType, pageRequest, currentUser, ticketType, priority, category, subCategory, status);
            result.setTotalPages(conversationHeadTemplateDTOPage.getTotalPages());
            result.setTotalElements(conversationHeadTemplateDTOPage.getTotalElements());
            result.setData((!groupBy.equalsIgnoreCase(groupByType.NONE.getType()) && groupByName == null) ? CustomerSupportFiltersMapper.toConversationHeadTilesGroupBy(conversationHeadTemplateDTOPage.getContent()) : CustomerSupportFiltersMapper.toConversationHeadOMTiles(conversationHeadTemplateDTOPage.getContent()));
            //conversationHeadDTOs = conversationHeadTemplateDTOPage.getContent().stream().map(conversationHeadTemplateDTO -> ConversationHeadMapper.toConversationHeadDTOCustom(conversationHeadTemplateDTO)).collect(Collectors.toList());
            //conversationHeadDTOs = conversationHeadTemplateDTOPage.getContent();
            // conversationHeadDTOs.stream().forEach(conversationHeadDTO -> {
            // conversationHeadDTO.setConversationHistoryDTOList(conversationHistoryService.setReplies(conversationHeadDTO.getId()));});
//            LOGGER.info("Total Records: " + totalElements);
//            LOGGER.info("Total Pages: " + totalPages);
            result.setGroupBy(groupBy);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    private Page<ConversationHeadTemplateDTO> getTicketsGroupByResult(String groupBy, String groupByName, String moduleName, String searchWord, CustomerSupportManagementGroupBy groupByType, PageRequest pageable, User currentUser, String ticketType, String priority, String category, String subCategory, String status) {
        Page<ConversationHeadTemplateDTO> conversationHeadTemplateDTOPage = null;

        if (!groupBy.equalsIgnoreCase(CustomerManagementGroupBy.NONE.getType()) && groupByName != null) {
            if (currentUser != null && currentUser.getUserType().getId() == 2) {
                conversationHeadTemplateDTOPage = conversationHeadRepository.findAllByModuleAndSourceId(moduleName, null, groupBy, groupByName, searchWord, ticketType, priority, category, subCategory, status, pageable);
            } else {
                UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.userLevelPrivilegeByAccountId(currentUser.getAcctId());
                if (userLevelPrivilege != null && userLevelPrivilege.getEntity() != null) {
                    Entity entity = userLevelPrivilege.getEntity();
                    String sourceId = String.valueOf(entity.getId());
                    conversationHeadTemplateDTOPage = conversationHeadRepository.findAllByModuleAndSourceId(moduleName, sourceId, groupBy, groupByName, searchWord, ticketType, priority, category, subCategory, status, pageable);
                } else {
                    throw new NotFoundException("Entity not found for logged in user");
                }
            }
        } else {
            switch (groupByType) {
                case NONE:
                    if (currentUser != null && currentUser.getUserType().getId() == 2) {
                        conversationHeadTemplateDTOPage = conversationHeadRepository.findAllByModuleAndSourceId(moduleName, null, null, null, searchWord, ticketType, priority, category, subCategory, status, pageable);
                    } else {
                        UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.userLevelPrivilegeByAccountId(currentUser.getAcctId());
                        if (userLevelPrivilege != null && userLevelPrivilege.getEntity() != null) {
                            Entity entity = userLevelPrivilege.getEntity();
                            String sourceId = String.valueOf(entity.getId());
                            conversationHeadTemplateDTOPage = conversationHeadRepository.findAllByModuleAndSourceId(moduleName, sourceId, groupBy, null, searchWord, ticketType, priority, category, subCategory, status, pageable);
                        } else {
                            throw new NotFoundException("Entity not found for logged in user");
                        }
                    }
                    break;
                case STATUS:
                case TICKET_TYPE:
                case REQUESTER:
                case PRIORITY:
                case CREATED_BY:
                    if (currentUser != null && currentUser.getUserType().getId() == 2) {
                        conversationHeadTemplateDTOPage = conversationHeadRepository.findAllByModuleAndSourceIdWithGrouping(groupByType.getType(), moduleName, null, pageable);
                    } else {
                        UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.userLevelPrivilegeByAccountId(currentUser.getAcctId());
                        if (userLevelPrivilege != null && userLevelPrivilege.getEntity() != null) {
                            Entity entity = userLevelPrivilege.getEntity();
                            String sourceId = String.valueOf(entity.getId());
                            conversationHeadTemplateDTOPage = conversationHeadRepository.findAllByModuleAndSourceIdWithGrouping(groupByType.getType(), moduleName, sourceId, pageable);
                        } else {
                            throw new NotFoundException("Entity not found for logged in user");
                        }
                    }
                    break;
            }
        }
        return conversationHeadTemplateDTOPage;
    }

    @Override
    public ResponseEntity<?> findAllUniqueRequesterByModuleV2(String moduleName, String searchWord) {
        List<ConversationHeadCustomersTemplateDTO> conversationHeadTemplateDTOPage = null;
        List<ConversationHeadDTO> conversationHeadDTOs = null;
        try {
            User currentUser = userService.getLoggedInUser();
            if (currentUser != null && currentUser.getUserType().getId() == 2) {
                conversationHeadTemplateDTOPage = conversationHeadRepository.findAllUniqueRequesterByModule(moduleName, null, searchWord);
            } else {
                UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.userLevelPrivilegeByAccountId(currentUser.getAcctId());
                if (userLevelPrivilege != null && userLevelPrivilege.getEntity() != null) {
                    Entity entity = userLevelPrivilege.getEntity();
                    String sourceId = String.valueOf(entity.getId());
                    conversationHeadTemplateDTOPage = conversationHeadRepository.findAllUniqueRequesterByModule(moduleName, sourceId, searchWord);
                } else {
                    return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Entity not found for logged in user");
                }
            }
            conversationHeadDTOs = conversationHeadTemplateDTOPage.stream().map(conversationHeadTemplateDTO -> ConversationHeadMapper.toConversationHeadDTOCustom(conversationHeadTemplateDTO)).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Data not found");
        }
        return utility.buildSuccessResponse(HttpStatus.OK, "Data found successfully", conversationHeadDTOs);
    }

    @Override
    public ConversationHead findByIdV2(Long id) {
        User currentUser = userService.getLoggedInUser();
        ConversationHead conversationHead = conversationHeadRepository.findById(id).orElseThrow(() -> new NotFoundException(ConversationHead.class, id));
        List<ConversationHistory> conversationHistoryList = conversationHistoryRepository.findByConversationHead(conversationHead);
        conversationHead.setConversationHistoryList(conversationHistoryList);
        if (EUserType.CUSTOMER.getName().equals(currentUser.getUserType())) {
            //source id is the entity id of the customer for which ticket is created
            Long sourceId = Long.valueOf(conversationHead.getSourceId());
            UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.userLevelPrivilegeByAccountId(currentUser.getAcctId());
            if (userLevelPrivilege != null && userLevelPrivilege.getEntity() != null) {
                Entity entity = userLevelPrivilege.getEntity();
                if (sourceId.longValue() != entity.getId().longValue()) {
                    throw new ForbiddenException("Forbidden", 0L);
                }
            } else {
                throw new ForbiddenException("Forbidden", 0L);
            }
        }
        return conversationHead;
    }

    @Override
    public ResponseEntity<?> findHeadDetailById(Long id, Long compKey) {
        try {
            ConversationHead conversationHead = findByIdV2(id);
            if (conversationHead != null) {
                Long assigneeId = conversationHead.getAssignee();
                // add all conversation head attachments
                List<Long> conversationHeadDocuIds = conversationHead.getConversationReferenceList().stream().map(ConversationReference::getReferenceId).collect(Collectors.toList());
                List<DocuLibrary> docuList = docuLibraryRepository.findAllById(conversationHeadDocuIds);
                ConversationHeadDTO conversationHeadDTO = ConversationHeadMapper.toConversationHeadDTO(conversationHead);
                //set support agent details
                if (assigneeId != null) {
                    Optional<EntityRole> assigneeOptional = entityRoleRepository.findById(assigneeId);
                    if (assigneeOptional.isPresent()) {
                        EntityRole assignee = assigneeOptional.get();
                        EntityDetail entityDetail = entityDetailRepository.findCustomerByEntityId(assignee.getEntity() != null ? assignee.getEntity().getId() : null);
                        conversationHeadDTO.setAssigneeEntityName(assignee.getEntity() != null ? assignee.getEntity().getEntityName() : "unassigned");
                        conversationHeadDTO.setAssigneeImgUri(entityDetail != null ? entityDetail.getUri() : "");
                        conversationHeadDTO.setAssigneeEntityId(assignee.getEntity() != null ? assignee.getEntity().getId() : null);
                    }
                } else {
                    conversationHeadDTO.setAssigneeEntityName("unassigned");
                    conversationHeadDTO.setAssigneeImgUri("");
                    conversationHeadDTO.setAssigneeEntityId(null);
                }
                Map<Long, DocuLibrary> docuMap = docuList.stream().collect(Collectors.toMap(DocuLibrary::getDocuId, Function.identity()));
                conversationHeadDTO.getConversationReferenceDTOList().forEach(conversationReferenceDTO -> {
                    Long referenceId = conversationReferenceDTO.getReferenceId();
                    if (docuMap.containsKey(referenceId)) {
                        DocuLibrary docuLibrary = docuMap.get(referenceId);
                        String path = compKey + "";
                        String uri = docuLibrary.getUri() != null ? docuLibrary.getUri() : "";
                        docuLibrary.setUri(path + uri);
                        conversationReferenceDTO.setDocuLibrary(DocuMapper.toDocuLibraryDTO(docuLibrary));
                    }
                });

                return utility.buildSuccessResponse(HttpStatus.OK, "Data found successfully", conversationHeadDTO);
            } else {
                return utility.buildErrorResponse(HttpStatus.NOT_FOUND, "Data not found");
            }
        } catch (ForbiddenException | NotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Data not found");
        }

    }

    @Override
    public BaseResponse getFiltersData() {
        try {
            CustomerSupportFiltersTile customerSupportFiltersTile = CustomerSupportFiltersMapper.
                    toCustomerSupportFiltersTile(conversationHeadRepository.findAllFiltersData());
            return BaseResponse.builder().data(customerSupportFiltersTile).code(HttpStatus.OK.value()).message("Data Found Successfully").build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.UNPROCESSABLE_ENTITY.value()).message("Error while finding data").build();
        }
    }
}
