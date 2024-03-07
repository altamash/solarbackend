package com.solar.api.tenant.repository.service;


import com.solar.api.tenant.model.extended.service.WorkOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface WorkOrderDetailRepository extends JpaRepository<WorkOrderDetail, Long> {

}
