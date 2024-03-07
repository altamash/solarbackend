package com.solar.api.tenant.repository.userMapping;

import com.solar.api.tenant.model.user.userMapping.UserMapping;
import com.solar.api.tenant.service.etl.ETLStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMappingRepo extends JpaRepository<UserMapping, Long> {
    @Query("SELECT u FROM UserMapping u WHERE u.entityId NOT IN :entityIds")
    List<UserMapping> findByEntityIdNotIn(@Param("entityIds") List<Long> entityIds);

}
