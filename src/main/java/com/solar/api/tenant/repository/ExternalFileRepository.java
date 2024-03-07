package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.externalFile.ExternalFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExternalFileRepository extends JpaRepository<ExternalFile, Long> {

    List<ExternalFile> findByImportType(String importType);

    ExternalFile findByHeader(String header);

    ExternalFile findByImportTypeId(Long import_id);

    Optional<ExternalFile> findByAssociatedParser(String associatedParser);
}
