package com.solar.api.saas.service;

import com.solar.api.saas.model.SchedulerTest;

import java.util.List;

public interface SchedulerTestService {

    SchedulerTest save(SchedulerTest schedulerTest);

    List<SchedulerTest> saveAll(List<SchedulerTest> schedulerTestList);

    List<SchedulerTest> getAll();
}
