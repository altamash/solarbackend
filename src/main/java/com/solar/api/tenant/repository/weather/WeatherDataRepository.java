package com.solar.api.tenant.repository.weather;

import com.solar.api.tenant.mapper.tiles.weatherTile.*;
import com.solar.api.tenant.mapper.tiles.weatherTile.GardenDetail;
import com.solar.api.tenant.mapper.tiles.weatherTile.WeatherDataForWeatherWidget;
import com.solar.api.tenant.mapper.tiles.weatherTile.WeatherReportTile;
import com.solar.api.tenant.model.weather.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {


    @Query("select distinct new com.solar.api.tenant.mapper.tiles.weatherTile.GardenDetail(edsd.refId , pl.geoLat , pl.geoLong ) " +
            "from ExtDataStageDefinition edsd " +
            "inner join PhysicalLocation pl " +
            "on edsd.siteLocationId = pl.id")
    List<GardenDetail> findLatAndLog();

    @Modifying
    @Transactional
    @Query("DELETE FROM WeatherData w WHERE w.gardenId IN :gardenIds")
    void deleteAllByGardenIds(List<String> gardenIds);


    @Deprecated
    @Query("select new com.solar.api.tenant.mapper.tiles.weatherTile.WeatherReportTile(" +
            "edsd.refId, edsd.refType, edsd.monPlatform, edsd.mpJson, " +
            "CONCAT(pl.add1, ', ', pl.add2, ', ', pl.add3), wid.description, " +
            "DATE_FORMAT(STR_TO_DATE(wd.validTimeUtc, '%Y-%m-%d'), '%M %d, %Y, %W'), " +
            "coalesce(wid.weatherCode,wd.wxPhraseLong), CONCAT('7:00 AM', ' - ', '7:00 PM'), coalesce(wid.impact_percentage,'0%')) " +
            "from ExtDataStageDefinition edsd " +
            "Left join PhysicalLocation pl on pl.id = edsd.siteLocationId " +
            "left join WeatherData wd on wd.gardenId = edsd.refId " +
            "left join WeatherImpactDescription wid on wid.weatherCode = wd.wxPhraseLong " +
            "where wd.dayOrNight = 'D'" +
            "and STR_TO_DATE(SUBSTRING(wd.validTimeUtc, 1, 10), '%Y-%m-%d') BETWEEN  STR_TO_DATE(:startDate, '%Y-%m-%d') " +
            "AND STR_TO_DATE(:endDate, '%Y-%m-%d') " +
            "and edsd.id = (select MAX(edsd2.id) from ExtDataStageDefinition edsd2 where edsd2.refId = edsd.refId)")
    List<WeatherReportTile> findAllGarInfoForWeatherApi(@Param("startDate") String startDate, @Param("endDate") String endDate);


    //weatherInfo
    @Query("select wd from WeatherData wd where wd.gardenId in (:gardenIds)")
    List<WeatherData> findAllWeatherDataByGardenIdIn(@Param("gardenIds") List<String> gardenIds);


    @Query("select COUNT(*) from WeatherData")
    Long countRecords();

    @Deprecated
    @Query(value = "SELECT DISTINCT edsd.ref_id as refId," +
            "       DATE_FORMAT(STR_TO_DATE(SUBSTRING(wd.sunrise_time_local, 12, 8), '%T'), '%h:%i %p') as sunrise," +
            "       DATE_FORMAT(STR_TO_DATE(SUBSTRING(wd.sunset_time_local, 12, 8), '%T'), '%h:%i %p') as sunset," +
            "       wd.wx_Phrase_Long as wxphraselong, wd.narrative as narrative, wd.precip_Chance as precipchance,wd.temperature as temperature,wd.uv_description as uvdescription ," +
            "       CONCAT(pl.add1, ', ', pl.add2, ', ', pl.add3) as address, " +
            "       pl.ext1 as state ," +
            "       pl.geo_Lat as geoLat ," +
            "       pl.geo_Long as geoLong " +
            "FROM ext_data_stage_definition edsd " +
            "LEFT JOIN weather_data wd ON wd.garden_id = edsd.ref_id " +
            "LEFT JOIN Physical_Locations pl ON edsd.site_Location_Id = pl.id " +
            "WHERE DATE_FORMAT(STR_TO_DATE(wd.valid_time_local, '%Y-%m-%d'), '%Y-%m-%d') = DATE_FORMAT(CURRENT_DATE, '%Y-%m-%d') AND  ( wd.day_or_night = 'D' OR wd.day_or_night = 'null' OR wd.day_or_night is null ) " +
            "  AND wd.garden_id = :variantId ", nativeQuery = true)
    WeatherWidgetData findWeatherWidgetData(@Param("variantId") String variantId);

    @Query("select distinct new com.solar.api.tenant.mapper.tiles.weatherTile.GardenDetail(edsd.refId , pl.geoLat , pl.geoLong ) " +
            "from ExtDataStageDefinition edsd " +
            "inner join PhysicalLocation pl " +
            "on edsd.siteLocationId = pl.id " +
            "where edsd.refId in (:gardenIds)")
    List<GardenDetail> findLatAndLogByGardenIds(List<String> gardenIds);

    @Query("SELECT new com.solar.api.tenant.mapper.tiles.weatherTile.WeatherDataForWeatherWidget (DATE_FORMAT(STR_TO_DATE(SUBSTRING(wdd.validTimeLocal, 1, 16), '%Y-%m-%d %H:%i'), '%b %e, %Y %h:%i %p') ," +
            " wdd.temperature, wdd.wxPhraseLong ) " +
            " FROM WeatherDataDaily wdd " +
            "        WHERE " +
            "        STR_TO_DATE(SUBSTRING(wdd.validTimeLocal, 1, 10), '%Y-%m-%d') BETWEEN  STR_TO_DATE(:startDate, '%Y-%m-%d')" +
            "        AND STR_TO_DATE(:endDate, '%Y-%m-%d') " +
            "        AND wdd.gardenId in (:gardenIds) ")
    List<WeatherDataForWeatherWidget> getWeatherDataForOneAndTwoDays(@Param("gardenIds") String gardenIds,
                                                                     @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Deprecated
    @Query("SELECT new com.solar.api.tenant.mapper.tiles.weatherTile.WeatherDataForWeatherWidget(" +
            "CASE WHEN wdd.dayOrNight = 'D' THEN DATE_FORMAT(STR_TO_DATE(SUBSTRING(wdd.sunriseTimeLocal, 1, 19),  '%Y-%m-%dT%H:%i:%s'), '%Y-%m-%d %h:%i %p') " +
            "WHEN wdd.dayOrNight = 'N' THEN DATE_FORMAT(STR_TO_DATE(SUBSTRING(wdd.sunsetTimeLocal, 1, 19),  '%Y-%m-%dT%H:%i:%s'), '%Y-%m-%d %h:%i %p') END " +
            " ,wdd.temperature, wdd.wxPhraseLong ) " +
            "        FROM WeatherData wdd " +
            "        WHERE " +
            "        STR_TO_DATE(SUBSTRING(wdd.validTimeLocal, 1, 10), '%Y-%m-%d') BETWEEN  STR_TO_DATE(:startDate, '%Y-%m-%d')" +
            "        AND STR_TO_DATE(:endDate, '%Y-%m-%d') " +
            "        AND wdd.gardenId in (:gardenIds) ")
    List<WeatherDataForWeatherWidget> getWeatherDataForFiveAndSevenDays(@Param("gardenIds") String gardenIds,

                                                                        @Param("startDate") LocalDate startDate,

                                                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT new com.solar.api.tenant.mapper.tiles.weatherTile.WeatherDataForWeatherWidget(" +
            " DATE_FORMAT(STR_TO_DATE(wdd.validTimeLocal, '%Y-%m-%d'), '%b %d')" +
            " ,wdd.temperature, wdd.wxPhraseShort ) " +
            "        FROM WeatherData wdd " +
            "        WHERE " +
            "        STR_TO_DATE(SUBSTRING(wdd.validTimeLocal, 1, 10), '%Y-%m-%d') BETWEEN  STR_TO_DATE(:startDate, '%Y-%m-%d')" +
            "        AND STR_TO_DATE(:endDate, '%Y-%m-%d') " +
            "        AND wdd.gardenId in (:gardenIds) ")
    List<WeatherDataForWeatherWidget> getWeatherDataForFiveAndSevenDaysV2(@Param("gardenIds") String gardenIds,

                                                                        @Param("startDate") LocalDate startDate,

                                                                        @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT DISTINCT edsd.ref_id as refId," +
            "       wd.sunrise_time_local as sunrise," +
            "       wd.sunset_time_local as sunset," +
            "       wd.wx_Phrase_Short as wxphraselong, wd.narrative as narrative, wd.precip_Chance as precipchance,wd.temperature as temperature,wd.uv_description as uvdescription ," +
            "       CONCAT(pl.add1, ', ', pl.add2, ', ', pl.add3) as address, " +
            "       pl.ext1 as state ," +
            "       pl.geo_Lat as geoLat ," +
            "       pl.geo_Long as geoLong " +
            "FROM ext_data_stage_definition edsd " +
            "LEFT JOIN weather_data wd ON wd.garden_id = edsd.ref_id " +
            "LEFT JOIN Physical_Locations pl ON edsd.site_Location_Id = pl.id " +
            "WHERE DATE_FORMAT(STR_TO_DATE(wd.valid_time_local, '%Y-%m-%d'), '%Y-%m-%d') = DATE_FORMAT(CURRENT_DATE, '%Y-%m-%d') " +
            "  AND wd.garden_id = :variantId ", nativeQuery = true)
    WeatherWidgetData findWeatherWidgetDataV2(@Param("variantId") String variantId);
    @Query("select new com.solar.api.tenant.mapper.tiles.weatherTile.WeatherReportTile(" +
            "edsd.refId, edsd.refType, edsd.monPlatform, edsd.mpJson, " +
            "CONCAT(pl.add1, ', ', pl.add2, ', ', pl.add3), wid.description, " +
            "DATE_FORMAT(STR_TO_DATE(wd.validTimeUtc, '%Y-%m-%d'), '%M %d, %Y, %W'), " +
            "coalesce(wid.weatherCode,wd.wxPhraseShort), CONCAT('12:00 AM', ' - ', '12:00 PM'), coalesce(wid.impact_percentage,'0%')) " +
            "from ExtDataStageDefinition edsd " +
            "Left join PhysicalLocation pl on pl.id = edsd.siteLocationId " +
            "left join WeatherData wd on wd.gardenId = edsd.refId " +
            "left join WeatherImpactDescription wid on wid.weatherCode = wd.wxPhraseShort " +
            "where " +
            "STR_TO_DATE(SUBSTRING(wd.validTimeLocal, 1, 10), '%Y-%m-%d') BETWEEN  STR_TO_DATE(:startDate, '%Y-%m-%d') " +
            "AND STR_TO_DATE(:endDate, '%Y-%m-%d') " +
            "and edsd.id = (select MAX(edsd2.id) from ExtDataStageDefinition edsd2 where edsd2.refId = edsd.refId)")
    List<WeatherReportTile> findAllGarInfoForWeatherApiV2(@Param("startDate") String startDate, @Param("endDate") String endDate);

}
