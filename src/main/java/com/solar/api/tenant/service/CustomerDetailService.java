package com.solar.api.tenant.service;

import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.dataexport.payment.PaymentDataDTO;

import java.util.List;

public interface CustomerDetailService {
    CustomerDetail add(CustomerDetail customerDetail);

    CustomerDetail update(CustomerDetail customerDetail);

    CustomerDetail findById(Long id);

    List<CustomerDetail> findAll();

    CustomerDetail save(CustomerDetail customerDetail);

    CustomerDetail findByEntity(Entity entity);

    List<CustomerDetail> saveAll(List<CustomerDetail> users);

    public BaseResponse loadCustomerFilterData(String exportDTO) ;


    BaseResponse getCustomerReadingExportData(List<String> customerType,List<String> states,List<String> salesAgentId ,String startDate, String endDate, Integer pageNumber, Integer pageSize);

    BaseResponse getCustomerSalesAgent();

    List<PaymentDataDTO> getCustomerByCustomerType(List<String> customerType);
}
