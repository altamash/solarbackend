package com.solar.api.tenant.repository;


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

public interface CustomerDetailRepository extends JpaRepository<CustomerDetail, Long> {

    CustomerDetail findByEntityId(Long entityId);
    @Query(value="SELECT YEAR(sign_up_date) AS Year, " +
            "MONTHNAME(sign_up_date) AS Month,Count(customer_type) AS Count FROM customer_detail " +
            "where is_contract_sign = :isContractSign and is_customer = :isCustomer " +
            "and (year(sign_up_date) = year(CURDATE()) OR year(sign_up_date) = (year(CURDATE())-1)) " +
            "GROUP BY YEAR(sign_up_date), MONTHNAME(sign_up_date),Month(sign_up_date) order by  YEAR(sign_up_date) desc, Month(sign_up_date)",nativeQuery = true)
    List<PaymentManagementGraphTemplate> getNewCustomerGraph(@Param("isContractSign") boolean isContractSign,@Param("isCustomer") boolean isCustomer);

    @Query("select new com.solar.api.tenant.mapper.billing.paymentManagement.PaymentManagementGraphDTO(cd.customerType ,count(cd.customerType) )" +
            "FROM CustomerDetail cd where cd.isContractSign = :isContractSign and cd.isCustomer = :isCustomer GROUP BY cd.customerType")
    List<PaymentManagementGraphDTO> getCustomerTypeGraph(@Param("isContractSign") boolean isContractSign, @Param("isCustomer") boolean isCustomer);

    @Query("select distinct  cd.customerType " +
            "from CustomerDetail cd  where cd.customerType is not null")
    List<String> findAllUniqueCusTypeForFilters();

    @Query("select distinct  cd.states " +
            "from CustomerDetail cd  where cd.states is not null and cd.customerType in (:cusType)")
    List<String> findAllUniqueStateForFilters(@Param("cusType") List<String> customerType);

    @Query("select distinct new com.solar.api.tenant.mapper.tiles.dataexport.customerdetail.DataExportCustomerTile(e.id , e.entityName ,cd.customerType , u.emailAddress," +
            "e.contactPersonPhone ,ploc.zipCode,concat(ploc.add2,concat(',',ploc.add3)) ,CONCAT(UPPER(SUBSTRING(cd.states, 1, 1)), LOWER(SUBSTRING(cd.states, 2))) ,rfi.repId ) " +
            "   from Entity e " +
            "   inner join UserLevelPrivilege prv " +
            "   on prv.entity.id = e.id " +
            "   inner join User u " +
            "   on u.acctId=  prv.user.acctId " +
            "   left join CaReferralInfo rfi " +
            "   on rfi.entity.id = e.id"+
            "   left join CustomerDetail cd " +
            "   on cd.entityId = e.id " +
            "   left join LocationMapping locmap " +
            "   on locmap.sourceId = u.acctId " +
            "   left join PhysicalLocation ploc " +
            "   on ploc.id = locmap.locationId " +
            "   where upper(cd.customerType) in (:customerType)  " +
            "   and locmap.primaryInd = 'Y' " +
            "   and (e.isDeleted is null or e.isDeleted = false) " +
            "   and cd.states in (:states) " +
            "   and (rfi.repId in (:salesAgentIds) or (:isAgentNA = true and rfi.repId is null)) " +
            "   and function('STR_TO_DATE', e.createdAt, '%Y-%m-%d %H:%i:%s') between function('STR_TO_DATE', :startDate, '%b %d, %Y') and function('STR_TO_DATE', :endDate, '%b %d, %Y')"
            )
    Page<DataExportCustomerTile> customerDataExport(@Param("customerType") List<String> customerType,
                                                    @Param("states") List<String> states,
                                                    @Param("salesAgentIds") List<Long> salesAgentIds,
                                                    @Param("startDate") String startDate,
                                                    @Param("endDate") String endDate,
                                                    @Param("isAgentNA") Boolean isAgentNA,
                                                    Pageable pageable);
    @Query("select distinct new com.solar.api.tenant.mapper.tiles.dataexport.customerdetail.DataExportCustomerTile(e.id , e.entityName ,cd.customerType , u.emailAddress," +
            "e.contactPersonPhone ,ploc.zipCode,concat(ploc.add2,concat(',',ploc.add3)) ,CONCAT(UPPER(SUBSTRING(cd.states, 1, 1)), LOWER(SUBSTRING(cd.states, 2))) ,rfi.repId ) " +
            "   from Entity e " +
            "   inner join UserLevelPrivilege prv " +
            "   on prv.entity.id = e.id " +
            "   inner join User u " +
            "   on u.acctId=  prv.user.acctId " +
            "   left join CaReferralInfo rfi " +
            "   on rfi.entity.id = e.id"+
            "   left join CustomerDetail cd " +
            "   on cd.entityId = e.id " +
            "   left join LocationMapping locmap " +
            "   on locmap.sourceId = u.acctId " +
            "   left join PhysicalLocation ploc " +
            "   on ploc.id = locmap.locationId " +
            "   where upper(cd.customerType) in (:customerType)  " +
            "   and locmap.primaryInd = 'Y' " +
            "   and (e.isDeleted is null or e.isDeleted = false) " +
            "   and cd.states in (:states) " +
            " and function('STR_TO_DATE', e.createdAt, '%Y-%m-%d %H:%i:%s') between function('STR_TO_DATE', :startDate, '%b %d, %Y') and function('STR_TO_DATE', :endDate, '%b %d, %Y')"
    )
    Page<DataExportCustomerTile> customerDataExport(@Param("customerType") List<String> customerType,
                                                    @Param("states") List<String> states,
                                                    @Param("startDate") String startDate,
                                                    @Param("endDate") String endDate,
                                                    Pageable pageable);
    @Query(value = "Select distinct new com.solar.api.tenant.model.dataexport.customer.CustomerExportSalesAgent(e.entityName , e.id,er.id)" +
            "from Entity e " +
            "inner join EntityRole er " +
            "on er.entity.id= e.id " +
            "inner join CaReferralInfo rf " +
            "on rf.repId = er.id")
    List<CustomerExportSalesAgent> findAllSalesAgent();

    @Query(value = "Select new com.solar.api.tenant.model.dataexport.customer.CustomerExportSalesAgent(e.entityName , e.id , er.id)" +
            "from Entity e " +
            "inner join EntityRole er " +
            "on er.entity.id= e.id " +
            "inner join CaReferralInfo rf " +
            "on rf.repId = er.id " +
            "where rf.repId in (:repIds)" )
    List<CustomerExportSalesAgent> findSalesAgentIn(@Param("repIds") List<Long> repIds);

    @Query("Select new com.solar.api.tenant.model.dataexport.payment.PaymentDataDTO(cd.entityId ,ulp.user.acctId ,e.entityName , ed.uri) " +
            "from CustomerDetail cd " +
            "Left Join Entity e on  e.id = cd.entityId " +
            "left Join  EntityDetail ed on ed.entity.id = e.id " +
            "left Join UserLevelPrivilege ulp on ulp.entity.id = e.id " +
            "Inner join CustomerSubscription cs on cs.userAccount.acctId = ulp.user.acctId " +
            "where cd.customerType in (:customerType) ")
    List<PaymentDataDTO> getCustomerListBasedByCustomerType(@Param("customerType") List<String> customerType);
    @Query("select distinct cd.states from CustomerDetail cd  where cd.states is not null ")
    List<String> findAllUniqueStates();
}