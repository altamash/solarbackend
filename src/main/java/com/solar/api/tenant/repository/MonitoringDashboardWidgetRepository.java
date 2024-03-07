package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.pvmonitor.MonitorReadingDayWise;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.WidgetDataDTO;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.WidgetDataResult;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.WidgetWeatherDataResult;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.WidgetWeatherDetailDataResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Repository
public interface MonitoringDashboardWidgetRepository extends JpaRepository<MonitorReadingDayWise, Long> {
    @Query(value = "SELECT" +
            "    edsd.ref_id as refId," +
            "    edsd.ref_type as refType," +
            "    CONCAT(IF(JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.S_GS')) IS NOT NULL," +
            "    CONCAT(JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.S_GS')), ' kWh'),COALESCE('0 kWh', ''))) AS systemSize," +
            "    MAX(JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.MP'))) as mp," +
            "    CONCAT(ploc.add1, ', ', ploc.add2, ', ', ploc.add3) as address," +
            "    ploc.ext1 as state," +
            "    COALESCE(ploc.google_Coordinates, '') as googleCoordinates," +
            "    ploc.geo_Lat as geoLat," +
            "    ploc.geo_Long as geoLong," +
            "    COALESCE(JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.INST_TYPE')), ' ') as installationType, " +
            "    GROUP_CONCAT(DISTINCT edsd.id ORDER BY edsd.id ASC) AS maxId, " +
            "    COALESCE(JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.CRS_TYP')), ' ') as gardenType " +
            "FROM" +
            "    ext_data_stage_definition edsd " +
            "LEFT JOIN" +
            "    physical_locations ploc " +
            "        ON edsd.site_location_id = ploc.id " +
            "WHERE" +
            "    edsd.ref_id IN (:variantIds) " +
            "GROUP BY" +
            "    edsd.ref_id, edsd.ref_type, systemSize, address, state, googleCoordinates, geoLat, geoLong, installationType,gardenType", nativeQuery = true)
    List<WidgetDataResult> findWidgetDataByVariantIds(@Param("variantIds") List<String> variantIds);
    @Query(value = "SELECT" +
            "    edsd.ref_id as refId," +
            "    edsd.ref_type as refType," +
            "    CONCAT(IF(JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.S_GS')) IS NOT NULL," +
            "    JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.S_GS')),COALESCE('0', ''))) AS systemSize," +
            "    MAX(JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.MP'))) as mp," +
            "    CONCAT(ploc.add1, ', ', ploc.add2, ', ', ploc.add3) as address," +
            "    ploc.ext1 as state," +
            "    COALESCE(ploc.google_Coordinates, '') as googleCoordinates," +
            "    ploc.geo_Lat as geoLat," +
            "    ploc.geo_Long as geoLong," +
            "    COALESCE(JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.INST_TYPE')), ' ') as installationType, " +
            "    GROUP_CONCAT(DISTINCT edsd.id ORDER BY edsd.id ASC) AS maxId, " +
            "    COALESCE(JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.CRS_TYP')), ' ') as gardenType " +
            "FROM" +
            "    ext_data_stage_definition edsd " +
            "LEFT JOIN" +
            "    physical_locations ploc " +
            "        ON edsd.site_location_id = ploc.id " +
            "WHERE" +
            "    edsd.subs_id = :subscriptionId " +
            "GROUP BY" +
            "    edsd.ref_id, edsd.ref_type, systemSize, address, state, googleCoordinates, geoLat, geoLong, installationType,gardenType", nativeQuery = true)
    List<WidgetDataResult> findWidgetDataBySubscriptionId(@Param("subscriptionId") String subscriptionId);
    @Query(value = "SELECT distinct (edsd.ref_id) as refId, DATE_FORMAT(STR_TO_DATE(SUBSTRING(wd.sunrise_time_local, 12, 8), '%T'), '%h:%i %p') as sunrise, " +
            "DATE_FORMAT(STR_TO_DATE(SUBSTRING(wd.sunset_time_local, 12, 8), '%T'), '%h:%i %p') as sunset, " +
            "COALESCE(wd.time_zone,' ') as timezone  "+
            "FROM ext_data_stage_definition edsd " +
            "LEFT JOIN weather_data wd ON wd.garden_id = edsd.ref_id " +
            "WHERE " +
            "DATE_FORMAT(STR_TO_DATE(wd.valid_time_local, '%Y-%m-%d'), '%Y-%m-%d') = DATE_FORMAT(CURRENT_DATE, '%Y-%m-%d') " +
            "AND ( wd.day_or_night = 'D' OR wd.day_or_night = 'null' OR wd.day_or_night is null )" +
            "AND wd.garden_id in (:variantIds)", nativeQuery = true)
    List<WidgetWeatherDataResult> findWidgetWeatherDataByVariantIds(@Param("variantIds") List<String> variantIds);
    @Query(value = "SELECT distinct (edsd.ref_id) as refId, DATE_FORMAT(STR_TO_DATE(SUBSTRING(wd.sunrise_time_local, 12, 8), '%T'), '%h:%i %p') as sunrise, " +
            "DATE_FORMAT(STR_TO_DATE(SUBSTRING(wd.sunset_time_local, 12, 8), '%T'), '%h:%i %p') as sunset, " +
            "COALESCE(wd.time_zone,' ') as timeZone  "+
            "FROM ext_data_stage_definition edsd " +
            "LEFT JOIN weather_data wd ON wd.garden_id = edsd.ref_id " +
            "WHERE " +
            "DATE_FORMAT(STR_TO_DATE(wd.valid_time_local, '%Y-%m-%d'), '%Y-%m-%d') = DATE_FORMAT(CURRENT_DATE, '%Y-%m-%d') " +
            "AND ( wd.day_or_night = 'D' OR wd.day_or_night = 'null' OR wd.day_or_night is null )" +
            "AND edsd.subs_id = :subscriptionId", nativeQuery = true)
    List<WidgetWeatherDataResult> findWidgetWeatherDataBySubscriptionId(@Param("subscriptionId") String subscriptionId);
    @Query(value = "SELECT distinct (edsd.ref_id) as refId,wd.wx_phrase_long as weather "+
            "FROM ext_data_stage_definition edsd " +
            "LEFT JOIN weather_data wd ON wd.garden_id = edsd.ref_id " +
            "WHERE " +
            "DATE_FORMAT(STR_TO_DATE(wd.valid_time_local, '%Y-%m-%d'), '%Y-%m-%d') = DATE_FORMAT(CURRENT_DATE, '%Y-%m-%d') " +
            "AND  ( wd.day_or_night = 'D' OR wd.day_or_night = 'null' OR wd.day_or_night is null ) " +
            "AND wd.garden_id in(:variantIds)", nativeQuery = true)
    List<WidgetWeatherDetailDataResult> findWidgetWeatherDetailByVariantIds(@Param("variantIds") List<String> variantIds);
}



