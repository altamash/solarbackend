package com.solar.api.tenant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.AppConstants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.model.dataexport.customer.CustomerExportDTO;
import com.solar.api.tenant.mapper.tiles.dataexport.customerdetail.DataExportCustomerPaginationTile;
import com.solar.api.tenant.mapper.tiles.dataexport.customerdetail.DataExportCustomerTile;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.dataexport.customer.CustomerExportSalesAgent;
import com.solar.api.tenant.model.dataexport.payment.PaymentDataDTO;
import com.solar.api.tenant.repository.CustomerDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
//@Transactional("tenantTransactionManager")
public class CustomerDetailServiceImpl implements CustomerDetailService {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    CustomerDetailRepository customerDetailRepository;

    @Override
    public CustomerDetail add(CustomerDetail customerDetail) {
        return customerDetailRepository.save(customerDetail);
    }

    @Override
    public CustomerDetail update(CustomerDetail customerDetail) {
//        Optional<CustomerDetail> customerDetailObj = customerDetailRepository.findById(customerDetail.getId());
//        if (!customerDetailObj.isPresent()) {
//            throw new SolarApiException("customerDetail doesnt exists.");
//        }
//        return customerDetailRepository.save(customerDetailObj.get());
        return null;
    }


    @Override
    public CustomerDetail findById(Long id) {
        Optional<CustomerDetail> customerDetailOptional = customerDetailRepository.findById(id);
        if (!customerDetailOptional.isPresent()) {
            throw new NotFoundException(CustomerDetail.class, id);
        }
        return customerDetailOptional.get();
    }

    @Override
    public List<CustomerDetail> findAll() {
        return customerDetailRepository.findAll();
    }

    @Override
    public CustomerDetail save(CustomerDetail customerDetail) {
        return customerDetailRepository.save(customerDetail);
    }

    @Override
    public CustomerDetail findByEntity(Entity entity) {
        return customerDetailRepository.findByEntityId(entity.getId());
    }

    @Override
    public List<CustomerDetail> saveAll(List<CustomerDetail> customerDetailList) {
        return customerDetailRepository.saveAll(customerDetailList);
    }


    //old function
    @Override
    public BaseResponse loadCustomerFilterData(String exportDTO) {
        CustomerExportDTO filterDTO = new CustomerExportDTO();

        try {
            if (exportDTO != null) {
                filterDTO = new ObjectMapper().readValue(exportDTO, CustomerExportDTO.class);
                filterDTO.setStatus(customerDetailRepository.findAllUniqueStateForFilters(filterDTO.getCustomerType()));
            } else {
                filterDTO.setCustomerType(customerDetailRepository.findAllUniqueCusTypeForFilters());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(filterDTO).build();
    }

    @Override
    public BaseResponse getCustomerReadingExportData(List<String> customerType, List<String> states, List<String> salesAgentList, String startDate, String endDate, Integer pageNumber, Integer pageSize) {
        DataExportCustomerPaginationTile result = new DataExportCustomerPaginationTile();

        try {
            Page<DataExportCustomerTile> page = null;
            if (endDate == null) {
                endDate = startDate;
            }
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
            if (salesAgentList == null) {
                page = customerDetailRepository.customerDataExport(customerType, states, startDate, endDate, pageRequest);
            } else {
                Boolean agentIsNA = salesAgentList.stream().anyMatch(str -> str.equals("NA"));
                List<Long> salesAgentIds = salesAgentList.stream().filter(str -> {
                    try {
                        Long.parseLong(str);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }).map(Long::valueOf).collect(Collectors.toList());
                page = customerDetailRepository.customerDataExport(customerType, states, salesAgentIds, startDate, endDate, agentIsNA, pageRequest);
            }

            List<Long> repIds = page.stream().map(DataExportCustomerTile::getRepId).filter(s -> s.matches("\\d+")).map(Long::valueOf).distinct().collect(Collectors.toList());
            List<CustomerExportSalesAgent> salesAgent = customerDetailRepository.findSalesAgentIn(repIds);
            page.stream().forEach(c -> {
                Optional<CustomerExportSalesAgent> salesAgentName = salesAgent.stream().filter(sa -> sa.getRepId().equals(c.getRepId())).findFirst();
                if (salesAgentName.isPresent()) {
                    c.setSalesAgent(salesAgentName.get().getName());
                }
            });
            result.setDataExportCustomerTileList(page.getContent());
            result.setTotalPages(page.getTotalPages());
            result.setTotalElements(page.getTotalElements());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    @Override
    public BaseResponse getCustomerSalesAgent() {
        List<CustomerExportSalesAgent> data;
        try {
            data = customerDetailRepository.findAllSalesAgent();
            data.add(CustomerExportSalesAgent.builder().name("NA").repId("NA").build());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(data).build();
    }

    @Override
    public List<PaymentDataDTO> getCustomerByCustomerType(List<String> customerType) {

        return customerDetailRepository.getCustomerListBasedByCustomerType(customerType).stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PaymentDataDTO::getEntityId))), // You can replace getEntityId with getAccntId if you wish to use accntId instead
                        ArrayList::new
                ));
    }


}
