package com.solar.api.saas.repository;

import com.solar.api.saas.model.extended.MeasureDefinitionSAAS;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface MeasureDefinitionRepository extends JpaRepository<MeasureDefinitionSAAS, Long> {

    //List<MeasureDefinition> findByCode(String code);

    @Query("SELECT md FROM MeasureDefinitionSAAS md WHERE md.code in (:codes)")
    List<MeasureDefinitionSAAS> findByCodes(@Param("codes") Set<String> codes);

    List<MeasureDefinitionSAAS> findByIdIn(List<Long> ids);

    MeasureDefinitionSAAS findByCode(String code);

    @Query("SELECT md FROM MeasureDefinitionSAAS md WHERE md.id not in :ids")
    List<MeasureDefinitionSAAS> findAllIdsNotIn(List<Long> ids);

    List<MeasureDefinitionSAAS> findByRegModuleId(Long regModuleId);

    MeasureDefinitionSAAS findByIdOrderByIdAsc(Long id);

    @Query(value = "SELECT  GROUP_CONCAT(id ORDER BY id ASC SEPARATOR ',') AS measureIds," +
            "    GROUP_CONCAT(measure ORDER BY id ASC SEPARATOR ',') AS measureNames, " +
            "    GROUP_CONCAT(ifnull(format,'') ORDER BY id ASC SEPARATOR ',') AS formats " +
            "    FROM saas_schema.measure_definition where id in (:measureIds) ", nativeQuery = true)
    MeasureDefinitionTemplate getAllHeaderAndFormat(List<Long> measureIds);

    @Query("select m from MeasureDefinitionSAAS m where m.regModuleId = :regModuleId and m.measure not in (:measures)")
    List<MeasureDefinitionSAAS> findByRegModuleIdMeasuresNotIn(Long regModuleId, List<String> measures);
}
