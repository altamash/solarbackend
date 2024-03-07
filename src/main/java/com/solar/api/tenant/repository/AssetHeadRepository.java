package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.assetHead.AssetHead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetHeadRepository extends JpaRepository<AssetHead, Long> {

    List<AssetHead> findAllByRegisterId(Long registerHeadId);
}
