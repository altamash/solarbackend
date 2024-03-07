package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTemplate;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface MeasureDefinitionTenantRepository extends JpaRepository<MeasureDefinitionTenant, Long> {

    //List<MeasureDefinition> findByCode(String code);

    @Query("SELECT md FROM MeasureDefinitionTenant md WHERE md.code in (:codes)")
    List<MeasureDefinitionTenant> findByCodes(@Param("codes") Set<String> codes);

    List<MeasureDefinitionTenant> findByIdIn(List<Long> ids);

    MeasureDefinitionTenant findByCode(String code);

    List<MeasureDefinitionTenant> findByRegModuleId(Long regModuleId);

    MeasureDefinitionTenant findByIdOrderByIdAsc(Long id);

    @Query(value= "SELECT  GROUP_CONCAT(id ORDER BY id ASC SEPARATOR ',') AS measureIds," +
            "    GROUP_CONCAT(measure ORDER BY id ASC SEPARATOR ',') AS measureNames, " +
            "    GROUP_CONCAT(ifnull(format,'') ORDER BY id ASC SEPARATOR ',') AS formats " +
            "    FROM measure_definition where id in (:measureIds) ", nativeQuery = true)
    MeasureDefinitionTemplate getAllHeaderAndFormat(List<Long> measureIds);

}
