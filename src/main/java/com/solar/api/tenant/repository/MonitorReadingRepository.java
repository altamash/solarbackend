package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.pvmonitor.MonitorReadingCustomResponse;
import com.solar.api.tenant.mapper.tiles.dataexport.powermonitoring.DataExportPMTile;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MonitorReadingRepository extends JpaRepository<MonitorReading, Long>, MonitorReadingRepositoryCustom {
    MonitorReading findByUserIdAndTime(Long userId, Date time);

    MonitorReading findBySubscriptionIdAndTime(Long subscriptionId, Date time);

    MonitorReading findBySubscriptionIdMongoAndTime(String subscriptionIdMongo, Date time);

    @Query(value = "SELECT MR.* from monitor_reading MR " +
            "where " +
            "MR.user_id = :userId " +
            "and " +
            "MR.subscription_id in (:subscriptions) " +
            "and " +
            "MR.time in ( :time ) " +
            "order by MR.time asc", nativeQuery = true)
    List<MonitorReading> findByUserIdAndTimeIn(@Param("userId") Long userId,
                                               @Param("time") List<Date> time,
                                               @Param("subscriptions") List<CustomerSubscription> customerSubscriptions);

    @Query(value = "SELECT MR.* FROM monitor_reading MR " +
            "where MR.subscription_id = :subId and MR.time in (:time ) " +
            "order by MR.time asc", nativeQuery = true)
    List<MonitorReading> findBySubscriptionIdAndTimeIn(@Param("subId") Long subId,
                                                       @Param("time") List<Date> time);

    @Query(value = "SELECT MR.* FROM monitor_reading MR " +
            "where MR.subscription_id = :subId " +
            "and " +
            "date_format(MR.time,'%Y-%m-%d %H:%i') >  date_format(:startTime, '%Y-%m-%d %H:%i') " +
            "and " +
            "date_format(MR.time,'%Y-%m-%d %H:%i') <= date_format(:endTime,'%Y-%m-%d %H:%i') ", nativeQuery = true)
    List<MonitorReading> findBySubscriptionIdAndStartDtTimeAndEndDtTime(@Param("subId") Long subId,
                                                                        @Param("startTime") String startTime,
                                                                        @Param("endTime") String endTime);

//    @Query(value =  "SELECT MR.* FROM customer_subscription_mapping  CSM " +
//            "INNER JOIN monitor_reading MR " +
//            "ON CSM.customer_subscription_id = MR.subscription_id " +
//            "where " +
//            "CSM.rate_Code = :rate_code " +
//            "and " +
//            "CSM.customer_subscription_id = :subId " +
//            "and " +
//            "date_format(MR.time,'%Y-%m-%d %H:%i') >  date_format(:startTime, '%Y-%m-%d %H:%i') " +
//            "and " +
//            "date_format(MR.time,'%Y-%m-%d %H:%i') <= date_format(:endTime,'%Y-%m-%d %H:%i') ", nativeQuery = true)
//    List <MonitorReading> findByUserIdAndTimeInSolis(@Params("subId") Long userId,
//                                                             @Params("startTime") String  startTime,
//                                                             @Params("endTime") String  endTime,
//                                                             @Params("rate_code") String rate_code);


    // @Params("rate_code") String rate_code);
//    List<MonitorReading> findBySubscriptionIdAndTimeIn(Long subscriptionId, List<Date> time);

    @Query("select mr from MonitorReading mr where mr.id = (select max(id) from MonitorReading where subscriptionId = :subscriptionId)")
    MonitorReading getLastRecord(Long subscriptionId);

    @Query("SELECT mr FROM MonitorReading mr WHERE mr.subscriptionIdMongo = :subscriptionIdMongo AND mr.time =" +
            " (SELECT MAX(time) FROM MonitorReading WHERE subscriptionIdMongo = :subscriptionIdMongo)")
    MonitorReading getLastRecord(String subscriptionIdMongo);

    //@Query("SELECT mr from MonitorReading mr where mr.id = (select max(id) from MonitorReading where userId = :userId and grossYield > 0)")
    @Query("SELECT mr from MonitorReading mr where mr.id = (select max(id) from MonitorReading where subscriptionId = :subscriptionId and grossYield > 0)")
    MonitorReading getLastGrossYieldRecordLegacy(Long subscriptionId);

    @Query("SELECT mr from MonitorReading mr where mr.id = (select max(id) from MonitorReading where subscriptionIdMongo = :subscriptionIdMongo and grossYield > 0)")
    MonitorReading getLastGrossYieldRecord(String subscriptionIdMongo);

    //    @Query(value =
//            "select  distinct(user_id) as userId, sum(annual_yield) as annualYield , sum(current_value) as currentValue ," +
//                    " sum(sytem_size) as sytemSize , sum(peak_value) as peakValue ," +
//                    " sum(monthly_yield) as monthlyYield , sum(daily_yield) as dailyYield ," +
//                    " sum(gross_yield) as grossYield ," +
//                    "  GROUP_CONCAT(subscription_id_mongo SEPARATOR ',') as subscriptionIds" +
//                    " from" +
//                    " (select id,user_id,subscription_id_mongo,annual_yield,current_value,sytem_size,monthly_yield,daily_yield,gross_yield,peak_value from  monitor_reading mr where mr.id in (" +
//                    " select max(id) from monitor_reading " +
//                    " group by subscription_id_mongo,user_id" +
//                    " having subscription_id_mongo in(:subId))) as tbl" +
//                    " group by user_id", nativeQuery = true)
    @Query(value =
            "SELECT subscription_id_mongo AS subscriptionIds, SUM(annual_yield) AS annualYield, SUM(current_value) AS currentValue, SUM(sytem_size) AS sytemSize," +
                    " SUM(peak_value) AS peakValue, SUM(monthly_yield) AS monthlyYield,SUM(daily_yield) AS dailyYield,SUM(gross_yield) AS grossYield " +
                    "FROM (SELECT id,user_id, subscription_id_mongo,annual_yield,current_value,sytem_size, monthly_yield, daily_yield, gross_yield, peak_value " +
                    "FROM monitor_reading mr  WHERE mr.id IN (SELECT  MAX(id) FROM monitor_reading WHERE subscription_id_mongo IN (:subId) GROUP BY subscription_id_mongo )) AS tbl " +
                    "GROUP BY subscription_id_mongo", nativeQuery = true)
    List<MonitorReadingCustomResponse> getLastRecordByUserAndSubscription(List<String> subId);

    @Query("SELECT MAX(mr.peakValue) FROM MonitorReading mr WHERE mr.subscriptionIdMongo = :subscriptionId")
    Long getMaxPeakValue(String subscriptionId);

    @Query(value = "select mr.* from monitor_reading mr " +
            "where mr.subscription_id_mongo = :subscriptionId " +
            "and mr.inverter_number = :inverterNumber " +
            "order by mr.time desc  limit 1", nativeQuery = true)
    MonitorReading getLastRecordByTime(String subscriptionId, String inverterNumber);

    @Query(value = "select mr.* from monitor_reading mr where mr.subscription_id_mongo = :subscriptionId and mr.inverter_number = :inverterNumber" +
            " and date_format(MR.time,'%Y-%m-%d') = date_format(:time,'%Y-%m-%d') " +
            "order by mr.time desc  limit 1", nativeQuery = true)
    MonitorReading getLastRecordByTime(String subscriptionId, String inverterNumber, LocalDate time);

    //  select mr.* FROM ec1017.monitor_reading mr WHERE mr.id = (SELECT MAX(m.id) FROM ec1017.monitor_reading m
    //             where m.subscription_id_mongo = '6481f9f86a8f775c1b7015aa' and m.inverter_number = '2233' and m.time < '2023-06-20 07:45:00');
    @Query("select mr FROM MonitorReading mr WHERE mr.id = (SELECT MAX(m.id) FROM MonitorReading m" +
            " WHERE m.subscriptionIdMongo = :subscriptionId and m.inverterNumber = :inverterNumber and m.site = :siteId and m.time < :dateTime)")
    Optional<MonitorReading> getLastRecord(String subscriptionId, String inverterNumber, String siteId, Date dateTime);

    @Query("select mr FROM MonitorReading mr WHERE mr.id = (SELECT MAX(m.id) FROM MonitorReading m" +
            " WHERE m.subscriptionIdMongo = :subscriptionId and m.time < :dateTime)")
    Optional<MonitorReading> getLastRecord(String subscriptionId, Date dateTime);


    @Query(value = "SELECT MR.* FROM monitor_reading MR " +
            "where MR.subscription_id_mongo = :subIdMongo and MR.time in (:time ) " +
            "order by MR.time asc", nativeQuery = true)
    List<MonitorReading> findBySubscriptionIdMongoAndTimeIn(@Param("subIdMongo") String subIdMongo,
                                                            @Param("time") List<Date> time);

    @Query(value = "SELECT MR.* FROM monitor_reading MR " +
            "where MR.subscription_id_mongo = :subId " +
            "and " +
            "date_format(MR.time,'%Y-%m-%d %H:%i') >  date_format(:startTime, '%Y-%m-%d %H:%i') " +
            "and " +
            "date_format(MR.time,'%Y-%m-%d %H:%i') <= date_format(:endTime,'%Y-%m-%d %H:%i') ", nativeQuery = true)
    List<MonitorReading> findBySubscriptionIdMongoAndStartDtTimeAndEndDtTime(@Param("subId") Long subId,
                                                                             @Param("startTime") String startTime,
                                                                             @Param("endTime") String endTime);

    @Query(value = "select MAX(MR.daily_yield) from monitor_reading MR " +
            "where MR.time " +
            "like %:time% " +
            "and MR.inverter_number = :inverterNo " +
            "and MR.subscription_id_mongo = :subsId", nativeQuery = true)
    Double getHighestValueOfTheDay(@Param("time") LocalDate time,
                                   @Param("inverterNo") String inverterNo,
                                   @Param("subsId") String subsId);

    @Query(value = "select MAX(MR.current_value) from monitor_reading MR " +
            "where MR.time " +
            "like %:time% " +
            "and MR.inverter_number = :inverterNo " +
            "and MR.subscription_id_mongo = :subsId", nativeQuery = true)
    Double getHighestCurrentValueOfTheDay(@Param("time") LocalDate time,
                                          @Param("inverterNo") String inverterNo,
                                          @Param("subsId") String subsId);

    @Query(value =
            "SELECT subscription_id_mongo AS subscriptionIds, SUM(annual_yield) AS annualYield, SUM(current_value) AS currentValue, SUM(sytem_size) AS sytemSize," +
                    " SUM(peak_value) AS peakValue, SUM(monthly_yield) AS monthlyYield,SUM(daily_yield) AS dailyYield,SUM(gross_yield) AS grossYield " +
                    "FROM (SELECT id,user_id, subscription_id_mongo,annual_yield,current_value,sytem_size, monthly_yield, daily_yield, gross_yield, peak_value " +
                    "FROM monitor_reading mr  WHERE mr.id IN (SELECT  MAX(id) FROM monitor_reading WHERE subscription_id_mongo IN (:subId) GROUP BY subscription_id_mongo )) AS tbl " +
                    "GROUP BY subscription_id_mongo",
            countQuery = "SELECT COUNT(*) FROM (SELECT MAX(id) FROM monitor_reading WHERE subscription_id_mongo IN (:subId) GROUP BY subscription_id_mongo) AS subQuery",
            nativeQuery = true)
    Page<MonitorReadingCustomResponse> getLastRecordByUserAndSubscription(List<String> subId, Pageable pageable);

    @Query("select max(mr.peakValue) FROM MonitorReading mr WHERE DATE(mr.time) = :date AND mr.subscriptionIdMongo = :subscriptionId AND mr.site = :site")
    Double getPeakValueForDate(Date date, String subscriptionId, String site);

    @Query("select new com.solar.api.tenant.mapper.tiles.dataexport.powermonitoring.DataExportPMTile(edsd.refType, " +
            " case when :isCust = true then concat(concat(cs.userAccount.firstName, ' '), cs.userAccount.lastName) else edsd.subscriptionName end, " +
            " concat(coalesce(mr.currentValue, 0), ' kWh'), function('DATE_FORMAT', mr.time, '%b %d, %Y'), function('DATE_FORMAT', mr.time, '%I:%i %p'),edsd.refId,edsd.subsId) " +
            "from MonitorReading mr " +
            "join ExtDataStageDefinition edsd on edsd.subsId = mr.subscriptionIdMongo " +
            "join CustomerSubscription  cs on cs.extSubsId = mr.subscriptionIdMongo " +
            "where mr.subscriptionIdMongo in (:subIds) " +
            "and mr.id in ( " +
            "   select max(mr2.id) " +
            "   from MonitorReading mr2 " +
            "   where mr2.subscriptionIdMongo in (:subIds) " +
            "   and function('STR_TO_DATE', mr2.time, '%Y-%m-%d %H:%i:%s') between function('STR_TO_DATE', :startDate, '%b %d, %Y') and function('STR_TO_DATE', :endDate, '%b %d, %Y')" +
            "   group by mr2.time,mr2.subscriptionIdMongo " +
            ") " +
            "order by function('STR_TO_DATE', mr.time, '%Y-%m-%d %H:%i:%s'), mr.subscriptionIdMongo ")
    Page<DataExportPMTile> findDayWiseExportDataMR(@Param("subIds") List<String> subIds,
                                                   @Param("startDate") String startDate,
                                                   @Param("endDate") String endDate,
                                                   @Param("isCust") Boolean isCust,
                                                   Pageable pageable);

    List<MonitorReading> findBySubscriptionIdMongoAndTimeGreaterThan(String subscriptionIdMongo, Date time);

    @Query("SELECT mr FROM MonitorReading mr " +
            "WHERE mr.id IN (" +
            "   SELECT MAX(subQuery.id) FROM MonitorReading subQuery " +
            "   WHERE subQuery.subscriptionIdMongo IN :subscriptionIds " +
            "   AND subQuery.grossYield > 0 " +
            "   GROUP BY subQuery.subscriptionIdMongo" +
            ")")
    List<MonitorReading> getLastGrossYieldRecords(@Param("subscriptionIds") List<String> subscriptionIds);

    @Query("SELECT mr FROM MonitorReading mr " +
            "WHERE mr.id IN (" +
            "   SELECT MAX(subQuery.id) FROM MonitorReading subQuery " +
            "   WHERE subQuery.subscriptionIdMongo IN :subscriptionIds " +
            "   AND subQuery.grossYield > 0 " +
            "   AND MONTH(subQuery.time) = SUBSTRING(:monthYear, 1, 2) " +
            "   AND YEAR(subQuery.time) = SUBSTRING(:monthYear, 4, 4) " +
            "   GROUP BY subQuery.subscriptionIdMongo" +
            ")")
    List<MonitorReading> getLastGrossYieldRecordsByMonthYear(@Param("subscriptionIds") List<String> subscriptionIds, @Param("monthYear") String monthYear);

    @Query(value = "select mr.* from monitor_reading mr where mr.subscription_id_mongo = :subscriptionId and mr.inverter_number = :inverterNumber" +
            " and STR_TO_DATE(mr.time, '%Y-%m-%d %H:%i:%s') IN (:times) ", nativeQuery = true)
    List<MonitorReading> findBySubsIdAndInverterNoAndTimeIn(String subscriptionId, String inverterNumber, Set<String> times);

    @Query(value = "select mr.* from monitor_reading mr where mr.subscription_id_mongo = :subscriptionId and mr.inverter_number = :inverterNumber" +
            " and STR_TO_DATE(mr.time, '%Y-%m-%d %H:%i:%s') = STR_TO_DATE(:time, '%Y-%m-%d %H:%i:%s') ", nativeQuery = true)
    Optional<MonitorReading> findBySubsIdAndInverterNoAndTime(String subscriptionId, String inverterNumber, String time);

    @Modifying
    @Query("DELETE FROM MonitorReading mr " +
            "WHERE mr.time BETWEEN :startDate AND :endDate " +
            "AND mr.subscriptionIdMongo =:subscriptionId ")
    void deleteRecordsInRangeAndCondition(@Param("startDate") Date startDate,
                                          @Param("endDate") Date endDate,
                                          @Param("subscriptionId") String subscriptionId);
}
