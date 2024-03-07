package com.solar.api.tenant.repository.docuSign;

import com.solar.api.tenant.mapper.tiles.DocSigningTemplateTile;
import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.docuSign.DocumentSigningTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DocumentSigningTemplateRepository extends JpaRepository<DocumentSigningTemplate, Long> {

    //    customerType, String functionality, String status,
//    String dateRange
    List<DocumentSigningTemplate> findAllByCustomerTypeInAndFunctionalityInAndEnabledAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
            List<String> customerTypes, List<String> functionalities, Boolean status, LocalDateTime startDate, LocalDateTime endDate);

    List<DocumentSigningTemplate> findAllByCustomerTypeInAndFunctionalityInAndEnabled(
            List<String> customerTypes, List<String> functionalities, Boolean status);

    List<DocumentSigningTemplate> findAllByCustomerTypeInAndFunctionalityInAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
            List<String> customerTypes, List<String> functionalities, LocalDateTime endDate, LocalDateTime startDate);

    List<DocumentSigningTemplate> findAllByCustomerTypeInAndFunctionalityIn(
            List<String> customerTypes, List<String> functionalities);

    // composite key : functionality + org id + entity id + contract id
    Optional<DocumentSigningTemplate> findByFunctionalityAndOrganizationAndEntityAndContract(
            String functionality, Organization organization, Entity entity, Contract contract);

    //    find by org_id + customer_type + functionality
    List<DocumentSigningTemplate> findByFunctionalityAndCustomerTypeAndOrganizationAndEnabled(
            String functionality, String customerType, Organization organization, Boolean enabled);

    @Modifying(clearAutomatically = true)
    @Query(value = "update doc_signing_template set enabled = 0" +
            " where customer_type = :customerType and org_id = :organizationId and functionality = :functionality " +
            "and entity_id is null and contract_id is null", nativeQuery = true)
    public void disableTemplateByCustomerTypeAndFunctionality(@Param("organizationId") Long organizationId,
                                                              @Param("customerType") String customerType, @Param("functionality") String functionality);

    @Query("SELECT dst from DocumentSigningTemplate dst where dst.id = :id and dst.enabled = false")
    DocumentSigningTemplate findByIdEnabled(Long id);

    DocumentSigningTemplate findByTemplateName(String templateName);

    @Query("select new com.solar.api.tenant.mapper.tiles.DocSigningTemplateTile(dst.id, dst.templateName, dst.enabled, dst.functionality, dst.organization.id, dst.customerType " +
            ", dst.extTemplateId, dst.createdAt, dst.updatedAt, dst.organization.organizationName) " +
            "from DocumentSigningTemplate dst where d" +
            "st.customerType = :customerType and dst.functionality = :functionality and dst.organization.id = :organizationId and dst.enabled = true")
    List<DocSigningTemplateTile> findActiveContractsByCustomerTypeFunctionalityOrganizationId(@Param("customerType") String customerType,
                                                                                              @Param("functionality") String functionality,
                                                                                              @Param("organizationId") Long organizationId);
}
