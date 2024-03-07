package com.solar.api.tenant.service.extended.project;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.model.extended.project.activity.ActivityDetail;
import com.solar.api.tenant.model.extended.project.activity.ActivityHead;

import java.util.List;

public interface ActivityService {

    ActivityHead saveActivityHead(ActivityHead activityHead, Long projectHeadId);

    ActivityHead updateActivityHead(ActivityHead activityHeadD);

    List<ActivityHead> findAllActivityHeads();

    List<ActivityHead> findAllActivityByProjectId(Long projectId);

    ActivityHead findById(Long id);

    ObjectNode deleteActivityHead(Long id, String comments);

    void deleteAllActivityHeads();

    ///////ActivityDetail/////
    ActivityDetail saveActivityDetail(ActivityDetail activityDetail);

    List<ActivityDetail> saveActivityDetails(List<ActivityDetail> activityDetails);

    List<ActivityDetail> updateActivityDetails(List<ActivityDetail> activityDetails);

    ActivityDetail findActivityDetailById(Long id);

    List<ActivityDetail> findAllActivityDetails();

    void deleteActivityDetail(Long id);

    void deleteAllActivityDetails();

    Double getTotalHours (Long employeeId, Long projectId);
}
