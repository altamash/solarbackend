package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.extended.document.DocumentDTO;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocuLibraryRepository extends JpaRepository<DocuLibrary, Long> {
    List<DocuLibrary> findAllByCompKey(Long compKey);

    List<DocuLibrary> findAllByCodeRefId(String codeRefId);

    List<DocuLibrary> findAllByCodeRefIdAndCodeRefType(String codeRefId, String codeRefType);

    List<DocuLibrary> findAllByCodeRefIdAndCodeRefTypeAndVisibilityKey(String codeRefId, String codeRefType, Boolean visibilityKey);

    List<DocuLibrary> findAllByCodeRefTypeAndNotesAndVisibilityKey(String codeRefType, String notes, Boolean visibilityKey);

    @Query("select dl from DocuLibrary dl where dl.codeRefId =:codeRefId and dl.codeRefType=:codeRefType and dl.visibilityKey=:visibilityKey and dl.notes=:notes")
    DocuLibrary findByCodeRefIdAndCodeRefTypeAndAndNotesAndVisibilityKey(@Param("codeRefId") String codeRefId, @Param("codeRefType") String code,
                                                                         @Param("notes") String notes, @Param("visibilityKey") Boolean visibilityKey);

    @Query("select new com.solar.api.tenant.mapper.extended.document.DocumentDTO(dl.docuId,dl.docuName,dl.docuType," +
            "dl.size,dl.uri,dl.status,dl.entity.entityName,ed.uri,dl.entity.id,dl.updatedAt) " +
            "from DocuLibrary dl left join EntityDetail ed on ed.entity.id = dl.entity.id " +
            "where dl.codeRefId =:codeRefId and dl.codeRefType=:codeRefType and dl.visibilityKey=true")
    List<DocumentDTO> findAllDocumentDTOByCodeRefIdAndCodeRefType(@Param("codeRefId") String codeRefId, @Param("codeRefType") String codeRefType);
}
