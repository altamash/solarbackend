package com.solar.api.saas.service;

import com.solar.api.saas.model.SchedulerTest;
import com.solar.api.saas.repository.SchedulerTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchedulerTestServiceImpl implements SchedulerTestService {

    @Autowired
    SchedulerTestRepository schedulerTestRepository;

    @Override
    public SchedulerTest save(SchedulerTest schedulerTest) {
        return schedulerTestRepository.save(schedulerTest);
    }

    @Override
    public List<SchedulerTest> saveAll(List<SchedulerTest> schedulerTestList) {
        return schedulerTestRepository.saveAll(schedulerTestList);
    }

    @Override
    public List<SchedulerTest> getAll() {
        return schedulerTestRepository.findAll();
    }
}
