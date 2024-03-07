package com.solar.api.tenant.repository.acquisition;

import com.solar.api.tenant.mapper.ca.CaUserTemplateDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.tiles.docuLibrary.DocuLibraryTile;
import com.solar.api.tenant.mapper.tiles.signingRequestTracker.SigningReqTrackerTile;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.model.ca.CaReferralInfo;
import com.solar.api.tenant.model.docuSign.SigningRequestTracker;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcquisitionRepo  extends JpaRepository<SigningRequestTracker , Long> {
    @Query(value = "SELECT new com.solar.api.tenant.mapper.tiles.signingRequestTracker.SigningReqTrackerTile(s.id, s.documentSigningTemplate.id, s.extTemplateId, s.extRequestId, dst.templateName, dst.functionality, dst.customerType, " +
            "DATE_FORMAT(STR_TO_DATE(s.expiryDate, '%Y-%m-%d %H:%i:%s'), '%M %e, %Y'), s.status, org.organizationName, " +
            "COALESCE(dl.uri, NULL), COALESCE(dl.codeRefType, NULL)) " +
            "FROM SigningRequestTracker s " +
            "LEFT JOIN DocumentSigningTemplate dst ON dst.id = s.documentSigningTemplate.id " +
            "LEFT JOIN Organization org ON org.id = dst.organization.id " +
            "LEFT JOIN DocuLibrary dl ON dl.codeRefId = CAST(s.id AS string) AND dl.codeRefType = 'SIGNREQ' AND dl.entity.id = :entityId " +
            "WHERE s.entity.id = :entityId ")
    List<SigningReqTrackerTile> getByEntityId(@Param("entityId") Long entityId);

    @Query("select new com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO(p.id, p.add1, p.add2, p.zipCode, p.geoLat, p.geoLong, p.ext1, p.ext2, p.contactPerson, p.email, p.locationName, p.locationType) " +
            "from PhysicalLocation p " +
            "inner join LocationMapping lm " +
            "on lm.locationId = p.id " +
            "where lm.sourceId = :acctId ")
    List<PhysicalLocationDTO> findBySourceId(@Param("acctId") Long acctId);

    @Query(value = "select new com.solar.api.tenant.mapper.tiles.docuLibrary.DocuLibraryTile(dl.docuId, dl.status, dl.docuName, org.organizationName, cd.customerType, dl.uri ) " +
            "from DocuLibrary dl " +
            "left join Organization org " +
            "on dl.organization.id = org.id " +
            "left join CustomerDetail cd " +
            "on  cd.entityId = dl.entity.id  " +
            "where dl.entity.id = :entityId and dl.codeRefType = 'SIGNREQ' and dl.codeRefId=:entityId ")
    List<DocuLibraryTile> findByCodeRefIdAndCodeRefType(@Param("entityId") Long entityId);
    @Query(value = "SELECT NEW com.solar.api.tenant.mapper.ca.CaUserTemplateDTO(prv.entity.id,prv.user.acctId,prv.user.firstName,prv.user.lastName,prv.user.emailAddress,date_format(prv.user.createdAt, '%b %d, %Y %h:%i %p'), " +
            "cd.customerType,cd.states,prv.entity.contactPersonPhone,ploc.zipCode,concat(ploc.add2,concat(',',ploc.add3)),ed.uri, " +
            "CASE WHEN sc.isChecked IS true THEN '1' WHEN sc.isChecked IS false THEN '0' ELSE 'Null' END,um.ref_id,Count(ch.id)) " +
            " FROM UserLevelPrivilege prv" +
            " INNER JOIN CustomerDetail cd" +
            " ON cd.entityId = prv.entity.id" +
            " LEFT JOIN EntityDetail ed" +
            " ON ed.entity.id = prv.entity.id " +
            " LEFT JOIN LocationMapping locmap" +
            " ON locmap.sourceId = prv.user.acctId" +
            " LEFT JOIN PhysicalLocation ploc" +
            " ON ploc.id = locmap.locationId" +
            " LEFT JOIN CaSoftCreditCheck sc" +
            " ON sc.entity.id = prv.entity.id" +
            " LEFT JOIN UserMapping um " +
            " ON um.entityId = prv.entity.id " +
            " LEFT JOIN ConversationHead ch " +
            " ON ch.sourceId  = CAST(prv.entity.id AS string) AND ch.category = 'Customer Acquisition'" +
            " WHERE " +
            "   (prv.entity.id IN (:entityIds) " +
            "   AND (locmap.primaryInd = 'Y' OR locmap.primaryInd IS NULL) " +
            "   AND (prv.entity.isDeleted IS NULL OR prv.entity.isDeleted = false) " +
            "   AND (:statusListSize = 0 OR cd.states IN (:statusList)) " + // conditional clause for statuses
            "   AND (:zipCodeListSize = 0 OR ploc.zipCode IN (:zipCodeList)) " + // conditional clause for zipCodes
            "   AND FUNCTION('STR_TO_DATE', prv.user.createdAt, '%Y-%m-%d %H:%i:%s') " +
            "   BETWEEN COALESCE(CASE WHEN :startDate = '' OR :startDate IS NULL THEN '1900-01-01 00:00:00' ELSE CONCAT(FUNCTION('STR_TO_DATE', :startDate, '%b %d, %Y'), ' 00:00:00') " +
            "   END, '1900-01-01 00:00:00') " + // default to a very old date if startDate is null
            "   AND COALESCE(CASE WHEN :endDate = '' OR :endDate IS NULL THEN '9999-12-31 23:59:59' ELSE CONCAT(FUNCTION('STR_TO_DATE', :endDate, '%b %d, %Y'), ' 23:59:59') " +
            "   END,'9999-12-31 23:59:59')) " + // Closing the WHERE clause
            " GROUP BY " +
            " prv.entity.id, prv.user.acctId, prv.user.firstName, " +
            " prv.user.lastName, prv.user.emailAddress, prv.user.createdAt, " +
            " cd.customerType, cd.states, prv.entity.contactPersonPhone, " +
            " ploc.zipCode, ploc.add2, ploc.add3, " +
            " ed.uri, sc.isChecked, um.ref_id " +
            " HAVING COUNT(ch.id) > 0 " + // Add the HAVING clause to filter by count
            " ORDER BY prv.user.createdAt DESC ")
    List<CaUserTemplateDTO> findAllCaUsersByCorrespondenceCount(@Param("entityIds") List<Long> entityIds,
                                                                @Param("startDate") String startDate,
                                                                @Param("endDate") String endDate,
                                                                @Param("statusList") List<String> statusList,
                                                                @Param("statusListSize") int statusListSize,
                                                                @Param("zipCodeList") List<String> zipCodeList,
                                                                @Param("zipCodeListSize") int zipCodeListSize);
}
