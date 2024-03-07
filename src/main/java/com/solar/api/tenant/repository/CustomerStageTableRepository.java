package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.CustomerStageTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CustomerStageTableRepository extends JpaRepository<CustomerStageTable, Long> {

    List<CustomerStageTable> findByUploadId(String uploadId);

//    Optional<CustomerStageTable> findByUploadIdAndEmail(String uploadId, String email);

//    List<CustomerStageTable> findByUploadIdAndEmailIn(String uploadId, List<String> emails);

//    @Transactional
//    @Modifying
//    void deleteByUploadIdAndEmailIn(String uploadId, List<String> emails);

    @Transactional
    @Modifying
    void deleteByUploadId(String uploadId);
}
