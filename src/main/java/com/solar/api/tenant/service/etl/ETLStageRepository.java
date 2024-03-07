package com.solar.api.tenant.service.etl;


import com.solar.api.tenant.mapper.billing.paymentManagement.PaymentManagementGraphDTO;
import com.solar.api.tenant.mapper.billing.paymentManagement.PaymentManagementGraphTemplate;
import com.solar.api.tenant.mapper.tiles.dataexport.customerdetail.DataExportCustomerTile;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.dataexport.customer.CustomerExportSalesAgent;
import com.solar.api.tenant.model.dataexport.payment.PaymentDataDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ETLStageRepository extends JpaRepository<ETLStage, Long> {
    Page<ETLStage> findAll(Pageable pageable);
    List<ETLStage> findByEntityIdIsNotIn(List<Long> entityIds);
}