package com.solar.api.tenant.repository.contract;

import com.solar.api.tenant.mapper.contract.LinkedContractDTO;
import com.solar.api.tenant.model.contract.OrganizationDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrganizationDetailRepository extends JpaRepository<OrganizationDetail, Long> {
    List<OrganizationDetail> findByOrgId(Long orgId);

    OrganizationDetail findByOrgIdAndMongoRefId(Long orgId, Long refId);

    @Query("SELECT od  " +
            "FROM OrganizationDetail od WHERE od.orgId = :orgId and od.mongoRefId is not null")
    OrganizationDetail findRefIdByOrgId(Long orgId);


    @Query("SELECT CASE WHEN COUNT(L) > 0 THEN true ELSE false END " +
            "FROM PhysicalLocation L WHERE L.id = :orgDetailId AND L.externalRefId = :orgId")
    boolean findContractsOrgAssociationExist(Long orgDetailId, Long orgId);

    List<OrganizationDetail> findByOrgIdAndIsDeleted(Long orgId, boolean isDeleted);

    @Query("select new com.solar.api.tenant.mapper.contract.LinkedContractDTO(od.id as id , od.mongoRefId as productId ,od.mongoRefId as variantId, o.subType as subType, o.organizationType as type )" +
            "from Organization o " +
            "inner join OrganizationDetail od " +
            "on o.id = od.orgId " +
            "where od.isDeleted=0 and od.isDeleted is not null")
    List<LinkedContractDTO> findAllLinkedContracts();

    @Query("select new com.solar.api.tenant.mapper.contract.LinkedContractDTO(od.id as id, od.mongoRefId as productId ,od.mongoRefId as variantId, od.mongoRefId, od.mongoRefId ) " + "from OrganizationDetail od " +
            "where od.isDeleted=false and od.isDeleted is not null and od.orgId = :orgId")
    List<LinkedContractDTO> findAllLinkedContractsMaster(@Param("orgId") Long orgId);

    @Query("select new com.solar.api.tenant.mapper.contract.LinkedContractDTO(od.id as id , od.mongoRefId as productId ,od.mongoRefId as variantId, o.subType as subType, o.organizationType as type )" +
            "from Organization o " +
            "inner join OrganizationDetail od " +
            "on o.id = od.orgId " +
            "where od.isDeleted=0 and od.isDeleted is not null and o.subType = :unitType and od.orgId != :orgId")
    List<LinkedContractDTO> findAllLinkedContractsByUnitType(@Param("unitType") Long unitType, @Param("orgId") Long orgId);

    @Query(value = "SELECT DISTINCT od.variant_id FROM organization_detail od WHERE od.org_id = :orgId and od.variant_id is not null", nativeQuery = true)
    List<String> getGardenIds(@Param("orgId") Long orgId);

    @Query(value = "SELECT DISTINCT od.variant_id FROM organization_detail od WHERE od.variant_id is not null", nativeQuery = true)
    List<String> getAllGarden();

    OrganizationDetail findByBusinessUnitId(Long businessUnitId);

    @Query("Select orgDetail from OrganizationDetail orgDetail where orgDetail.orgId = :orgId " +
            "and orgDetail.businessUnitId = :businessUnitId and orgDetail.mongoRefId = :mongoRefId " +
            "and orgDetail.isDeleted = :isDeleted")
    OrganizationDetail findByOrgIdAndBusinessUnitIdAndMongoRefIdAndIsDeleted(@Param("orgId") Long orgId,
                                                                             @Param("businessUnitId") Long businessUnitId,
                                                                             @Param("mongoRefId") String mongoRefId,
                                                                             @Param("isDeleted") Boolean isDeleted);
}
