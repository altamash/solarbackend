package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.assetHead.AssetDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetDetailRepository extends JpaRepository<AssetDetail, Long> {
}
