package com.solar.api.tenant.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.solar.api.tenant.model.ApiAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ApiAccessLogRepository extends JpaRepository<ApiAccessLog, Long> {
    @Query("SELECT a FROM ApiAccessLog a WHERE a.userId = :userId " +
            "AND (LOWER(a.apiAccessed) LIKE '%signin%' OR LOWER(a.apiAccessed) LIKE '%logout/abrupt%') " +
            "AND a.accessedAt >= :sevenDaysAgo order by a.accessedAt desc")
    Page<ApiAccessLog> findApiAccessLogsForUserWithinLast7Days(
            @Param("userId") Long userId,
            @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo,
            Pageable pageable
    );

    @Query("SELECT a FROM ApiAccessLog a WHERE a.userId = :userId " +
            "AND (LOWER(a.apiAccessed) LIKE '%signin%' OR LOWER(a.apiAccessed) LIKE '%logout/abrupt%') " +
            "AND a.accessedAt >= :daysAgo order by a.accessedAt desc")
    List<ApiAccessLog> findApiAccessLogsForUserWithinLast7Days(
            @Param("userId") Long userId,
            @Param("daysAgo") LocalDateTime daysAgo
    );

     Optional<ApiAccessLog> findBySessionIsNotNullAndTimeOfLoginIsNotNullAndSessionOrderByTimeOfLoginDesc(String session);

}
