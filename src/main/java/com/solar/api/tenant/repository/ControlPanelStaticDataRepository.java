package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.controlPanel.ControlPanelStaticDataDTO;
import com.solar.api.tenant.model.controlPanel.ControlPanelStaticData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ControlPanelStaticDataRepository extends JpaRepository<ControlPanelStaticData, Long> {

    ControlPanelStaticData findControlPanelStaticDataByVariantId(String variantId);
    @Query("select new com.solar.api.tenant.mapper.controlPanel.ControlPanelStaticDataDTO(cp.id as cpId, cp.variantId, cp.variantName , cp.variantSize, cp.variantOccupancy, (cp.variantSize - cp.variantOccupancy) as availableCapacity, pLoc) " +
            "from ControlPanelStaticData cp " +
            "inner join PhysicalLocation pLoc " +
            "on cp.LocId = pLoc.id "+
            "where (cp.variantSize - cp.variantOccupancy) > 0 ")
    List<ControlPanelStaticDataDTO> findAllControlPanelStaticDataByVariantSize();

    @Query("select new com.solar.api.tenant.mapper.controlPanel.ControlPanelStaticDataDTO(cp.id as cpId, cp.variantId, cp.variantName , cp.variantSize, cp.variantOccupancy, (cp.variantSize - cp.variantOccupancy) as availableCapacity, pLoc) " +
            "from ControlPanelStaticData cp " +
            "inner join PhysicalLocation pLoc " +
            "on cp.LocId = pLoc.id "+
            "where cp.variantId in( :variantId )")
    ControlPanelStaticDataDTO findControlPanelStaticDataByVariantIdIn(String variantId);

    List<ControlPanelStaticData> findControlPanelStaticDataByVariantIdIn(List<String> variantIds);







}
